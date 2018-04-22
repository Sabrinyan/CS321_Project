
public class EventLines {
	private String XPATH, action, input;
	
	public EventLines(String event) {
		String[] parts = event.split("###");
		this.XPATH = (parts[0].split("TH="))[1];
		this.action = (parts[1].split(":"))[1];
		this.input = "";
	}
	
	public EventLines(String xpath, String action, String input) {
		this.XPATH = xpath;
		this.action = action;
		this.input = input;
	}
	public String getXPath() {
		return this.XPATH;
	}
	
	public String getAction() {
		return this.action;
	}
	
	public String getInput() {
		return this.input;
	}
	@Override public String toString() {
		return String.format("XPath = %s\t\tAction = %s\t\tInput = %s", this.XPATH, this.action, this.input);
	}
}
