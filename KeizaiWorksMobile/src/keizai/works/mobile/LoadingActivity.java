package keizai.works.mobile;

/*
 * LoacingActivity.java
 * created by: Joshua Alday
 * Description: This class gets data from web and creates a database
 */

import java.io.BufferedInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

public class LoadingActivity extends Activity {
    DatabaseHandler db;//Will be used to create/insert database
    URL url;//URL to get JSON Data
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);
        
        //**Setting up Database and updating**
        db = new DatabaseHandler(this);
        db.deleteStock();//this will make sure old data is deleted
        
        //create Database
        try {
			fetchStockData();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
    }//end onCreate
    
    //**creating Database**
    public void fetchStockData() throws InterruptedException, ExecutionException, TimeoutException{
    	try {
			url = new URL("http://keizaiworks.mypressonline.com/getStocksDroid.php");
			new getStockStats().execute(url);//New Thread to keep system halt
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }//end fetchStockData
    
    //**getting stock information on new thread from keizaiworks.mypressonline.com**
    private class getStockStats extends AsyncTask<URL, Integer, String>{
    	
    	ProgressDialog mDialog;
    	
    	//This sets up a progress Dialog that displays for the user to know data is loading
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(LoadingActivity.this);
            mDialog.setMessage("Please wait...");
            mDialog.show();
        }

    	//Get inputstream from website(s) in background thread
		@Override
		protected String doInBackground(URL... urls) {
			String output = "";
			try {
				URL url = urls[0];
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				try {
			        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			        output = readStream(in);
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
		//Add output into Database
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
	    	    jArray.length();
	    	    JSONObject json_data=null;
	    	      
	    	    for(int i=0;i<jArray.length();i++){
	    	    	json_data = jArray.getJSONObject(i);
	    	        stockName[i] = json_data.getString("stockName");
	    	        stockStats[i] = json_data.getString("stockStats");
	    	        stockFrom[i] = json_data.getString("stockFrom");
	    	        stockParent[i] = json_data.getString("stockParent");
	    	    }
	    	    
	    	    //adding stocks
	    	    for (int i=0;i<jArray.length();i++){
	    	    	db.addStock(new Stock(stockName[i], stockFrom[i], stockParent[i], stockStats[i]));
	    	    }
	    	}catch(JSONException e1){
	    		Toast.makeText(getBaseContext(), "No Data Found" ,Toast.LENGTH_LONG).show();
	    	} catch (ParseException e1){
	    		e1.printStackTrace();
	    	}
	    	
	    	Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
	    	mDialog.dismiss();
	    }//end onPostExecute

	    //readStream is a support function for getStockStats thread
	    private String readStream(InputStream in) {
			try {
			      ByteArrayOutputStream bo = new ByteArrayOutputStream();
			      int i = in.read();
			      while(i != -1) {
			        bo.write(i);
			        i = in.read();
			      }
			      return bo.toString();
			} catch (IOException e) {
			    return "";
			}
		}//end readStream
	}//end getStockStats AsyncTask
}
