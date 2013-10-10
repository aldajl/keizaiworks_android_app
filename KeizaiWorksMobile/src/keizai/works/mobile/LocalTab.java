package keizai.works.mobile;

/*
 * LocalTab.java
 * created by: Joshua Alday
 * description: This class creates the Local Tab and displays information on stocks
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class LocalTab extends Fragment implements ActionBar.TabListener {
 
    private Fragment mFragment;
    TableLayout statsTable;//Will be used to create Table that will display Stock info
    URL url;//Will be used to get JSON Data
    DatabaseHandler db;//Will be used to update Database
    String tabContextRef = "japan";//Will be used to know which stocks to pull from
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.local_layout);//Use national_layout as tab layout
        
        statsTable = (TableLayout) getActivity().findViewById(R.id.local_stats_table);//Will be used to add/edit table
        db = new DatabaseHandler(getActivity());//Gets the current Database to update
        postStockData(tabContextRef);//create tables with info in db
        
        final Button fetchDataBtn = (Button)getActivity().findViewById(R.id.local_fetch_data_btn);//Fetch button to update db and post new data
        fetchDataBtn.setOnClickListener(new View.OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			statsTable.removeAllViewsInLayout();//Will remove all tables so information isn't posted twice
    			fetchStockData();
    		}
    	});
    }//end onCreate
 
    //!! Start Auxiliary Functions
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        mFragment = new LocalTab();
        ft.add(android.R.id.content, mFragment);
        ft.attach(mFragment);
    }
 
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        ft.remove(mFragment);
    }
 
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
 
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    }
    //!! End Auxiliary Functions
    
    //**updating Database**
    public void fetchStockData(){
    	try {
			url = new URL("http://keizaiworks.mypressonline.com/getStocksDroid.php");
			new updateStockStats().execute(url);//ASynceTask Thread, will update db and post data
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }//end fetchStockData
    
    //**updating stock information on new thread from keizaiworks.mypressonline.com**
	private class updateStockStats extends AsyncTask<URL, Integer, String>{

		//Get inputstream from website(s) in background thread
		@Override
		protected String doInBackground(URL... urls) {
			String output = null;//will hold JSON Data
			try {
				URL url = urls[0];//get the first URL. This may change when multiple URLs are introduced
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				try {
			        InputStream in = new BufferedInputStream(urlConnection.getInputStream());//gets printed data from URL
			        output = readStream(in);//read the inputStream into one string
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
	    	    
	    	    //adding title rows
	    	    TableRow titleRow = new TableRow(getActivity());
	    	      
	    	    TextView titName = new TextView(getActivity());
	    	    titName.setText("Stock Name");
	    	    titName.setBackground(getResources().getDrawable(R.drawable.cell_shape));
	    	    titName.setPadding(5, 5, 5, 5);
	    	    titleRow.addView(titName);
	    	      
	    	    TextView titStat = new TextView(getActivity());
	    	    titStat.setText("Quote");
	    	    titStat.setBackground(getResources().getDrawable(R.drawable.cell_shape));
	    	    titStat.setPadding(5, 5, 5, 5);
	    	    titleRow.addView(titStat);
	    	      
	    	    statsTable.addView(titleRow);
	    	    
	    	    //update database
	    	    List<Stock> stocks = db.getStocks();
	    	    for (int i=0;i<jArray.length();i++){
	    	    	db.updateStock(new Stock(stocks.get(i).getId(), stockName[i], stockFrom[i], stockParent[i], stockStats[i]));
	    	    }
	    	    
	    	    //post new stocks in table
	    	    postStockData(tabContextRef);
	    	    
	    	}catch(JSONException e1){
	    	    Toast.makeText(getActivity().getBaseContext(), "No Data Found" ,Toast.LENGTH_LONG).show();
	    	}catch (ParseException e1){
	    		e1.printStackTrace();
	    	}
	    }//end onPostExecute

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
	}//end getStockStats AsyncTask
	
	//post stock data on table
	public void postStockData(String stockFrom){
		List<Stock> stocks = db.getStocks(stockFrom);
		for(Stock stock : stocks){
			TableRow newRow = new TableRow(getActivity());
	    	TextView rowName = new TextView(getActivity());
	    	TextView rowStat = new TextView(getActivity());
	    	  
	    	newRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    	
	    	rowName.setText(stock.getName());
	    	rowName.setBackground(getResources().getDrawable(R.drawable.cell_shape));
	    	rowName.setPadding(5, 5, 5, 5);
	    	newRow.addView(rowName);
	    	  
	    	rowStat.setText(stock.getStat());
	    	rowStat.setBackground(getResources().getDrawable(R.drawable.cell_shape));
	    	rowStat.setPadding(5, 5, 5, 5);
	    	newRow.addView(rowStat);
	    	  
	    	statsTable.addView(newRow);
		}
	}//end postStockData
}//end NationalTab
