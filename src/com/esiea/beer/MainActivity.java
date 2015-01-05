package com.esiea.beer;

import com.esiea.beer.fragment.MyFragmentPagerAdapter;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity implements TabListener {
	
	private ActionBar actionBar;
	private ViewPager viewPager;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Getting a reference to action bar of this activity
        actionBar = getActionBar();
        // Set tab navigation mode
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        // Getting a reference to ViewPager from the layout
        viewPager = (ViewPager) findViewById(R.id.pager);
        
        // Getting a reference to FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        
        // Defining a listener for pageChange
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        	@Override
        	public void onPageSelected(int position) {        		
        		super.onPageSelected(position);
        		actionBar.setSelectedNavigationItem(position);
        	}        	
        };
        
        // Setting the pageChange listener to the viewPager
        viewPager.setOnPageChangeListener(pageChangeListener);
        
        // Creating an instance of FragmentPagerAdapter
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(fm);

        viewPager.setAdapter(fragmentPagerAdapter);

        actionBar.setDisplayShowTitleEnabled(true);
        
        // Creating Android Tab
		ActionBar.Tab tab1 = actionBar.newTab();
        tab1.setText("Types de bière");
        tab1.setTabListener(this);

        ActionBar.Tab tab2 = actionBar.newTab();
        tab2.setText("Pays des bières");
        tab2.setTabListener(this);

        ActionBar.Tab tab3 = actionBar.newTab();
        tab3.setText("Bières du monde");
        tab3.setTabListener(this);

        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        actionBar.addTab(tab3);      
        
	}
	
	// Defining tab listener
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

}
