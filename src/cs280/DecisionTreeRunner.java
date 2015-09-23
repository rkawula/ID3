package cs280;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;


public class DecisionTreeRunner {

	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		int colNum;
		String posVal;
		String negVal;
		String str;

		while (true) {
			System.out.print("Input file: ");
			str = in.nextLine();
			try {
				System.out.println("Enter the column number for the class: ");
				colNum = in.nextInt();
				if (colNum < 0) {
					System.out.println("The column number cannot be negative.");
					continue;
				}
			} catch (InputMismatchException e) {
				System.out.println("That is not an integer!");
				continue;
			}
			System.out.println("Enter the positive value for the class: ");
			posVal = in.next();
			System.out.println("Enter the negative value for the class: ");
			negVal = in.next();
			break;
		}
		DecisionTree myID3 = new DecisionTree(colNum, posVal, negVal);

		int status = -1;
		try {
			status = myID3.readData(str);
		} catch (IOException e) {
			System.err.println("Error when trying to read data!");
		}

		if (status <= 0) {
			in.close();
			return;
		}

		myID3.createDecisionTree();
		in.close();
	}
}
