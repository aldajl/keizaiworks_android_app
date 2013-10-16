package keizai.works.mobile;

/*
 * LoadTask.java
 * created by: Joshua Alday
 * Description: Used to update Database with current information
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.ParseException;
import android.os.AsyncTask;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class LoadTask {
	
	private Activity activity = null;		//Will be used to reference context
	private URL url = null;				//Will be used to get JSON Data
	private String tabContextRef = null;	//Will be used to know which stocks to pull from
	private DatabaseHandler db = null;		//Will be used to handle updates to database
	private TableLayout statsTable = null;	//Will be used to create Table that will display Stock info
    
	//constructor, receive vital references
	LoadTask(Activity activity, URL url, String tabContextRef, TableLayout statsTable){
		this.activity = activity;
		this.url = url;
		this.tabContextRef = tabContextRef;
		
		db = new DatabaseHandler(activity);
		this.statsTable = statsTable;
	}
	
	public void execute(){
		new updateStockStats().execute(url);	//ASynceTask Thread, will update db and post data
	}
	
	//**updating stock information on new thread from keizaiworks.mypressonline.com**
	private class updateStockStats extends AsyncTask<URL, Integer, String>{
			
			ProgressDialog mDialog;
			
			//This sets up a progress Dialog that displays for the user to know data is loading
	    	@Override
	        protected void onPreExecute() {
	            super.onPreExecute();

	            mDialog = new ProgressDialog(activity);
	            mDialog.setMessage("Please wait...");
	            mDialog.show();
	        }

			//Get inputstream from website(s) in background thread
			@Override
			protected String doInBackground(URL... urls) {
				String output = null;								//will hold JSON Data
				try {
					URL url = urls[0];								//get the first URL. This may change when multiple URLs are introduced
					HttpURLConnection urlConnection = 
							(HttpURLConnection) url.openConnection();
					try {
				        InputStream in = new BufferedInputStream(
				        		urlConnection.getInputStream());	//gets printed data from URL
				        output = readStream(in);					//read the inputStream into one string
					}
					finally{
				        urlConnection.disconnect();
				    }
				} catch (IOException e) {
					e.printStackTrace();
				}
				return output;
			}//end doInBackground
			
			//**Update progress (bar) in background**
			protected void onProgressUpdate(Integer... progress) {
		         publishProgress(progress[0]);
		    }//end onProgressUpdate

			//**Execute when doInBackground is finished**
			//Update output into Database
		    protected void onPostExecute(String result) {
		    	String[] stockName = null;
		    	String[] stockStats = null;
		    	String[] stockFrom = null;
	  	      	String[] stockParent = null;
		    	JSONArray jArray = null;
		    	
		    	try{
		    	    jArray = new JSONArray(result);
		    	    stockName = new String[jArray.length()];
		    	    stockStats = new String[jArray.length()];
		    	    stockFrom = new String[jArray.length()];
		    	    stockParent = new String[jArray.length()];
		    	    JSONObject json_data=null;
		    	      
		    	    for(int i=0;i<jArray.length();i++){
		    	    	json_data = jArray.getJSONObject(i);
		    	        stockName[i] = json_data.getString("stockName");
		    	        stockStats[i] = json_data.getString("stockStats");
		    	        stockFrom[i] = json_data.getString("stockFrom");
		    	        stockParent[i] = json_data.getString("stockParent");
		    	    }
		    	    
		    	    //update database
		    	    List<Stock> stocks = db.getStocks();
		    	    for (int x=0;x<jArray.length();x++){
		    	    	db.updateStock(new Stock(stocks.get(x).getId(), stockName[x], stockFrom[x], stockParent[x], stockStats[x]));
		    	    }
		    	    
		    	    //adding title rows
		    	    TableRow titleRow = new TableRow(activity);
		    	      
		    	    TextView titName = new TextView(activity);
		    	    titName.setText("Stock Name");
		    	    titName.setBackground(activity.getResources().getDrawable(R.drawable.cell_shape));
		    	    titName.setPadding(5, 5, 5, 5);
		    	    titleRow.addView(titName);
		    	      
		    	    TextView titStat = new TextView(activity);
		    	    titStat.setText("Quote");
		    	    titStat.setBackground(activity.getResources().getDrawable(R.drawable.cell_shape));
		    	    titStat.setPadding(5, 5, 5, 5);
		    	    titleRow.addView(titStat);
		    	      
		    	    statsTable.addView(titleRow);
		    	    
		    	    //post new stocks in table
		    	    postStockData(tabContextRef);
		    	    mDialog.dismiss();
		    	    
		    	}catch(JSONException e1){
		    	    Toast.makeText(activity.getBaseContext(), "No Data Found" ,Toast.LENGTH_LONG).show();
		    	}catch (ParseException e1){
		    		e1.printStackTrace();
		    	}
		    }//end onPostExecute
		}//end getStockStats AsyncTask
		
	//readStream is a support function for updateStockStats thread
    private String readStream(InputStream in) {
		try {
		    ByteArrayOutputStream bo = new ByteArrayOutputStream();
		    int i = in.read();
		    while(i != -1) {
		    	bo.write(i);
		    	i = in.read();
		    }
		    return bo.toString();
		    }catch (IOException e) {
		    	return "";
		    }
	}//end readStream
    
		//post stock data on table
		public void postStockData(String stockFrom){
			List<Stock> stocks = db.getStocks(stockFrom);
			for(Stock stock : stocks){
				TableRow newRow = new TableRow(activity);
		    	TextView rowName = new TextView(activity);
		    	TextView rowStat = new TextView(activity);
		    	  
		    	newRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		    	
		    	rowName.setText(stock.getName());
		    	rowName.setBackground(activity.getResources().getDrawable(R.drawable.cell_shape));
		    	rowName.setPadding(5, 5, 5, 5);
		    	newRow.addView(rowName);
		    	  
		    	rowStat.setText(stock.getStat());
		    	rowStat.setBackground(activity.getResources().getDrawable(R.drawable.cell_shape));
		    	rowStat.setPadding(5, 5, 5, 5);
		    	newRow.addView(rowStat);
		    	  
		    	statsTable.addView(newRow);
			}
		}//end postStockData
}
