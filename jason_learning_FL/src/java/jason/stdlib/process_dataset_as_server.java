package jason.stdlib; 

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import jason.stdlib.set.create;
import jason.asSyntax.Literal;
import jason.asSyntax.Rule;
import jason.asSyntax.Pred;

import static jason.asSyntax.ASSyntax.createAtom;
import static jason.asSyntax.ASSyntax.parseLiteral;
import static jason.asSyntax.Pred.parsePred;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.text.ParseException;
import java.util.*;
import java.util.regex.*;

import org.antlr.v4.codegen.model.ModelElement;

import rule2formula.DataSet;
import rule2formula.DecisionRule;

    
import model.ModelRuleMatchCount4Jason;
import coordinator.WekaModelWrapper;




public class process_dataset_as_server extends DefaultInternalAction {  

    private static ModelRuleMatchCount4Jason model = new ModelRuleMatchCount4Jason();



    private static ListTermImpl evalToPredicates(Evaluation eval) {
        String summary = eval.toSummaryString();
        String[] lines = summary.split("\\r?\\n");
        // List<String> predicates = new ArrayList<>();
        ListTermImpl predicates  = new ListTermImpl();

        Pattern pattern = Pattern.compile("^(.*?)\\s+([0-9.]+)(?:\\s+([0-9.]+))?.*$");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("=")) continue;

            Matcher m = pattern.matcher(line);
            if (m.matches()) {
                // String name = m.group(1).toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("_+", "_");
                // String v1 = m.group(2);
                // String v2 = m.group(3);
                Pred p = new Pred(m.group(1).toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("_+", "_"), 2);
                p.addTerm(new NumberTermImpl(Double.parseDouble(m.group(2))));
                if(m.group(3)!=null)
                   p.addTerm(new NumberTermImpl(Double.parseDouble(m.group(3))));


                predicates.add(p);
                // if (v2 != null) {
                //     //predicates.add(name + "(" + v1 + "," + v2 + ").");
                //     predicates.add(new P)
                // } else {
                //     predicates.add(name + "(" + v1 + ").");
                // }
            }
        }



        return predicates;
    }


    public static List<Literal> evalToClassDetails(Evaluation eval) {
    List<Literal> beliefs = new ArrayList<>();

    try {
        String details = eval.toClassDetailsString();
        String[] lines = details.split("\\r?\\n");
        

        boolean tableStarted = false;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("=")) continue;

            if (line.startsWith("TP Rate")) {
                tableStarted = true;
                continue;
            }
            if (!tableStarted) continue;

            String[] parts = line.split("\\s+");
            if (parts.length < 9) continue;

            try {
                String className;
                double tpRate, fpRate, precision, recall, fMeasure, mcc, rocArea, prcArea;

                // caso normal (classe no final)
                if (!line.startsWith("Weighted")) {
                    tpRate    = parseSafe(parts[0]);
                    fpRate    = parseSafe(parts[1]);
                    precision = parseSafe(parts[2]);
                    recall    = parseSafe(parts[3]);
                    fMeasure  = parseSafe(parts[4]);
                    mcc       = parseSafe(parts[5]);
                    rocArea   = parseSafe(parts[6]);
                    prcArea   = parseSafe(parts[7]);
                    className = parts[8];
                } else { 
                    // caso Weighted Avg. (nome vem no início)
                    className = "weighted_avg";
                    tpRate    = parseSafe(parts[2]);
                    fpRate    = parseSafe(parts[3]);
                    precision = parseSafe(parts[4]);
                    recall    = parseSafe(parts[5]);
                    fMeasure  = parseSafe(parts[6]);
                    mcc       = parseSafe(parts[7]);
                    rocArea   = parseSafe(parts[8]);
                    prcArea   = parseSafe(parts[9]);
                }

                // cria o predicado principal
                Literal pred = ASSyntax.createLiteral(className.toLowerCase());

                // adiciona métricas como subtermos
                pred.addTerm(ASSyntax.createStructure("tp_rate", new NumberTermImpl(tpRate)));
                pred.addTerm(ASSyntax.createStructure("fp_rate", new NumberTermImpl(fpRate)));
                pred.addTerm(ASSyntax.createStructure("precision", new NumberTermImpl(precision)));
                pred.addTerm(ASSyntax.createStructure("recall", new NumberTermImpl(recall)));
                pred.addTerm(ASSyntax.createStructure("f_measure", new NumberTermImpl(fMeasure)));
                pred.addTerm(ASSyntax.createStructure("mcc", new NumberTermImpl(mcc)));
                pred.addTerm(ASSyntax.createStructure("roc_area", new NumberTermImpl(rocArea)));
                pred.addTerm(ASSyntax.createStructure("prc_area", new NumberTermImpl(prcArea)));

                beliefs.add(pred);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return beliefs;
}


/** 
 * Converte string numérica no formato do Weka em double.
 * Substitui vírgula por ponto e trata "?" como NaN.
 */
private static double parseSafe(String value) {
    if (value.equals("?")) return Double.NaN;
    return Double.parseDouble(value.replace(",", "."));
}


     /**
     * Converte a matriz de confusão do Weka em crenças Jason.
     * Cada célula vira um predicado do tipo:
     * confusion(ClassReal, ClassPredita, Valor).
     *
     * @param eval objeto Evaluation já calculado
     * @return lista de Literals Jason
     */
    public static List<Literal> evalToConfusionMatrix(Evaluation eval) {
        List<Literal> beliefs = new ArrayList<>();

        try {
            // Obtém a matriz de confusão
            double[][] cm = eval.confusionMatrix();

            // Obtém nomes das classes a partir do dataset
            Instances data = eval.getHeader(); // dataset usado na avaliação
            Attribute classAttr = data.classAttribute();

            for (int i = 0; i < cm.length; i++) {
                for (int j = 0; j < cm[i].length; j++) {
                    int val = (int) cm[i][j]; // número de instâncias
                    String actual = classAttr.value(i);
                    String predicted = classAttr.value(j);

                    Literal l = ASSyntax.createLiteral(
                            "confusion",
                            ASSyntax.createAtom(actual),
                            ASSyntax.createAtom(predicted),
                            ASSyntax.createNumber(val)
                    );
                    beliefs.add(l);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return beliefs;
    }


    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        //ModelRuleMatchCount4Jason model = new ModelRuleMatchCount4Jason("");
        model.loadDatasetMetrics((Pred)args[0]);
        model.loadRules((Pred)args[1]);
        //model.setTestDataSetFileName(args[2].toString().replaceAll("^\"|\"$", ""));
        // System.out.println("+++++++++");
        // System.out.println(model.toString());
        // System.out.println(">>> " + model.toPred());
        // System.out.println("+++++++++");


        // testando o modelo 
		// TODO - retirar a necessidade de passar o ds de teste para o modelo, pode ser passado direto para o wrapper
        ArffLoader loader = new ArffLoader();
		loader.setSource(new File(args[2].toString().replaceAll("^\"|\"$", "")));		
        //loader.setSource(new File(model.getTestDataSetFileName()));		
        Instances testDataSet = loader.getDataSet(); 


        // estabelece qual é a classe no dataset - só funciona se for a última 
        testDataSet.setClassIndex(testDataSet.numAttributes() - 1);
		        
        // WekaModelWrapper é um wrapper pra podermos usar as classes de avaliação de desempenho já existentes no Weka
        WekaModelWrapper myClassifier = new WekaModelWrapper();
        myClassifier.setModel(model);
        weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(testDataSet);
		eval.evaluateModel(myClassifier, testDataSet);
		
        
		// mostra os resultados no padrão weka
		// System.out.println(eval.toSummaryString("\nResults\n======\n", false));
        // System.out.println(eval.toClassDetailsString("\nClass Details\n======\n"));
        // System.out.println(eval.toMatrixString("\nConfusion Matrix\n======\n"));

        
        Pred predModel = model.toPred();
        Pred summary = new Pred("summary");
        summary.addTerm(evalToPredicates(eval));
        predModel.addAnnot(summary);

        Pred classDetails = new Pred("class_details");
        for(Literal l : evalToClassDetails(eval)){
            classDetails.addTerm(l);
        }
        predModel.addAnnot(classDetails);


        Pred confusion_matrix = new Pred("confusion_matrix");
        ListTermImpl confusion_matrix_list = new ListTermImpl();
        for(Literal l : evalToConfusionMatrix(eval)){
            confusion_matrix_list.add(l);
        }
        confusion_matrix.addTerm(confusion_matrix_list);
        predModel.addAnnot(confusion_matrix);


        //return true;
        return un.unifies(predModel, args[3]);




    }    

}    