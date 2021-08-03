import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

/*
Owner		:	Vamsi Krishna Utla
Student ID	:	B00870632
Email		:	vm271757@dal.ca
Date		:	09-12-2020	
Purpose		:	This file/class is used to define the main method to interact with classes MobileDevice and Government for the purpose of contact tracing of COVID-19
*/

/*this class is usually maintained by the Government body to allow the end users to sync their data (contacts and tests) with government's database
 *the end user can also be agency which uses this to record test results to government's database
 *the configuration file for government needs to be provided in the main method before executing the same as it should be confidential
 *provides a menu to end users to select their request in order to proceed further 
 */
public class MainUI {

	public static void main(String[] args) {
		
		Government government=null;											//declare a government instance
		List<MobileDevice> mobileDevices=new ArrayList<MobileDevice>();		//declare a list of mobile devices. A new device will be added when the end user wishes to create a new one
		Scanner scanner=new Scanner(System.in);									//declare scanner to read input from console
		String input="C:\\config.txt";										//used for storing the path for initializing government object, can also be used as a storage variable for string type
		int choice;															//choice that is used in a menu 
		final boolean true_var=true;										//constant for a boolean value - true
		final boolean false_var=false;										//constant for a boolean value - true
		
		final int zero=0;													//constant for value '0'
		final int one=1;													//constant for value '1'
		final int two=2;													//constant for value '2'
		final int three=3;													//constant for value '3'
		final int four=4;													//constant for value '4'
		final int five=5;													//constant for value '5'
		final int six=6;													//constant for value '6'
		final int seven=7;													//constant for value '7'
		final int error=-1;													//constant for error code '-1'
		
		try {
			government=new Government(input);			//initialize government object
		}
		
		//report an issue with required details in case of any exceptions and return
		catch (InvalidInputException e) {
			System.out.println(e.getString()+"\n Please try again.");
			scanner.close();
			return;
		}
		catch (ClassNotFoundException e) {
			System.out.println("Invalid class for JDBC driver. Please contact system administrator.");
			System.out.println("Exiting program...");
			scanner.close();
			return;
		}
		catch(IOException e) {
			System.out.println("Issue with configuration file.\nPlease try again.");
			scanner.close();
			return;
		}
		catch (SQLException e) {
			System.out.println("Unable to connect to database. Please contact system administrator.");
			scanner.close();
			return;
		}
		
		
		//display welcome message
		System.out.println("Welcome to Canadian Federal Government's application for contact tracing of COVID-19!");
		
		//design a menu functionality using do while loop
		do {
			//display menu items in every loop
			System.out.println("\nPlease select your choice from the below menu:");
			System.out.println("'1' -> To add/register a new mobile device.");
			System.out.println("'2' -> To add contacts to a registered mobile device.");
			System.out.println("'3' -> To record a test against a registered mobile device.");
			System.out.println("'4' -> To sync the contact details of a registered mobile device to the government server and identify the result.");
			System.out.println("'5' -> To record a test result to government database.");
			System.out.println("'6' -> To find the large gatherings on a given day.");
			System.out.println("'7' -> Quit the application.");
			System.out.println("\nEnter your choice:");	
			
			choice=scanner.nextInt();	//allow the suer to pass the input through console
			scanner.nextLine();
			//user opted to register or create a new mobile device
			if(choice==one){
				System.out.println("Please enter the configuration file path containing the details for regsitering the new mobile device");
				input=scanner.nextLine();			//collect the input of file path for the configuration file that needs to be passed during object creation of mobile device
				MobileDevice newMobileDevice=null;	//declare the new mobile device
				try{
					newMobileDevice=new MobileDevice(input,government);	//initialize using the given input path of configuration file and government object
				}
				//display suitable error message in case of any exceptions along with details on next steps
				catch(InvalidInputException e) {
					System.out.println(e.getString()+" Failed to register new mobile device. Please fix the issue and retry.");
				} 
				catch (IOException e) {
					System.out.println("Failed to register new mobile device due to issue with content in the configuration file. Please fix the issue and retry.");
				}
				
				//if created successfully, display the unique hash of the mobile device along with successful message and add it to array list of mobile devices
				if(newMobileDevice!=null) {
					System.out.println("New mobile device registered successfully. Unique identifier for reference: "+newMobileDevice.deviceHash);
					mobileDevices.add(newMobileDevice);	//add it to list
				}
			}
			//user opted to add contacts to a new or existing mobile device
			else if(choice==two) {
				int serialNumber=0;		//variable to store which mobile device in the sequence of array list should the contacts be added to	
				String hash="";			//variable to store contact hash
				int date=error;			//variable to store date of contact
				int duration=error;		//variable to store duration of contact
				
				//display list of mobile devices (if any), else prompt the user to enter -1 in order to create a new mobile device and then add contacts
				if(mobileDevices.size()==zero) {
					System.out.println("No mobile devices registered yet in this session. Please enter -1 to register a new mobile device and then add contacts");
				}
				else {
					System.out.println("Select the mobile device number from the following devices to which you would like to record contacts information.");					
					System.out.println("Enter -1 if the mobile device is not present in the below list.");
				}
				
				//display the list of mobile devices
				for(int i=0;i<mobileDevices.size();i++) {
					
					//display each device in a single line
					
					System.out.println((i+1)+". "+mobileDevices.get(i).deviceHash);
				
				}
				System.out.println("Enter input:\n");
				serialNumber=scanner.nextInt();		//read input from console
				scanner.nextLine();
				//if input is given as -1
				if(serialNumber==error) {
					System.out.println("Please enter the configuration file path of the exisiting mobile device:\n");
					input=scanner.nextLine();			//read input file path for configuration file in order to create a new mobile device
					MobileDevice newMobileDevice=null;	//declare new mobile device
					try{
						newMobileDevice=new MobileDevice(input,government);	//initialize new mobile device
					}
					//in case of any exceptions, print the appropriate message and continue to next iteration
					catch(InvalidInputException e) {
						System.out.println(e.getString()+" Failed to register mobile device. Please fix the issue and retry.");
						continue;
					} 
					catch (IOException e) {
						System.out.println("Failed to register mobile device due to issue with content in the configuration file. Please fix the issue and retry.");
						continue;
					}
					if(newMobileDevice!=null) {
						mobileDevices.add(newMobileDevice);	//add mobile device to list 
					}
					System.out.println("Enter contact's unique hash displayed on your bluetooth contact screen.\n");
					hash=scanner.nextLine();		//read contact hash that needs to be recorded
					System.out.println("Enter date of contact i.e., number of days starting from 01-01-2020.\n");
					date=scanner.nextInt();			//read contact's date from console
					scanner.nextLine();
					System.out.println("Enter duration (in minutes) of contact\n");
					duration=scanner.nextInt();		//read duration of the contact from console
					scanner.nextLine();
					try {
						//record the new contact to mobile device
						mobileDevices.get(mobileDevices.size()-1).recordContact(hash, date, duration);
						System.out.println("Contcat recorded suucessfully.");
					}
					//in case of any exceptions, print the appropriate message
					catch(InvalidInputException e) {
						System.out.println(e.getString()+" Please try again after fixing the issue.");
					}
				}
				else {
					
					//check to see for invalid serial number that is read from console
					if(serialNumber>mobileDevices.size() || serialNumber<=zero) {
						System.out.println("Invalid input.");
					}
					else {
						System.out.println("Enter contact's unique hash displayed on your bluetooth contact screen.\n");
						hash=scanner.nextLine();	//read contact hash that needs to be recorded
						System.out.println("Enter date of contact i.e., number of days starting from 01-01-2020.\n");
						date=scanner.nextInt();		//read contact's date from console
						scanner.nextLine();
						System.out.println("Enter duration (in minutes) of contact\n");
						duration=scanner.nextInt();	//read duration of the contact from console
						scanner.nextLine();
						try {
							//record the new contact to mobile device
							mobileDevices.get(serialNumber-one).recordContact(hash, date, duration);
							System.out.println("Contcat recorded suucessfully.");
						}
						//in case of any exceptions, print the appropriate message
						catch(InvalidInputException e) {
							System.out.println(e.getMessage()+" Please try again after fixing the issue.");
						}
					}
				}
			}
			//user/agency has opted to inform positive test result to an existing or a new mobile device 
			else if(choice==three) {
				int serialNumber=0;		//variable to store which mobile device in the sequence of array list should the test hash be recorded to
				String hash="";			//variable to store contact hash
				
				//display list of mobile devices (if any), else prompt the user to enter -1 in order to create a new mobile device and then add the positive test hash
				if(mobileDevices.size()==zero) {
					System.out.println("No mobile devices registered yet in this session. Please enter -1 to register a new mobile device and then record positive tests against it.");
				}
				else {
					System.out.println("Select the mobile device number from the following devices to which you would like to record positive test information.");					
					System.out.println("Enter -1 if the mobile device is not present in the below list.");
				}
				
				//display the list of mobile devices
				for(int i=0;i<mobileDevices.size();i++) {
					
					//display each device in a single line
					
					System.out.println((i+1)+". "+mobileDevices.get(i).deviceHash);
				}
				System.out.println("Enter input:\n");
				serialNumber=scanner.nextInt();		//read input from console
				scanner.nextLine();
				//if input is given as -1
				if(serialNumber==error) {
					System.out.println("Please enter the configuration file path of the exisiting mobile device:\n");
					input=scanner.nextLine();			//read input file path for configuration file in order to create a new mobile device
					MobileDevice newMobileDevice=null;	//declare new mobile device
					try{
						newMobileDevice=new MobileDevice(input,government);		//initialize new mobile device
					}
					//in case of any exceptions, print the appropriate message and continue to next iteration
					catch(InvalidInputException e) {
						System.out.println(e.getMessage()+" Failed to register mobile device. Please fix the issue and retry.");
					} 
					catch (IOException e) {
						System.out.println("Failed to register mobile device due to issue with content in the configuration file. Please fix the issue and retry.");
					}
					if(newMobileDevice!=null) {
						mobileDevices.add(newMobileDevice);		//add mobile device to list
					}
					System.out.println("Enter positive test's unique hash.\n");
					hash=scanner.nextLine();	//read positive test hash
					try {
						//record the new positive test hash to newly created mobile device
						mobileDevices.get(mobileDevices.size()-1).positiveTest(hash);
						System.out.println("Positive test recorded suucessfully.");
					}
					//in case of any exceptions, print the appropriate message
					catch(InvalidInputException e) {
						System.out.println(e.getMessage()+" Please try again after fixing the issue.");
					}
				}
				else {
					
					//check to see for invalid serial number that is read from console
					if(serialNumber>mobileDevices.size() || serialNumber<=zero) {
						System.out.println("Invalid input.");
					}
					else {
						System.out.println("Enter psoitive test's unique hash.\n");
						hash=scanner.nextLine(); 	//read positive test hash
						
						try {
							//record the new positive test hash to selected mobile device
							mobileDevices.get(serialNumber-one).positiveTest(hash);
							System.out.println("Positive test recorded suucessfully.");
						}
						//in case of any exceptions, print the appropriate message
						catch(InvalidInputException e) {
							System.out.println(e.getMessage()+" Please try again after fixing the issue.");
						}
					}
				}
			}	
			//user has opted to sync the contacts and test details of a mobile device to government server
			else if(choice==four) {
				int serialNumber=0;
				
				//display list of mobile devices (if any), else prompt the user to enter -1 in order to create a new mobile device and then sync with government's server
				if(mobileDevices.size()==zero) {
					System.out.println("No mobile devices registered yet in this session. Please enter -1 to register a new mobile device and then sync it.");
				}
				else {
					System.out.println("Select the mobile device number from the following devices to which you would like to record positive test information.");					
					System.out.println("Enter -1 if the mobile device is not present in the below list.");
				}
				
				//display the list of mobile devices
				for(int i=0;i<mobileDevices.size();i++) {
					
					//display each device in a single line
					
					System.out.println((i+1)+". "+mobileDevices.get(i).deviceHash);
				}
				System.out.println("Enter input:\n");
				serialNumber=scanner.nextInt();		//read input from console
				scanner.nextLine();
				//if input is given as -1
				if(serialNumber==error) {
					System.out.println("Please enter the configuration file path of the exisiting mobile device:\n");
					input=scanner.nextLine();			//read input file path for configuration file in order to create a new mobile device
					MobileDevice newMobileDevice=null;	//declare new mobile device
					try{
						newMobileDevice=new MobileDevice(input,government);		//initialize new mobile device
					}
					//in case of any exceptions, print the appropriate message and continue to next iteration
					catch(InvalidInputException e) {
						System.out.println(e.getMessage()+" Failed to register mobile device. Please fix the issue and retry.");
						continue;
					} 
					catch (IOException e) {
						System.out.println("Failed to register mobile device due to issue with content in the configuration file. Please fix the issue and retry.");
						continue;
					}
					if(newMobileDevice!=null) {
						mobileDevices.add(newMobileDevice);	//add new mobile device to list
					}
					try {
						
						//sync the newly created mobile device
						boolean result=mobileDevices.get(mobileDevices.size()-1).synchronizeData();
						//if true, print the message that the user of the mobile device has been in contact with a positively tested COVID-19 person in the last 14 days 
						if(result) {
							System.out.println("The selected mobile device has been in contact with a positive COVID contact in past 14 days.");
						}
						//else, print the message that the user of the mobile device has not been in contact with a positively tested COVID-19 person in the last 14 days
						else{
							System.out.println("The selected mobile device has not been in contact with a positive COVID contact in past 14 days.");								
						}
					}
					//in case of any exceptions, print the appropriate message
					catch(InvalidInputException e) {
						System.out.println(e.getMessage()+" Please try again after fixing the issue.");
					}
					catch(SQLException e) {
						System.out.println("Issue with contacting government server. Please contact system administartor.");
					}
					catch(IOException e) {
						System.out.println("Issue with contacting government server. Please contact system administartor.");
					}
					catch(ClassNotFoundException e) {
						System.out.println("Issue with contacting government server. Please contact system administartor.");
					}
					catch(ParseException e) {
						System.out.println("Issue with contacting government server. Please contact system administartor.");
					}
				}
				else {
					
					//check to see for invalid serial number that is read from console
					if(serialNumber>mobileDevices.size() || serialNumber<=zero) {
						System.out.println("Invalid input.");
					}
					else {
						try {
							
							//sync the newly created mobile device
							boolean result=mobileDevices.get(serialNumber-one).synchronizeData();
							
							//if true, print the message that the user of the mobile device has been in contact with a positively tested COVID-19 person in the last 14 days
							if(result) {
								System.out.println("The selected mobile device has been in contact with a positive COVID contact in past 14 days.");
							}
							//else, print the message that the user of the mobile device has not been in contact with a positively tested COVID-19 person in the last 14 days
							else{
								System.out.println("The selected mobile device has not been in contact with a positive COVID contact in past 14 days.");								
							}
						}
						//in case of any exceptions, print the appropriate message
						catch(InvalidInputException e) {
							System.out.println(e.getMessage()+" Please try again after fixing the issue.");
						}
						catch(SQLException e) {
							System.out.println("Issue with contacting government server. Please contact system administartor.");
						}
						catch(IOException e) {
							System.out.println("Issue with contacting government server. Please contact system administartor.");
						}
						catch(ClassNotFoundException e) {
							System.out.println("Issue with contacting government server. Please contact system administartor.");
						}
						catch(ParseException e) {
							System.out.println("Issue with contacting government server. Please contact system administartor.");
						}
					}
				}
			}
			//agency/user has opted to record a test result details to government's database
			else if(choice==five) {
				String hash="";		//variable for storing the test hash
				int date=error;		//variable for storing the date of test
				int result=error;	//variable for storing the result of the test
				System.out.println("Enter test hash:\n");
				hash=scanner.nextLine();	//read test hash from console
				System.out.println("Enter test date (number of days from 01-01-2020):\n");
				date=scanner.nextInt();		//read the date of test from the console
				scanner.nextLine();
				System.out.println("Enter test result ('0' in case of negative and '1' in case of a positive test):\n");
				result=scanner.nextInt();	//read the result of the test from the console
				scanner.nextLine();
				if(result==zero) {	
					try {
						//record the test result (false) details to government's database
						government.recordTestResult(hash, date, false_var);
						System.out.println("Test details recorded suucessfully");
					}
					//in case of any exceptions, print the appropriate message
					catch (SQLException e) {
						System.out.println("Issue with contacting government server. Please contact system administartor.");

					}
					catch (ClassNotFoundException e) {
						System.out.println("Issue with contacting government server. Please contact system administartor.");

					}
				}
				else if(result==one) {
					try {
						//record the test result (true) details to government's database
						government.recordTestResult(hash, date, true_var);
						System.out.println("Test details recorded suucessfully");
					}
					//in case of any exceptions, print the appropriate message
					catch (SQLException e) {
						System.out.println("Issue with contacting government server. Please contact system administartor.");

					}
					catch (ClassNotFoundException e) {
						System.out.println("Issue with contacting government server. Please contact system administartor.");
					}
				}
				else {
					//print a message showing invalid input with respect to result
					System.out.println("Invalid input.");
				}
			}
			//user opted to identify the number of large gatherings on a given day
			else if(choice==six) {
				int date=error;		//date of large gathering
				int minSize=error;	//minimum size of contacts for gatherings to have
				int minTime=error;	//minimum time of duration contacts should be holding for a large gathering
				float density;		//large gatherings needs to have a density greater than this density
				int result=0;		//end result to store number of large gatherings on a given date
				
				System.out.println("Enter the date as number of days from 01-01-2020 on which you would like to find the number of large gatherings.\n");
				date=scanner.nextInt();			//read date on which large gatherings needs to be computed
				scanner.nextLine();
				System.out.println("Enter the minimum size of the gathering.\n");
				minSize=scanner.nextInt();		//read the minimum size of a gathering
				scanner.nextLine();
				System.out.println("Enter the minimum time of the large gathering.\n");
				minTime=scanner.nextInt();		//read the minimum duration in a large gathering contacts
				scanner.nextLine();
				System.out.println("Enter the minimum density of the large gathering.\n");
				density=scanner.nextFloat();	//read density 
				scanner.nextLine();
				
				try {
					//call method 'findGatherings' with necessary attributes to get the number of large gatherings
					result=government.findGatherings(date, minSize, minTime, density);
					System.out.println("Number of lage gatherigs = "+result);
				} 
				//in case of any exceptions, print the appropriate message
				catch (InvalidInputException e) {
					System.out.println(e.getMessage()+" Please try again after fixing the issue.");
				} 
				catch (ClassNotFoundException e) {
					System.out.println("Issue with contacting government server. Please contact system administartor.");
				} 
				catch (SQLException e) {
					System.out.println("Issue with contacting government server. Please contact system administartor.");
				}
				
			}
			//exit in case of choice = 7
			else if(choice==seven) {
				System.out.println("Exiting...");
			}
			else{
				System.out.println("Invalid input.");
			}
		}while(choice!=seven);
		
		scanner.close();
	}
}