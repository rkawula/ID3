package cs280;

/*  
    The class to represent a training instance.
 */
public class Instance {
	
	private String[] attributes;
	
	public Instance(int numAttributes) {
		attributes = new String[numAttributes];
	}
	
	public String getAttributeInColumn(int column) {
		return attributes[column];
	}
	
	public void setAttribute(int index, String value) {
		attributes[index] = value;
	}
}