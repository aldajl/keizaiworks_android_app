package keizai.works.mobile;

/*
 * !!Starting Activity!!
 * LoginActivity.java
 * created by: Joshua Alday
 * Description: Log the user in and add user information to database
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
public class LoginActivity extends Activity {
	
	URL url;					//Will be used to reference url
	EditText inLog;				//Will be used to store userid input
	EditText inPwd;				//Will be used to store passwd input
	DatabaseHandler db = null;	//Will be used to handle updates to database
	
	//Start essential methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        
        try {
			url = new URL("http://keizaiworks.mypressonline.com/droidLoginCheck.php");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
 
        inLog = (EditText) findViewById(R.id.login_usr_edt);
        inPwd = (EditText) findViewById(R.id.login_psw_edt);
        db = new DatabaseHandler(this);
        db.createUser();
        db.deleteUser();
        Button testLogin = (Button) findViewById(R.id.login_btn);
        
        testLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
                new loginTask().execute(url);
            }
        });
    }
    
    private class loginTask extends AsyncTask<URL, Integer, String>
    {
    	@Override
		protected String doInBackground(URL... urls) {
    		String output = null;
			HttpURLConnection urlConnection;
			url = urls[0];
			try {
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				urlConnection.setRequestProperty("userid", inLog.getText().toString());
				urlConnection.setRequestProperty("pass", inPwd.getText().toString());
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userid", inLog.getText().toString()));
				params.add(new BasicNameValuePair("pass", inPwd.getText().toString()));
				DataOutputStream writer = new DataOutputStream (urlConnection.getOutputStream());
				writer.writeBytes(getQuery(params));
				writer.flush();
				writer.close();

				try{
					InputStream in = new BufferedInputStream(urlConnection.getInputStream());//gets printed data from URL
			        output = readStream(in); //read the inputStream into one string
				}
				finally{
					urlConnection.disconnect();
				}
			} catch (IOException e) {
				output = "error";
				e.printStackTrace();
			}
			
	        return output;
		}//end doInBackground
		
		//**Update progress (bar) in background**
		protected void onProgressUpdate(Integer... progress) {
	         publishProgress(progress[0]);
	    }//end onProgressUpdate

		//**Execute when doInBackground is finished**
	    protected void onPostExecute(String result) {
    		JSONObject jObject = null;
	    	String userName = null;
	    	String userFrom = null;
	    	try {
				jObject = new JSONObject(result);
				userName = jObject.getString("userid");
				userFrom = jObject.getString("country");
				db.addUser(new User(userName, userFrom));
				Intent i = new Intent(getApplicationContext(), LoadingActivity.class);
                startActivity(i);
				
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(), "Log in failed. Please check userid and password" ,Toast.LENGTH_LONG).show();
			}
	    }//end onPostExecute
	}//end AsyncTask
    
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
    
    //getQuery is used to construct post data query
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }//end getQuery
}//end LoginActivity.java