package keizai.works.mobile;

/*
 * LocalTab.java
 * created by: Joshua Alday
 * description: This class creates the Local Tab and displays information on stocks
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class LocalTab extends Fragment implements ActionBar.TabListener {
 
    private Fragment mFragment = null;
    
    TableLayout statsTable = null;	//Will be used to create Table that will display Stock info
    TextView userName_lbl = null;	//Will be used to display UserName
    URL url = null;					//Will be used to get JSON Data
    DatabaseHandler db = null;		//Will be used to update Database
    String tabContextRef = null;	//Will be used to know which stocks to pull from
    LoadTask loadTask = null;		//Will be used to load/update database
    User user = null;				//Will be used to store user data
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.local_layout);			//Use national_layout as tab layout
        
        //Setting up url and creating loadTask thread that will update database
        try {
			url = new URL("http://keizaiworks.mypressonline.com/getStocksDroid.php");
			loadTask = new LoadTask(getActivity(), url, tabContextRef, (TableLayout) getActivity().findViewById(R.id.local_stats_table));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        statsTable = (TableLayout) 
        		getActivity().findViewById(R.id.local_stats_table);		//Will be used to add/edit table
        db = new DatabaseHandler(getActivity());						//Gets the current Database to update
        userName_lbl = (TextView) 
        		getActivity().findViewById(R.id.local_username_lbl);
        user = db.getUser(1);
        userName_lbl.setText(user.getUserName());
        tabContextRef = user.getUserFrom();
        postStockData(tabContextRef);									//create tables with info in db
        
        final Button fetchDataBtn = (Button) 
        		getActivity().findViewById(R.id.local_fetch_data_btn);	//Fetch button to update db and post new data
        fetchDataBtn.setOnClickListener(new View.OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
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
    	statsTable.removeAllViewsInLayout();	//Will remove all tables so information isn't posted twice
    	loadTask.execute();						//ASynceTask Thread, will update db and post data
    }//end fetchStockData
	
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
