package rule2formula;

public class CategoricalAttributeValue extends AttributeValue<String> {
	
	private String value;
		

	public CategoricalAttributeValue(String value) {
		super();
		this.setValue(value);
	}

	@Override
	public void setValue(String value) {
		this.value = value.replaceAll("-", "");
		
	}

	@Override
	public String getValue() {
		return this.value;
	}

}
