package rule2formula;

public class BooleanAttributeValue extends AttributeValue<Boolean> {
	
	private Boolean value;
		
	public BooleanAttributeValue(Boolean value) {
		super();
		this.value = value;
	}

	@Override
	public void setValue(Boolean value) {
		this.value = value;		
	}

	@Override
	public Boolean getValue() {
		return this.value;
	}

}
