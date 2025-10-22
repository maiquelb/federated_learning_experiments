package rule2formula;

import weka.classifiers.trees.J48; 
import weka.classifiers.rules.M5Rules; 
import weka.core.Instances; 
import weka.core.converters.ArffLoader; 
 
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer; 
 
public class DecisionTreeToRules { 
    public static void main(String[] args) { 
        try { 
            // Load dataset 
            ArffLoader loader = new ArffLoader(); 
            //loader.setSource(new File(args[0]));
            loader.setSource(new File("/mnt/1C4C766F4C764414/maiquel/git/federated_learning_experiments/datasets/weather.arff"));
            //loader.setSource(new File("/home/mauri/Downloads/wekaAndJDK/weka-3-8-6/data/breast-cancer.arff"));
            Instances data = loader.getDataSet(); 
            data.setClassIndex(data.numAttributes() - 1); // Set the last attribute as the class 
 
            // Build the decision tree classifier 
            J48 tree = new J48(); 
            //tree.setUnpruned(true);
            tree.buildClassifier(data);           
            
            // produce a list with attnames
            List<String> attNames = new LinkedList<String>();
            for (int i = 0; i < data.numAttributes(); i++) {
            	attNames.add(data.attribute(i).name());
            }
            
            System.out.println("Unpruned: " + tree.getUnpruned());
            System.out.println(attNames.toString());           
            System.out.println(tree.toString());  
            
            String treeV[] = tree.toString().replaceAll(" ", "").split("\\n");
            System.out.println(Arrays.toString(treeV));
            
            
            List<String[]> ruleList = new LinkedList<String[]>();
            
            processTree(treeV, 3, "", attNames, ruleList);
            //System.out.println(treeV[4]);
            
            //addRuleToList(", node-caps=yes, |deg-malig=1:recurrence-events(1.01/0.4)", ruleList, attNames);
            

            
            for (String rr[]: ruleList)
            	System.out.println(Arrays.toString(rr));
            
            
            // write a file rules.txt
            //FileWriter fw = new FileWriter(new File(args[0] + ".rules"));
            FileWriter fw = new FileWriter(new File("rules"));
            for (String rr[]: ruleList) {
            	fw.append(Arrays.toString(rr).replaceAll(",", " ").replaceAll("\\[", "").replaceAll("\\]", ""));
            	fw.append("\n");
            }
            
            fw.close();
            
            
        }catch(Exception e) {
        	e.printStackTrace();
        }
            

    }

	private static void processTree(String[] treeV, int i, String rule, List<String> attNames, List<String[]> ruleList) {
		if (i < treeV.length && rulePart(treeV[i], attNames)) {

			if (!treeV[i].startsWith("|")) {
				rule = "";
			}
			
			if (treeV[i].contains(":")) {				
				System.out.println(rule + ", " + treeV[i]);
				
				// adicionar a rule na lista
				addRuleToList(rule + ", " + treeV[i], ruleList, attNames);
				
				
			} else {			
				rule = rule + ", " + treeV[i];
			}			
			
			processTree(treeV, i + 1, rule, attNames, ruleList);			
		}
		
	}



	private static void addRuleToList(String rule, List<String[]> ruleList, List<String> attNames) {
		String[] ruleVector = new String[attNames.size()];
		Arrays.fill(ruleVector, "*");
		
		rule = rule.replaceAll("\\|","").replaceAll(":", ",").replace(" ", "");
		rule = rule.substring(0, rule.indexOf("("));
				
		for (String rp: rule.split(",")) {
			System.out.println(rp);
			if (rp.contains("=")) {
				String rpatt= rp.split("=")[0];
				String rpvalue = rp.split("=")[1];				
				ruleVector[attNames.indexOf(rpatt)] = rpvalue;				
			} else {
				ruleVector[attNames.size() - 1] = rp;
			}
		}
		
		ruleList.add(ruleVector);
		
		
	}

	private static boolean rulePart(String s, List<String> attNames) {
		if (s.startsWith("|")) {
			return true;
		}
		
		for (String att: attNames) {
			if (s.startsWith(att)) {
				return true;
			}
		}		
		
		return false;
	} 

} 

		
