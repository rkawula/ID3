package part3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;

class Node {
	double entropy; 

	// Data temporarily loaded into the heap.
	ArrayList<String[]> localData = new ArrayList<String[]>();

	// If this isn't a leaf node, the attribute used to divide the node.
	// -1 means that this node is a leaf.
	int splitAttribute = -1;
	
	// Which node this is.
	int number;

	// The attribute value used to create this node.
	// This is the value of the parent's splitAttribute that led to this
	// node being created.
	String splitValue;

	Node[] children;
	Node parent;	
	// Holds useful statistics in the heap, so you don't need to go to disk.
	DataMapper dataMapper;
	//Total instances in this node.
	private int instances = 0;

	Node() {
		// Constructor for the root only!!
		number = 1;
	}

	Node(int numAttributes) {
		dataMapper = new DataMapper(this, numAttributes);
	}
	
	public void setNumber(int num) {
		number = num;
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

	public void addAndCompressData(String[] data) {
		localData.add(data);
		dataMapper.compress(data);
		if (localData.size() >= 500000) {
			String fileName = dataMapper.writeDataToDisk(localData);
			System.out.println("Paged 500,000 rows to " + fileName + ".");
			localData.clear();
			System.gc();
		}
		instances++;
	}
	
	/**
	 * Gets a list of filenames for the all of the data in this node.
	 * @return A list of the files for this node's data. May return an
	 * empty array if there is no data.
	 */
	public String[] getAllPages() {
		if (!localData.isEmpty()) {
			dataMapper.writeDataToDisk(localData);
			localData.clear();
		}
		return dataMapper.getPages();
	}
	
	public int getNumOfInstances() {
		return instances;
	}


}