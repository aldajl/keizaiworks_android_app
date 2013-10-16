package keizai.works.mobile;

/*
 * DatabaseHandler.java
 * created by: Joshua Alday
 * description: Class for handling interactions with database.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	//dbl or databaselight is referenced to database
	private SQLiteDatabase dbl = this.getWritableDatabase();
	
	//Database Shared Variables
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "stocksManager";
	public static final String KEY_ID = "id";
	
	//Stock Variables
	public static final String TABLE_NAME_STOCKS = "allStocks";
	public static final String KEY_NAME_STOCKS = "stockName";
	public static final String KEY_FROM_STOCKS = "stockFrom";
	public static final String KEY_PARENT_STOCKS = "stockParent";
	public static final String KEY_STAT_STOCKS = "stockStat";
	
	//User Variables
	public static final String TABLE_NAME_USER = "userLogInfo";
	public static final String KEY_NAME_USER = "userName";
	public static final String KEY_FROM_USER = "userFrom";
	
	public DatabaseHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	//Currently Not Used
	@Override
	public void onCreate(SQLiteDatabase db){
		
	}
	
	//Currently Not Used
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_STOCKS);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_USER);
		onCreate(db);
	}
	
	//add Stock to Database
	public void addStock(Stock stock){		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME_STOCKS, stock.getName());
		values.put(KEY_FROM_STOCKS, stock.getFrom());
		values.put(KEY_PARENT_STOCKS, stock.getParent());
		values.put(KEY_STAT_STOCKS, stock.getStat());
		
		dbl.insert(TABLE_NAME_STOCKS, null, values);
	}
	
	//add User to Database
	public void addUser(User user){
		ContentValues values = new ContentValues();
		values.put(KEY_NAME_USER, user.getUserName());
		values.put(KEY_FROM_USER, user.getUserFrom());
		
		dbl.insert(TABLE_NAME_USER, null, values);
	}
	
	//get User from id (currently the only working/reliable method. Just use 1 as id)
	public User getUser(int id){
		Cursor cursor = dbl.query(TABLE_NAME_USER, 
				new String[] {KEY_ID, KEY_NAME_USER, KEY_FROM_USER}, KEY_ID+"=?",
				new String[] {String.valueOf(id)},null,null,null,null);			//cursor points to sections of query results
		if (cursor != null)
			cursor.moveToFirst();
		
		User user = new User(cursor.getString(1), cursor.getString(2));
		return user;
	}
	
	//get Stock by id
	public Stock getStock(int id){
		
        Cursor cursor = dbl.query(TABLE_NAME_STOCKS, new String[] { KEY_ID,
                KEY_NAME_STOCKS, KEY_FROM_STOCKS, KEY_PARENT_STOCKS, KEY_STAT_STOCKS }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        
        if (cursor != null)
            cursor.moveToFirst();
 
        Stock stock = new Stock(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        return stock;
	}
	
	//get all Stock in List
	public List<Stock> getStocks(){
		List<Stock> stockList = new ArrayList<Stock>();
		String selectQuery = "SELECT * FROM "+TABLE_NAME_STOCKS;
		Cursor cursor = dbl.rawQuery(selectQuery, null);
		
		if(cursor.moveToFirst()){
			do{
				Stock stock = new Stock(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
				stockList.add(stock);
			}while(cursor.moveToNext());
		}
		return stockList;
	}
	
	//get all Stock in List per location
	public List<Stock> getStocks(String stockFrom){
		List<Stock> stockList = new ArrayList<Stock>();
		String selectQuery = "SELECT * FROM "+TABLE_NAME_STOCKS+" WHERE stockFrom = '"+stockFrom+"'";
		Cursor cursor = dbl.rawQuery(selectQuery, null);
		
		if(cursor.moveToFirst()){
			do{
				Stock stock = new Stock(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
				stockList.add(stock);
			}while(cursor.moveToNext());
		}
		return stockList;
	}
	
	//update the database. Used in LoadTask class
	public int updateStock(Stock stock){
		ContentValues values = new ContentValues();
		values.put(KEY_NAME_STOCKS, stock.getName());
		values.put(KEY_FROM_STOCKS, stock.getFrom());
		values.put(KEY_PARENT_STOCKS, stock.getParent());
		values.put(KEY_STAT_STOCKS, stock.getStat());
		
		return dbl.update(TABLE_NAME_STOCKS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(stock.getId()) });
	}
	
	//will delete old stock data
	public void deleteStocks(){
		dbl.execSQL("DELETE FROM "+TABLE_NAME_STOCKS);
	}
	
	//will delete old user data and reset id for ease of use
	public void deleteUser(){
		dbl.execSQL("DELETE FROM "+TABLE_NAME_USER);
		dbl.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name=  '" + TABLE_NAME_USER + "'");
	}
	
	//Create user and Stock Tables
	//SIDENOTE: this would normally be done in onCreate(). However,
	//in the case of pre-existent tables, error occurs.
	//This method will solve that issue
	public void createUser(){
		String QUERY_CREATE_USR = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_USER+" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_NAME_USER+" TEXT,"+KEY_FROM_USER+" TEXT)";
		dbl.execSQL(QUERY_CREATE_USR);
	}
	
	public void createStock(){
		String QUERY_CREATE_STOCKS = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_STOCKS+" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_NAME_STOCKS+" TEXT,"+KEY_FROM_STOCKS+" TEXT,"+KEY_PARENT_STOCKS+" TEXT,"+KEY_STAT_STOCKS+" TEXT)";
		dbl.execSQL(QUERY_CREATE_STOCKS);
	}
}
