package keizai.works.mobile;

public class Stock {
	private int id;
	private String name;
	private String from;
	private String parent;
	private String stat;
	
	public Stock(){
		
	}
	
	public Stock(String name, String from, String parent, String stat){
		this.name = name;
		this.from = from;
		this.parent = parent;
		this.stat = stat;
	}
	
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
}
