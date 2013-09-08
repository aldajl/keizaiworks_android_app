package com.stocks.keizaiworksandroid;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity{

	TextView textView;
	Tab tab;
	URL url; 
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textView = (TextView)findViewById(R.id.TextView01);
        
        
        final Button fetchDataBtn = (Button) findViewById(R.id.fetch_data_btn);
        fetchDataBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fetchStockData();
			}
		});
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void fetchStockData(){
    	try {
			url = new URL("http://keizaiworks.mypressonline.com/getStocksDroid.php");
			new getStockStats().execute(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	private class getStockStats extends AsyncTask<URL, Integer, String>{

		@Override
		protected String doInBackground(URL... urls) {
			// TODO Auto-generated method stub
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return output;
		}
		
		protected void onProgressUpdate(Integer... progress) {
	         setProgress(progress[0]);
	    }

	    protected void onPostExecute(String result) {
	    	String[] stockName = null;
	    	String[] stockStats = null;
	    	JSONArray jArray = null;
	    	
	    	try{
	    	      jArray = new JSONArray(result);
	    	      stockName = new String[jArray.length()];
	    	      stockStats = new String[jArray.length()];
	    	      jArray.length();
	    	      JSONObject json_data=null;
	    	      for(int i=0;i<jArray.length();i++){
	    	             json_data = jArray.getJSONObject(i);
	    	             stockName[i] = json_data.getString("stockName");
	    	             stockStats[i] = json_data.getString("stockStats");
	    	         }
	    	      textView.setText("");
	    	      for (int i=0;i<jArray.length();i++){
	    	    	  textView.append(stockName[i] +": "+ stockStats[i]+"\n");
	    	      }
	    	      }
	    	      catch(JSONException e1){
	    	       Toast.makeText(getBaseContext(), "No Data Found" ,Toast.LENGTH_LONG).show();
	    	      } catch (ParseException e1) {
	    	   e1.printStackTrace();
	    	 }
	    }

	    private String readStream(InputStream in) {
			// TODO Auto-generated method stub
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
		}
	}
    
    /*public void fetchStockData(){
    	//"http://keizaiworks.mypressonline.com/getStocksDroid.php"
    	
    	JSONArray jArray = null;
    	String result = null;
    	StringBuilder sb = null;
    	InputStream is = null;
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	
    	try{
    	     HttpClient httpclient = new DefaultHttpClient();


    	     //Why to use 10.0.2.2
    	     HttpPost httppost = new HttpPost("http://keizaiworks.mypressonline.com/getStocksDroid.php");
    	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	     HttpResponse response = httpclient.execute(httppost);
    	     HttpEntity entity = response.getEntity();
    	     is = entity.getContent();
    	     }catch(Exception e){
    	         Log.e("log_tag", "Error in http connection"+e.toString());
    	    }
    	//convert response to string
    	try{
    	      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
    	       sb = new StringBuilder();
    	       sb.append(reader.readLine() + "\n");

    	       String line="0";
    	       while ((line = reader.readLine()) != null) {
    	                      sb.append(line + "\n");
    	        }
    	        is.close();
    	        result=sb.toString();
    	        }catch(Exception e){
    	              Log.e("log_tag", "Error converting result "+e.toString());
    	        }

    	String[] stockName;
    	String[] stockStats;
    	try{
    	      jArray = new JSONArray(result);
    	      stockName = new String[jArray.length()];
    	      stockStats = new String[jArray.length()];
    	      jArray.length();
    	      JSONObject json_data=null;
    	      for(int i=0;i<jArray.length();i++){
    	             json_data = jArray.getJSONObject(i);
    	             stockName[i] = json_data.getString("stockName");
    	             stockStats[i] = json_data.getString("stockStats");
    	         }
    	      textView.setText("");
    	      for (int i=0;i<jArray.length();i++){
    	    	  textView.append(stockName[i] +": "+ stockStats[i]+"\n");
    	      }
    	      }
    	      catch(JSONException e1){
    	       Toast.makeText(getBaseContext(), "No Data Found" ,Toast.LENGTH_LONG).show();
    	      } catch (ParseException e1) {
    	   e1.printStackTrace();
    	 }
    	}*/
}
