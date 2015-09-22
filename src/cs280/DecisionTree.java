package cs280;

import java.io.*;
import java.util.*;

public class DecisionTree {
	int numAttributes; // The number of attributes.
	String[] attributeNames; // The names of all attributes.
	private final int classColumn = 4; // The index of the class attribute.
	private final String positiveClassValue = "yes";
	private final String negativeClassValue = "no";
	/* 
    The attributes variable contains the possible values for each attribute.
    For example, attributes.get(0) contains the values of the 0-th attribute.
	 */
	ArrayList<ArrayList<String>> attributes;
	Node root = new Node();

	/*
    Given a set of instances and an attribute, return the 
    values of that attribute for those instances.
	 */
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

	/*
    Given a set of instances, return the majority class.
	 */
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

	/*  
    Returns a subset of data, in which the value of the specified attribute 
    of all data points is the specified value.
	 */
	public ArrayList<Instance> getSubset(ArrayList<Instance> data, int column, String value) {
		ArrayList<Instance> subset = new ArrayList<Instance>();

		for (Instance instance : data) {
			if (instance.getAttributeInColumn(column).equals(value)) {
				subset.add(instance);
			}
		}
		
		return subset;
	}

	/*  
    Calculates the entropy of the set of instances.
    Right now this function runs, but it's just giving a random number.
    As you may suspect, this will not lead to a nice tree.
    You'll need to figure out how to calculate entropy from 
    the instances passed into the function.

    Some tips: 
      To access instance n : Instance foo = (Instance) data.get(n)
      To get the value for attribute x of instance n : n.attributes[x]
      The first attribute in our data set (attribute 0) is the class attribute,
        It's stored in a hard-coded variable called attributeClass.
      All the attribute values are stored in the attributes variable. To get the 
        3rd value of the 1st attribute: attributes.get(0).get(2).  
        The 0 is getting the first attribute, the 2 is getting the third value 
        of that attribute.
	 */

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
		
		if (positiveOccurrences < 1 || negativeOccurrences < 1) {
			return 1;
		}
		
		double positive = (double) positiveOccurrences / (double) totalOccurrences;
		double negative = (double) negativeOccurrences / (double) totalOccurrences;
		
		double entropy = -(positive * (Math.log(positive) / Math.log(2))
				+ negative * (Math.log(negative) / Math.log(2)));
		return entropy;

	}

	/*  
    This function splits the specified node according to the id3 
    algorithm.  It recursively divides all children nodes until it is 
    not possible to divide any further.
	 */
	/**
	 * 
	 * @param node The parent node to split if it is not meant to be a leaf.
	 * @param attributeList Remaining attributes that have not yet been split.
	 */
	public void splitNode(Node node, ArrayList<Integer> attributeList) {
		// Base case, no attributes left for splitting.
		if (attributeList.size() == 0) {
			return;
		}

		double bestEntropy = 1.0;
		boolean selected = false;
		int selectedAttribute = 0;

		node.setEntropy(calculateEntropy(node.getInstances()));

		// No need to split -- this node has perfect entropy.
		if (node.getEntropy() == 0.0) {
			System.out.println("Leaf with perfect entropy.");
			return;
		}

		// Need to make children.
		// Find the maximum decrease in entropy.
		// Loop over all the different attributes, skipping the class attribute.
		for (int i = 0; i < attributeList.size(); i++) {
			int currentColumn = attributeList.get(i);
			if ( classColumn == currentColumn ) {
				continue;
			}

			// How many values does this attribute have?
			int numValues = attributes.get(currentColumn).size();

			// Loop over all the values of this attribute.
			double runningEntropy = 0.0;
			for (int j = 0; j < numValues; j++) {

				// Use the getSubset function to find the instances in your 
				// data that have value j on attribute i.

				ArrayList<Instance> subsetForCurrentColumnAndValue =
						getSubset(node.getInstances(), currentColumn, attributeNames[j]);

				// Once you have the subset, make sure that there are actually
				// elements in that subset.  If not, skip to the next value.

				if (subsetForCurrentColumnAndValue.isEmpty()) {
					continue;
				}
				// Calculate the entropy of this subset.

				double currentEntropy = calculateEntropy(subsetForCurrentColumnAndValue);
				System.out.println("Current entropy is " + currentEntropy);
				// And add the weighted sum of the entropy to your runningEntropy. 
				// Basically multiply the entropy by the number of things in your subset.

				runningEntropy += currentEntropy * subsetForCurrentColumnAndValue.size();
			}

			// Compute the average.
			runningEntropy = runningEntropy / (double) node.getInstances().size(); // Weighted average.
			if (!selected) {
				selected = true;
				bestEntropy = runningEntropy;
				selectedAttribute = currentColumn;
				System.out.println("Selecting attribute " + selectedAttribute + ", with entropy of " + runningEntropy);
			} else if (runningEntropy < bestEntropy) {
				bestEntropy = runningEntropy;
				selectedAttribute = currentColumn;
				System.out.println("New best entropy for the first time: " + bestEntropy);
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
	public int readData(String filename) throws Exception {

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

		for (int i=0; i < numAttributes; i++) {
			attributeNames[i]  = tokenizer.nextToken();
		}

		while(true) {
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
				point.attributes[i] = value;
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

	/*  
    This function prints the decision tree in the form of rules.
    The action part of the rule is of the form 
      outputAttribute = "symbolicValue"
	 */

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

	/*  
      This function creates the decision tree and prints it in the 
      form of rules on the console.
	 */
	public void createDecisionTree() {
		ArrayList<Integer> splitAttributes = new ArrayList<Integer>();
		for(int i = 0; i < numAttributes; i++) {
			splitAttributes.add(i);
		}
		splitNode(root, splitAttributes);
		printTree(root, "");
	}

}