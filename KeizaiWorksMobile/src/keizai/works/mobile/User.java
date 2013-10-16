package keizai.works.mobile;

/*
 * User.java
 * created by: Joshua Alday
 * description: Class used to store user information
 */

public class User {
	private String userName;	//Will be used to reference userid
	private String userFrom;	//Will be used to reference location
	
	//constructor, include all items
	public User(String userName, String userFrom){
		this.userName = userName;
		this.userFrom = userFrom;
	}
	
	public String getUserName(){
		return this.userName;
	}
	
	public String getUserFrom(){
		return this.userFrom;
	}
}//end User.java
