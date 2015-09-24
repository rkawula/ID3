package part3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;

class Node {
	double entropy; 

	// Data must now be saved to disk in order to reduce space occupied in the heap.
	File dataOnDisk;
	// Data temporarily loaded into the heap.
	ArrayList<String[]> localData = new ArrayList<String[]>();

	// If this isn't a leaf node, the attribute used to divide the node.
	// -1 means that this node is a leaf.
	int splitAttribute = -1;

	// The attribute value used to create this node.
	// This is the value of the parent's splitAttribute that led to this
	// node being created.
	String splitValue;

	Node[] children;
	Node parent;	
	// Holds useful values in the heap, so you don't need to go to disk.
	DataMapper dataMapper;
	
	Node() {
		// Constructor for the root. Do nothing yet.
	}

	Node(int numAttributes) {
		dataMapper = new DataMapper(this, numAttributes);
	}

	void writeDataToDisk(ArrayList<Instance> data, int numNodes) {
		// Take chunks of data at a time.
		// Append it to a growing file.
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("node_data/Node #" + numNodes + ".txt", "UTF-8");
			writer.append(Instance.formatDataAsRows(data));
		} catch (UnsupportedEncodingException e) {
			System.err.println("Bad news when writing to file: " + e);
		} catch (FileNotFoundException e) {
			System.err.println("For some reason this file wasn't found. . ?" + e);
		} finally {
			writer.close();
		}
	}

	public String getMajorityClass(int column, String pos, String neg) {
		return dataMapper.getMajorityClass(column, pos, neg);
	}
	
	public String[] getValuesForColumn(int column) {
		Set<String> values = dataMapper.getValuesFor(column);
		if (values.isEmpty()) {
			return new String[] { "" };
		}
		return values.toArray(new String[values.size()]);
	}
	
	public void addAndCompressData(ArrayList<String[]> data) {
		localData = data;
		for (String[] instance : data) {
			dataMapper.compress(instance);
		}
	}
		
}