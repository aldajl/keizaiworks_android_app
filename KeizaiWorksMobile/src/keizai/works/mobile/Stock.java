package keizai.works.mobile;

/*
 * Stock.java
 * created by: Joshua Alday
 * description: Class to handle stock information
 */

public class Stock {
	private int id;		//Will be used to reference position
	private String name;	//Will be used to reference name
	private String from;	//Will be used to reference location
	private String parent;	//Will be used to reference source
	private String stat;	//Will be used to reference Quote
	
	//basic constructor
	public Stock(){
		
	}
	
	//constructor, include essential items
	public Stock(String name, String from, String parent, String stat){
		this.name = name;
		this.from = from;
		this.parent = parent;
		this.stat = stat;
	}
	
	//constructor, include all items
	public Stock(int id, String name, String from, String parent, String stat){
		this.id = id;
		this.name = name;
		this.from = from;
		this.parent = parent;
		this.stat = stat;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getFrom(){
		return from;
	}
	
	public String getParent(){
		return parent;
	}
	
	public String getStat(){
		return stat;
	}
}//end Stock.java
