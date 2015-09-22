package cs280;

import java.util.ArrayList;


/* The class to represent a node in the tree. */
class Node {
	// The entropy of the instances.
	private double entropy; 

	// The set of instances of this node.
	public ArrayList<Instance> data;

	// If this isn't a leaf node, the attribute used to divide the node.
	public int splitAttribute;

	// The attribute value used to create this node.
	// This is the value of the parent's splitAttribute that led to this
	// node being created.
	public String splitValue;

	// References to child nodes.
	public Node[] children;

	// The parent of this node (root has parent of null).
	public Node parent;

	// The constructor.
	public Node() {
		data = new ArrayList<Instance>();
		entropy = 1.0;
	}
	
	public double getEntropy() {
		return entropy;
	}
	
	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}
	
	public ArrayList<Instance> getInstances() {
		return data;
	}
}