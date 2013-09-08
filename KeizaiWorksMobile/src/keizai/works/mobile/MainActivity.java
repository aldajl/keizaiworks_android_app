package keizai.works.mobile;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;

public class MainActivity extends Activity {
    Tab tab;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        ActionBar actionBar = getActionBar();
 
        actionBar.setDisplayShowHomeEnabled(false);
 
        actionBar.setDisplayShowTitleEnabled(false);
 
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        tab = actionBar.newTab().setTabListener(new NationalTab());
        tab.setText("National");
        actionBar.addTab(tab);
 
        tab = actionBar.newTab().setTabListener(new FragmentTab2());
        tab.setText("Local");
        actionBar.addTab(tab);
 
        tab = actionBar.newTab().setTabListener(new FragmentTab3());
        tab.setText("Search/Fav");
        actionBar.addTab(tab);
    }
}
