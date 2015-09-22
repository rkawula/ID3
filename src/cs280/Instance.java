package cs280;

/*  
    The class to represent a training instance.
 */
class Instance {
	
	public String[] attributes;
	
	public Instance(int numAttributes) {
		attributes = new String[numAttributes];
	}
	
	public String getAttributeInColumn(int column) {
		return attributes[column];
	}
}