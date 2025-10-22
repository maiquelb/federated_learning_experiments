package rule2formula;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jason.asSyntax.parser.ParseException;
import jason.asSyntax.parser.TokenMgrError;

import static jason.asSyntax.ASSyntax.parseFormula;

import rule2formula.NumericAttributeValue.Operator;

public class AttributeTest {

	@Test
	public void testToLogicalFormula() {

		
		assertEquals(1, 1);
		
		try {
			Attribute booleanAttribute = new Attribute("windy", new BooleanAttributeValue(true));
			assertEquals(booleanAttribute.toLogicalFormula().toString(), "windy");
			System.out.println(booleanAttribute.toLogicalFormula().toString());
			booleanAttribute.getValue().setValue(false);
			assertEquals(booleanAttribute.toLogicalFormula().toString(), "not (windy)");
			
			
			Attribute categoricalAttribute = new Attribute("outlook", "sunny");
			assertEquals(categoricalAttribute.toLogicalFormula().toString(), "sunny(outlook)");
			
			Attribute numericAttribute = new Attribute("age", new NumericAttributeValue(Operator.LESS_THAN_OR_EQUAL, 4.3));
			assertEquals(numericAttribute.toLogicalFormula(), parseFormula("(age(Var__age) & (Var__age <= 4.3))"));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TokenMgrError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
