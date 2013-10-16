package keizai.works.mobile;

import android.os.Bundle;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar;

public class SearchTab extends Fragment implements ActionBar.TabListener {

   private Fragment mFragment;

   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       getActivity().setContentView(R.layout.search_layout);
   }

   public void onTabSelected(Tab tab, FragmentTransaction ft) {
       // TODO Auto-generated method stub
       mFragment = new SearchTab();
       ft.add(android.R.id.content, mFragment);
       ft.attach(mFragment);
   }

   public void onTabUnselected(Tab tab, FragmentTransaction ft) {
       // TODO Auto-generated method stub
       ft.remove(mFragment);
   }

   public void onTabReselected(Tab tab, FragmentTransaction ft) {
       // TODO Auto-generated method stub

   }

}
