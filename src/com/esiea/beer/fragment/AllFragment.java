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
 
import com.esiea.beer.BeerActivity;
import com.esiea.beer.R;
import com.esiea.beer.helper.AlertDialogManager;
import com.esiea.beer.helper.ConnectionDetector;
import com.esiea.beer.helper.JSONParser;

public class AllFragment extends ListFragment{
    
    // Connection detector
    private ConnectionDetector cd;
 	// Alert dialog manager
    private AlertDialogManager alert = new AlertDialogManager();
 
    // Creating JSON Parser object
    private JSONParser jsonParser = new JSONParser();
    private JSONArray all = null;
    private ArrayList<HashMap<String, String>> allList;
 
    // Beer JSON URL
    private static final String URL_ALL = "http://binouze.fabrigli.fr/bieres.json";
    // JSON node names
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_BUVEUR = "buveur";


    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
	 
		super.onActivityCreated(savedInstanceState);
		
		cd = new ConnectionDetector(AllFragment.this.getActivity());
        
		// Check for Internet connection	
        if (!cd.isConnectingToInternet()) {
        	// Internet Connection is not present
            alert.showAlertDialog(AllFragment.this.getActivity(), " ", "Pas de connection Internet", false);
            // Stop executing code by return
            return;
        }
        
        Toast.makeText(AllFragment.this.getActivity(), "Mise à jour...", Toast.LENGTH_SHORT).show();
	    
        // HashMap for ListView
	    allList = new ArrayList<HashMap<String, String>>();
	 
	    // Loading All Beer JSON in Background Thread
	    new LoadAll().execute();
	    
	 	// Get listView
	    ListView lv = getListView();
	         
	    lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	    
	    	@Override
	    	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
	    		
	    		// On selecting a beer
                // BeerActivity will be launched to show the beer description
	    		Intent i = new Intent(AllFragment.this.getActivity().getApplicationContext(), BeerActivity.class);
	            
	    		// Send beer id to BeerActivity to get the right beer
	    		String beer_id = ((TextView) view.findViewById(R.id.beer_id)).getText().toString();
	    		i.putExtra("beer_id", beer_id);  
	    		
	    		startActivity(i);
	                
	    	}
	    	
	    });
	    
	}
	
    
    // Background AsyncTask to Load all beer by making HTTP Request
	class LoadAll extends AsyncTask<String, String, String> {
	 
       // Getting All beer JSON
	   protected String doInBackground(String... args) {
		   
		   // Building Parameters
		   List<NameValuePair> params = new ArrayList<NameValuePair>();
	       
		   // Getting JSON string from URL
		   String json = jsonParser.makeHttpRequest(URL_ALL, "GET", params);
	 
		   // Check log cat for JSON response
	       Log.d("All JSON: ", "> " + json);
	 
	       try {               
	    	   
	    	   all = new JSONArray(json);
	    	   
	    	   		if (all != null) {

	    	   			// Looping through All beer
	    	   			for (int i = 0; i < all.length(); i++) {
	    	   				
	                        JSONObject c = all.getJSONObject(i);
	 
	                     	// Storing each JSON item values in variable
	                        String id = c.getString(TAG_ID);
	                        String name = c.getString(TAG_NAME);
	                        String category = c.getString(TAG_CATEGORY);
	                        String buveur = c.getString(TAG_BUVEUR);
	 
	                        // Creating new HashMap
	                        HashMap<String, String> map = new HashMap<String, String>();
	 
	                        // Adding each child node to HashMap key => value
	                        map.put(TAG_ID, id);
	                        map.put(TAG_NAME, name);
	                        
	                        if(category.equals("null"))
	                        	map.put(TAG_CATEGORY, "Autre");
	                        else
	                        	map.put(TAG_CATEGORY, category);
	                        
	                        if(buveur.equals("null"))
	                        	map.put(TAG_BUVEUR, "Inconnu (ou mort de soif !)");
	                        else
	                        	map.put(TAG_BUVEUR, buveur);
	 
	                        // Adding HashList to ArrayList
	                        allList.add(map);
	                        
	                    }
	                }else {
	                    Log.d("All: ", "null");
	                }
	 
	       } catch (JSONException e) {
	       		e.printStackTrace();
	       }
	 
	       return null;
	            
	   }

       // After completing background task Dismiss the progress dialog
	   protected void onPostExecute(String file_url) {

		   // Updating UI from Background Thread
		   AllFragment.this.getActivity().runOnUiThread(new Runnable() {
	       
			   public void run() {
	                    
				   ListAdapter adapter = new SimpleAdapter(AllFragment.this.getActivity(), allList,
	                   R.layout.list_item_all, new String[] {TAG_ID, TAG_NAME, TAG_CATEGORY, TAG_BUVEUR}, 
	                    	new int[] {R.id.beer_id, R.id.beer_name, R.id.beer_category, R.id.beer_buveur});
				   
				   // Updating listView 
				   setListAdapter(adapter);
				   
	           }
			   
		   });
	 
	   }
	   
	}

}