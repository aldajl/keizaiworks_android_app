package keizai.works.mobile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NationalTab extends Fragment implements ActionBar.TabListener {
 
    private Fragment mFragment;
    TextView textView;
    URL url;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from fragment1.xml
        getActivity().setContentView(R.layout.national_layout);
        
        textView = (TextView) getActivity().findViewById(R.id.TextView01);
        final Button fetchDataBtn = (Button)getActivity().findViewById(R.id.fetch_data_btn);
        fetchDataBtn.setOnClickListener(new View.OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			fetchStockData();
    		}
    	});
    }
 
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
        mFragment = new NationalTab();
        // Attach fragment1.xml layout
        ft.add(android.R.id.content, mFragment);
        ft.attach(mFragment);
    }
 
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
        // Remove fragment1.xml layout
        ft.remove(mFragment);
    }
 
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
 
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
	         publishProgress(progress[0]);
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
	    	       Toast.makeText(getActivity().getBaseContext(), "No Data Found" ,Toast.LENGTH_LONG).show();
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
 
}
