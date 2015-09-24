package part3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A mapper class to handle large amounts of data. This class will compress
 * data read from a training set, and allow a machine learning algorithm
 * to more efficiently use large test sets that can't fit in the heap
 * as complete objects.
 * 
 * This class does not use threads.
 * 
 * @author Rachel Kawula rkawula@gmail.com
 *
 */
class DataMapper {

	/**
	 * Array of column numbers to names.
	 */
	static String[] columnNames;

	/**
	 * The node that this datamapper belongs to, and is responsible for.
	 */
	Node mappedNode;
	
	/**
	 * The files names that the node's data has been mapped to.
	 */
	ArrayList<String> pagedData = new ArrayList<String>();
	
	/**
	 * Holds the number of times that this value occurred within this column,
	 * across all instances. ie if column 5 has contained "e" 3 times and "q" 5 times:
	 * valueFrequencyInColumn[5].get("e") == 3
	 * valueFrequencyInColumn[5].get("q") == 5
	 */
	HashMap<String, Integer>[] valueFrequencyInColumn;
	/**
	 * Makes a new mapper.
	 * @param columns The number of columns that are in the dataset.
	 */
	public DataMapper(Node mapped, int columns) {
		mappedNode = mapped;
		valueFrequencyInColumn = new HashMap[columns];
		for (int i = 0; i < columns; i++) {
			valueFrequencyInColumn[i] = new HashMap<String, Integer>();
		}
	}

	/**
	 * Given a set of attributes for this data set, the mapper will compress the
	 * values by mapping column number to string value to frequency.
	 * 
	 * @param attributes An array that has the index representing the column number,
	 * set to contain the value discovered in that column for this instance of the
	 * data set.
	 */
	public void compress(String[] attributes) {
		for (int i = 0; i < valueFrequencyInColumn.length; i++) {
			if (valueFrequencyInColumn[i] == null) {
				valueFrequencyInColumn[i] = new HashMap<String, Integer>();
			}
			// If this column's value for the instance is already in our hashmap,
			// increment the frequency. Otherwise, add it with frequency of 1.
			String key = attributes[i];
			if (valueFrequencyInColumn[i].containsKey(key)) {
				int frequency = valueFrequencyInColumn[i].get(key);
				valueFrequencyInColumn[i].put(key, frequency + 1);
			} else {
				valueFrequencyInColumn[i].put(key, 1);
			}
		}
	}

	/**
	 * Sets the names of the columns for the dataset.
	 * @param colNames The names, from left to right, read in
	 * from the dataset. The left-most column begins at 0.
	 */
	public static void setColumnNames(String[] colNames) {
		columnNames = colNames;
	}

	/**
	 * Gets the label for the column in the dataset.
	 * @param column The number for the column. Counting begins at the
	 * left-most column, as 0.
	 * @return The name of that column.
	 */
	public static String getColumnTitle(int column) {
		return columnNames[column];
	}

	public Set<String> getValuesFor(int currentColumn) {
		Set<String> keys = valueFrequencyInColumn[currentColumn].keySet();
		return keys == null ? new HashSet<String>() : keys;
	}

	/**
	 * Determines the majority class for the node. 
	 * @param classColumn
	 * @param positive
	 * @param negative
	 * @return
	 */
	String getMajorityClass(int classColumn, String positive, String negative) {

		HashMap<String, Integer> map = valueFrequencyInColumn[classColumn];
		if (map == null || map.isEmpty()) {
			return "Empty set?!";
		}
		if (map.containsKey(positive) && map.containsKey(negative)) {
			int posFreq = valueFrequencyInColumn[classColumn].get(positive);
			int negFreq = valueFrequencyInColumn[classColumn].get(negative);
			// How to handle this case????? Entropy == 1.0
			if (posFreq == negFreq) {
				return "Tie!";
			}
			return posFreq > negFreq ? positive : negative;
		}
		if (map.containsKey(positive)) {
			return positive;
		}
		return negative;

	}

	private static String formatDataAsRows(ArrayList<String[]> data) {
		StringBuilder sb = new StringBuilder();
		for (String[] row : data) {
			for (int i = 0; i < row.length; i++) {
				sb.append(row[i] + " ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	String writeDataToDisk(ArrayList<String[]> data) {
		// Take chunks of data at a time.
		// Append it to a growing file.
		int pageNumber = pagedData.size();
		PrintWriter writer = null;
		String fileName = "node_data" + File.separator + "Node" + mappedNode.number + "_part" + pageNumber + ".txt";
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			writer.append(formatDataAsRows(data));
		} catch (UnsupportedEncodingException e) {
			System.err.println("Bad news when writing to file: " + e);
		} catch (FileNotFoundException e) {
			System.err.println("For some reason this file wasn't found. . ?" + e);
		} finally {
			writer.close();
		}
		pagedData.add(fileName);
		return fileName;
	}
	
	String[] getPages() {
		return pagedData.toArray(new String[pagedData.size()]);
	}


}
