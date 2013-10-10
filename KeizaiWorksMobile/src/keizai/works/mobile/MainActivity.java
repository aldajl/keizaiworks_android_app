package keizai.works.mobile;

/*
 * MainActivity.java
 * created by: Joshua Alday
 * Description: This class creates the main framework of the application
 */

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;

public class MainActivity extends Activity {
    Tab tab;//Will be used to create tabs
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //**Adding Action Bar Tabs**
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        //add national tab
        tab = actionBar.newTab().setTabListener(new NationalTab());
        tab.setText("National");
        actionBar.addTab(tab);
 
        //add local tab
        tab = actionBar.newTab().setTabListener(new LocalTab());
        tab.setText("Local");
        actionBar.addTab(tab);

        //add search/fav tab
        tab = actionBar.newTab().setTabListener(new SearchTab());
        tab.setText("Search/Fav");
        actionBar.addTab(tab);
    }//end onCreate
}//end MainActivity
