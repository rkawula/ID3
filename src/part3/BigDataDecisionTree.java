package part3;

import java.io.*;
import java.util.*;

/**
 * An ID3 tree designed to handle large amounts of data.
 * NOT THREADSAFE!
 * 
 * @author Rachel Kawula rkawula@gmail.com
 *
 */
public class BigDataDecisionTree {
	int numAttributes;
	private int numNodes = 1;
	private final int classColumn;
	private final String positiveClassValue;
	private final String negativeClassValue;
	// attributes used to map column to possible values within that column.
	Node root = new Node(); 

	/**
	 * Constructs a new Big Data ID3 tree.
	 * @param classColumn The numbers of columns (attributes) for each instance.
	 * @param positiveClassValue The positive value for the class -- this is what we train to match.
	 * @param negativeClassValue The negative value for the class.
	 */
	public BigDataDecisionTree(int classColumn, String positiveClassValue, String negativeClassValue) {
		this.classColumn = classColumn;
		this.positiveClassValue = positiveClassValue;
		this.negativeClassValue = negativeClassValue;
	}

	/**
	 * Returns all of the possible values within a specific column of data, for a specific set
	 * of instances.
	 * @param data The list of instances to examine.
	 * @param column The column we are selecting values from.
	 * @return Every value found within that column, without duplicates.
	 */
	public ArrayList<String> getAllValuesInColumn(ArrayList<String[]> data, int column) {
		ArrayList<String> values = new ArrayList<String>();
		for (String[] instance : data) {
			String value = instance[column];
			int index = values.indexOf(value);
			if (index < 0) {
				values.add(value);
			}
		}
		return values;
	}

	/**
	 * Given a node, finds the most common classification for the instances in that node.
	 * @param data The set of instances to examine.
	 * @return The most common class for those instances. In the case of a tie, returns "Tie!"
	 */
	public String majorityClass(Node node) {
		String result = node.getMajorityClass(classColumn, positiveClassValue, negativeClassValue);
		return result;
	}

	/**
	 * Finds all of the instances within a set of instances that have a specific value in a specific column.
	 * It will return an empty set if no items have that value in that column.
	 * 
	 * @param data The set of instances to reduce.
	 * @param column The column number to search within.
	 * @param value The specific value that all instances must match.
	 * @return A list of all instances that contained that value within that column.
	 */
	public ArrayList<String[]> getCompleteSubset(Node node, int column, String value) {
		
		ArrayList<String[]> subset = new ArrayList<String[]>();
		// read from current node's disk.
		
		for (int i = 0; i < data.size(); i++) {
			String instanceValue = data.get(i)[column];
			if (instanceValue.equals(value)) {
				subset.add(data.get(i));
			}
		}

		return subset;
	}
	
	/**
	 * Calculates the entropy for a set of instances.
	 * @param data The instances to be examined for entropy.
	 * @return The amount of entropy in that set.
	 */
	public double calculateEntropy(Node node) {
		
		// If there are no pages, then there is no data
		if (node.getAllPages() == null) {
			return 0;
		}

		// Uses main memory, not disk.
		int positiveOccurrences = node.dataMapper.getWordFrequency(classColumn, positiveClassValue);
		int negativeOccurrences = node.dataMapper.getWordFrequency(classColumn, negativeClassValue);

		// Pure set.
		if (positiveOccurrences < 1 || negativeOccurrences < 1) {
			return 0;
		}

		double positive = (double) positiveOccurrences / node.getNumOfInstances();
		double negative = (double) negativeOccurrences / node.getNumOfInstances();

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
		// There will always be at least one attribute left in the list
		// (the class attribute that we're training for).
		if (attributeList.size() == 1) {
			return;
		}
		
		double bestEntropy = 0.0;
		boolean selected = false;
		int selectedAttribute = -1;
		
		node.number = ++numNodes;
		node.entropy = calculateEntropy(node);
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
			
			// Total the entropy for that category of children, to determine best gain afterward.
			// Don't need to write it anywhere.
			// Read from current node's data files.
			
			String[] pages = node.getAllPages();
			// Make sure there really is data to examine.
			if (pages == null) {
				System.out.println("There were no pages to read. If you're reading"
						+ " this, something went wrong. . .");
				continue;
			}
			
			double runningEntropy = 0.0;
			
			// Should I make a cache of the mappings (positives/negatives) for this column?
			// Not writing to disk yet.
			
			// Each child produced if split on this value.
			for (String currentValue : node.getValuesForColumn(currentColumn)) {
				int posInstances = 0;
				for (int j = 0; j < pages.length; i++) {
					// Make a file reader, and a string tokenizer.
					// Open the current page of the parent with the file reader.
					FileInputStream in;
					// error handle for file we can't read/write/doesn't exist
					File inputFile = new File(pages[j]);
					in = new FileInputStream(inputFile);

					BufferedReader bin = new BufferedReader(new InputStreamReader(in));
					String input = bin.readLine();
					if (input == null) {
						System.err.println("Empty node file!");
					}
					StringTokenizer tokenizer;
					while (true) {
						input = bin.readLine();
						// EOF
						if (input == null) {
							break;
						}
						// If the current column didn't match the value we want, continue.

						tokenizer = new StringTokenizer(input);

						int tokenCount = tokenizer.countTokens();
						for (int i = 0; i < tokenCount; i++) {
							if (tokenizer.nextToken();
						}
					// Each line that matches value in column gets checked
					// for positive/negatve.
					//for (line in pages[j]) {
						// if matches value in column && matches pos class
						// 	posInstances++; 
					//}											
				}
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

			runningEntropy = runningEntropy / (double) node.localData.size();
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
		String[] valuesInColumn = node.getValuesForColumn(selectedAttribute);
		if (valuesInColumn.length < 1 || "".equals(valuesInColumn[0])) {
			return;
		}
		int numValues = valuesInColumn.length;
		node.splitAttribute = selectedAttribute;
		node.children = new Node[numValues];
		for (int j = 0; j < numValues; j++) {
			node.children[j] = new Node(numAttributes);
			node.children[j].parent = node;
			String thisValue = valuesInColumn[j];
			// add subset to datamapper & compress
			for (String[] instance : getCompleteSubset(node.localData, selectedAttribute, thisValue)) {
				node.children[j].addAndCompressData(instance);				
			}
			node.children[j].splitValue = thisValue;
		}

		// Recursively divide children nodes.
		// First, remove the attribute from the attribute list.
		attributeList.remove(new Integer(selectedAttribute));
		for (int j = 0; j < numValues; j++) {
			splitNode(node.children[j], attributeList);
		}
	}

	/**
	 * Initial transformation from the file data to Instance objects. Since we must
	 * be able to handle large amounts of data, we can't hold all Instances in the heap
	 * at the same time. Instead, map important statistics about this data within our
	 * root's mapper.
	 * @param filename The file containing our training data.
	 * @return A status code of 0 means failure; 1 means success.
	 * @throws IOException
	 */
	public int readData(String fileName) throws IOException {

		FileInputStream in = null;
		try {
			File inputFile = new File(fileName);
			in = new FileInputStream(inputFile);
		} catch ( Exception e) {
			System.err.println( "Unable to open data file: " + 
					fileName + "\n" + e);
			return 0;
		}

		BufferedReader bin = new BufferedReader(new InputStreamReader(in));

		String input;

		// Read the first line;
		input = bin.readLine();
		if (input == null) {
			System.err.println( "No data found in the data file: " + 
					fileName + "\n");
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

		// Intialize the mapper with the right number of columns.
		root.dataMapper = new DataMapper(root, numAttributes);

		// Provide the text for our column titles, for a
		// clean output, and add it to our dataMapper.
		String[] attNames = new String[numAttributes];
		for (int i = 0; i < numAttributes; i++) {
			attNames[i]  = tokenizer.nextToken();
		}
		
		DataMapper.setColumnNames(attNames);

		while (true) {
			input = bin.readLine();
			if (input == null) break;
			tokenizer = new StringTokenizer(input);
			int numtokens = tokenizer.countTokens();
			if (numtokens != numAttributes) {
				System.err.println( "Read " + root.localData.size() + " data");
				System.err.println( "Last line read: " + input);
				System.err.println( "Expecting " + numAttributes  + " attributes");
				bin.close();
				return 0;
			}

			String[] row = new String[numAttributes];
			for (int i = 0; i < numAttributes; i++) {
				row[i] = tokenizer.nextToken(); 
			}
			//TODO: make the root add&compress too.
			// Send to our root's mapper.
			//root.dataMapper.compress(row);
			// Also store it locally.
			//root.localData.add(row);
			//root.addAndCompressData(row);
		}
		bin.close();
		return 1;
	}

	public void createDecisionTree() {
		ArrayList<Integer> splitAttributes = new ArrayList<Integer>();
		for(int i = 0; i < numAttributes; i++) {
			splitAttributes.add(i);
		}
		splitNode(root, splitAttributes);
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
			String result = currentNode.getMajorityClass(
					classColumn, positiveClassValue, negativeClassValue);
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

	public int getClassColumn() {
		return classColumn;
	}

}