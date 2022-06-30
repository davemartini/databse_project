package databaseHW;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Main utility class for starting the application 
 * 
 * 
 * 
 * Creates database using supplied hardcoded CSV file 
 * parses it and loads it into a binary file then creates a binary index file 
 * Then creates the database reader
 * Then starts the UserConsole session 
 * 
 * @author David Martinez
 * @version 1.0
 *
 */
public class DatabaseInitializer {
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		String testFileName = "company_roster_100.csv"; // first three lines builds data base and index file
		LoadDatabase myBase = new LoadDatabase();
		myBase.parseToFile(testFileName);
		
		databaseReader baseRead = new databaseReader(); //creates reader
		
		UserConsole myConsole = new UserConsole(Arrays.toString(myBase.headers), baseRead); //starts console dialog

		
	}
	
}
	

