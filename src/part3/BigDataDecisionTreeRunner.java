package part3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class BigDataDecisionTreeRunner {


	private static void printTree(BigDataDecisionTree tree, Node node, String tab) {
		int outputAttribute = tree.getClassColumn();

		// If we're at a leaf print out the class.
		if (node.children == null) {
			ArrayList<String> values = tree.getAllValuesInColumn(node.localData, outputAttribute);

			// If we know the class then print it, otherwise, print the majority
			// of the parent.
			if (values.size() == 1) {
				System.out.println(tab + "  " + DataMapper.getColumnTitle(outputAttribute) + 
						" = \"" + values.get(0) + "\";");
			} else {
				System.out.print(tab + "  " +DataMapper.getColumnTitle(outputAttribute) + " = {");
				System.out.print(tree.majorityClass(node.parent));
				System.out.println( " };");
			}
			return;
		}
		// If we're not at a leaf, call printTree on each child.
		int numValues = node.children.length;
		int i = 0;
		for (String attributeValue : node.dataMapper.getValuesFor(node.splitAttribute)) {
			System.out.println(tab + "if ( " + 
					DataMapper.getColumnTitle(node.splitAttribute) + " == \"" +
					attributeValue + "\") {" );
			printTree(tree, node.children[i], tab + "  ");
			if (i != numValues - 1 ) {
				System.out.print(tab +  "} else ");
			} else {
				System.out.println(tab +  "}");
			}
			i++;
		}
	}

	private static void help() {
		System.out.println("Welcome to Rachel's BIG Big Data program!");
		System.out.println("To start, you'll need to know 4 things:");
		System.out.println("\t1. The relative or absolute file path to your data.");
		System.out.println("\t2. The column # that you are trying to train for (with two possible values).");
		System.out.println("\t3. The positive value within that class.");
		System.out.println("\t4. The negative value within that class.");
		System.out.println("Enter 'q' to quit at any time.");
		System.out.println("Enter 'help' to review this message.");
	}

	private static void quit() {
		System.out.println("Goodbye!");
		System.exit(0);
	}

	private static boolean executeACommand(String input) {
		if (input.equalsIgnoreCase("help") || input.equalsIgnoreCase("q")) {
			if (input.equalsIgnoreCase("q")) {
				quit();
			} else {
				help();
			}
			return true;
		}
		return false;
	}

	private static String readNegativeValue(Scanner in) {
		String negVal = "";
		while (true) {
			System.out.println("Enter the negative value for the class: ");

			negVal = in.next();
			if (executeACommand(negVal)) {
				continue;
			}
			return negVal;
		}
	}

	private static String readPositiveValue(Scanner in) {
		String posVal = "";
		while (true) {
			System.out.println("Enter the positive value for the class: ");

			posVal = in.next();
			if (executeACommand(posVal)) {
				continue;
			}
			return posVal;
		}
	}

	private static String readInputFile(Scanner in) {
		String str = "";
		while (true) {
			str = in.nextLine();
			if (executeACommand(str)) {
				System.out.print("Input file: ");
				continue;
			}
			return str;
		}
	}

	private static int readColumnNumber(Scanner in) {
		int colNum = 0;
		while (true) {
			
			String instruction = in.next();
			if (executeACommand(instruction)) {
				System.out.println("Enter the column number for the class:");
				continue;
			}

			try {
				colNum = Integer.parseInt(instruction);
			} catch (NumberFormatException e) {
				System.out.println("Try again! Enter a number for the class column.");
				continue;
			}
			if (colNum < 0) {
				System.out.println("The column number cannot be negative.");
				continue;
			}
			return colNum;
		}
	}
	
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		int colNum;
		String posVal;
		String negVal;
		String str;

		help();

		System.out.print("Input file: ");
		str = readInputFile(in);

		System.out.println("Enter the column number for the class:");
		colNum = readColumnNumber(in);
		
		posVal = readPositiveValue(in);

		negVal = readNegativeValue(in);

		BigDataDecisionTree myID3 = new BigDataDecisionTree(colNum, posVal, negVal);
		int status = 0;
		try {
			status = myID3.readData(str);
		} catch (IOException e) {
			System.err.println("Error when trying to read data! Is this the right"
					+ " file path?" + "\n" + e);
			System.exit(0);
		}
		
		if (status < 1) {
			return;
		}
		
		myID3.createDecisionTree();
		printTree(myID3, myID3.root, "");
		System.out.print("Enter a test file: ");
		in.nextLine();
		String testData = in.nextLine();
		myID3.classifyTestData(testData);
		in.close();
	}
}

