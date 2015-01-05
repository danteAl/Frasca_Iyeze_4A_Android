package com.esiea.beer.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
import com.esiea.beer.R;
import com.esiea.beer.helper.AlertDialogManager;
import com.esiea.beer.helper.ConnectionDetector;
import com.esiea.beer.helper.JSONParser;
import com.esiea.beer.listActivity.CountriesListActivity;

public class CountryFragment extends ListFragment {
    
    // Connection detector
    private ConnectionDetector cd;
 	// Alert dialog manager
    private AlertDialogManager alert = new AlertDialogManager();
    
    // Creating JSON Parser object
    private JSONParser jsonParser = new JSONParser();
    private JSONArray countries = null;
    private ArrayList<HashMap<String, String>> countriesList;
 
    // Countries JSON URL
    private static final String URL_COUNTRIES = "http://binouze.fabrigli.fr/countries.json";
    // JSON node names
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";

    
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
	 
		super.onActivityCreated(savedInstanceState);
		
		cd = new ConnectionDetector(CountryFragment.this.getActivity());
	         
		// Check for Internet connection	
        if (!cd.isConnectingToInternet()) {
        	// Internet Connection is not present
            alert.showAlertDialog(CountryFragment.this.getActivity(), " ", "Pas de connection Internet", false);
            // Stop executing code by return
            return;
        }
	    
        Toast.makeText(CountryFragment.this.getActivity(), "Mise à jour...", Toast.LENGTH_SHORT).show();
        
        // HashMap for ListView
	    countriesList = new ArrayList<HashMap<String, String>>();
	 
	    // Loading beer Countries JSON in Background Thread
	    new LoadCountries().execute();
	         
	    ListView lv = getListView();
	         
	    lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	    
	    	@Override
	    	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
	              
	    		// On selecting a country
                // CountriesListActivity will be launched to show all beer corresponding
	    		Intent i = new Intent(CountryFragment.this.getActivity().getApplicationContext(), CountriesListActivity.class);
	                 
	    		// Send country id and country name to CountriesListActivity
	    		String country_id = ((TextView) view.findViewById(R.id.country_id)).getText().toString();
	    		i.putExtra("country_id", country_id);
	    		String country_name = ((TextView) view.findViewById(R.id.country_name)).getText().toString();
	    		i.putExtra("country", country_name);
	                 
	    		startActivity(i);
	                
	    	}
	    	
	    });
	    
	}
	

    // Background AsyncTask to Load all beer countries by making HTTP Request
	class LoadCountries extends AsyncTask<String, String, String> {
	 
		// Getting beer countries JSON
		protected String doInBackground(String... args) {
		   
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
	       
			// Getting JSON string from URL
			String json = jsonParser.makeHttpRequest(URL_COUNTRIES, "GET", params);
	 
			// Check log cat for JSON response
			Log.d("countries JSON: ", "> " + json);
	 
			try {               
	    	   
	    	   countries = new JSONArray(json);
	    	   
	    	   		// Looping through countries
	    	   		if (countries != null) {

	    	   			for (int i = 0; i < countries.length(); i++) {
	    	   				
	                        JSONObject c = countries.getJSONObject(i);
	 
	                        // Storing each JSON item values in variable
	                        String id = c.getString(TAG_ID);
	                        String name = c.getString(TAG_NAME);
	 
	                        // Creating new HashMap
	                        HashMap<String, String> map = new HashMap<String, String>();
	 
	                        // Adding each child node to HashMap key => value
	                        map.put(TAG_ID, id);
	                        map.put(TAG_NAME, name);
	 
	                        // Adding HashList to ArrayList
	                        countriesList.add(map);
	                        
	                    }
	    	   			
	                }else {
	                    Log.d("countries: ", "null");
	                }
	 
	       } catch (JSONException e) {
	       		e.printStackTrace();
	       }
	 
	       return null;
	            
	   }

	   // After completing background task Dismiss the progress dialog
	   protected void onPostExecute(String file_url) {

		   // Updating UI from Background Thread
		   CountryFragment.this.getActivity().runOnUiThread(new Runnable() {
	       
			   public void run() {
	                    
				   ListAdapter adapter = new SimpleAdapter(CountryFragment.this.getActivity(), countriesList,
	                    R.layout.list_item_countries, new String[] {TAG_ID, TAG_NAME}, 
	                    	new int[] {R.id.country_id, R.id.country_name});
	                     
				   // Updating listView 
				   setListAdapter(adapter);
				   
	            }
			   
		   });
	 
	   }
	   
	}

}