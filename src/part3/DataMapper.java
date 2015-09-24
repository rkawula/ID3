package part3;

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
	
	Node mappedNode;
	
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
	
	
	public static void setColumnNames(String[] colNames) {
		columnNames = colNames;
	}
	
	public static String getColumnTitle(int column) {
		return columnNames[column];
	}

	public Set<String> getValuesFor(int currentColumn) {
		Set<String> keys = valueFrequencyInColumn[currentColumn].keySet();
		return keys == null ? new HashSet<String>() : keys;
	}

	String getMajorityClass(int classColumn, String positive, String negative) {
		int posFreq = valueFrequencyInColumn[classColumn].get(positive);
		int negFreq = valueFrequencyInColumn[classColumn].get(negative);
		// How to handle this case????? Entropy == 1.0
		if (posFreq == negFreq) {
			return "Tie!";
		}
		return posFreq > negFreq ? positive : negative;
	}


}