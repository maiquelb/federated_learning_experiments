package jason.stdlib; 

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
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

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.text.ParseException;
import java.util.*;
import java.util.regex.*;

import rule2formula.DataSet;
import rule2formula.DecisionRule;


public class process_dataset extends DefaultInternalAction {

        @Override
        public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

            Pred predResult = new Pred("dataset", 2);
            predResult.addTerm(readDataSetMetrics(args[0].toString().replaceAll("^\"|\"$", "")));
            predResult.addTerm(readDataSetRules(args[1].toString().replaceAll("^\"|\"$", ""),
                                                args[2].toString().replaceAll("^\"|\"$", ""))
                                );


            
            return un.unifies(predResult, args[3]);
            //return true;
        }


        private Pred readDataSetMetrics(String filePath) {
            String sResult = "";
            // Tenta abrir o arquivo
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String linha;
                // Lê linha por linha
                while ((linha = br.readLine()) != null) {
                    String[] data = linha.split(":", 2);
                    String pred = data[0] + "("+ data[1] +")".replaceAll("-","_");
                    sResult = sResult+pred+",";                    
                    
                }
                sResult = sResult.replaceAll("-","_");
                //remover a última vírgula                
                sResult = sResult.substring(0,sResult.length()-1);
                sResult = "dataset_metrics("+sResult+")";
                return parsePred(sResult);
            } catch (IOException e) {
                System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            }
             catch (Exception e) {
                System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            }
            return null;
        }


        private Pred readDataSetRules(String rulesFilePath, String rulesMetricsFilePath) {
            // Tenta abrir o arquivo
            try (BufferedReader brRules = new BufferedReader(new FileReader(rulesFilePath));
                 BufferedReader brMetrics = new BufferedReader(new FileReader(rulesMetricsFilePath))) {
            // try (BufferedReader brRules = new BufferedReader(new FileReader(rulesFilePath))) {
                ListTermImpl listResult = new ListTermImpl();
                String linha, sMetrics;
                // Lê linha por linha
                while ((linha = brRules.readLine()) != null) 
                 if((sMetrics = brMetrics.readLine())!=null){ //TODO: tratar casos em que não haja uma linha correspondente no arquivo de métricas    

                    String[] metrics = sMetrics.split(",");
                    Pred support = new Pred("support",1); 
                    support.addTerm(new NumberTermImpl(Double.parseDouble(metrics[0].trim())));

                    Pred error = new Pred("error",1); 
                    error.addTerm(new NumberTermImpl(Double.parseDouble(metrics[1].trim())));
                    
                    //cria uma lista com a quantidade de elementos correspondentes à regra
                    ListTermImpl l = new ListTermImpl();
                    String[] elements = linha.split(",");
                    for(String e: elements) 
                        if(e.trim().equals("*"))
                            l.add(createAtom("null"));
                        else
                            l.add(createAtom(e.trim().replaceAll("-", "_")));

      
                    l.add(support);
                    l.add(error);
                    listResult.add(l);
                }
                
                Pred predResult = new Pred("rules", 1);
                predResult.addTerm(listResult);

                // System.out.println("==== " + predResult);

                return predResult;
            
                
            } catch (IOException e) {
                System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            }
             catch (Exception e) {
                System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            }
            
            return null;
        }
    
}
