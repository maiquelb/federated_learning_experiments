
package jason.stdlib;

import static jason.asSyntax.ASSyntax.createAtom;
import static jason.asSyntax.Pred.parsePred;

import java.util.List;

import jason.asSyntax.ListTermImpl;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Pred;

public class Util {

    public static Pred generateDataSetRules(List<String> rules){
        ListTermImpl listResult = new ListTermImpl();
        for(String currentRule : rules){ //for each rule
            System.out.println("processando regra " + currentRule);
            //split the elements of the rule
            String[] ruleArray = currentRule.split(",");

            //build predicates for support and error
            Pred support = new Pred("support",1); 
            support.addTerm(new NumberTermImpl(ruleArray[ruleArray.length-1])); //support is the latest element of the rule

            Pred error = new Pred("error",1); 
            error.addTerm(new NumberTermImpl(ruleArray[ruleArray.length-2])); //error is the penultimate element of the rule

            ListTermImpl l = new ListTermImpl();
            for(int i=0;i<ruleArray.length-2;i++)
               if(ruleArray[i].trim().equals("*"))
                 l.add(createAtom("null"));
            else
                 l.add(createAtom(ruleArray[i].trim().replaceAll("-", "_")));
            l.add(support);
            l.add(error);
            listResult.add(l);
        }            
        Pred predResult = new Pred("rules", 1);
        predResult.addTerm(listResult);

        return predResult;
    }


    public static Pred generateDataSetMetrics(List<String> metrics) {
        String sResult = "";
        for(String linha: metrics) { // Lê linha por linha                               
            String[] data = linha.split(":", 2);
            String pred = data[0] + "("+ data[1] +")".replaceAll("-","_");
            sResult = sResult+pred+",";                                        
            sResult = sResult.replaceAll("-","_");                                          
        } 
        //remover a última vírgula                
        sResult = sResult.substring(0,sResult.length()-1);     
        sResult = "dataset_metrics("+sResult+")"; 
        return parsePred(sResult);
    }


}    
