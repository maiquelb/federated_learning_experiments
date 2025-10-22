package rule2formula;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

import rule2formula.NumericAttributeValue.Operator;


public class DataSetTest {

	@Test
	public void test() {
		
		Attribute attWindy = new Attribute("windy", false);
		Attribute attBreastQuad = new Attribute("breast_quad", "left_lower");
		Attribute attAge = new Attribute("age", Operator.GREATER_THAN_OR_EQUAL  ,44.5);
		
		
		HashSet<Attribute> antecedent = new HashSet<>();
		antecedent.add(attWindy);
		antecedent.add(attBreastQuad);
		antecedent.add(attAge);
		
		
		Attribute consequent = new Attribute("tumor_size", 1.24);
		
		DecisionRule rule = new DecisionRule();
		rule.setAntecedent(antecedent);
		rule.setConsequent(consequent);
		rule.setSupport(1.99);
		
		
		System.out.println(rule.toJasonLogicalFormula().toString());
		
		
		
		//sassertEquals(rule.toJasonLogicalFormula().toString(), "tumor_size(1.24)[support(1.99)] :- (not (windy) & (left_lower(breast_quad) & (age(Var__age) & (Var__age >= 44.5))))");
		
		
	}
	
	@Test
	public void test_getFromWekaDecisionTree() {
		
		DataSet dataset = new DataSet();
//		String tree = "J48 pruned tree\n"
//				+ "------------------\n"
//				+ "\n"
//				+ "outlook = sunny\n"
//				+ "|   humidity = high: no (3.0)\n"
//				+ "|   humidity = normal: yes (2.0)\n"
//				+ "outlook = overcast: yes (4.0)\n"
//				+ "outlook = rainy\n"
//				+ "|   windy = TRUE: no (2.0)\n"
//				+ "|   windy = FALSE: yes (3.0)\n"
//				+ "\n"
//				+ "Number of Leaves  : 	5\n"
//				+ "\n"
//				+ "Size of the tree : 	8";
		String tree = "outlook = sunny\n"
				    + "|   humidity = high: no (3.0)\n"
				    + "|   humidity = normal: yes (2.0)\n"
				    + "outlook = overcast: yes (4.0)\n"
				    + "outlook = rainy\n"
				    + "|   windy = TRUE: no (2.0)\n"
				    + "|   windy = FALSE: yes (3.0)\n"
				;
		dataset.getFromWekaDecisionTree(tree, "play");
		
		
//		Attribute outlook_sunny = new Attribute("outlook", "sunny");
//		Attribute outlook_overcast = new Attribute("outlook", "overcast");
//		Attribute outlook_rainy = new Attribute("outlook", "rainy");
//		Attribute humidity_high = new Attribute("humidity", "high");
//		Attribute humidity_normal = new Attribute("humidity", "normal");
//		Attribute windy_true = new Attribute("windy", true);
//		Attribute windy_false = new Attribute("windy", false);
//		
//		DecisionRule rule_sunny_humidity_high = new DecisionRule();
//		rule_sunny_humidity_high 
		
		assertEquals(dataset.getRules().size(),5);
		
//		assertEquals(dataset.toString(), "\nwindy=true,outlook=rainy : play=false\n"
//				+ "outlook=rainy,windy=false : play=true\n"
//				+ "outlook=sunny,humidity=high : play=false\n"
//				+ "outlook=sunny,humidity=normal : play=true\n"
//				+ "outlook=overcast : play=true"
//				);
		
		
	}

}
