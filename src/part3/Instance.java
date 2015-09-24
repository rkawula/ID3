package part3;

import java.util.ArrayList;


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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < attributes.length; i++) {
			builder.append(attributes[i] + " ");
		}
		return builder.toString();
	}
	
	public static String formatDataAsRows(ArrayList<Instance> data) {
		if (data.isEmpty()) return "";
		StringBuilder sb = new StringBuilder();
		for (Instance instance : data) {
			sb.append(instance.toString() + "\n");
		}
		return sb.toString();
	}
}