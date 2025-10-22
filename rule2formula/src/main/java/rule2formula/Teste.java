package rule2formula;

import jason.asSyntax.ListTermImpl;


import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.asSyntax.Rule;
import jason.asSyntax.parser.ParseException;
import jason.asSyntax.parser.TokenMgrError;
import jason.asSyntax.Pred;


import static jason.asSyntax.ASSyntax.createAtom;
import static jason.asSyntax.ASSyntax.parseLiteral;
import static jason.asSyntax.ASSyntax.parseRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Debug.Random;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class Teste {


	public static void main(String args[]) {
		ListTermImpl l =  run_j48("/mnt/1C4C766F4C764414/maiquel/git/federated_learning_experiments/datasets/weather.arff");
		//ListTermImpl l =  run_j48("/mnt/1C4C766F4C764414/maiquel/git/federated_learning_experiments/datasets/breast-cancer.arff");
		//ListTermImpl l =  run_j48("/mnt/1C4C766F4C764414/maiquel/git/federated_learning_experiments/datasets/car.arff");
		for(Term t:l)
			System.out.println("--- " + t);

	}

	private static ListTermImpl run_j48(String datasetFileName){
		try {
			DataSource source = new DataSource(datasetFileName); // load the dataset
			Instances dataset = source.getDataSet();

			// set the classifier attribute
			if (dataset.classIndex() == -1) 
				dataset.setClassIndex(dataset.numAttributes() - 1); // latest attribute   

			// set up the classifier J48 (C4.5)
			J48 arvore = new J48();
			arvore.setUnpruned(false); 

			// build the classifier
			arvore.buildClassifier(dataset);


			// evaluate
			Evaluation eval = new Evaluation(dataset);
			eval.crossValidateModel(arvore, dataset, 10, new Random(1));

			System.out.println(arvore.toString());

			//format rules to a suitable representation for Jason
			extrairDataSet(arvore.toString(),dataset.classAttribute().name());
			
			return  extrairRegras(arvore.toString(),dataset.classAttribute().name());

		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        

		return null;

	}


	public static Term condition2Term(String condition){
		//		System.out.println("Condition: " + condition);
		try{
			Literal l = parseLiteral(condition.replaceAll("FALSE", "false").replaceAll("TRUE", "true"));
			return l;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}


	/*
      Returns a list of rules in the form [[C],result(classname,class_value,weight)]
         s.t. C is a list of conditions


      For example, a possible input is:

       outlook = sunny
       |   humidity = high: no (3.0)
       |   humidity = normal: yes (2.0)
       outlook = overcast: yes (4.0)
       outlook = rainy
       |   windy = TRUE: no (2.0)
       |   windy = FALSE: yes (3.0)


     For this input, the expected output is:
       [
        [outlook(sunny),humidity(high)],result(play,no,3.0)],
        [outlook(sunny),humidity(normal)],result(play,yes,3.0)],
        [outlook(overcast)],result(play,yes,4.0)],
        [outlook(rainy),windy(true)],result(play,no,2.0)],
        [outlook(rainy),windy(false)],result(play,yes,2.0)],
       ]
	 * 
	 * 
	 */
	public static ListTermImpl extrairRegras(String treeStr, String className) {
		

		
		DataSet dataset = new DataSet();

		HashSet<Attribute> attributes = new HashSet<Attribute>();
		
		Attribute att = null;

		ListTermImpl listConditions = new ListTermImpl(); //list of conditions - managed as a stack (LIFO policy)
		ListTermImpl listResult = new ListTermImpl(); //list of rules (to be returned by this method)

		List<String> regras = new ArrayList<>();

		String[] linhas = treeStr.split("\n"); //array of strings, a string for each line of the input tree

		Stack<String> caminhoAtual = new Stack<>(); //stack 
		Stack<Attribute> stackAttributes = new Stack<Attribute>();

		Pattern linhaFolha = Pattern.compile(".*: ([^ ]+) \\((\\d+\\.?\\d*)(/\\d+\\.?\\d*)?\\)");

		for (String linha : linhas) { //for each line of the input tree


			//TODO: está carregando os atributos da regra anterior para a proxima regra. Resolver.

			if (!linha.contains(":") && !linha.contains("=")) continue;
			int nivel = contarNivel(linha);
			String condicaoOuClasse = linha.trim();

			// Remove níveis anteriores se necessário
			while (caminhoAtual.size() > nivel) {
				System.out.println("Removendo " + listConditions.get(listConditions.size()-1) + "/" + att + " - " + attributes.size());
												
				listConditions.remove(listConditions.size()-1);
				caminhoAtual.pop();
				attributes.remove(stackAttributes.peek());
				stackAttributes.pop();
			}

			if (linhaFolha.matcher(linha).matches()) {
				// Linha com classe (folha)
				Matcher m = linhaFolha.matcher(linha);
				if (m.find()) {
					String classe = m.group(1);
					String peso = m.group(2);


					// Remove condição final do tipo: windy = TRUE: no (2.0)
					String[] partes = condicaoOuClasse.split(":");
					String cond = partes[0].trim();

					caminhoAtual.push(formatarCond(cond));
					listConditions.add(condition2Term(formatarCond(cond)));

					att = condition2Attribute(cond);	

					if(att!=null)
						try {
							attributes.add(att);
							stackAttributes.add(att);
						} catch (TokenMgrError e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					ListTermImpl listRule = new ListTermImpl();
					listRule.add(listConditions.clone());
					listRule.add(condition2Term(String.format("result(%s,%s,%s)",  className, classe, peso)));



					Attribute consequent;
					if(classe.equals("true")|classe.equals("yes")) 
						consequent = new Attribute(className, true);
					else
						if(classe.equals("false")|classe.equals("no"))
							consequent = new Attribute(className, false);
						else
							consequent = new Attribute(className, classe);

					DecisionRule rule = new DecisionRule();
					rule.setAntecedent(attributes);
					rule.setConsequent(consequent);
					System.out.println("formula: " + rule.toJasonLogicalFormula().toString());
					listResult.add(listRule);

					System.out.println("* Adicionando rule " + rule);
					
					dataset.addRule(rule);

					attributes.remove(att);

					
					listConditions.remove(listConditions.size()-1);
					caminhoAtual.pop(); // Remove condição final
					stackAttributes.pop();
				}
			} else {
				// Condição intermediária, sem o '|'
				if(!condicaoOuClasse.contains("Number of Leaves")&&!condicaoOuClasse.contains("Size of the tree")){
					condicaoOuClasse = condicaoOuClasse.replace("|   ", "").trim(); // Remove o '|' e o espaço
					caminhoAtual.push(formatarCond(condicaoOuClasse));
					listConditions.add(condition2Term(formatarCond(condicaoOuClasse)));

					attributes.clear();
					att = condition2Attribute(condicaoOuClasse);
					attributes.add(att);
					stackAttributes.add(att);
					if(att!=null)
						try {
							//System.out.println("ATRIBUTO RAIZ:  " + att);
						} catch (TokenMgrError e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

				}
			}

		}
		//
		//		System.out.println("**** Logical Formulae ****");
		//		Iterator it =  dataset.getRules().iterator();
		//		while(it.hasNext())
		//			System.out.println(it.next());
		////		
		//		for(DecisionRule r : dataset.getRules()) {
		//			System.out.println(r.toJasonLogicalFormula());
		//		}
		//		
		return listResult;
	}


	/**
	 * Transform conditions read from trees (e.g. |   humidity = high) into atrributes
	 * @param condition
	 * @return
	 */
	public static  Attribute condition2Attribute(String condition) {
		//System.out.println("[condition2Attribute] " + condition );
		Term t = condition2Term(formatarCond(condition));
		Attribute att = null;
		//if(t.isPred() && ((Pred)t).getArity()==1 && ((Pred)t).getTerm(0).toString().equals("true")|((Pred)t).getTerm(0).toString().equals("false"))
		if(t.isPred() && ((Pred)t).getArity()==1 && ((Pred)t).getTerm(0).toString().equals("true"))
			att	 = new Attribute(((Pred)t).getFunctor().toString(), true);
		else
			if(t.isPred() && ((Pred)t).getArity()==1 && ((Pred)t).getTerm(0).toString().equals("false"))
				att	 = new Attribute(((Pred)t).getFunctor().toString(), false);
			else
				if(t.isPred() && ((Pred)t).getArity()==1) //categorical attribute
					att = new Attribute(((Pred)t).getFunctor().toString(), ((Pred)t).getTerm(0).toString());

		return att;
	}

	public static DataSet extrairDataSet(String treeStr, String className) {
		System.out.println("aaaaa");
		System.out.println(treeStr);
		System.out.println("zzzz");
		
		DataSet dataset = new DataSet(); //the resulting dataset of inference rules
		HashSet<Attribute> attributes = new HashSet<Attribute>(); //temporary set of attributes of the rule being currently processed 
		Attribute att = null; 

//		ListTermImpl listConditions = new ListTermImpl(); //list of conditions - managed as a stack (LIFO policy)
//		ListTermImpl listResult = new ListTermImpl(); //list of rules (to be returned by this method)

//		List<String> regras = new ArrayList<>();

		String[] linhas = treeStr.split("\n"); //array of strings, a string for each line of the input tree

		Stack<String> caminhoAtual = new Stack<>(); //stack 
		
		Stack<Attribute> stackAttributes = new Stack<Attribute>(); //a stack of attributes being processed., required to handle the tree of attributes 

		Pattern linhaFolha = Pattern.compile(".*: ([^ ]+) \\((\\d+\\.?\\d*)(/\\d+\\.?\\d*)?\\)");

		for (String linha : linhas) { //for each line of the input tree

			if (!linha.contains(":") && !linha.contains("=")) continue;
			int nivel = contarNivel(linha);
			String condicaoOuClasse = linha.trim();

			// Remove níveis anteriores se necessário
			while (caminhoAtual.size() > nivel) {
				caminhoAtual.pop();
				attributes.remove(stackAttributes.peek());
				stackAttributes.pop();
			}

			if (linhaFolha.matcher(linha).matches()) {
				// Linha com classe (folha)
				Matcher m = linhaFolha.matcher(linha);
				if (m.find()) {
					String classe = m.group(1);
					String peso = m.group(2);


					// Remove condição final do tipo: windy = TRUE: no (2.0)
					String[] partes = condicaoOuClasse.split(":");
					String cond = partes[0].trim();

					caminhoAtual.push(formatarCond(cond));

					att = condition2Attribute(cond);	

					if(att!=null)
						try {
							attributes.add(att);
							stackAttributes.add(att);
						} catch (TokenMgrError e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					//listRule.add(condition2Term(String.format("result(%s,%s,%s)",  className, classe, peso)));



					Attribute consequent;
					if(classe.equals("true")|classe.equals("yes")) 
						consequent = new Attribute(className, true);
					else
						if(classe.equals("false")|classe.equals("no"))
							consequent = new Attribute(className, false);
						else
							consequent = new Attribute(className, classe);

					DecisionRule rule = new DecisionRule();
					rule.setAntecedent((Set<Attribute>) attributes.clone());
					rule.setConsequent(consequent);
					System.out.println("formula: " + rule.toJasonLogicalFormula().toString());

					System.out.println("@ Adicionando rule " + rule);
					
					dataset.addRule(rule);

					attributes.remove(att);

					
					caminhoAtual.pop(); // Remove condição final
					stackAttributes.pop();
				}
			} else {
				// Condição intermediária, sem o '|'
				if(!condicaoOuClasse.contains("Number of Leaves")&&!condicaoOuClasse.contains("Size of the tree")){
					condicaoOuClasse = condicaoOuClasse.replace("|   ", "").trim(); // Remove o '|' e o espaço
					caminhoAtual.push(formatarCond(condicaoOuClasse));

					attributes.clear();
					att = condition2Attribute(condicaoOuClasse);
					attributes.add(att);
					stackAttributes.add(att);


				}
			}

		}
		//
		//		System.out.println("**** Logical Formulae ****");
		//		Iterator it =  dataset.getRules().iterator();
		//		while(it.hasNext())
		//			System.out.println(it.next());
		////		
		//		for(DecisionRule r : dataset.getRules()) {
		//			System.out.println(r.toJasonLogicalFormula());
		//		}
		//		
		
		System.out.println("************");
		System.out.println(dataset.toString());
		System.out.println("=============");
		
		return dataset;
	}

	public static int contarNivel(String linha) {
		int nivel = 0;
		while (linha.startsWith("|   ")) {
			nivel++;
			linha = linha.substring(4);
		}
		return nivel;
	}

	public static String formatarCond(String cond) {
		return cond.replace("|","").replace(" = ", "(") + ")";
	}

}
