package part1;

import java.io.IOException;
import java.util.Scanner;


public class DecisionTreeRunner {

	public static void help() {
		System.out.println("Welcome to Rachel's Big Data program!");
		System.out.println("To start, you'll need to know 4 things:");
		System.out.println("\t1. The relative or absolute file path to your data.");
		System.out.println("\t2. The column # that you are trying to train for (with two possible values).");
		System.out.println("\t3. The positive value within that class.");
		System.out.println("\t4. The negative value within that class.");
		System.out.println("Enter 'q' to quit at any time.");
		System.out.println("Enter 'help' to review this message.");
	}

	public static void quit() {
		System.out.println("Goodbye!");
		System.exit(0);
	}
	

	public static boolean executeACommand(String input) {
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

	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		int colNum;
		String posVal;
		String negVal;
		String str;

		help();

		System.out.print("Input file: ");

		while (true) {
			str = in.nextLine();
			if (executeACommand(str)) {
				System.out.print("Input file: ");
				continue;
			}
			break;
		}

		System.out.println("Enter the column number for the class:");
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
			break;
		}

		while (true) {
			System.out.println("Enter the positive value for the class: ");

			posVal = in.next();
			if (executeACommand(posVal)) {
				continue;
			}
			break;
		}

		while (true) {
			System.out.println("Enter the negative value for the class: ");

			negVal = in.next();
			if (executeACommand(negVal)) {
				continue;
			}
			break;
		}

		DecisionTree myID3 = new DecisionTree(colNum, posVal, negVal);

		int status = -1;
		try {
			status = myID3.readData(str);
		} catch (IOException e) {
			System.err.println("Error when trying to read data! Is this the right"
					+ " file path?");
		}

		if (status <= 0) {
			in.close();
			return;
		}

		myID3.createDecisionTree();
		System.out.println("Enter a test file: ");
		in.nextLine();
		String testData = in.nextLine();
		myID3.classifyTestData(testData);
		in.close();
	}
}

