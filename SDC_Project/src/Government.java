import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.Date;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;
import java.text.*;

/*
Owner		:	Vamsi Krishna Utla
Student ID	:	B00870632
Email		:	vm271757@dal.ca
Date		:	29-11-2020	
Purpose		:	This file/class is used to define the functionality of the government server that is used for the purpose of contact tracing of COVID-19
*/

/*this class is used as a government server for writing details associated to contacts and tests to database
 *usually handled by the government body that has access to database
 *this class reports if a user has been in contact with any other user who has been tested positive within 14 days of their contact
 *this class is also used for finding the number of large gatherings that are recorded on a given day
 *uses configuration file which consists of details for connecting to database and additional parameters such as contacts and tests table names
 *uses multiple data structures such as Lists, Sets and Maps to carry out various operations
 *data is directly written to database to avoid data loss in case if the program crashes in between
 *maintains separate connections for each method to avoid loss of connection as government server will be running 24/7
 */
public class Government {

	private String domainName="";								//used for storing the domain name of the database that is provided through configuration file
	private String username="";									//used for storing the user name to connect to database that is provided through configuration file
	private String password="";									//used for storing the password to connect to database that is provided through configuration file
	private String dbName="";									//used for storing the database name of the database that is provided through configuration file
	private String contactsTable="";							//variable for storing the table name of contacts table of the database
	private String testsTable="";								//variable for storing the table name of tests table of the database
	
	private final String contactColumnOne="contact_from";		//variable for storing the column name of the first column in contact's table of the database (declared as final to avoid further update)
	private final String contactColumnTwo="contact_to";			//variable for storing the column name of the second column in contact's table of the database (declared as final to avoid further update)
	private final String contactColumnThree="date";				//variable for storing the column name of the third column in contact's table of the database (declared as final to avoid further update)
	private final String contactColumnFour="duration";			//variable for storing the column name of the fourth column in contact's table of the database (declared as final to avoid further update)
	private final String contactColumnFive="flag";				//variable for storing the column name of the fifth column in contact's table of the database (declared as final to avoid further update)
	private final String testColumnOne="contact";				//variable for storing the column name of the first column in test's table of the database (declared as final to avoid further update)
	private final String testColumnTwo="test_hash";				//variable for storing the column name of the second column in test's table of the database (declared as final to avoid further update)
	private final String testColumnThree="date";				//variable for storing the column name of the third column in test's table of the database (declared as final to avoid further update)
	private final String testColumnFour="result";				//variable for storing the column name of the fourth column in test's table of the database (declared as final to avoid further update)
	private final String testColumnFive="verification_status";	//variable for storing the column name of the fifth column in test's table of the database (declared as final to avoid further update)
		
	private boolean connectionFlag=true;						//flag used for verifying if connection to database has been successfully setup or not
	private boolean domainNameFlag=false;						//flag used for verifying if domain name has been read successfully or not from the configuration file
	private boolean usernameFlag=false;							//flag used for verifying if user name has been read successfully or not from the configuration file
	private boolean passwordFlag=false;							//flag used for verifying if password has been read successfully or not from the configuration file
	private boolean dbNameFlag=false;							//flag used for verifying if database name has been read successfully or not from the configuration file
	private boolean contactsTableFlag=false;					//flag used for verifying if contacts table name has been read successfully or not from the configuration file
	private boolean testsTableFlag=false;						//flag used for verifying if test table name has been read successfully or not from the configuration file
	
	private final int zero=0;									//constant for value '0'
	private final int one=1;									//constant for value '1'
	private final int two=2;									//constant for value '2'
	private final int three=3;									//constant for value '3'
	private final int four=4;									//constant for value '4'
	private final int five=5;									//constant for value '5'
	private final int fifteen=15;								//constant for value '15'
	private final int fourteen=14;								//constant for value '14'
	private final int negFourteen=-14;							//constant for value '-14'
	private final boolean true_var=true;						//constant for boolean value - true
	private final boolean false_var=false;						//constant for boolean value - false
	
	
	/*constructor used for setting up the configuration required for connecting to database with the help of given 'configurationFile'
	 *configuration file should have the parameters database (domain name), user (user name), password, dbName (database name), contactsTable (contact's table name), testsTable (test's table name) in the given sequence inside the configuration file
	 *throws exceptions in case of any issues
	 *connection flag is set to false in case of unsuccessful connection to database so that this flag can be used before executing any other methods
	 */
	Government(String configurationFile) throws InvalidInputException, IOException, ClassNotFoundException, SQLException{
		
		String line;						//variable of type String used for storing data of each line when the configuration file is being read
		String[] arguments;					//array of String used for storing key-value of parameters (i.e., domain name, user name, password, database name, contacts table name and tests table name) given in the configuration file  
		int exceptionFlag=0;				//flag used for differentiating if there is an issue related to given domain name or given database name
		Connection connect=null;			//variable of type Connection used for setting up session between this class and given database
		Statement statement=null;			//variable of type Statement responsible for running queries against the database

		//throw custom exception of type InvalidInputException in case the configuration file is given as null or empty string 
		if(configurationFile==null || configurationFile.length()==zero) {
			throw new InvalidInputException("Invalid configuration file.");
		}
		
		//initialize BufferedReader to read data from the given configuration file
		//use file reader though configuration file is represented as a String
		BufferedReader buffReader=new BufferedReader(new FileReader(configurationFile));
		line=buffReader.readLine();			//read first line
		
		//block of code that is used to move to the first line of the file by avoiding empty lines in the beginning of the given configuration file
		while(line!=null && line.equals("")) {
			line=buffReader.readLine();
		}
		
		//after reading all the empty lines (if any), if the line reaches null, then the configuration file is empty and invalid
		//close the  BufferedReader instance and throw a custom exception indicating invalid configuration file
		if(line==null) {
			buffReader.close();													//close buffered reader
			throw new InvalidInputException("Invalid configuration file!");		//throw exception
		}
		
		//if the data starts, read each line and determine if it corresponds to any parameter that can be used for configuring the details used for connecting to database
		//parse through each line until all the required parameters are read or if the file reached end of data
		while((!usernameFlag || !passwordFlag || !domainNameFlag|| !dbNameFlag || !contactsTableFlag || !testsTableFlag) && line!=null) {
			arguments=line.split("\\s");			//split the data in the line by using "<space>" as a separator
			
			//if line contains more than two or less than two data segments separated by "<space>", consider that line as invalid and move to next line
			if(arguments.length!=two) {
				line=buffReader.readLine();
			}
			else {
				//line contains two segments of the data as per the format separated by "<space>"
				
				//if the line represents the database (domain name), read it
				if(arguments[zero].equalsIgnoreCase("database")) {
					domainName=arguments[one];		//read data into required attribute
					domainNameFlag=true;			//set the flag to true indicating domain name has been read successfully
				}
				//if the line represents the user name, read it
				else if(arguments[zero].equalsIgnoreCase("user")) {
					username=arguments[one];		//read data into required attribute
					usernameFlag=true;				//set the flag to true indicating user name has been read successfully
				}
				//if the line represents the password, read it
				else if(arguments[zero].equalsIgnoreCase("password")) {
					password=arguments[one];		//read data into required attribute
					passwordFlag=true;				//set the flag to true indicating password has been read successfully
				}
				//if the line represents the database name, read it
				else if(arguments[zero].equalsIgnoreCase("dbName")) {
					dbName=arguments[one];			//read data into required attribute
					dbNameFlag=true;				//set the flag to true indicating database name has been read successfully
				}
				//if the line represents name of the contact's table, read it
				else if(arguments[zero].equalsIgnoreCase("contactsTable")) {
					contactsTable=arguments[one];	//read data into required attribute
					contactsTableFlag=true;			//set the flag to true indicating name of the contact's table has been read successfully
				}
				//if the line represents name of the test's table, read it
				else if(arguments[zero].equalsIgnoreCase("testsTable")) {
					testsTable=arguments[one];		//read data into required attribute
					testsTableFlag=true;			//set the flag to true indicating name of the test's table has been read successfully
				}
				line=buffReader.readLine();			//move to next line
				
			}
		}
		
		//if any of the parameter is not read, close the buffered reader instance and throw custom exception indicating invalid format of the configuration file
		if(!usernameFlag || !passwordFlag || !domainNameFlag || !dbNameFlag || !contactsTableFlag || !testsTableFlag) {
			buffReader.close();
			throw new InvalidInputException("Invalid configuration file!");
		}
		
		//load JDBC driver class during run time
		Class.forName("com.mysql.cj.jdbc.Driver");	
		
		try {
			connect=DriverManager.getConnection(domainName, username, password);	//try to get a connection to database using given parameters
			exceptionFlag=1;														//flag set to true indicating no issues with domain name
			statement=connect.createStatement();									//connect to create statement and execute queries  
			statement.execute("use "+dbName+";");									//verify if given database can be used or not
		}
		catch(Exception e) {
			connectionFlag=false;		//update flag to false indicating database connection is not possible with the given parameters in the configuration file												
			
			//throw custom exception with required details in case there is an issue with domain name
			if(exceptionFlag==0) {	
				throw new InvalidInputException("Unable to connect to "+domainName+" with provided details.");
			}
			//throw custom exception with required details in case there is an issue with database name
			else {
				throw new InvalidInputException("Invalid "+dbName);
			}
		}
		//close all the necessary streams
		finally {
			buffReader.close();		//close buffered reader
			if(connect!=null) {
				connect.close();		//close connection
			}
			if(statement!=null) {
				statement.close();		//close statement 
			}
		}
	}
	
	/*this method is generally called from synchronizeData() method of MobileDevice class to sync the contact details and positive test details with government server
	 *contact details and positive tests are included in the 'contactInfo' which follows an XML format
	 *it is also used for letting the caller of this method know whether they have been in contact with any of the person who has been tested positive for COVID-19 within 14 days of their contact
	 *uses two array lists as data structures to store contacts and positive tests before writing them to database
	 *uses a separate connection to communicate with database
	 *throws multiple exceptions in case of any issues
	 *identifies the mobile device with the help of 'initiator' that is passed as a parameter to this method
	 *consists of three sections -> 1. read the data 	2. write data to database	3. identify the result    
	 */
	boolean mobileContact(String initiator, String contactInfo) throws InvalidInputException,IOException, ClassNotFoundException, SQLException, ParseException {
	
		ArrayList<Contact> contacts=new ArrayList<Contact>();		//array list of type Contact used as a data structure for storing contacts in the given sequential order
		ArrayList<String> testHashes=new ArrayList<String>();		//array list of type Contact used as a data structure for storing positive tests (test hashes) in the given sequential order
		
		ResultSet resultSet=null;		//used for storing the results after a select query is executed
		Connection connect=null;		//variable of type Connection used for setting up session between this class and given database
		Statement statement=null;		//variable of type Statement responsible for running queries against the database
		
		String line="";									//variable of type String used for storing data of each line when the data is being read
		
		final String tab="\t";									//string constant for a tab character
		final String version="<?xml version=";					//string constant for a version starting tag
		final String contactsOpenTag="<Contacts>";				//string constant for a Contacts open tag i.e., <Contacts>
		final String contactOpenTag="<Contact>";				//string constant for a Contact open tag i.e., <Contact>
		final String contactHashOpenTag="<ContactHash>";		//string constant for a ContactHash open tag i.e., <ContactHash>
		final String dateOpenTag="<Date>";						//string constant for a Date open tag i.e., <Date>
		final String durationOpenTag="<Duration>";				//string constant for a Duration open tag i.e., <Duration>
		final String contactHashCloseTag="</ContactHash>";		//string constant for a Contacts closing tag i.e., </Contacts>
		final String dateCloseTag="</Date>";					//string constant for a Date closing tag i.e., </Date>
		final String durationCloseTag="</Duration>";			//string constant for a Duration closing tag i.e., </Duration>
		final String testsOpenTag="<Tests>";					//string constant for a Tests open tag i.e., <Tests>		
		final String testOpenTag="<Test>";						//string constant for a Test open tag i.e., <Test>
		final String testsCloseTag="</Tests>";					//string constant for a Tests closing tag i.e., </Tests>	
		final String testCloseTag="</Test>";					//string constant for a Test closing tag i.e., </Test>
		final String contactsCloseTag="</Contacts>";			//string constant for a Contacts closing tag i.e., </Contacts>
		final String contactCloseTag="</Contact>";				//string constant for a Contact closing tag i.e., </Contact>
		
		String methodQuery="";					//string for storing the query that needs to be executed against the database
		String contactHash=null;				//variable for storing the contactHash i.e., unique identifier of a mobile device
		String date=null;						//variable used for storing the date related data
		final String true_string="true";		//string constant representing boolean value as true
		String duration=null;					//variable used for storing the duration related data
		String testHash=null;					//variable used for storing the testHash i.e., unique identifier of a test
		
		boolean result=false;					//variable used for storing end result
		
		boolean encodingFlag=false;				//flag to check if the current line consists of a version tag
		boolean validContactSTagFlag=false;		//flag to verify that the cursor is currently inside <Contacts> (used while reading data)
		boolean validContactTagFlag=false;		//flag to verify that the cursor is currently inside <Contact> (used while reading data)
		boolean validTestSTagFlag=false;		//flag to verify that the cursor is currently inside <Tests> (used while reading data)
		
		//variables used for calculating current date as number of days
		final String startDay="2020-01-01";						//string variable for string the start date
		Date firstDay;											//variable of type Date to store start/first date
		Date currDay;											//variable of type Date to store current date
		final String format="yyyy-MM-dd";						//variable used for specifying the date format
		DateFormat dateFormat=new SimpleDateFormat(format);		//variable of type DateFormat used for calculating the current date (by parsing the string) 
		long timeDifference;									//variable to store the time difference between start date and current date in milliseconds
		final long seconds=1000*60*60*24;						//constant for storing milliseconds seconds in a day
		int currentDate=-1;										//used for storing the current day as a number
	
		//if the connection flag that is set while creating a government object is false, throw custom exception with required details as processing is not possible without a proper connection to database 
		if(connectionFlag==false) {
			throw new InvalidInputException("Unable to connect to database with provided details.");
		}
		
		//check to see if the given initiator is null or empty string
		//if yes, throw custom exception with required details
		if(initiator==null || initiator.length()==zero) {
			throw new InvalidInputException("Invalid initiator.");
		}
		
		//check to see if the given contact info is null or empty string
		//if yes, throw custom exception with required details
		if(contactInfo==null || contactInfo.length()==zero) {
			throw new InvalidInputException("Invalid contactInfo.");
		}
		
		//initialize BufferedReader to read data from the given contactInfo
		//use string reader as contactInfo is represented as a string
		BufferedReader buffReader=new BufferedReader(new StringReader(contactInfo));
		line=buffReader.readLine();		//read first line
		
		//block of code that is used to move to the first line of the string by avoiding empty lines in the beginning of the given string
		while(line!=null && line.equals("")) {
			line=buffReader.readLine();
		}
		
		//after reading all the empty lines (if any), if the line reaches null, then the given string is empty and invalid
		//close the  BufferedReader instance and throw a custom exception indicating invalid contactInfo
		if(line==null) {
			buffReader.close();											//close buffered reader
			throw new InvalidInputException("Invalid contactInfo.");	//throw exception
		}
		
		//if the data starts, read each line and determine if it corresponds version tag (i.e., start of the XML data)
		//parse through each line until version tag is read or if the string reached end of data
		while(line!=null && !encodingFlag) {
			if(line.startsWith(version)) {		//check to see if the line starts with version tag
				encodingFlag=true;				//if yes, update encoding flag to true indicating version tag is read
			}
			line=buffReader.readLine();			//read next line
		}
		
		//if string reaches end of data, close buffered reader and throw custom exception indicating necessary details
		//note that though there are no contacts and tests, the string should at least consist of Contacts and Tests open and closed tags 
		if(line==null) {
			buffReader.close();											//close buffered reader
			throw new InvalidInputException("Invalid contactInfo.");	//throw custom exception
		}
		
		//below block of code calculates current date as a number
		firstDay=dateFormat.parse(startDay);					//convert start date stored as a string to a Date 
		currDay=new Date();										//initialize current date variable with current date
		timeDifference=currDay.getTime()-firstDay.getTime();	//identify the time difference in milliseconds between current date and first date
		currentDate=(int)(timeDifference/seconds);				//formula to get the number of the current date
		
		
		Class.forName("com.mysql.cj.jdbc.Driver");								//load JDBC driver class during run time
		connect=DriverManager.getConnection(domainName, username, password);	//try to get a connection to database using given parameters
		statement=connect.createStatement();									//connect to create statement and execute queries
		statement.execute("use "+dbName+";");									//use the given database 
		
		//read until current line (cursor) reaches end of string
		while(line!=null) {
			
			/*update validContactSTagFlag to true indicating that we're currently inside <Contacts> if the following criteria satisfies:
			 * 	1. line contains only ContactsOpenTag i.e., <Contacts>
			 * 	2. no previously open Contacts tag i.e., <Contacts> that has not been closed (to check if proper nesting has been followed or not)
			 *  3. the current line is not in the middle of any <Tests> tag
			 */
			if(line.contentEquals(contactsOpenTag) && !validContactSTagFlag && !validTestSTagFlag) {
				validContactSTagFlag=true;		//update flag to true
			}
			/*update validContactTagFlag to true indicating that we're currently inside <Contact> if the following criteria satisfies:
			 * 	1. line contains only ContactOpenTag i.e., <Contact>
			 * 	2. currently inside <Contacts> tag
			 *  3. no previously open Contact tag i.e., <Contact> that has not been closed (to check if proper nesting has been followed or not)
			 */
			else if(line.contentEquals(tab+contactOpenTag) && validContactSTagFlag && !validContactTagFlag) {
				validContactTagFlag=true;		//update flag to true
			}
			/*read contact hash if the following criteria satisfies:
			 * 	1. currently inside <Contacts> tag
			 * 	2. currently inside <Contact> tag
			 *  3. line starts with contact hash open tag i.e., <ContactHash>
			 */
			else if(validContactSTagFlag && validContactTagFlag && line.startsWith(tab+tab+contactHashOpenTag)) {
				int start=line.indexOf(contactHashOpenTag)+contactHashOpenTag.length();		//get the index of where contact hash open tag is ending
				int end=line.indexOf(contactHashCloseTag,start);							//get the start index of contact hash closing tag from start position
				
				//additional checks before recording the contact hash
				if(contactHash==null && line.startsWith(tab+tab+contactHashOpenTag) && line.endsWith(contactHashCloseTag)) {
					contactHash=line.substring(start, end);		//read contact hash based on the start and end indices of the current line
				}
			}
			/*read contact's date if the following criteria satisfies:
			 * 	1. currently inside <Contacts> tag
			 * 	2. currently inside <Contact> tag
			 *  3. line starts with date open tag i.e., <Date>
			 */
			else if(validContactSTagFlag && validContactTagFlag && line.startsWith(tab+tab+dateOpenTag)) {
				int start=line.indexOf(dateOpenTag)+dateOpenTag.length();		//get the index of where date open tag is ending
				int end=line.indexOf(dateCloseTag,start);						//get the start index of date closing tag from start position
				
				//additional checks before recording the contact's date
				if(date==null && line.startsWith(tab+tab+dateOpenTag) && line.endsWith(dateCloseTag)) {
					date=line.substring(start, end);	//read contact's date based on the start and end indices of the current line
				}
			}
			/*read contact's duration if the following criteria satisfies:
			 * 	1. currently inside <Contacts> tag
			 * 	2. currently inside <Contact> tag
			 *  3. line starts with duration open tag i.e., <Duration>
			 */
			else if(validContactSTagFlag && validContactTagFlag && line.startsWith(tab+tab+durationOpenTag)) {
				int start=line.indexOf(durationOpenTag)+durationOpenTag.length();	//get the index of where duration open tag is ending
				int end=line.indexOf(durationCloseTag,start);						//get the start index of duration closing tag from start position
				
				//additional checks before recording the contact's duration
				if(duration==null && line.startsWith(tab+tab+durationOpenTag) && line.endsWith(durationCloseTag)) {
					duration=line.substring(start, end);	//read contact's duration based on the start and end indices of the current line
				}
			}
			//if the current line is equal to contact closing tag i.e., </Contact>, update the flag indicating current contact has been read
			else if(line.contentEquals(tab+contactCloseTag)) {
				
				//add the current contact details such as contact hash, date and duration of contact to contact's array list 
				if(contactHash!=null && date!=null && duration!=null  && validContactTagFlag && validContactSTagFlag) {
					Contact contact=new Contact(contactHash,Integer.parseInt(date),Integer.parseInt(duration));		//create a new cntact with current contact details
					contacts.add(contact);		//add it to array list of contacts
				}
				validContactTagFlag=false;		//update flag indicating contact has been read
				contactHash=null;				//update contact hash to null for reading next contact
				date=null;						//update contact's date to null for reading next contact
				duration=null;					//update contact's duration to null for reading next contact
			}
			//if the current line is equal to contacts closing tag i.e., </Contacts>, update the flag indicating contacts have been read
			else if(line.contentEquals(contactsCloseTag)) {
				validContactSTagFlag=false;		//update flag
			}
			/*update validTestSTagFlag to true indicating that we're currently inside <Tests> if the following criteria satisfies:
			 * 	1. line contains only TestsOpenTag i.e., <Tests>
			 * 	2. no previously open Tests tag i.e., <Tests> that has not been closed (to check if proper nesting has been followed or not)
			 *  3. the current line is not in the middle of any <Contacts> tag
			 */
			else if(line.contentEquals(testsOpenTag) && !validContactSTagFlag && !validTestSTagFlag) {
				validTestSTagFlag=true;		//update the flag
			}
			//if the current line starts with test open tag i.e., <Test> and end with </Test>, read data and store it in tests array list
			else if(line.startsWith(tab+testOpenTag) && validTestSTagFlag) {
				int start=line.indexOf(testOpenTag)+testOpenTag.length();	//get the index of where test open tag is ending
				int end=line.indexOf(testCloseTag,start);					//get the start index of test closing tag from start position
				
				//additional checks before recording the positive test details
				if(testHash==null && line.startsWith(tab+testOpenTag) && line.endsWith(testCloseTag)) {
					testHash=line.substring(start, end);	//read the test hash of positive test result
					testHashes.add(testHash);				//add the test hash to array list of positive test hashes
					testHash=null;							//update test hash to null to read next tests
				}
			}
			//if the current line is equal to tests closing tag i.e., </Tests>, update the flag indicating tests have been read
			else if(line.contentEquals(testsCloseTag)) {
				validTestSTagFlag=false;		//update flag
			}
			line=buffReader.readLine();		//read next line
		}
		
		//check to see if the tags are properly nested or not
		//if not, throw custom exception with required details
		if(validContactSTagFlag || validContactTagFlag || validTestSTagFlag) {
			throw new InvalidInputException("Issues found in tags of stringInfo.");
		}
		
		//the below for loop is used for writing the contact details that are read above to database
		for(int i=0;i<contacts.size();i++) {
			if(!initiator.equals(contacts.get(i).getContactHash())) {
				boolean alreadyConsidered=false_var;						//flag to indicate the contact has newly been inserted to the contact's table
				boolean duplicate=false;									//flag to indicate a duplicate entry to the database contact's table
				
				//get the current contact's details from array list
				contactHash=contacts.get(i).getContactHash();				//get contact hash of the current contact
				date=Integer.toString(contacts.get(i).getDate());			//get contact date of the current contact
				duration=Integer.toString(contacts.get(i).getDuration());	//get contact duration of the current contact
				
				//query to get the records from contact's table if there is already a contact row existing between initiator and current contact
				String query="select * from "+contactsTable+" where "+contactColumnOne+"=\""+initiator+"\" and "+contactColumnTwo+"=\""+contactHash+"\";";
				resultSet=statement.executeQuery(query);		//execute query and store results in resultSet
				
				//if resultSet is empty, it indicates that there are no contact information with respect to initiator and current contact stored in the contact's table
				if(!resultSet.next()) {
					alreadyConsidered=true_var;		//update flag indicating a new record has been inserted
					
					//prepare insert statement with necessary details
					String internalQuery="insert into "+contactsTable+" values (\""+initiator+"\",\""+contactHash+"\","+date+","+duration+","+zero+");";
					statement.execute(internalQuery);		//execute above query to insert the data into contact's table
				}
				else {
					//records with respect to contact between initiator and current contact exists
					
					//query to get the details if the contact between initiator and current contacts exists with same date 
					query="select * from "+contactsTable+" where "+contactColumnOne+"=\""+initiator+"\" and "+contactColumnTwo+"=\""+contactHash+"\" and "+contactColumnThree+"="+date+";";
					resultSet=statement.executeQuery(query);		//execute above query and store results in resultSet
					
					//if no record is present, update the duplicate flag to false so that it can be inserted as a new record at the end of this loop
					if(!resultSet.next()) {
						duplicate=false_var;	//update duplicate flag to false
					}
					else {
						//records with contact between initiator and current contact exists in contact's table with same date
						
						resultSet=statement.executeQuery(query);	//execute the query again as the cursor cannot be shifted back (as the above 'if' block moved the cursor forward)
						
						//iterate through all the records
						while(resultSet.next()){
							duplicate=true_var;		//update the duplicate flag to true to avoid getting a new record created at end of this 'for' loop
							
							//if the record's date and current contact's date is equal, add the additional duration to existing duration in the table
							if(resultSet.getString(three).contentEquals(date)) {
								int totalDuration=resultSet.getInt(four)+Integer.parseInt(duration);	//get the updated duration in the given date
								
								//update the duration by using below update query
								String internalQuery="update "+contactsTable+" set "+contactColumnFour+"="+totalDuration+
															 " where "+contactColumnOne+"=\""+initiator+"\" and "+contactColumnTwo+"=\""+contactHash+"\" and "+contactColumnThree+"="+resultSet.getInt(three)+";";
								Statement s1=connect.createStatement();		//setup a new statement as we cannot use already running statement
								s1.execute(internalQuery);					//execute the update query
								s1.close();									//close statement s1
							}
						}
					}
				}
				resultSet=null;		//update the resultSet to null so that it can be reused 
				
				//if not a new contact or update to contact of same day, insert the new record to contact's table
				if(!duplicate && !alreadyConsidered) {
					
					//design the insert query with required details
					String internalQuery="insert into "+contactsTable+" values (\""+initiator+"\",\""+contactHash+"\","+date+","+duration+","+zero+");";
					Statement s1=connect.createStatement();		//create a new statement as we cannot use already running statement
					s1.execute(internalQuery);					//execute query
					s1.close();									//close statement s1
				}
			}
		}
		
		//the below for loop is used for writing the positive test details that are read above to database
		for(int i=0;i<testHashes.size();i++) {
			
			//design query to check if there is a test exists with same test hash in the tests table of the database
			String query="select * from "+testsTable+" where "+testColumnTwo+"=\""+testHashes.get(i)+"\";";
			resultSet=statement.executeQuery(query);	//execute query
			
			//if same test exists and it has not been verified i.e., a test record with no contact hash exists, then update the contact hash to the test record
			if(resultSet.next()) {
				resultSet=statement.executeQuery(query);	//execute the query again to point cursor before beginning
				
				//iterate through the resultSet
				while(resultSet.next()) {
					
					//if not verified
					if(resultSet.getInt(five)!=one) {
						
						//prepare update statement using string variable
						String internalQuery="update "+testsTable+" set "+testColumnOne+"=\""+initiator+"\","+testColumnFive+"="+one+
											 " where "+testColumnTwo+"=\""+resultSet.getString(two)+"\";";
						Statement s2=connect.createStatement();		//setup a new statement as we cannot use currently running statement
						s2.execute(internalQuery);					//execute query
						s2.close();									//close statement s2
					}
				}
			}
			//else, insert the positive test as a new record with necessary details
			else{
				String internalQuery="insert into "+testsTable+" ("+testColumnOne+","+testColumnTwo+","+testColumnFive+") values(\""+initiator+"\",\""+testHashes.get(i)+"\","+one+");";
				statement.execute(internalQuery);
			}
		}
		
		//below block of code is used for identifying if the initiator has been in contact with any COVID-19 positively tested contact within 14 days of the contact
		//get all the records of contacts between initiator and its contacts by using the below query stored as a string
		methodQuery="select * from "+contactsTable+" where "+contactColumnOne+"=\""+initiator+"\" and "+contactColumnFive+"="+zero+";";
		resultSet=statement.executeQuery(methodQuery);	//execute query and store results in a resultSet
		
		//iterate the set of results obtained
		while(resultSet.next()) {
			//consider contacts in range of last 14 days
			if(currentDate-resultSet.getInt(three)<fifteen) {
				String contactTo=resultSet.getString(two);		//read the contact with whom initiator met
				
				//check if the contact has a positive test case recorded in tests table
				String internalQuery="select * from "+testsTable+" where "+testColumnOne+"=\""+contactTo+"\" and "+testColumnFour+"="+true_string+";";
				ResultSet tempSet=null;							//create a new result set as existing result set that is currently in use cannot be referenced
				Statement s1=connect.createStatement();			//setup a new statement as we cannot use currently running statement
				tempSet=s1.executeQuery(internalQuery);			//execute query
				
				//if found any record, proceed further
				if(tempSet.next()) {
					tempSet=s1.executeQuery(internalQuery);		//execute the query again to move the cursor before beginning of the result set
					
					//iterate through the results of test records
					while(tempSet.next()) {
						int difference=resultSet.getInt(three)-tempSet.getInt(three);	//get the day difference between day of contact and day of test 
						
						//if its in 14 days range, update the flag to true
						if((difference<fourteen && difference>=zero) || (difference<zero && difference>negFourteen)) {
							if(result!=true) {
								result=true;
							}
							
							//run an update query to set the flag of the current contact record as 1 so that it wouldn't be processed next time
							internalQuery="update "+contactsTable+" set "+contactColumnFive+"="+one
										  +" where "+contactColumnOne+"=\""+resultSet.getString(one)+"\" and "
									                +contactColumnTwo+"=\""+resultSet.getString(two)+"\" and "
										            +contactColumnThree+"="+resultSet.getInt(three)+" and "
									                +contactColumnFour+"="+resultSet.getInt(four)+";";
							
							Statement s2=connect.createStatement();		//setup a new statement as we cannot use currently running statement
							s2.execute(internalQuery);					//execute update query
							s2.close();									//close statement s2
						}
					}
				}
				s1.close();		//close statement s1
			}
		}

		//close streams
		if(resultSet!=null) {
			resultSet.close();		//close resultSet
		}
		if(statement!=null) {
			statement.close();		//close statement
		}
		if(connect!=null) {
			connect.close();		//close connection
		}
		
		return result;			//return end result
	}
	
	/*this method is used to record COVID-19 tests to government's database
	 *each test includes a unique identifier i.e., 'testHash', 'date' on which test was done and the 'result' of the test 
	 *usually called by an agency responsible for performing tests
	 *throws exceptions in case of any issues
	 *sets up a new connection to database in order to insert and update data with respect to tests in database 
	 */
	void recordTestResult(String testHash, int date, boolean result) throws InvalidInputException, ClassNotFoundException, SQLException {
		
		//if the connection flag that is set while creating a government object is false, then throw custom exception with required details as processing is not possible without a proper connection to database 
		if(connectionFlag==false) {
			throw new InvalidInputException("Unable to connect to database with provided details.");
		}
		
		String methodQuery="";			//variable used for storing the query that needs to be executed against the given database
		boolean emptyFlag=true_var;		//flag to indicate a new test or updating existing test that was recorded via mobileContact()
		ResultSet resultSet=null;		//used for storing the results after a select query is executed
		Connection connect=null;		//variable of type Connection used for setting up session between this class and given database
		Statement statement=null;		//variable of type Statement responsible for running queries against the database
	
		//check to see if the test hash passed is null or empty string
		//if yes, throw custom exception with required details
		if(testHash==null || testHash.length()==zero) {
			throw new InvalidInputException("Invalid test hash.");
		}
		
		//check to see if the date passed less than or equal to zero
		//if yes, throw custom exception with required details
		if(date<=zero) {
			throw new InvalidInputException("Invalid date.");
		}
		
		Class.forName("com.mysql.cj.jdbc.Driver");								//load JDBC driver class during run time
		connect=DriverManager.getConnection(domainName, username, password);	//try to get a connection to database using given parameters
		statement=connect.createStatement();									//connect to create statement and execute queries
		statement.execute("use "+dbName+";");									//use the given database 
	
		//design a query to retrieve tests having the same test hash
		methodQuery="select * from "+testsTable+" where "+testColumnTwo+"=\""+testHash+"\";";
		resultSet=statement.executeQuery(methodQuery);		//execute query and store the details in resultSet
		
		//iterate through the result set	
		while(resultSet.next()) {
			emptyFlag=false_var;		//update flag to false indicating a test already exists in the tests table with same test hash
			
			//design query to update the existing test as per the given details by the agency
			String internalQuery="update "+testsTable+" set "+testColumnThree+"="+date+","+testColumnFour+"="+result+" where "+testColumnTwo+"=\""+resultSet.getString(two)+"\";";
			Statement s2=connect.createStatement();		//setup a new statement as we cannot use currently running statement
			s2.execute(internalQuery);					//execute update query
			s2.close();									//close statement s2
		}
		
		//if there is no test with same test hash and thus needs to be inserted newly
		if(emptyFlag==true_var) {
			
			//insert into tests table with given details
			String internalQuery="insert into "+testsTable+" ("+testColumnTwo+","+testColumnThree+","+testColumnFour+","+testColumnFive+") values(\""+testHash+"\","+date+","+result+","+zero+");";
			statement.execute(internalQuery);
		}
		
		//close streams
		if(resultSet!=null) {
			resultSet.close();		//close resultSet
		}
		if(statement!=null) {
			statement.close();		//close statement
		}
		if(connect!=null) {
			connect.close();		//close connection
		}
		
		return;
	}
	
	
	/*this method is used for identifying the large gatherings on a given 'date'
	 *a group of contacts is said to be a gathering if it has 'minSize' individuals
	 *a large gathering is detected if from the above gatherings there are contacts with at least 'minTime' duration. 
	 *The number of such contacts are compared against the 'density' using a formula c/(n(n-1)/2) where 'n' is the total number of contacts in a gathering
	 *throws exceptions in case of any issues   
	 *uses a new connection to communicate with government's database
	 *uses two main data structures i.e., maps and lists to store the contacts recorded on the given date
	 *uses four additional data structures of maps and lists to store contacts in a gathering and large gathering
	 */
	int findGatherings(int date, int minSize, int minTime, float density) throws InvalidInputException, ClassNotFoundException, SQLException{
		
		//if the connection flag that is set while creating a government object is false, then throw custom exception with required details as processing is not possible without a proper connection to database 
		if(connectionFlag==false) {
			throw new InvalidInputException("Unable to connect to database with provided details.");
		}
		
		ResultSet resultSet=null;		//used for storing the results after a select query is executed
		Connection connect=null;		//variable of type Connection used for setting up session between this class and given database
		Statement statement=null;		//variable of type Statement responsible for running queries against the database
		
		int gatherings=0;				//number of gatherings that needs to be reported
		String methodQuery="";			//used for storing queries that needs to be executed against the given database
		String tempContact=null;		//variable for storing the device hash of a contact temporarily before moving the same to a data structure
		
		/*map used as a data structure to represent devices hashes that were in contact on the given date as keys
		 *and values as boolean values where false indicates the contact has not yet been considered as part of any large gathering
		 *true value indicating the contact has already been considered as part of a large gathering
		 */
		Map<String,Boolean> contactMap=new HashMap<String,Boolean>();
	
		//list used as a data structure for storing the list of contacts that were in contact on the given date in the database
		List<String> contactList=new ArrayList<String>();
		
		//check to see if the date passed is less than or equal to zero
		//if yes, throw custom exception with required details
		if(date<=0) {
			throw new InvalidInputException("Invalid date.");
		}
		
		//check to see if the minSize passed is less than zero
		//if yes, throw custom exception with required details
		if(minSize<0) {
			throw new InvalidInputException("Invalid minimum size.");
		}
		
		//check to see if the minTime passed is less than zero
		//if yes, throw custom exception with required details
		if(minTime<0) {
			throw new InvalidInputException("Invalid minimum time.");
		}
		
		//check to see if the density passed is less than zero
		//if yes, throw custom exception with required details
		if(density<0.0) {
			throw new InvalidInputException("Invalid density.");
		}
		
		Class.forName("com.mysql.cj.jdbc.Driver");								//load JDBC driver class during run time
		connect=DriverManager.getConnection(domainName, username, password);	//try to get a connection to database using given parameters
		statement=connect.createStatement();									//connect to create statement and execute queries
		statement.execute("use "+dbName+";");									//use the given database
		
		//query to get the contacts on the given date
		methodQuery="select * from "+contactsTable+" where "+contactColumnThree+"="+date+";";
		resultSet=statement.executeQuery(methodQuery);		//execute query and store the results into result set
		
		//iterate through the result set
		while(resultSet.next()) {
			
			//get the device hash of the current contact if it's not null from column one of the database
			if(resultSet.getString(one)!=null) {
				tempContact=resultSet.getString(one);
			}
			
			//add the device hash of the current contact as a key to map and value as false if the map does not already contain the device hash
			//add the device hash of the current contact to the list of contacts
			if(tempContact!=null && !contactMap.containsKey(tempContact)) {
				contactMap.put(tempContact, false);		//add to map
				contactList.add(tempContact);			//add to list
			}
			
			//update tempContact to null to read next contact
			tempContact=null;
			
			//get the device hash of the current contact if it's not null from column two of the database
			if(resultSet.getString(two)!=null) {
				tempContact=resultSet.getString(two);
			}
			
			//add the device hash of the current contact as a key to map and value as false if the map does not already contain the device hash
			//add the device hash of the current contact to the list of contacts
			if(tempContact!=null && !contactMap.containsKey(tempContact)) {
				contactMap.put(tempContact,false);
				contactList.add(tempContact);
			}
			
			//update tempContact to null to read next contact
			tempContact=null;
		}
		
		//traverse through the list of contacts using the below for loop to identify the large gatherings
		for(int i=0;i<contactList.size();i++) {
			
			//select only contacts that have not yet been considered as part of any large gathering 
			if(!contactMap.get(contactList.get(i))) {
				resultSet=null;		//update result to null in every loop
				
				
				String A=contactList.get(i);	//add first contact as 'A'
				String B=null;
				for(int j=i+1;j<contactList.size() && !contactMap.get(contactList.get(i));j++) {
					
					List<String> contactsListGathering=new ArrayList<String>();		//list as a data structure to store contacts of a gathering so that they can be traversed
					Set<String> contactsSetGathering=new HashSet<String>();			//set as a data structure to store unique contacts of a gathering
					
					//if next contact is not already been considered as part of any large gathering, proceed further
					if(!contactMap.get(contactList.get(j))) {
						B=contactList.get(j);			//add contact to 'B'
						
						//add 'A' to gathering's list and set if not already added
						if(!contactsSetGathering.contains(A)) {
							contactsSetGathering.add(A);
							contactsListGathering.add(A);
						}
						
						//add 'B' to gathering's list and set if not already added
						if(!contactsSetGathering.contains(B)) {
							contactsSetGathering.add(B);
							contactsListGathering.add(B);
						}
						String internalQuery="";		//variable for storing a query that needs to be executed against the given database
						
						//design a query to get contacts where 'A' is present on given date
						internalQuery="select * from "+contactsTable+" where ("+contactColumnOne+"=\""+A+"\" || "+contactColumnTwo+"=\""+A+"\") and "+contactColumnThree+"="+date+";";
						resultSet=statement.executeQuery(internalQuery);	//execute query
						
						//iterate through the results obtained from above query
						while(resultSet.next()) {
							
							//identify if the current contact is also in contact with 'B' on the same day
							
							
							boolean flag=false_var;	//flag used to indicate if the current contact is in contact with both 'A' and 'B' or not
							
							//if result contains 'A' as first column, check to see if the contact in second column has also met 'B'
							if(resultSet.getString(one).equals(A)) {
									
								//design a query to get identify if 'B' has met the current contact on the same date
								internalQuery="select * from "+contactsTable+" where (("+contactColumnOne+"=\""+B+"\" && "+contactColumnTwo+"=\""+resultSet.getString(two)+"\") || ("+contactColumnTwo+"=\""+B+"\" && "+contactColumnOne+"=\""+resultSet.getString(two)+"\")) and "+contactColumnThree+"="+date+";";
								ResultSet tempSet=null;						//create a new temporary result set
								Statement s1=connect.createStatement();		//create a temporary statement to execute queries
								tempSet=s1.executeQuery(internalQuery);		//execute query
								
								//if found a record, it indicates the contact exists with 'B' as well
								if(tempSet.next()) {
									flag=true_var;	//update flag to true
								}
								tempSet.close();	//close temporary result set
								s1.close();			//close temporary statement s1
							}
							//if result contains 'A' as second column, check to see if the contact in first column has also met 'B'
							else if(resultSet.getString(two).equals(A)) {
								
								//design a query to get identify if 'B' has met the current contact on the same date
								internalQuery="select * from "+contactsTable+" where (("+contactColumnOne+"=\""+B+"\" && "+contactColumnTwo+"=\""+resultSet.getString(one)+"\") || ("+contactColumnTwo+"=\""+B+"\" && "+contactColumnOne+"=\""+resultSet.getString(one)+"\")) and "+contactColumnThree+"="+date+";";
								ResultSet tempSet=null;						//create a new temporary result set
								Statement s1=connect.createStatement();		//create a temporary statement to execute queries
								tempSet=s1.executeQuery(internalQuery);		//execute query
							
								//if found a record, it indicates the contact exists with 'B' as well
								if(tempSet.next()) {
									flag=true_var;	//update flag to true
								}
								tempSet.close();	//close temporary result set
								s1.close();			//close temporary statement s1
							}
							//if true i.e., contact met both 'A' and 'B' on same day, add it to gatherings list and set only if its not already considered as part of other large gatherings (i.e., mao value should be false)
							if(flag==true_var) {
								
								//in case contact is present in first column
								if(!contactsSetGathering.contains(resultSet.getString(one)) && !contactMap.get(resultSet.getString(one))) {
									contactsSetGathering.add(resultSet.getString(one));
									contactsListGathering.add(resultSet.getString(one));
								}
								//in case contact is present in second column
								if(!contactsSetGathering.contains(resultSet.getString(two)) && !contactMap.get(resultSet.getString(two))) {
									contactsSetGathering.add(resultSet.getString(two));
									contactsListGathering.add(resultSet.getString(two));
								}
							}
						}
						resultSet=null;		//update result set so that it can be reused
						
						//if only the size of the gathering is greater or equal to given minSize
						if(contactsSetGathering.size()>=minSize) {
							Set<String> contactsSetLargeGathering=new HashSet<String>();		//set as a data structure to store contacts of a large gathering
							
							//identify if there exists a contact between contacts of the gathering on the same date and with minTime duration
							for(int a=0;a<contactsListGathering.size();a++) {
								for(int b=a+1;b<contactsListGathering.size();b++) {
									//design the query to identify if there exists a contact between 'a' and 'b' on the same date and with minTime duration
									internalQuery="select * from "+contactsTable+" where (("+contactColumnOne+"=\""+contactsListGathering.get(a)+"\" && "+contactColumnTwo+"=\""+contactsListGathering.get(b)+
												  "\") || ("+contactColumnOne+"=\""+contactsListGathering.get(b)+"\" && "+contactColumnTwo+"=\""+contactsListGathering.get(a)+"\")) and "+contactColumnThree+"="+date+" and "+contactColumnFour+">="+minTime+";";
									resultSet=statement.executeQuery(internalQuery);	//execute query
									
									//iterate through the result set
									while(resultSet.next()) {
										
										//add the contacts to set of large gathering if they are not already present
										if(!contactsSetLargeGathering.contains(resultSet.getString(one))) {
											contactsSetLargeGathering.add(resultSet.getString(one));
										}
										if(!contactsSetLargeGathering.contains(resultSet.getString(two))) {
											contactsSetLargeGathering.add(resultSet.getString(two));
										}
									}
								}
							}
							
							//below block of code is used for calculating density of large gathering
							int n=contactsSetGathering.size();										//calculate 'n' i.e., size of contacts in a gathering
							float calculatedDensity=(float) ((n*(n-1))/two);						//calculate (n*(n-1))/2
							calculatedDensity=contactsSetLargeGathering.size()/calculatedDensity;	//calculate density as c/((n*(n-1))/2)
							
							//if the calculated density is greater than given density, increment the counter of larger gatherings and update the map to remove the contacts being considered in processing next large gatherings
							if(calculatedDensity>density) {
								gatherings++;		//increment gatherings
								
								//use iterator to parse through contacts of a large gathering
								Iterator<String> iterator=contactsSetLargeGathering.iterator();
								while(iterator.hasNext()) {
									contactMap.replace(iterator.next(), true_var);	//update the value for current contact as true
								}
							}
						}
					}
				}
			}
		}	

		//close streams
		if(resultSet!=null) {
			resultSet.close();		//close resultSet
		}
		if(statement!=null) {
			statement.close();		//close statement
		}
		if(connect!=null) {
			connect.close();		//close connection
		}
		
		return gatherings;		//return the end result of number of large gatherings
			
	}
}