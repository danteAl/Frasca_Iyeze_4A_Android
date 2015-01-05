package com.esiea.beer.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	
	final int PAGE_COUNT = 3;
	
	public MyFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	//This method will be invoked when a page is requested to create
	@Override
	public Fragment getItem(int arg0) {
		
		// Android tab is selected
		switch(arg0) {
		
			case 0:
				TypeFragment typeFragment = new TypeFragment();
				return typeFragment;
				
			case 1:
				CountryFragment countryFragment = new CountryFragment();
				return countryFragment;
				
			case 2:
				AllFragment allFragment = new AllFragment();
				return allFragment;
				
		}
		
		return null;
	}

	//Returns the number of pages
	@Override
	public int getCount() {		
		return PAGE_COUNT;
	}
	
}
