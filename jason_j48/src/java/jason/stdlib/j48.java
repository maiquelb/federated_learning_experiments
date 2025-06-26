package jason.stdlib; 

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import jason.asSyntax.Literal;
import jason.asSyntax.Rule;

import static jason.asSyntax.ASSyntax.createAtom;
import static jason.asSyntax.ASSyntax.parseLiteral;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation;

import java.text.ParseException;
import java.util.*;
import java.util.regex.*;

import rule2formula.DataSet;
import rule2formula.DecisionRule;


public class j48 extends DefaultInternalAction {

        @Override
        public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
            DataSet rules = run_j48(args[0].toString().replaceAll("^\"|\"$", ""));     
            ListTermImpl listRules = new ListTermImpl();
            Iterator<DecisionRule> it = rules.getRules().iterator();
            while(it.hasNext()){
                Rule r = it.next().toJasonLogicalFormula();
                listRules.add(r);
                ts.getAg().addBel(r);
                //System.out.println(it.next()); 
                //ts.getAg().addBel(it.next().toJasonLogicalFormula());
            }
            return un.unifies(listRules, args[1]);
            //return true;
        }

        private DataSet run_j48(String datasetFileName){
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

                DataSet ds = new DataSet();
                if(ds.getFromWekaDecisionTree(arvore.toString(),dataset.classAttribute().name())){
                   //System.out.println(ds);
                //    Iterator<DecisionRule> it = ds.getRules().iterator();
                //    while(it.hasNext())
                //       System.out.println(it.next().toJasonLogicalFormula());
                    return ds;
                }

               //format rules to a suitable representation for Jason
               //return  extrairRegras(arvore.toString(),dataset.classAttribute().name());
               
               
        
            }catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }        
        
        return null;
       
        }


        public static Term condition2Term(String condition){
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
            [outlook(sunny),humidity(high)],result(play,no,3.0),
            [outlook(sunny),humidity(normal)],result(play,yes,3.0),
            [outlook(overcast)],result(play,yes,4.0),
            [outlook(rainy),windy(true)],result(play,no,2.0),
            [outlook(rainy),windy(false)],result(play,yes,2.0),
           ]
         * 
         * 
         */
        public static ListTermImpl extrairRegras(String treeStr, String className) {
            
            ListTermImpl listResult = new ListTermImpl(); //list of rules (to be returned by this method)
            ListTermImpl listConditions = new ListTermImpl(); //list of conditions - managed as a stack (LIFO policy)
            

            List<String> regras = new ArrayList<>();

            String[] linhas = treeStr.split("\n"); //array of strings, a string for each line of the input tree
    
            Stack<String> caminhoAtual = new Stack<>(); //stack 
    
            Pattern linhaFolha = Pattern.compile(".*: ([^ ]+) \\((\\d+\\.?\\d*)(/\\d+\\.?\\d*)?\\)");
    
            for (String linha : linhas) { //for each line of the input tree
                System.out.println("---> " + linha);
                

                if (!linha.contains(":") && !linha.contains("=")) continue;
                int nivel = contarNivel(linha);
                String condicaoOuClasse = linha.trim();
    
                // Remove níveis anteriores se necessário
                while (caminhoAtual.size() > nivel) {
                    listConditions.remove(listConditions.size()-1);
                    caminhoAtual.pop();
                }
    
                if (linhaFolha.matcher(linha).matches()) {
            //         // Linha com classe (folha)
                     Matcher m = linhaFolha.matcher(linha);
            //         if (m.find()) {
            //             String classe = m.group(1);
            //             String peso = m.group(2);
    
    
            //             // Remove condição final do tipo: windy = TRUE: no (2.0)
            //             String[] partes = condicaoOuClasse.split(":");
            //             String cond = partes[0].trim();
            //             caminhoAtual.push(formatarCond(cond));
            //             listConditions.add(condition2Term(formatarCond(cond)));

            //             ListTermImpl listRule = new ListTermImpl();
            //             listRule.add(listConditions.clone());
            //             listRule.add(condition2Term(String.format("result(%s,%s,%s)",  className, classe, peso)));      
            //             listResult.add(listRule);                  

            //             listConditions.remove(listConditions.size()-1);
            //             caminhoAtual.pop(); // Remove condição final
            //         }
                } else {
                    // Condição intermediária, sem o '|'
                    // if(!condicaoOuClasse.contains("Number of Leaves")&&!condicaoOuClasse.contains("Size of the tree")){
                    //    condicaoOuClasse = condicaoOuClasse.replace("|   ", "").trim(); // Remove o '|' e o espaço
                    //    caminhoAtual.push(formatarCond(condicaoOuClasse));
                    //    listConditions.add(condition2Term(formatarCond(condicaoOuClasse)));

                    // }
                }
                
            }

            return listResult;
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
            // outlook = sunny -> outlook(sunny)
            return cond.replace("|","").replace(" = ", "(") + ")";
        }
    
}