import static org.junit.Assert.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import org.junit.jupiter.api.Test;

/*
Owner		:	Vamsi Krishna Utla
Student ID	:	B00870632
Email		:	vm271757@dal.ca
Date		:	10-12-2020	
Purpose		:	This file/class is used to define the test cases in order to test the functionality of classes MobileDevice and Government for the purpose of contact tracing of COVID-19
*/


/*this class lists down 11 test cases that cover different functionalities of classes MobileDevice and Government
 *the 11 test cases are derived from the set of test cases from external documentation
 *some test cases may provide a different output/fail as they depend on the data present in the database  
 */
class JUnitTestCases {

	//test case designed for testing the creation of government object (server).
	@Test
	void test_create_government_object() {
		Government government=null;		//initially declare the government object as null
		final String path="C:\\config.txt";	//the path can be updated based on the requirement
		
		try {
			government=new Government(path);	//initialize the government object
			assertNotNull("Failed: Government object not created.",government);	//test to check if government object is created successfully or not
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | IOException | SQLException e) {
			System.out.println("Exception occured while creating government object");
		}
		
		
	}
	
	//test case designed for testing the creation of mobile device object. 
	@Test
	void test_create_mobileDevice_object() {
		Government government=null;		//initially declare the government object as null
		
		try {
			government=new Government("C:\\config.txt");	//initialize the government object
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | IOException | SQLException e) {
			System.out.println("Exception occured while creating government object");
		}
		
		MobileDevice mobileDevice_one=null;	//initially declare the mobileDevice object as null
		
		try {
			mobileDevice_one=new MobileDevice("C:\\m1.txt",government);	//initialize the object
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | IOException e) {
			System.out.println("Unable to register mobile device.");
		}
		
		//test to check if mobileDevice object is created successfully or not
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_one);
	}

	//this test case is used for testing the scenario when contact(s) from a mobile device is added and synced with government server.
	@Test
	void test_add_contacts_and_sync() {
		Government government=null;		//initially declare the government object as null
		
		try {
			government=new Government("C:\\config.txt");	//initialize the government object
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | IOException | SQLException e) {
			System.out.println("Exception occured while creating government object");
		}
		
		//declare five new mobile devices
		MobileDevice mobileDevice_one=null;
		MobileDevice mobileDevice_two=null;
		MobileDevice mobileDevice_three=null;
		MobileDevice mobileDevice_four=null;
		MobileDevice mobileDevice_five=null;
		
		//declare the paths for five new mobile devices (each acts as a configuration file to separate mobile device)
		final String m1_path="C:\\m1.txt";
		final String m2_path="C:\\m2.txt";
		final String m3_path="C:\\m3.txt";
		final String m4_path="C:\\m4.txt";
		final String m5_path="C:\\m5.txt";
		
		final int date=345;		//set date to 345
		
		try {
			
			//initialize the five mobile devices
			mobileDevice_one=new MobileDevice(m1_path,government);
			mobileDevice_two=new MobileDevice(m2_path,government);
			mobileDevice_three=new MobileDevice(m3_path,government);
			mobileDevice_four=new MobileDevice(m4_path,government);
			mobileDevice_five=new MobileDevice(m5_path,government);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | IOException e) {
			System.out.println("Unable to register mobile devices.");
		}
		
		//check to see if the five mobile devices have been created successfully or not
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_one);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_two);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_three);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_four);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_five);
		
		
		if(mobileDevice_one!=null) {
			
			//add contacts to first mobile device 
			mobileDevice_one.recordContact(mobileDevice_two.deviceHash, date, 10);		//add second mobile device as a contact to first mobile device
			mobileDevice_one.recordContact(mobileDevice_three.deviceHash, date-1, 1);	//add third mobile device as a contact to first mobile device
			mobileDevice_one.recordContact(mobileDevice_four.deviceHash, date+1, 27);	//add fourth mobile device as a contact to first mobile device
			mobileDevice_one.recordContact(mobileDevice_five.deviceHash, date+3, 100);	//add fifth mobile device as a contact to first mobile device
			try {
				//synchronize the first mobile device's contact details to government database (no positive tests)
				assertFalse("Invalid outcome.",mobileDevice_one.synchronizeData());
			} 
			//in case of any exceptions, print the appropriate message
			catch (InvalidInputException | ClassNotFoundException | IOException | SQLException | ParseException e) {
				System.out.println("Unable to sync contacts");
			}
		}
	}
	
	/*
	 * this test case is used for testing the scenario when contact(s) from a mobile device is added 
	 * and synced with government server where one or more of the contacts have been tested positive within the 
	 * 14 days period from the contact. The outcome may vary if you are trying to sync for the first time or not.
	 */
	@Test
	void test_add_contacts_and_sync_with_positive_result() {
		Government government=null;		//initially declare the government object as null
		
		try {
			government=new Government("C:\\config.txt");		//initialize the government object
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | IOException | SQLException e) {
			System.out.println("Exception occured while creating government object");
		}
		
		//declare three new mobile devices
		MobileDevice mobileDevice_one=null;
		MobileDevice mobileDevice_six=null;
		MobileDevice mobileDevice_seven=null;
		
		//declare the paths for three new mobile devices (each acts as a configuration file to separate mobile device)
		final String m1_path="C:\\m1.txt";
		final String m6_path="C:\\m6.txt";
		final String m7_path="C:\\m7.txt";
		final int date=340;		//initialize date as 340
		
		try {
			
			//initialize the three mobile devices
			mobileDevice_one=new MobileDevice(m1_path,government);
			mobileDevice_six=new MobileDevice(m6_path,government);
			mobileDevice_seven=new MobileDevice(m7_path,government);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | IOException e) {
			System.out.println("Unable to register mobile devices.");
		}
		
		//check to see if the three mobile devices have been created successfully or not
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_one);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_six);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_seven);
		
		if(mobileDevice_one!=null) {
			//add contacts to first mobile device 
			mobileDevice_one.recordContact(mobileDevice_six.deviceHash, date, 9);		//add second mobile device as a contact to first mobile device
			mobileDevice_one.recordContact(mobileDevice_seven.deviceHash, date-1, 37);	//add third mobile device as a contact to first mobile device
			try {
				if(mobileDevice_seven!=null) {
					mobileDevice_six.positiveTest("test_003");				//report a positive test case to second mobile device
					
					//report test cases to government
					government.recordTestResult("test_003", 340, true);		
					government.recordTestResult("test_004", 341, false);
					
					//synchronize the second mobile device's contact details and positive tests to government database
					mobileDevice_six.synchronizeData();
					
					//synchronize the third mobile device's contact details and positive tests to government database
					mobileDevice_seven.synchronizeData();
				}
				
				//synchronize the first mobile device's contact details to government database (no positive tests for first mobile device)
				assertTrue("Invalid outcome.",mobileDevice_one.synchronizeData());
			} 
			//in case of any exceptions, print the appropriate message
			catch (InvalidInputException | ClassNotFoundException | IOException | SQLException | ParseException e) {
				System.out.println("Unable to sync contacts");
			}
		}
	}
	
	/*this test case is designed for testing the scenario when multiple syncs are performed consecutively
	 *the outcome may vary if you are trying to sync for the first time or not.
	 */
	@Test
	void test_consecutive_sync() {
		Government government=null;		//initially declare the government object as null
		
		try {
			government=new Government("C:\\config.txt");	//initialize the government object
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | IOException | SQLException e) {
			System.out.println("Exception occured while creating government object");
		}
		
		//declare two new mobile devices
		MobileDevice mobileDevice_one=null;
		MobileDevice mobileDevice_eight=null;
		
		//declare the paths for two new mobile devices (each acts as a configuration file to separate mobile device)
		final String m1_path="C:\\m1.txt";
		final String m8_path="C:\\m8.txt";
		final int date=343;		//initialize date as 343
		
		try {
			
			//initialize the two mobile devices
			mobileDevice_one=new MobileDevice(m1_path,government);
			mobileDevice_eight=new MobileDevice(m8_path,government);
			
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | IOException e) {
			System.out.println("Unable to register mobile devices.");
		}
		
		//check to see if the two mobile devices have been created successfully or not
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_one);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_eight);
		
		if(mobileDevice_one!=null) {
			
			//add contact to first mobile device
			mobileDevice_one.recordContact(mobileDevice_eight.deviceHash, date+1, 90);		//add second mobile device as a contact to first mobile device
			try {
				if(mobileDevice_eight!=null) {
					//report test cases to government
					government.recordTestResult("test_005", 342, true);
					mobileDevice_eight.positiveTest("test_005");	//report a positive test case to second mobile device	
				}
				
				//synchronize the first mobile device's contact details to government database consecutively
				assertTrue("Invalid outcome.",mobileDevice_one.synchronizeData());
				assertFalse("Invalid outcome.",mobileDevice_one.synchronizeData());
			} 
			//in case of any exceptions, print the appropriate message
			catch (InvalidInputException | ClassNotFoundException | IOException | SQLException | ParseException e) {
				System.out.println("Unable to sync contacts");
			}
		}
	}
	
	//this test case is used for testing the scenario where same contact is made on different parts of the day either with same duration or a different duration.
	@Test
	void test_multiple_contacts_same_day() {
		Government government=null;		//initially declare the government object as null
		
		try {
			government=new Government("C:\\config.txt");		//initialize the government object
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | IOException | SQLException e) {
			System.out.println("Exception occured while creating government object");
		}
		
		//declare two new mobile devices
		MobileDevice mobileDevice_one=null;
		MobileDevice mobileDevice_eight=null;
		
		//declare the paths for two new mobile devices (each acts as a configuration file to separate mobile device)		
		final String m1_path="C:\\m1.txt";
		final String m8_path="C:\\m8.txt";
		final int date=340;	//initialize date as 340
		
		
		try {
			//initialize the two mobile devices
			mobileDevice_one=new MobileDevice(m1_path,government);
			mobileDevice_eight=new MobileDevice(m8_path,government);
			
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | IOException e) {
			System.out.println("Unable to register mobile devices.");
		}
		
		if(mobileDevice_one!=null) {
			
			//add same contact to first mobile device on same day with same duration
			mobileDevice_one.recordContact(mobileDevice_eight.deviceHash, date, 90);
			mobileDevice_one.recordContact(mobileDevice_eight.deviceHash, date, 90);
			try {
				assertFalse("Invalid outcome.",mobileDevice_one.synchronizeData());
			} 
			//in case of any exceptions, print the appropriate message
			catch (InvalidInputException | ClassNotFoundException | IOException | SQLException | ParseException e) {
				System.out.println("Unable to sync contacts");
			}
		}	
	}
	
	//this test case is used for testing the different input validations for class MobileDevice.
	@Test
	void test_input_validations_mobile_device() {
		
		Government government=null;		//declare a government object
		String m1_path="C:\\m1.txt";	//declare the path of configuration file for creating mobile device
		MobileDevice m1=null;	//declare a mobile device
		final int date=340;		//initialize date as 340
		final int duration=30;	//initialize duration as 30
		
		try {
			government=new Government("C:\\config.txt");		//initialize the government object
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | IOException | SQLException e) {
			System.out.println("Exception occured while creating government object");
		}
		
		//below block of code is used for testing when an empty string is passed as configuration file for creating a mobile device object
		try {
			m1=new MobileDevice("",government);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		} 
		catch (IOException e) {
			String message="null";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when a null is passed as configuration file for creating a mobile device object
		try {
			m1=new MobileDevice(null,government);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		} 
		catch (IOException e) {
			String message="null.";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when an null is passed as government object for creating a mobile device object
		try {
			m1=new MobileDevice(m1_path,null);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Government object passed is null!";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		} 
		catch (IOException e) {
			String message="null";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when an empty string is passed as contact hash for recording a contact to a mobile device
		try {
			m1=new MobileDevice(m1_path,government);
			m1.recordContact("", date, duration);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Indiviudal details are null!";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		} 
		catch (IOException e) {
			String message="null";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
			
		//below block of code is used for testing when a string with special characters is passed as contact hash for recording a contact to a mobile device
		//below block of code is used for testing when 0 is passed as date for recording a contact to a mobile device
		try {
			m1=new MobileDevice(m1_path,government);
			m1.recordContact("@abc", 0, duration);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid date!";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		} 
		catch (IOException e) {
			String message="Indiviudal details are null!";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when 0 is passed as duration for recording a contact to a mobile device
		try {
			m1=new MobileDevice(m1_path,government);
			m1.recordContact("@abc", date, 0);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid duration!";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		} 
		catch (IOException e) {
			String message="Indiviudal details are null!";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when null is passed as test hash for recording a positive test to a mobile device
		try {
			m1=new MobileDevice(m1_path,government);
			m1.positiveTest(null);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid testHash!";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		} 
		catch (IOException e) {
			String message="Invalid testHash!";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when empty string is passed as test hash for recording a positive test to a mobile device
		try {
			m1=new MobileDevice(m1_path,government);
			m1.positiveTest("");
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid testHash!";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch (IOException e) {
			String message="Invalid testHash!";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
	}
	
	//this test case is used for testing the different input validations for class Government.
	@Test
	void test_input_validations_government() {
		Government government=null;		//declare a government object
		String empty="C:\\m9.txt";		//declare a path where configuration file is empty
		String path="C:\\config.txt";	//path for government's configuration file
		final int date=340;				//initialize date as 340
		final int duration=30;			//initialize duration as 30
		
		//below block of code is used for testing when a null is passed as configuration file for creating a government object
		try {
			government=new Government(null);
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch ( ClassNotFoundException | IOException | SQLException e) {
			String message="Invalid configuration file.";
			System.out.println(e.getMessage());
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when an empty string is passed as configuration file for creating a government object
		try {
			government=new Government("");
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch ( ClassNotFoundException | IOException | SQLException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when aempty file is passed as configuration file for creating a government object
		try {
			government=new Government(empty);
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid configuration file!";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch ( ClassNotFoundException | IOException | SQLException e) {
			String message="Invalid configuration file.";
			System.out.println(e.getMessage());
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when a null is passed as initiator for syncing data to database
		try {
			government=new Government(path);
			government.mobileContact(null, "contactInfo");
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid initiator.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch ( ClassNotFoundException | IOException | SQLException | ParseException e) {
			String message="null";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
			
		//below block of code is used for testing when an empty string is passed as initiator for syncing data to database
		try {
			government=new Government(path);
			government.mobileContact("", "contactInfo");
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid initiator.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch ( ClassNotFoundException | IOException | SQLException | ParseException e) {
			String message="Invalid configuration file.";
			System.out.println(e.getMessage());
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when a null is passed as contact info and a string with special characters is passed as initiator for syncing data to database
		try {
			government=new Government(path);
			government.mobileContact("@abc", null);
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid contactInfo.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch ( ClassNotFoundException | IOException | SQLException | ParseException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when an empty string is passed as contact info for syncing data to database
		try {
			government=new Government(path);
			government.mobileContact("@abc", "");
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid contactInfo.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch ( ClassNotFoundException | IOException | SQLException | ParseException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when a null is passed as test hash and true as result for recording a test to database
		try {
			government=new Government(path);
			government.recordTestResult(null, date, true);
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid test hash.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch (ClassNotFoundException | IOException | SQLException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when an empty string is passed as test hash for recording a test to database
		try {
			government=new Government(path);
			government.recordTestResult("", date, false);
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid test hash.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch (ClassNotFoundException | IOException | SQLException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when a special character is passed as test hash, a negative value is passed as date and false result for recording a test to database
		try {
			government=new Government(path);
			government.recordTestResult("@abc", -1, false);
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid date.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch (ClassNotFoundException | IOException | SQLException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when a negative value is passed as date for finding large gatherings
		try {
			government=new Government(path);
			government.findGatherings(-1, 0, 0, 0);
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid date.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch (ClassNotFoundException | IOException | SQLException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when a negative value is passed as minTime for finding large gatherings
		try {
			government=new Government(path);
			government.findGatherings(date, 0, -1, duration);
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid minimum time.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch (ClassNotFoundException | IOException | SQLException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when a negative value is passed as minSize for finding large gatherings
		try {
			government=new Government(path);
			government.findGatherings(date, -1, 0, duration);
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid minimum size.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch (ClassNotFoundException | IOException | SQLException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
		
		//below block of code is used for testing when a negative value is passed as density for finding large gatherings
		try {
			government=new Government(path);
			government.findGatherings(date, 0, 0, -1);
		} 
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException e) {
			String message="Invalid density.";
			assertEquals("Test case failed.",e.getString(),message);	//check to see if required exception with necessary information has been received or not
		}
		catch (ClassNotFoundException | IOException | SQLException e) {
			String message="Invalid configuration file.";
			assertEquals("Test case failed.",e.getMessage(),message);	//check to see if required exception with necessary information has been received or not
		}
	}
	
	//this test case is designed for testing a scenario where a contact has multiple positive tests recorded.
	@Test
	void test_multiple_positive_tests() {
		Government government=null;		//declare government object
		String path="C:\\config,txt";	//declare a string of input configuration file path for government's object
		String m1_path="C:\\m1.txt";	//configuration file path for first mobile device
		String m8_path="C:\\m8.txt";	//configuration file path for second mobile device
		int date=345;					//initialize date as 345
		int duration=45;				//initialize duration as 45
		MobileDevice m1=null,m8=null;	//declare two mobile devices
		
		try {
			government=new Government(path);	//initialize the government object
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | IOException | SQLException e) {
			System.out.println("Test failed.");
		}
		
		try {
			
			//initialize two mobile devices
			m1=new MobileDevice(m1_path,government);
			m8=new MobileDevice(m8_path,government);
			
			//record contacts
			m1.recordContact(m8.deviceHash, date, duration);
			
			//record multiple positive tests to same mobile device
			m8.positiveTest("xxx03");
			m8.positiveTest("xxx05");
			
			//record tests to government server
			government.recordTestResult("xxx03", 320, true);
			government.recordTestResult("xxx05", 344, true);
			
			//sync mobile device
			m8.synchronizeData();
			assertTrue("Test failed.", m1.synchronizeData());	//check to see if multiple positive tests impact the output
	
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | IOException | SQLException | ParseException e) {
			System.out.println("Test failed.");
		} 
		
	}
	
	//this test case is designed for testing a scenario where more than one large gathering exists on the given date.
	@Test
	void test_for_gatherings_one() {
		Government government=null;		//declare government object
		
		try {
			government=new Government("C:\\config.txt");	//initialize the government object
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | IOException | SQLException e) {
			System.out.println("Exception occured while creating government object");
		}
		
		//declare five mobile devices
		MobileDevice mobileDevice_one=null;
		MobileDevice mobileDevice_two=null;
		MobileDevice mobileDevice_three=null;
		MobileDevice mobileDevice_four=null;
		MobileDevice mobileDevice_five=null;
		
		//declare the paths for five new mobile devices (each acts as a configuration file to separate mobile device)
		String m1_path="C:\\m1.txt";
		String m2_path="C:\\m2.txt";
		String m3_path="C:\\m3.txt";
		String m4_path="C:\\m4.txt";
		String m5_path="C:\\m5.txt";
		int date=346;
		
		try {
			//initialize five mobile devices
			mobileDevice_one=new MobileDevice(m1_path,government);
			mobileDevice_two=new MobileDevice(m2_path,government);
			mobileDevice_three=new MobileDevice(m3_path,government);
			mobileDevice_four=new MobileDevice(m4_path,government);
			mobileDevice_five=new MobileDevice(m5_path,government);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | IOException e) {
			System.out.println("Unable to register mobile devices.");
		}
		
		//check to see if the five mobile devices have been created successfully or not
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_one);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_two);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_three);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_four);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_five);
		
		if(mobileDevice_one!=null) {
			//record contacts for first mobile device
			mobileDevice_one.recordContact(mobileDevice_two.deviceHash, date, 10);
			mobileDevice_one.recordContact(mobileDevice_three.deviceHash, date, 100);
			mobileDevice_one.recordContact(mobileDevice_four.deviceHash, date, 27);
			mobileDevice_one.recordContact(mobileDevice_five.deviceHash, date, 100);
			try {
				assertFalse("Invalid outcome.",mobileDevice_one.synchronizeData());		//sync first mobile device
			} 
			//in case of any exceptions, print the appropriate message
			catch (InvalidInputException | ClassNotFoundException | IOException | SQLException | ParseException e) {
				System.out.println("Unable to sync contacts");
			}
		}
		try {
			
			//check to see if there are large gatherings found with given details
			int x=government.findGatherings(date, 1, 1, 0);
			assertEquals("Invalid number of gatherings.",x,1);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | SQLException e) {
			System.out.println("Tes case failed.");
		}
	}
	
	//this test case is designed for testing a scenario where no large gathering exists on the given date.
	@Test
	void test_for_gatherings_two() {
		Government government=null;		//declare government object
		
		try {
			government=new Government("C:\\config.txt");	//initialize the government object
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | IOException | SQLException e) {
			System.out.println("Exception occured while creating government object");
		}
		
		//declare five mobile devices
		MobileDevice mobileDevice_one=null;
		MobileDevice mobileDevice_two=null;
		MobileDevice mobileDevice_three=null;
		MobileDevice mobileDevice_four=null;
		MobileDevice mobileDevice_five=null;
		
		//declare the paths for five new mobile devices (each acts as a configuration file to separate mobile device)
		String m1_path="C:\\m1.txt";
		String m2_path="C:\\m2.txt";
		String m3_path="C:\\m3.txt";
		String m4_path="C:\\m4.txt";
		String m5_path="C:\\m5.txt";
		int date=346;
		
		try {
			
			//initialize five mobile devices
			mobileDevice_one=new MobileDevice(m1_path,government);
			mobileDevice_two=new MobileDevice(m2_path,government);
			mobileDevice_three=new MobileDevice(m3_path,government);
			mobileDevice_four=new MobileDevice(m4_path,government);
			mobileDevice_five=new MobileDevice(m5_path,government);
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | IOException e) {
			System.out.println("Unable to register mobile devices.");
		}
		
		//check to see if the five mobile devices have been created successfully or not
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_one);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_two);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_three);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_four);
		assertNotNull("Failed: MobileDevice object not created.",mobileDevice_five);
		
		if(mobileDevice_one!=null) {
			//record contacts for first mobile device
			mobileDevice_one.recordContact(mobileDevice_two.deviceHash, date, 10);
			mobileDevice_one.recordContact(mobileDevice_three.deviceHash, date, 100);
			mobileDevice_one.recordContact(mobileDevice_four.deviceHash, date, 27);
			mobileDevice_one.recordContact(mobileDevice_five.deviceHash, date, 100);
			try {
				assertFalse("Invalid outcome.",mobileDevice_one.synchronizeData());		//sync first mobile device
			} 
			//in case of any exceptions, print the appropriate message
			catch (InvalidInputException | ClassNotFoundException | IOException | SQLException | ParseException e) {
				System.out.println("Unable to sync contacts");
			}
		}
		try {
			
			//check to see if there are no large gatherings found with given details
			int x=government.findGatherings(date, 4, 50, 4);
			assertEquals("Invalid number of gatherings.",x,0);	
		}
		//in case of any exceptions, print the appropriate message
		catch (InvalidInputException | ClassNotFoundException | SQLException e) {
			System.out.println("Test case failed.");
		}
	}
}