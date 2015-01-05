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
import com.esiea.beer.listActivity.TypesListActivity;

public class TypeFragment extends ListFragment{
    
    // Connection detector
    private ConnectionDetector cd;
 	// Alert dialog manager
    private AlertDialogManager alert = new AlertDialogManager();
 
    // Creating JSON Parser object
    private JSONParser jsonParser = new JSONParser();
    private JSONArray types = null;
    private ArrayList<HashMap<String, String>> typesList;
 
 	// Categories JSON URL
    private static final String URL_TYPES = "http://binouze.fabrigli.fr/categories.json";
 	// JSON node names
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";

    
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
	 
		super.onActivityCreated(savedInstanceState);
		
		cd = new ConnectionDetector(TypeFragment.this.getActivity());
        
		// Check for Internet connection	
        if (!cd.isConnectingToInternet()) {
        	// Internet Connection is not present
            alert.showAlertDialog(TypeFragment.this.getActivity(), " ", "Pas de connection Internet", false);
            // Stop executing code by return
            return;
        }
        
        Toast.makeText(TypeFragment.this.getActivity(), "Mise à jour...", Toast.LENGTH_SHORT).show();
        
        // HashMap for ListView
	    typesList = new ArrayList<HashMap<String, String>>();
	 
	    // Loading beer Categories JSON in Background Thread
	    new LoadTypes().execute();
	    
	    // Get listView
	    ListView lv = getListView();
	         
	    lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	    
	    	@Override
	    	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
	    		
	    		// On selecting a category
                // TypesListActivity will be launched to show the beers category
	    		Intent i = new Intent(TypeFragment.this.getActivity().getApplicationContext(), TypesListActivity.class);
	                 
	    		// Send category and category id to TypesListActivity
	    		String type_id = ((TextView) view.findViewById(R.id.type_id)).getText().toString();
	    		i.putExtra("type_id", type_id);
	    		String category = ((TextView) view.findViewById(R.id.type_name)).getText().toString();
	    		i.putExtra("category", category);
	                 
	    		startActivity(i);
	                
	    	}
	    	
	    });
	    
	}
	

 	// Background AsyncTask to Load beer categories by making HTTP Request
	class LoadTypes extends AsyncTask<String, String, String> {
	 
		// Getting categories JSON
		protected String doInBackground(String... args) {
		   
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
		       
			// Getting JSON string from URL
		    String json = jsonParser.makeHttpRequest(URL_TYPES, "GET", params);
	 
		    // Check log cat for JSON response
	        Log.d("types JSON: ", "> " + json);
	 
	        try {               
	    	   
	    	   types = new JSONArray(json);
	    	   
	    	   		if (types != null) {

	    	   			// Looping through categories
	    	   			for (int i = 0; i < types.length(); i++) {
	    	   				
	                        JSONObject c = types.getJSONObject(i);
	 
	                        // Storing each JSON item values in variable
	                        String id = c.getString(TAG_ID);
	                        String name = c.getString(TAG_NAME);
	 
	                     	// Creating new HashMap
	                        HashMap<String, String> map = new HashMap<String, String>();
	 
	                        // Adding each child node to HashMap key => value
	                        map.put(TAG_ID, id);
	                        map.put(TAG_NAME, name);
	 
	                        // Adding HashList to ArrayList
	                        typesList.add(map);
	                        
	                    }
	    	   			
	    	   		// Creating new HashMap
	    	   		HashMap<String, String> map = new HashMap<String, String>();
	    	   		 
	    	   		// Adding each child node to HashMap key => value
                    map.put(TAG_ID, "null");
                    map.put(TAG_NAME, "Autre");
 
                    // Adding HashList to ArrayList
                    typesList.add(map);
	    	   			
	                }else {
	                    Log.d("Types: ", "null");
	                }
	 
	        } catch (JSONException e) {
	       		e.printStackTrace();
	        }
	 
	        return null;
	            
	    }

		// After completing background task Dismiss the progress dialog
	    protected void onPostExecute(String file_url) {

		    // Updating UI from Background Thread
		    TypeFragment.this.getActivity().runOnUiThread(new Runnable() {
	       
			    public void run() {
	                    
			 	   ListAdapter adapter = new SimpleAdapter(TypeFragment.this.getActivity(), typesList,
	                    R.layout.list_item_types, new String[] {TAG_ID, TAG_NAME}, 
	                    	new int[] {R.id.type_id, R.id.type_name});
	                    
			 	   // Updating listView 
			 	   setListAdapter(adapter);
	            }
			   
		    });
	 
	    }
	   
	}

}