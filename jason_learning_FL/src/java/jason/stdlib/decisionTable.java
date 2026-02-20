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
import node.RuleGeneratorAlgorithm;
import node.RuleGeneratorDecisionTable;


/***
 * Learn rules using j48.
 * Arguments:
 *    1. arff file name (with complete path)
 *    2. dataset metrics file name
 * Output:
 *    A predicate with the rules (bound in the 3rd argument)
*/
public class decisionTable extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {       
        RuleGeneratorAlgorithm ruleAlgo = new RuleGeneratorDecisionTable(args[0].toString().replaceAll("^\"|\"$", ""));
        
        ruleAlgo.generateRules();

        
        Pred predRules = Util.generateDataSetRules(ruleAlgo.getRulesWithMetrics());
        Pred predMetrics = Util.generateDataSetMetrics(ruleAlgo.getDatasetMetrics());

        Pred predResult = new Pred("dataset", 2);
        predResult.addTerm(predMetrics);
        predResult.addTerm(predRules );

        return un.unifies(predResult, args[1]);

    }




}
