/*
Owner		:	Vamsi Krishna Utla
Student ID	:	B00870632
Email		:	vm271757@dal.ca
Date		:	08-11-2020	
Purpose		:	This file/class  is used to define the structure of a contact that is used in contact tracing of COVID-19
*/

//this class consists of constructors for initializing class members along with required setter and getter methods
public class Contact {

	private String contactHash;		//unique identifier of the contact's mobile device
	private int date;				//date of contact
	private int duration;			//duration of the contact
	private boolean sync;			//flag to check if the contact has already been synced with government's database or not
	
	//constructor with no parameters that simply initializes the sync flag to false indicating it has not yet been synced with government's database
	protected Contact(){
		sync=false;		//set flag to false
	}

	//constructor with parameters used to initialize contactHash, date, duration and sync flag of the contact 
	protected Contact(String contactHash, int date, int duration){
		this.contactHash=contactHash;		//set contactHash of contact
		this.date=date;						//set date of contact
		this.duration=duration;				//set duration of contact
		sync=false;							//set flag to false
	}
	
	//getter method for returning contactHash of this contact
	protected String getContactHash() {
		return contactHash; 
	}
	
	//getter method for returning date of this contact
	protected int getDate() {
		return date;
	}
	
	//getter method for returning duration of this contact
	protected int getDuration() {
		return duration;
	}
	
	//getter method for identifying if the contact has been synced with government's database or not
	protected boolean getSync() {
		return sync;
	}
	
	//setter method for updating contactHash of the contact
	protected void setContactHash(String contactHash) {
		this.contactHash=contactHash;
	}
	
	//setter method for updating date of the contact
	protected void setDate(int date) {
		this.date=date;
	}
	
	//setter method for updating duration of the contact
	protected void setDuration(int duration) {
		this.duration=duration;
	}
	
	//setter method for updating sync status of the contact
	protected void setSync(boolean sync) {
		this.sync=sync;
	}
	
}
