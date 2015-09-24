package part1;

/**
 * An instance is a specific event in a big dataset. This class is used by 
 * decision trees for reading in training events. It holds all of the values
 * for an entry.
 * @author Rachel Kawula rkawula@gmail.com
 *
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