package rule2formula;

import java.util.Iterator;
import java.util.Set;

import jason.asSyntax.LogicalFormula;
import jason.asSyntax.Rule;
import jason.asSyntax.parser.ParseException;
import jason.asSyntax.parser.TokenMgrError;

import static jason.asSyntax.ASSyntax.parseRule;

public class DecisionRule {

	private Set<Attribute> antecedent;
	private Attribute consequent;
	private double support = 0;

	public Set<Attribute> getAntecedent() {
		return antecedent;
	}
	public void setAntecedent(Set<Attribute> antecedent) {
		this.antecedent = antecedent;
	}
	public Attribute getConsequent() {
		return consequent;
	}
	public void setConsequent(Attribute consequent) {
		this.consequent = consequent;
	}	
		

	public double getSupport() {
		return support;
	}
	public void setSupport(double support) {
		this.support = support;
	}
	
	public Rule toJasonLogicalFormula() {
		try {
			String formula = "";

			if(consequent.getValue() instanceof BooleanAttributeValue)
				formula = consequent.getId();
			else
				if(consequent.getValue() instanceof CategoricalAttributeValue)
					formula = consequent.getValue().getValue() +"(" + consequent.getId()+")";
				else
					formula = consequent.getId()+"(" + consequent.getValue().getValue() + ")";
			
			formula = formula + "[support(" + this.getSupport() +")]";

			formula = formula + ":-";


			String antecedentStr = "";
			Iterator<Attribute> it = antecedent.iterator();
			while(it.hasNext()) {

				antecedentStr = antecedentStr.concat(it.next().toLogicalFormula().toString());

				if(it.hasNext())
					antecedentStr = antecedentStr.concat(" & ");
			}
			
			
			//handling negation in the consequence (p->~q equiv. ~(~q)->~(p) equiv q->~p)
			if(consequent.getValue() instanceof BooleanAttributeValue && ((BooleanAttributeValue)consequent.getValue()).getValue()==false)
				formula = formula.concat("not(" + antecedentStr + ")");
			else
				formula = formula.concat(antecedentStr);
				
			
			formula = formula + ".";
			return parseRule(formula);


		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TokenMgrError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}

	public String toString() {
		String result = "";
		Iterator<Attribute> it = antecedent.iterator();
		while(it.hasNext()) {
			result = result.concat(it.next().toString());
			if(it.hasNext())
				result = result.concat(",");
		}
		result = result.concat(" : " + this.consequent.toString());
		return result;
	}


}
