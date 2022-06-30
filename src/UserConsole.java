package databaseHW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Console based View portion of the database MVC
 * 
 * Prompts user to make queries based on the data in the database 
 * 
 * Supports user options to query by exact match or by range for salaries
 * 
 * @author David Martinez
 * @version 1.0 
 *
 */
public class UserConsole {
	private String dataBaseHeaders;
	private databaseReader baseRead;
	
	UserConsole(String passedHeaders, databaseReader passedReader) throws IOException{
		dataBaseHeaders = passedHeaders;
		baseRead = passedReader;
		System.out.println("Simulated Employee Database Query Console");
		System.out.println("*****************************************");
		optionsPrinter();
		
	}
	
	public void optionsPrinter() throws IOException {
		System.out.println("");
		System.out.println("Options: Type the Number and then press enter");
		System.out.println("1 - Exact Match for Employee Record");
		System.out.println("2 - Range Search By Employee Salary");
		System.out.println("3 - Exit");
		userDescion();
	}
	
	public void userDescion() throws IOException {
		Scanner scan = new Scanner(System.in);
		String input = scan.nextLine();
		int userChoice = 0;
		
		try {
			userChoice = Integer.parseInt(input);
			
			if(userChoice == 1) {
				exactMatch();
			}
			
			if(userChoice == 2) {
				rangeValues();
			}
			if(userChoice == 3) {

				exitProtocol();
			}
			if(userChoice != 1 || userChoice != 2 || userChoice != 3) {
			
			}
			
			
		} catch (NumberFormatException e) {
			System.out.println("Invalid input");
			System.out.println("");
			optionsPrinter();
		}
		
	}
	
	public void exactMatch() throws IOException {
		System.out.println("");
		System.out.println("Exact Match for Employee Record");
		System.out.println("*******************************");
		System.out.println("Enter employee ID number then press enter -- (ex. 601769871 or 607221910)");
		Scanner scan = new Scanner(System.in);
		String thisPut = scan.nextLine();
		
		
		
		try {
			int matchChoice = Integer.parseInt(thisPut);
			
			String[] indexRecord = baseRead.fetchIndex(matchChoice);
			String dataBaseRecord = baseRead.fetchRecord(indexRecord);
			System.out.println("");
			System.out.println(dataBaseHeaders);
			System.out.println(dataBaseRecord);
			
			optionsPrinter();
			
			
		} catch (NumberFormatException e) {
			System.out.println("Invalid input or No record Found");
			System.out.println("");
			optionsPrinter();
		}
		
	}
	
	public void rangeValues() throws IOException {
		System.out.println("");
		System.out.println("Range Search By Employee Salary");
		System.out.println("*******************************");
		System.out.println("Enter lower bound salary number then press enter -- (ex. 0 or 5000 or 15000)");
		
		Scanner scan = new Scanner(System.in);
		String input = scan.nextLine();
		
		float lowerB = 0;
		float upperB = 0;
		
		try {
			lowerB = Float.parseFloat(input);
			
			if(lowerB < 0) {
				System.out.println("Invalid input");
				System.out.println("");
				optionsPrinter();
			}
			
		} catch (NumberFormatException e) {
			System.out.println("Invalid input Exception Thrown");
			System.out.println("");
			optionsPrinter();
		}
		
		
		System.out.println("");
		System.out.println("Enter upper bound salary number then press enter -- (ex. 10000 or 20000)");
		String input2 = scan.nextLine();
		
		try {
			upperB = Float.parseFloat(input2);
			
			if(upperB < 0) {
				System.out.println("Invalid input");
				System.out.println("");
				optionsPrinter();
			}
			
		} catch (NumberFormatException e) {
			System.out.println("Invalid input Exception Thrown");
			System.out.println("");
			optionsPrinter();
		}
		
		
		ArrayList<Long[]> cachedIndex = baseRead.cacheIndex();
		ArrayList<String> rangeResults = baseRead.constructRecordRange(cachedIndex, lowerB, upperB);
		System.out.println("");
		System.out.println(dataBaseHeaders);
		
		if(rangeResults.size() == 0) {
			System.out.println("No Records Found ...");
			optionsPrinter();
		}
		else {
			for(String baseString : rangeResults) {
				System.out.println(baseString);
			}
			System.out.println("-------------------------------------------------");
			
			optionsPrinter();
		}
		
	}
	public void exitProtocol() {
		System.out.println("Thank you -- Goodbye");
	}
	
}
