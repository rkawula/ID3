package cs280;

import java.util.Scanner;


public class DecisionTreeRunner {

	public static void main(String[] args) throws Exception {

		DecisionTree myID3 = new DecisionTree();
		Scanner in = new Scanner(System.in);

		System.out.print("Input file: ");
		String str = in.nextLine();

		int status = myID3.readData(str);
		if (status <= 0) {
			in.close();
			return;
		}
		myID3.createDecisionTree();
		in.close();
	}
}
