package rule2formula;

import static jason.asSyntax.ASSyntax.parseLiteral;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jason.asSyntax.Literal;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Pred;
import jason.asSyntax.Term;
import jason.asSyntax.parser.ParseException;
import jason.asSyntax.parser.TokenMgrError;

public class DataSet {

	private Set<DecisionRule> rules;
	
	private enum falseValues{};

	public DataSet() {
		this.rules = new HashSet<DecisionRule>();
	}

	public DataSet(Set<DecisionRule> rules) {
		this.rules = rules;
	}

	public void addRule(DecisionRule rule) {
		this.rules.add(rule);

	}

	public Set<DecisionRule> getRules(){
		return this.rules;
	}

	@Override
	public String toString() {
		String result = "";
		Iterator<DecisionRule> it = this.rules.iterator();
		while(it.hasNext())
			result = result + "\n" + it.next();		
		return result;
	}

	/**
	 * Builds the dataset from a weka decision tree.
	 * 
	 * For example, a possible input is:

       outlook = sunny
       |   humidity = high: no (3.0)
       |   humidity = normal: yes (2.0)
       outlook = overcast: yes (4.0)
       outlook = rainy
       |   windy = TRUE: no (2.0)
       |   windy = FALSE: yes (3.0)


     For this input, the expected output is:

	  { humidity=high,outlook=sunny : play=false
		outlook=rainy,windy=false : play=true
		humidity=normal,outlook=sunny : play=true
		outlook=overcast : play=true
		outlook=rainy,windy=true : play=false }
	 * 
	 * @return true whether the process runs successfully
	 */
	public boolean getFromWekaDecisionTree(String treeStr, String className) {

		DataSet dataset = new DataSet(); //the resulting dataset of inference rules
		HashSet<Attribute> attributes = new HashSet<Attribute>(); //temporary set of attributes of the rule being currently processed 
		Attribute att = null; 

		String[] linhas = treeStr.split("\n"); //array of strings, a string for each line of the input tree

		Stack<String> caminhoAtual = new Stack<>(); //stack 

		Stack<Attribute> stackAttributes = new Stack<Attribute>(); //a stack of attributes being processed., required to handle the tree of attributes 

		Pattern linhaFolha = Pattern.compile(".*: ([^ ]+) \\((\\d+\\.?\\d*)(/\\d+\\.?\\d*)?\\)");

		double support = 0;

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

					//extrair o suporte da parte descartada (ex. extrair 2.0 de "no (2.0)")
					Pattern pattern = Pattern.compile("\\((\\d+(\\.\\d+)?)\\)");
					Matcher matcher = pattern.matcher(partes[1]);
					if(matcher.find()) 
						support = Double.parseDouble(matcher.group(1));

					caminhoAtual.push(formatarWekaDecisionTreeCondition(condicaoOuClasse));

					
					//System.out.println("[DataSet] vai criar atributo " + cond);
					att = wekaDecisionTreeCondition2Attribute(cond);

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
					rule.setSupport(support);

					dataset.addRule(rule);



					attributes.remove(att);

					caminhoAtual.pop(); // Remove condição final
					stackAttributes.pop();
				}
			} else {
				// Condição intermediária, sem o '|'
				if(!condicaoOuClasse.contains("Number of Leaves")&&!condicaoOuClasse.contains("Size of the tree")){
					condicaoOuClasse = condicaoOuClasse.replace("|   ", "").trim(); // Remove o '|' e o espaço
					//caminhoAtual.push(formatarCond(condicaoOuClasse));
					caminhoAtual.push(formatarWekaDecisionTreeCondition(condicaoOuClasse));

					attributes.clear();
					//att = condition2Attribute(condicaoOuClasse);
					//att = wekaDecisionTreeCondition2Attribute(condicaoOuClasse);
					att = wekaDecisionTreeCondition2Attribute(condicaoOuClasse);
					attributes.add(att);
					stackAttributes.add(att);


				}
			}

		}

		//		System.out.println("************");
		//		System.out.println(dataset.toString());
		//		System.out.println("=============");
		//		
		//return dataset;

		this.rules = dataset.getRules();

		return true;

	}


	private int contarNivel(String linha) {
		int nivel = 0;
		while (linha.startsWith("|   ")) {
			nivel++;
			linha = linha.substring(4);
		}
		return nivel;
	}

	/**
	 * Transform conditions read from trees (e.g. |   humidity = high) into atrributes
	 * @param condition
	 * @return
	 */
	private Attribute wekaDecisionTreeCondition2Attribute(String condition) {		
		Term t = condition2Term(formatarWekaDecisionTreeCondition(condition));
//		if(((Pred)t).getArity()==1)				
//			System.out.println("[DataSet] condition " + t.toString() + " - " + ((Pred)t).getTerm(0).getClass().getName() );
		Attribute att = null;
		//if(t.isPred() && ((Pred)t).getArity()==1 && ((Pred)t).getTerm(0).toString().equals("true")|((Pred)t).getTerm(0).toString().equals("false"))
		if(t.isPred() && ((Pred)t).getArity()==1 && ((Pred)t).getTerm(0).toString().equals("true"))
			att	 = new Attribute(((Pred)t).getFunctor().toString(), true);
		else
			if(t.isPred() && ((Pred)t).getArity()==1 && ((Pred)t).getTerm(0).toString().equals("false"))
				att	 = new Attribute(((Pred)t).getFunctor().toString(), false);
		
			else
				if(t.isPred() && ((Pred)t).getArity()==1 && (((Pred)t).getTerm(0) instanceof NumberTermImpl)) {
					att = new Attribute(((Pred)t).getFunctor().toString(),  ((NumberTermImpl)(((Pred)t).getTerm(0))).solve() );
//					try {
//						System.out.println("[DataSet] going to create numeric atribute " + ((Pred)t).getFunctor().toString() + " - " + ((NumberTermImpl)(((Pred)t).getTerm(0))).solve() + " --- " + att.toLogicalFormula());
//					} catch (ParseException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (TokenMgrError e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}					
				}
		
			else
				if(t.isPred() && ((Pred)t).getArity()==1) //categorical attribute
					att = new Attribute(((Pred)t).getFunctor().toString(), ((Pred)t).getTerm(0).toString());

		return att;
	}


	private String formatarWekaDecisionTreeCondition(String cond) {
		return cond.replace("|","").replace(" = ", "(") + ")";
	}

	private static Term condition2Term(String condition){
		try{
			Literal l = parseLiteral(condition.replaceAll("FALSE", "false").replaceAll("TRUE", "true").replaceAll("-", ""));
			return l;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
