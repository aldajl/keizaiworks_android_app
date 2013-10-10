package keizai.works.mobile;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "stocksManager";
	public static final String TABLE_NAME = "allStocks";
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "stockName";
	public static final String KEY_FROM = "stockFrom";
	public static final String KEY_PARENT = "stockParent";
	public static final String KEY_STAT = "stockStat";
	
	public DatabaseHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		String QUERY_CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_NAME+" TEXT,"+KEY_FROM+" TEXT,"+KEY_PARENT+" TEXT,"+KEY_STAT+" TEXT)";
		db.execSQL(QUERY_CREATE_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);
	}
	
	public void addStock(Stock stock){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, stock.getName());
		values.put(KEY_FROM, stock.getFrom());
		values.put(KEY_PARENT, stock.getParent());
		values.put(KEY_STAT, stock.getStat());
		
		db.insert(TABLE_NAME, null, values);
		db.close();
	}
	
	public Stock getStock(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		
        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
                KEY_NAME, KEY_FROM, KEY_PARENT, KEY_STAT }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        
        if (cursor != null)
            cursor.moveToFirst();
 
        Stock stock = new Stock(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        return stock;
	}
	
	public List<Stock> getStocks(){
		List<Stock> stockList = new ArrayList<Stock>();
		String selectQuery = "SELECT * FROM "+TABLE_NAME;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor.moveToFirst()){
			do{
				Stock stock = new Stock(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
				stockList.add(stock);
			}while(cursor.moveToNext());
		}
		return stockList;
	}
	
	public List<Stock> getStocks(String stockFrom){
		List<Stock> stockList = new ArrayList<Stock>();
		String selectQuery = "SELECT * FROM "+TABLE_NAME+" WHERE stockFrom = '"+stockFrom+"'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor.moveToFirst()){
			do{
				Stock stock = new Stock(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
				stockList.add(stock);
			}while(cursor.moveToNext());
		}
		return stockList;
	}
	
	public int updateStock(Stock stock){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, stock.getName());
		values.put(KEY_FROM, stock.getFrom());
		values.put(KEY_PARENT, stock.getParent());
		values.put(KEY_STAT, stock.getStat());
		
		return db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(stock.getId()) });
	}
	
	public void deleteStock(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM "+TABLE_NAME);
	}
}
