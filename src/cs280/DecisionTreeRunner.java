package cs280;

import java.util.Scanner;


public class DecisionTreeRunner {

	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);

		System.out.print("Input file: ");
		String str = in.nextLine();
		
		System.out.println("Enter the column number for the class: ");
		int colNum = in.nextInt();
		System.out.println("Enter the positive value for the class: ");
		String posVal = in.next();
		System.out.println("Enter the negative value for the class: ");
		String negVal = in.next();
		DecisionTree myID3 = new DecisionTree(colNum, posVal, negVal);
		
		int status = myID3.readData(str);
		
		if (status <= 0) {
			in.close();
			return;
		}

		myID3.createDecisionTree();
		in.close();
	}
}
