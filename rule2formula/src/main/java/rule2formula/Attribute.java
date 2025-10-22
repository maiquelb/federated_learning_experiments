package rule2formula;


import jason.asSyntax.Atom;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.parser.ParseException;
import jason.asSyntax.parser.TokenMgrError;
import rule2formula.NumericAttributeValue.Operator;

import static jason.asSyntax.ASSyntax.parseFormula;

public class Attribute {

	String id;
	AttributeValue value;

	public Attribute(String id, AttributeValue value) {
		super();
		this.id = id;
		this.value = value;
	}

	public Attribute(String id, boolean value) {
		super();
		this.id = id;
		this.value = new BooleanAttributeValue(value);
	}

	public Attribute(String id, String value) {
		super();
		this.id = id;
		this.value = new CategoricalAttributeValue(value);
	}

	public Attribute(String id, Operator operator, Float value) {
		super();
		this.id = id;
		this.value = new NumericAttributeValue(operator, value);		
	}

	public Attribute(String id, Operator operator, double value) {
		super();
		this.id = id;
		this.value = new NumericAttributeValue(operator, value);
		System.out.println("[Attribute] creating numeric attribute " + id + " - " + value);
	}
	
	public Attribute(String id,  Float value) {
		super();
		this.id = id;
		this.value = new NumericAttributeValue(Operator.EQUAL, value);
	}

	public Attribute(String id,  double value) {
		super();
		this.id = id;
		this.value = new NumericAttributeValue(Operator.EQUAL, value);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AttributeValue getValue() {
		return value;
	}

	public void setValue(AttributeValue value) {
		this.value = value;
	}

	public LogicalFormula toLogicalFormula() throws ParseException, TokenMgrError {
		if(this.getValue() instanceof BooleanAttributeValue)
			if(((BooleanAttributeValue)this.getValue()).getValue())
				return parseFormula(this.getId());
			else
				return parseFormula("not("+ this.getId() +")");

		if(this.getValue() instanceof CategoricalAttributeValue) {
			return parseFormula(this.getValue().getValue()+"(" + this.getId() + ")");
		}

		if(this.getValue() instanceof NumericAttributeValue)
			return parseFormula(this.getId() + "(Var__" + this.getId()+")& Var__" + 
					this.getId() + ((NumericAttributeValue)this.getValue()).getOperatorAsSymbol() + 
					this.getValue().getValue() );

		return null;

	}

	@Override
	public String toString() {
		if(this.getValue() instanceof NumericAttributeValue)
			return id + ((NumericAttributeValue)this.getValue()).getOperatorAsSymbol() + value.getValue();
		else
			return  id + "=" + value.getValue() ;
	}



}
