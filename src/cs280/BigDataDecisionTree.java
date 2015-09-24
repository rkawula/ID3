package cs280;

import java.io.*;
import java.util.*;

public class BigDataDecisionTree {
	int numAttributes;
	String[] attributeNames;
	private final int classColumn;
	private final String positiveClassValue;
	private final String negativeClassValue;
	
	ArrayList<ArrayList<String>> attributes;

	Node root = new Node();

	public BigDataDecisionTree(int classColumn, String positiveClassValue, String negativeClassValue) {
		this.classColumn = classColumn;
		this.positiveClassValue = positiveClassValue;
		this.negativeClassValue = negativeClassValue;
	}

	public ArrayList<String> getAllValuesInColumn(ArrayList<Instance> data, int column) {
		ArrayList<String> values = new ArrayList<String>();
		for (Instance instance : data) {
			String value = instance.getAttributeInColumn(column);
			int index = values.indexOf(value);
			if (index < 0) {
				values.add(value);
			}
		}
		return values;
	}


	public String majorityClass(ArrayList<Instance> data) {
		HashMap<String, Integer> tracker = new HashMap<String, Integer>();
		String majorityClass = "";

		for (Instance instance : data) {
			String value = instance.getAttributeInColumn(classColumn);
			if (tracker.containsKey(value)) {
				tracker.put(value, tracker.get(value) + 1);
			} else {
				tracker.put(value, 0);
			}
		}

		int max = 0;
		for (String key : tracker.keySet()) {
			int occurrencesOfThisClass = tracker.get(key);
			if (occurrencesOfThisClass > max) {
				majorityClass = key;
				max = occurrencesOfThisClass;
			}
		}
		return majorityClass;
	}


	public ArrayList<Instance> getSubset(ArrayList<Instance> data, int column, String value) {
		ArrayList<Instance> subset = new ArrayList<Instance>();

		for (Instance instance : data) {
			if (instance.getAttributeInColumn(column).equals(value)) {
				subset.add(instance);
			}
		}

		return subset;
	}

	public double calculateEntropy(ArrayList<Instance> data) {
		int totalOccurrences = data.size();

		// Don't calculate entropy if there is no data.
		if (totalOccurrences == 0) {
			return 0;
		}

		// Subset of all positives or negatives from the above parameter subset;
		// the size is our positive or negative occurrences.
		int positiveOccurrences = getSubset(data, classColumn, positiveClassValue).size();
		int negativeOccurrences = getSubset(data, classColumn, negativeClassValue).size();

		// Pure set.
		if (positiveOccurrences < 1 || negativeOccurrences < 1) {
			return 0;
		}

		double positive = (double) positiveOccurrences / totalOccurrences;
		double negative = (double) negativeOccurrences / totalOccurrences;

		double entropy = -(positive * (Math.log(positive) / Math.log(2))
				+ negative * (Math.log(negative) / Math.log(2)));

		return entropy;

	}

	/**
	 * Recursive method to split nodes into children for the greatest entropy gain.
	 * @param node The parent node to split if it is not meant to be a leaf.
	 * @param attributeList Remaining attribute columns that have not yet been split.
	 */
	public void splitNode(Node node, ArrayList<Integer> attributeList) {
		// Base case, no attributes left to split.
		// There will always be at least one attribute left in the list
		// (the class attribute that we're training for).
		if (attributeList.size() == 1) {
			return;
		}

		double bestEntropy = 0.0;
		boolean selected = false;
		int selectedAttribute = -1;

		node.entropy = calculateEntropy(node.data);

		// No need to split -- this node has perfect entropy.
		if (node.entropy == 0.0) {
			return;
		}

		// Need to make children.
		// Find the maximum decrease in entropy.
		// Loop over all the different attributes, skipping the class attribute.
		for (int i = 0; i < attributeList.size(); i++) {
			int currentColumn = attributeList.get(i);
			if (classColumn == currentColumn) {
				continue;
			}

			// Loop over all the values of this attribute (all the children that would
			// be created if this attribute is chosen).
			double runningEntropy = 0.0;
			for (String currentValue : attributes.get(currentColumn)) {

				ArrayList<Instance> subsetForCurrentColumnAndValue =
						getSubset(node.data, currentColumn, currentValue);

				if (subsetForCurrentColumnAndValue.isEmpty()) {
					continue;
				}

				double currentEntropy = calculateEntropy(subsetForCurrentColumnAndValue);

				runningEntropy += subsetForCurrentColumnAndValue.size() * currentEntropy;
			}

			// Gain(S, CurrentColumn) = node entropy - (childOneTotal/total)*childOneEntropy - (childTwoTotal/total)*childTwoEntropy) . . 
			// Do Gain(S, EachRemainingColumn) until we find the largest result (means greatest drop in entropy)
			// We did runningEntropy = childOneTotal*childOneEntropy + childTwoTotal*childTwoEntropy. . .etc
			// Now we divide it all by size of the subset.

			runningEntropy = runningEntropy / (double) node.data.size();
			if (!selected) {
				selected = true;
				bestEntropy = runningEntropy;
				selectedAttribute = currentColumn;
			} else if (runningEntropy < bestEntropy) {
				bestEntropy = runningEntropy;
				selectedAttribute = currentColumn;
			}
		}

		// No attributes worth splitting on.
		if (!selected) {
			return;
		}

		// Now divide the dataset using the selected attribute.
		int numValues = attributes.get(selectedAttribute).size();
		node.splitAttribute = selectedAttribute;
		node.children = new Node[numValues];
		for (int j = 0; j < numValues; j++) {
			node.children[j] = new Node();
			node.children[j].parent = node;
			String thisValue = attributes.get(selectedAttribute).get(j);
			node.children[j].data = getSubset(node.data, selectedAttribute, thisValue);
			node.children[j].splitValue = thisValue;
		}

		// Recursively divide children nodes.
		// First, remove the attribute from the attribute list.
		attributeList.remove(new Integer(selectedAttribute));
		for (int j = 0; j < numValues; j++) {
			splitNode(node.children[j], attributeList);
		}
	}

	/* 
    Function to read the data file.
    The first line of the data file should contain the names of 
    all attributes.  The number of attributes is inferred from the 
    number of words in this line.  The last word is taken as the name of 
    the output attribute.  Each subsequent line contains the values of 
    attributes for a data point.
	 */
	public int readData(String filename) throws IOException{

		FileInputStream in = null;
		try {
			File inputFile = new File(filename);
			in = new FileInputStream(inputFile);
		} catch ( Exception e) {
			System.err.println( "Unable to open data file: " + 
					filename + "\n" + e);
			return 0;
		}

		BufferedReader bin = new BufferedReader(new InputStreamReader(in));

		String input;

		// Read the first line;
		input = bin.readLine();
		if (input == null) {
			System.err.println( "No data found in the data file: " + 
					filename + "\n");
			bin.close();
			return 0;
		}

		StringTokenizer tokenizer = new StringTokenizer(input);
		numAttributes = tokenizer.countTokens();
		if (numAttributes <= 1) {
			System.err.println("Read line: " + input);
			System.err.println("Could not obtain the names of attributes.");
			System.err.println("Expecting at least one input attribute and " +
					"one output attribute");
			bin.close();
			return 0;
		}

		attributes = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < numAttributes; i++) {
			attributes.add(new ArrayList<String>());
		}
		attributeNames = new String[numAttributes];

		for (int i = 0; i < numAttributes; i++) {
			attributeNames[i]  = tokenizer.nextToken();
		}

		while (true) {
			input = bin.readLine();
			if (input == null) break;

			tokenizer = new StringTokenizer(input);
			int numtokens = tokenizer.countTokens();
			if (numtokens != numAttributes) {
				System.err.println( "Read " + root.data.size() + " data");
				System.err.println( "Last line read: " + input);
				System.err.println( "Expecting " + numAttributes  + " attributes");
				bin.close();
				return 0;
			}

			Instance point = new Instance(numAttributes);
			String value;
			for (int i = 0; i < numAttributes; i++) {
				value = tokenizer.nextToken(); 
				point.setAttribute(i, value);
				int index = attributes.get(i).indexOf(value);
				if (index < 0) {
					attributes.get(i).add(value);
				}
			}
			root.data.add(point);

		}
		bin.close();
		return 1;

	}

	public void printTree(Node node, String tab) {
		int outputAttribute = classColumn;

		// If we're at a leaf print out the class.
		if (node.children == null) {
			ArrayList<String> values = getAllValuesInColumn(node.data, outputAttribute);

			// If we know the class then print it, otherwise, print the majority
			// of the parent.
			if (values.size() == 1) {
				System.out.println(tab + "  " + attributeNames[outputAttribute] + 
						" = \"" + values.get(0) + "\";");
			} else {
				System.out.print(tab + "  " + attributeNames[outputAttribute] + " = {");
				System.out.print(majorityClass(node.parent.data));
				System.out.println( " };");
			}
			return;
		}

		// If we're not at a leaf, call printTree on each child.
		int numValues = node.children.length;

		for (int i=0; i < numValues; i++) {
			System.out.println(tab + "if ( " + 
					attributeNames[node.splitAttribute] + " == \"" +
					attributes.get(node.splitAttribute).get(i) + "\") {" );
			printTree(node.children[i], tab + "  ");
			if (i != numValues - 1 ) {
				System.out.print(tab +  "} else ");
			} else {
				System.out.println(tab +  "}");
			}
		}
	}

	public void createDecisionTree() {
		ArrayList<Integer> splitAttributes = new ArrayList<Integer>();
		for(int i = 0; i < numAttributes; i++) {
			splitAttributes.add(i);
		}
		splitNode(root, splitAttributes);
		printTree(root, "");
	}
	
	public void classifyTestData(String testData) throws IOException {

		FileInputStream in;
		File inputFile = new File(testData);
		in = new FileInputStream(inputFile);

		BufferedReader bin = new BufferedReader(new InputStreamReader(in));
		String input = bin.readLine();
		if (input == null) {
			System.err.println("Empty test file!");
		}
		StringTokenizer tokenizer = new StringTokenizer(input);
		int numAttributes = tokenizer.countTokens();

		System.out.println("Input: " + input);
		System.out.println("Tokenized: has " + numAttributes + " columns.");
		int instanceCount = 0;

		int correctPredictions = 0;

		while (true) {
			input = bin.readLine();
			if (input == null) {
				break;
			}
			
			instanceCount++;
			String[] testInstanceAttributes = new String[numAttributes];

			tokenizer = new StringTokenizer(input);

			int i = 0;
			while (tokenizer.hasMoreTokens()) {
				testInstanceAttributes[i] = tokenizer.nextToken();
				i++;
			}
			
			// Recursive classifier.
			correctPredictions += predict(testInstanceAttributes, root);

		}
		System.out.println("" + correctPredictions + " instances predicted correctly, and " +
				(instanceCount - correctPredictions) + " incorrectly classified, out of "
						+ instanceCount + " test instances.");
		System.out.println("Accuracy: " + correctPredictions + "/" + instanceCount + " == " + (double) correctPredictions / instanceCount);
		in.close();
	}
	
	private int predict(String[] testInstanceAttributes, Node currentNode) {
		
		int attributeForSplitting = currentNode.splitAttribute;
		Node nextChild = new Node();
		if (attributeForSplitting == -1) {
			
			// Return the classification count for this node,
			// cause this is as good as it gets.
			String result = majorityClass(currentNode.data);
			return result.equals(testInstanceAttributes[classColumn]) ? 1 : 0;
			
		} else {
			// Figure out what attribute this node's children are split on.
			// Recurse in the child node for this instance's value of that
			// attribute.
			String testInstanceSplitValue = testInstanceAttributes[attributeForSplitting];
			boolean valueFound = false;
			for (int i = 0; i < currentNode.children.length; i++) {
				if (currentNode.children[i].splitValue.equals(testInstanceSplitValue)) {
					valueFound = true;
					nextChild = currentNode.children[i];
					break;
				}
			}
			if (!valueFound) {
				System.out.println("There was a problem: the value split on during testing"
						+ " does not exist as a child node!");
			}
		}
		return predict(testInstanceAttributes, nextChild);
	}

	class Node {
		private double entropy; 

		private ArrayList<Instance> data;

		// If this isn't a leaf node, the attribute used to divide the node.
		// -1 means that this node is a leaf.
		private int splitAttribute;

		// The attribute value used to create this node.
		// This is the value of the parent's splitAttribute that led to this
		// node being created.
		private String splitValue;

		private Node[] children;
		private Node parent;

		Node() {
			data = new ArrayList<Instance>();
			splitAttribute = -1;
		}

	}


}