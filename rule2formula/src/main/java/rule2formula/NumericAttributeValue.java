package rule2formula;

import coordinator.Model;	

public class NumericAttributeValue extends AttributeValue<Float> {
	
	private double value;
	private Operator operator;
	
	public enum Operator {
        EQUAL, LESS_THAN_OR_EQUAL, LESS_THAN, GREATER_THAN_OR_EQUAL, GREATER_THAN 
    }
	
	public NumericAttributeValue(Operator operator, double value) {
		super();
		this.operator = operator;
		this.value = value;
	}

	@Override
	public void setValue(Float value) {
		this.value = value;
		
	}

	@Override
	public Float getValue() {
		return (float) this.value;
	}

	
	public Operator getOperator() {
		return this.operator;
	}
	
	
	public String getOperatorAsSymbol() {
		if(this.getOperator() == Operator.EQUAL) return "==";
		if(this.getOperator() == Operator.LESS_THAN_OR_EQUAL) return "<=";
		if(this.getOperator() == Operator.LESS_THAN) return "<";
		if(this.getOperator() == Operator.GREATER_THAN_OR_EQUAL) return ">=";
		if(this.getOperator() == Operator.GREATER_THAN) return ">";
		return null;
		
	}
}
