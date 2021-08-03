/*
Owner		:	Vamsi Krishna Utla
Student ID	:	B00870632
Email		:	vm271757@dal.ca
Date		:	24-11-2020	
Purpose		:	This file/class is used as a custom exception that is initiated when invalid inputs are provided
*/

//extends RunTimeException
//suitable message is stored when a new instance of this exception is thrown
//retrieves message using getString()
public class InvalidInputException extends RuntimeException {

	String message;		//place holder for error message
	
	//constructor
	InvalidInputException(String message){
		this.message=message;
	}
	
	//method to retrieve error message
	String getString() {
		return message;
	}
}
