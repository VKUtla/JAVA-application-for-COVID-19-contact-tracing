import java.util.*;
import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;

/*
Owner		:	Vamsi Krishna Utla
Student ID	:	B00870632
Email		:	vm271757@dal.ca
Date		:	09-11-2020	
Purpose		:	This file/class is used to define the functionality of a mobile device that is used for the purpose of contact tracing of COVID-19
*/

/*this class is used for designing the functionality of a mobile device by recording its contacts and test results
 *this class uses two main data structures for storing contact details and positive test details 
 *every sync will write the contact and positive tests information to government's database
 *uses an instance of government to communicate with government server
 *uses configuration file (with two parameters i.e., 'device name' and 'network address') for initializing the mobile device 
 */
public class MobileDevice {

	private ArrayList<Contact> contacts=new ArrayList<Contact>();	//array list of type 'Contact' used as a data structure for storing contacts of this mobile device
	private ArrayList<String> tests=new ArrayList<String>();		//array list of type String used as a data structure for storing the test hashes of positive results corresponding to this mobile device
	private String networkAddress, deviceName;						//variables used for storing 'network address' and 'device name' of this mobile device
	protected String deviceHash;									//variable used for uniquely identifying a mobile device by hashing network address and device name
	private Government contactTracer;								//instance of Government used for communicating with Government server
	
	private final int zero=0;										//constant for value '0'
	private final int one=1;										//constant for value '1'
	private final int two=2;										//constant for value '2'
	
	/*Constructor used for setting up the configuration of mobile device using the given 'configurationFile'
	 *stores the government's instance as 'contactTracer' for communicating with government server through out the life cycle of this mobile device
	 *the configuration file should have address as first line and deviceName as second line with their values separated by "="
	 *throws exceptions in case of any issue in the format of the file
	 *uses BufferedReader for reading data from the configuration file on a line to line basis
	 */
	MobileDevice(String configurationFile, Government contactTracer) throws IOException,InvalidInputException {
		
		boolean addressFlag=false;			//flag to verify if the address of the mobile device has been ready successfully or not from the configuration file
		boolean deviceFlag=false;			//flag to verify if the deviceName of the mobile device has been ready successfully or not from the configuration file
		String line;						//variable of type String used for storing data of each line when the file is being read
		String[] arguments;					//array of String used for storing key-value of parameters (i.e., address and name) given in the configuration file  
		
		//throw custom exception of type InvalidInputException in case the given government's object is null
		if(contactTracer==null) {
			throw new InvalidInputException("Government object passed is null!");
		}
		
		//throw custom exception of type InvalidInputException in case the configuration file is given as null or empty string 
		if(configurationFile==null || configurationFile.length()==zero) {
			throw new InvalidInputException("Invalid configuration file.");
		}
	
		//initialize government instance that is used for the rest of the life cycle of this mobile device for communicating with government server
		this.contactTracer=contactTracer;
		
		//initialize BufferedReader to read data from the given configuration file
		//use file reader though configuration file is represented as a String
		BufferedReader buffReader=new BufferedReader(new FileReader(configurationFile));
		line=buffReader.readLine();		//read first line
		
		//block of code that is used to move to the first line of the file by avoiding empty lines in the beginning of the given configuration file
		while(line.equals("") && line!=null) {
			line=buffReader.readLine();
		}
		
		//after reading all the empty lines (if any), if the line reaches null, then the configuration file is empty and invalid
		//close the  BufferedReader instance and throw a custom exception indicating invalid configuration file
		if(line==null) {
			buffReader.close();													//close buffered reader
			throw new InvalidInputException("Invalid configuration file!");		//throw exception
		}
		
		//if the data starts, read each line and determine if it corresponds to any parameter that can be used for configuring the mobile device
		//parse through each line until address and device name are read or if the file reached end of data
		while((!addressFlag || !deviceFlag) && line!=null) {
			arguments=line.split("=");			//split the data in the line by using "=" as a separator
			
			//if line contains more than two or less than two data segments separated by "=", consider that line as invalid and move to next line
			if(arguments.length!=two) {
				line=buffReader.readLine();
			}
			else {
				//line contains two segments of the data as per the format separated by "="
				
				//if the line represents the address, read it
				if(arguments[zero].equalsIgnoreCase("address")) {
					networkAddress=arguments[one];		//read address into required variable
					addressFlag=true;					//set the address flag to true indicating address has been read successfully
				}
				//if the line represents the device name, read it
				else if(arguments[zero].equalsIgnoreCase("deviceName")) {
					deviceName=arguments[one];			//read device name into required variable
					deviceFlag=true;					//set the device name flag to true indicating device name has been read successfully
				}
				line=buffReader.readLine();				//move to next line
			}
		}
		
		//if any of the parameter is not read, close the buffered reader instance and throw custom exception indicating invalid format of the configuration file
		if(!deviceFlag || !addressFlag) {
			buffReader.close();													//close buffered reader
			throw new InvalidInputException("Invalid configuration file!");		//throw custom exception
		}
		
		//concatenate address and device name
		deviceHash=networkAddress+deviceName;
		
		//derive the hash code using the in built hashCode method
		deviceHash=Integer.toString(deviceHash.hashCode());
		buffReader.close();		//close buffered reader instance before leaving the constructor 
	}
	
	
	/*this method is used for recording a contact to this mobile device  
	 *each contact consists of a unique identifier i.e., 'individual', 'date' of contact and 'duration' of contact
	 *this will usually be triggered when the mobile devices gets in contact with other device
	 *uses other device's unique hash for storing the contact along with date and duration of the contact details
	 *assumes there wouldn't be any duplicate contact i.e., with same hash, date and duration that will be recorded
	 *even if duplicate contact is recorded, it assumes that it would be taken care at government server's end while writing to database
	 *throws custom exception in case of any issues
	 */
	void recordContact(String individual, int date, int duration) throws InvalidInputException {
		
		//if other contact's hash is passed as null or empty String, throw custom exception indicating required information 
		if(individual==null  || individual.length()==zero) {
			throw new InvalidInputException("Indiviudal details are null!");
		}
		
		//if date of contact is passed as less than or equal to zero, throw custom exception indicating required information as the date start from 1 (01-01-2020)
		if(date<=0) {
			throw new InvalidInputException("Invalid date!");
		}
		
		//if duration of contact is passed as less than or equal to zero, throw custom exception indicating required information as duration cannot be less than 0
		if(duration<=0) {
			throw new InvalidInputException("Invalid duration!");
		}
		
		//create a new contact instance using given information
		Contact contact=new Contact(individual,date,duration);
		
		//add the new contact to array list of contacts
		contacts.add(contact);

	}
	
	
	/*this method is used for storing the positive test case result of this mobile device (user)
	 *usually triggered by a agency to report a positive test result to the user of this mobile device
	 *the stored test results data can later be used for sending it over to government server 
	 *throws custom exception in case of any issues
	 */
	void positiveTest(String testHash) throws InvalidInputException {
		
		boolean duplicate=false;		//flag used for verifying if the given test is a duplicate or not
		
		//throw custom exception in case if the given test hash is invalid i.e., null or empty string
		if(testHash==null || testHash.length()==zero) {
			throw new InvalidInputException("Invalid testHash!");
		}
		
		//for loop used for identifying if the same test has been recorded earlier or not
		for(int i=0;i<tests.size();i++) {
			
			//if found the same test hash in the tests array list, it indicates a duplicate
			if(tests.get(i).equals(testHash)) {
				duplicate=true;			//update the flag to true indicating given test as duplicate
			}
		}
		
		//if not duplicate, add it to tests array list
		if(!duplicate) {
			tests.add(testHash);
		}
	}
	
	
	/*this method is used to send the contacts details and positive tests details to government server using contactTracer (as initialized in the constructor)
	 *the details are sent as a string in an XML format where first line consists of version tag followed by <Contacts> tag  
	 *<Contacts> tag in turn consists of separate <Contact> tags representing individual contacts
	 *<Contact> tag uses <ContactHash>, <Date> and <Duration> tags to hold contact's information
	 *similarly, <Tests> tag consists of <Test> tags representing individual positive test details
	 *all the tags are properly closed as per the XML format
	 *this method returns true if the mobile device was in contact with any other devices that were tested positive with in 14 days of contact
	 *communicates with government server using mobileContact() method 
	 *throws exceptions in case of any issues
	 */
	boolean synchronizeData() throws InvalidInputException, IOException, ClassNotFoundException, SQLException, ParseException {
		
		boolean result;													//variable for storing end result
		final String version="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";	//string constant for storing the version tag of XML
		String contactInfo;												//string used for storing all the information associated to contacts and tests in XML format
		final String newLine="\n";											//string constant for a new line character
		final String tab="\t";												//string constant for a tab character
		final String contactsOpenTag="<Contacts>";							//string constant for a Contacts open tag i.e., <Contacts>
		final String contactOpenTag="<Contact>";								//string constant for a Contact open tag i.e., <Contact>
		final String testsOpenTag="<Tests>";									//string constant for a Tests open tag i.e., <Tests>
		final String testOpenTag="<Test>";									//string constant for a Test open tag i.e., <Test>
		final String contactHashOpenTag="<ContactHash>";						//string constant for a ContactHash open tag i.e., <ContactHash>
		final String dateOpenTag="<Date>";									//string constant for a Date open tag i.e., <Date>
		final String durationOpenTag="<Duration>";							//string constant for a Duration open tag i.e., <Duration>
		final String contactsClosedTag="</Contacts>";							//string constant for a Contacts closing tag i.e., </Contacts>
		final String contactClosedTag="</Contact>";							//string constant for a Contact closing tag i.e., </Contact>
		final String testsClosedTag="</Tests>";								//string constant for a Tests closing tag i.e., </Tests>
		final String testClosedTag="</Test>";									//string constant for a Test closing tag i.e., </Test>
		final String contactHashClosedTag="</ContactHash>";					//string constant for a ContactHash closing tag i.e., </ContactHash>
		final String dateClosedTag="</Date>";									//string constant for a Date closing tag i.e., </Date>
		final String durationClosedTag="</Duration>";							//string constant for a Duration closing tag i.e., </Duration>
		
		contactInfo="";										//initialize to empty string
		contactInfo=contactInfo.concat(version);			//add version tag as first line
		contactInfo=contactInfo.concat(newLine);			//add new line
		contactInfo=contactInfo.concat(contactsOpenTag);	//add Contacts open tag
		
		//iterate through list of contacts stored in the array list of contacts and append them to string contactInfo with suitable tags in an XML format
		for(int i=zero;i<contacts.size();i++) {
			//consider contacts that have not already been synced with government's server
			if(!contacts.get(i).getSync()) {
				contacts.get(i).setSync(true);						//set sync status for the current contact as true
				contactInfo=contactInfo.concat(newLine);			//add a new line to contactInfo
				contactInfo=contactInfo.concat(tab);				//add a tab
				contactInfo=contactInfo.concat(contactOpenTag);		//add Contact open tag
				contactInfo=contactInfo.concat(newLine);			//add new line
				
				//add two tabs
				for(int j=zero;j<two;j++) {
					contactInfo=contactInfo.concat(tab);
				}
				
				//add contact hash by inserting the data in between contact hash's open and closing tags
				contactInfo=contactInfo.concat(contactHashOpenTag+contacts.get(i).getContactHash()+contactHashClosedTag);
				contactInfo=contactInfo.concat(newLine);			//add new line
				
				//add two tabs
				for(int j=zero;j<two;j++) {
					contactInfo=contactInfo.concat(tab);
				}
				
				//add date of contact details by inserting the data in between date's open and closing tags
				contactInfo=contactInfo.concat(dateOpenTag+contacts.get(i).getDate()+dateClosedTag);
				contactInfo=contactInfo.concat(newLine);			//add new line
				
				//add two tabs
				for(int j=zero;j<two;j++) {
					contactInfo=contactInfo.concat(tab);
				}
				
				//add duration of contact by inserting the data in between durations's open and closing tags
				contactInfo=contactInfo.concat(durationOpenTag+contacts.get(i).getDuration()+durationClosedTag);
				contactInfo=contactInfo.concat(newLine);			//add new line
				contactInfo=contactInfo.concat(tab);				//add a tab
				contactInfo=contactInfo.concat(contactClosedTag);	//add a contact's closing tag
			}
		}
		
		//contacts are added, now add Contact close tag and proceed to add positive test details
		contactInfo=contactInfo.concat(newLine);					//add new line
		contactInfo=contactInfo.concat(contactsClosedTag);			//add Contact close tag
		contactInfo=contactInfo.concat(newLine);					//add new line
		
	
		contactInfo=contactInfo.concat(testsOpenTag);				//add Tests open tag
		
		//iterate through the array list of positive tests and add them to contactInfo
		for(int i=0;i<tests.size();i++) {
			contactInfo=contactInfo.concat(newLine);				//add new line
			contactInfo=contactInfo.concat(tab);					//add a tab
			
			//add test hash by inserting the data in between test's open and closing tags
			contactInfo=contactInfo.concat(testOpenTag+tests.get(i)+testClosedTag);
		}
		
		//after tests are added (if any), close Tests tag now
		contactInfo=contactInfo.concat(newLine);					//add new line
		contactInfo=contactInfo.concat(testsClosedTag);				//add Tests closing tag
		
		//contact government server via mobileContact using contactTracer object and store the details into database
		//also, identify if this device has been in contact with any of the other devices that were tested positive within 14 days of their contact
		result=contactTracer.mobileContact(deviceHash,contactInfo);
		
		return result;		//return end result
	}
}