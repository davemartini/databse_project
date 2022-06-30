package databaseHW;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * 
 * @author David Martinez
 * @version 1
 * 
 * Class to be used to interact with and query database file
 *
 */
public class databaseReader {

	public databaseReader() {

	}

	/**
	 * 
	 * @param ep_id specific record match for employee ID
	 * @return string array with two indexes, [0] for the location of the DB file
	 *         pointer and [1] for the length of the data in bytes to read
	 * @throws IOException
	 */
	public String[] fetchIndex(int ep_id) throws IOException {
		String index = "index.dat";
		int IndexLoc = keyHash(ep_id);
		byte[] indexInfo = new byte[10];

		try {
			RandomAccessFile rafIndex = new RandomAccessFile(index, "r");
			rafIndex.seek(IndexLoc);
			rafIndex.read(indexInfo, 0, 10);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String fullIndex = new String(indexInfo, StandardCharsets.UTF_8);

		String[] findArray = fullIndex.split(",");

		return findArray;

	}

	/**
	 * 
	 * @param indexRecord uses the array returned from fetchIndex() to retrieve the
	 *                    database record for the employee ID entered
	 * @return String representation of an array containing all the information of
	 *         the employee record
	 * @throws IOException
	 */
	public String fetchRecord(String[] indexRecord) throws IOException {
		String data = "database_binary.dat";

		long pointerLocation = Long.parseLong(indexRecord[0]);
		int recordLength = Integer.parseInt(indexRecord[1]);

		byte[] databaseInfo = new byte[recordLength];

		try {
			RandomAccessFile rafData = new RandomAccessFile(data, "r");
			rafData.seek(pointerLocation);
			rafData.read(databaseInfo, 0, databaseInfo.length);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String fullDataRecord = new String(databaseInfo, StandardCharsets.UTF_8);

		//System.out.println(fullDataRecord);

		return fullDataRecord;

	}

	/**
	 * Key Hash algorithm to determine the location of the index record in the index
	 * file
	 * 
	 * @param ob parser returned object from the CSV
	 * @return integer hash value from passed object
	 */
	public int keyHash(Object ob) {
		String parseOb = ob.toString();

		int parsedKey = Integer.parseInt(parseOb);

		int key = (parsedKey / 2053) + 2053;

		return key;
	}

	/**
	 * Scans and parses entire index file and loads all Index records into RAM
	 * @throws IOException
	 * @return ArrayList of Long arrays containing two indexes each, one for each stored value in the index record 
	 */
	public ArrayList<Long[]> cacheIndex() throws IOException {
		String index = "index.dat";
		ArrayList<Long[]> cachedIndex = new ArrayList<>();
		
		
		try {
			RandomAccessFile rafIndex = new RandomAccessFile(index, "r");
			byte[] indexInfo = new byte[(int) rafIndex.length()];
			rafIndex.read(indexInfo, 0, indexInfo.length);
			String fullIndex = new String(indexInfo, StandardCharsets.UTF_8);
			
			ArrayList<String>  indexFullList = new ArrayList<>();
			
    		String[] findArray = fullIndex.split(",");
    		
    		
    		for(String record : findArray) {
    			
    			String stringTrim = record.trim();
    			indexFullList.add(stringTrim);
    		}
    		
//    		for(String trimmedStrings : indexFullList) {
//    			System.out.println(trimmedStrings);
//    		}
    		
    		
    		for(int i = 0; i < indexFullList.size(); i+=2) {
    			if(indexFullList.get(i).equals("") || indexFullList.get(i+1).equals("")) {
    				continue;
    			}
    			
    			Long[] cacheRecord = new Long[2];
    			
    			Long recordLocation = Long.parseLong(indexFullList.get(i));
    			Long byteLength = Long.parseLong(indexFullList.get(i+1));
    			
    			cacheRecord[0] = recordLocation;
    			cacheRecord[1] = byteLength;
    			
    			cachedIndex.add(cacheRecord);
    			
    		}

			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return cachedIndex;
		
	}
	
	/**
	 * Accepts cachedIndex arraylist and iterates over all the index records, pulling each database record
	 * checking the value of the salary found in the record then validating it against the user
	 * entered upper and lower bounds 
	 * 
	 * if accepted the database record is added to the return array
	 * 
	 * 
	 * @param cachedIndex arraylist of all index entries
	 * @param lowerBound the lower bound of the range search
	 * @param upperBound the upperbound of the range search
	 * @return ArrayList of strings containing the vaild records that match the query
	 * @throws IOException
	 */
	public ArrayList<String> constructRecordRange(ArrayList<Long[]> cachedIndex, float lowerBound, float upperBound) throws IOException {
		ArrayList<String> rangeRecord = new ArrayList<>();
		
		for(Long[] curRec : cachedIndex) {
			String location = Long.toString(curRec[0]);
			String length = Long.toString(curRec[1]);
	
			String[] indexRetCon = new String[2];
			
			indexRetCon[0] = location;
			indexRetCon[1] = length;
			
			String dataBaseRec = fetchRecord(indexRetCon);
			String trim = dataBaseRec.substring(1, dataBaseRec.length()-1);
			
			String[] checkPayRange = trim.split(",");
			//System.out.println(trim);
			float compareValue = Float.parseFloat(checkPayRange[4]);
			
			if(checkValidRange(compareValue, lowerBound, upperBound)){
				rangeRecord.add(dataBaseRec);
			}
			
			
			
		}
		
		return rangeRecord;
		
	}
	
	public boolean checkValidRange(float compareValue, float lowerBound, float upperBound) {
		if(compareValue >= lowerBound && compareValue <= upperBound) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
}