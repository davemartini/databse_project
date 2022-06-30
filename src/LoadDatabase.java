package databaseHW;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parser code adapted from AdvancedParser.java 
 *
 * Builds binary database file and index file 
 * 
 * Database file is written sequentially
 * 
 * index file acts as a hash map with the first index of the parsed CSV files acting as the primary key
 * 
 * The key is hashed and the index record is stored at that location in the random access file
 * 
 * The index record is a standard size of 10 bytes so retrival is always 10 bytes from the hashed key location 
 * 
 * index record contains how many bytes to move into the database file to the start of the record and 
 * how many bytes long the record is
 * 
 * @author David Martinez
 * @author Jason Miller 
 *
 */

public class LoadDatabase {
	RandomAccessFile rafIndex;
	RandomAccessFile rafData;
	final char QUOTE = '"';
	boolean expectHeaders;
	String[] headers;
	final char DELIMITER = ',';

	
	/**
	 * Constructor creates and intilizes the binary random access files
	 */
	public LoadDatabase(){
		String indexFile = "index.dat";
		String dbDataFile = "database_binary.dat";
		
		headers = null;
        this.expectHeaders = true;
        
		 try {
			rafIndex = new RandomAccessFile(indexFile, "rw");
			rafData = new RandomAccessFile(dbDataFile, "rw");
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	List<String> parseLineCSV(String line) {
		List<String> fields = new ArrayList<String>();
		int pos;
		char cc;
		boolean quoted = false;
		int start = 0;
		for (pos = 0; pos < line.length(); pos++) {
			cc = line.charAt(pos);
			if (cc == QUOTE) {
				quoted = !quoted;
			}
			if (!quoted && cc == DELIMITER) {
				String word = line.substring(start, pos);
				fields.add(word);
				start = pos + 1;
			}
		}
		if (start < line.length()) {
			String word = line.substring(start);
			fields.add(word);
		}
		return fields;
	}
	
	/**
	 * 
	 * @param word
	 * @return
	 */
	Object convertField(String word) {
		Object returnValue = word;
		try {
			int converted = Integer.valueOf(word);
			returnValue = new Integer(converted);
		} catch (Exception e) {
			// not a valid int
			try {
				double converted = Double.valueOf(word);
				returnValue = new Double(converted);
			} catch (Exception f) {
				// not a valid double
			}
		}
		return returnValue;
	}

	/**
	 * 
	 * Parses data from CSV file
	 * Writes each line in binary to database file 
	 * then calls indexer with the record information to be indexed
	 * 
	 * Incorporates code from Jason Miiller AdvancedParser.java
	 * 
	 * @param filename the CSV file name to parsed
	 * 
	 * 
	 * @throws IOException
	 */
	public void parseToFile(String filename) throws IOException {
		int fieldNum = 0;
		int numLines = 0;
		int pointerWriteLocation = 0;
		//List<Object[]> data = new ArrayList<Object[]>();
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		String line;
		while ((line = br.readLine()) != null) {
			List<String> fields = parseLineCSV(line);
			Object[] oneLine = new Object[fields.size()];
			if (numLines == 0 && this.expectHeaders) {
				headers = new String[fields.size()];
				headers = fields.toArray(headers);
			} else {
				for (int i = 0; i < fields.size(); i++) {
					String field = fields.get(i);
					Object converted = convertField(field);
					oneLine[i] = converted;
				}
				int hashedRecordKey = keyHash(oneLine[0]);
				String dataArray = Arrays.toString(oneLine);
				byte[] curRecord = dataArray.getBytes("UTF-8");
				rafData.write(curRecord, 0, curRecord.length);
				indexer(hashedRecordKey, pointerWriteLocation, curRecord.length);
				

				pointerWriteLocation = pointerWriteLocation+(curRecord.length);
				//data.add(oneLine);
			}
			numLines++;
		}
		br.close();
		
		rafData.close();
		rafIndex.close();


	}
	/**
	 * Writes location and length of bytes of each record in database to file 
	 * 
	 * Functions as a hash map 
	 * 
	 * Does not currently support for handling collisions
	 * 
	 * @param recordKey the first index in the parsed CSV line after being hashed, where to place the pointer in the file to write 
	 * @param pointerWriteLocation where the pointed should go in the database random access file to be at the start of the record
	 * @param recordLength how many bytes should be read from the file to get the complete record
	 * @throws IOException
	 */
	public void indexer(int recordKey, int pointerWriteLocation, int recordLength) throws IOException {
		
		String indexRecord = pointerWriteLocation+","+recordLength+",";
		byte[] indexRecordBytes = indexRecord.getBytes("UTF-8");
		//System.out.println(indexRecordBytes.length);
		
		byte[] standardRecord = new byte[10];
		System.arraycopy( indexRecordBytes, 0, standardRecord, 0,indexRecordBytes.length );
		//System.out.println(standardRecord.length);

		
		rafIndex.seek(recordKey);
		rafIndex.write(standardRecord);
		
		
		
		
		
	}
	/**
	 * 
	 * @param ob first index of read CSV line in its unparsed form 
	 * @return
	 */
	public int keyHash(Object ob) {
		String parseOb = ob.toString();
		
		int parsedKey = Integer.parseInt(parseOb);
		
		int key = (parsedKey / 2053) + 2053;
		
		//System.out.println(key);
		
		return key;
	}
	

	

}

