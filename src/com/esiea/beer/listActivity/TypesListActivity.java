package com.esiea.beer.listActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esiea.beer.BeerActivity;
import com.esiea.beer.R;
import com.esiea.beer.helper.AlertDialogManager;
import com.esiea.beer.helper.ConnectionDetector;
import com.esiea.beer.helper.JSONParser;
 
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
public class TypesListActivity extends ListActivity {
	
	private Menu myMenu;

	// Connection detector
    private ConnectionDetector cd;
 	// Alert dialog manager
    private AlertDialogManager alert = new AlertDialogManager();
 
    // Creating JSON Parser object
    private JSONParser jsonParser = new JSONParser();
    private JSONArray types = null;
    private ArrayList<HashMap<String, String>> typesList;
    private String category, id_category;
 
    // Beer JSON URL
    private static final String URL_TYPES = "http://binouze.fabrigli.fr/bieres.json";
 	// JSON node names
    private static final String TAG_ID = "id";
    private static final String TAG_CATEGORY_ID = "category_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_BUVEUR = "buveur";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
         
        cd = new ConnectionDetector(getApplicationContext());
        
        // HashMap for ListView   
        typesList = new ArrayList<HashMap<String, String>>();
        
        // Get category id and name
        Intent i = getIntent();
        category = i.getStringExtra("category");
        id_category = i.getStringExtra("type_id");
        
        setTitle("Catégorie : " + category);
        
        // Check for Internet connection	
        if (!cd.isConnectingToInternet()) {
        	// Internet Connection is not present
            alert.showAlertDialog(TypesListActivity.this, " ", "Pas de connection Internet", false);
            // Stop executing code by return
            return;
        }
        
        Toast.makeText(this.getApplicationContext(), "Mise à jour...", Toast.LENGTH_SHORT).show();
        
        // Loading Beer category JSON in Background Thread
	    new LoadTypes().execute();
	         
	    // Get listView
	    ListView lv = getListView();
	         
	    lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	    
	    	@Override
	    	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
	            
	    		// On selecting a beer
                // BeerActivity will be launched to show the beer description
	    		Intent i = new Intent(getApplicationContext(), BeerActivity.class);
	                 
	    		// Send beer id to BeerActivity to get the right beer
	    		String beer_id = ((TextView) view.findViewById(R.id.beer_id)).getText().toString();
	    		i.putExtra("beer_id", beer_id);  
	    		
	    		startActivity(i);
	                
	    	}
	    	
	    });
 
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
		getMenuInflater().inflate(R.menu.main, menu);
		myMenu = menu;
		
		cd = new ConnectionDetector(getApplicationContext());
		
		// Rotate item if Internet connection	
        if (cd.isConnectingToInternet()) {
        	
			MenuItem item = menu.findItem(R.id.action_refresh);
	        
	        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
	        ImageView iv = (ImageView)inflater.inflate(R.layout.iv_refresh, null);
	        
	        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
	        
	        rotation.setRepeatCount(Animation.INFINITE);
	        iv.startAnimation(rotation);
	        item.setActionView(iv);
        
        }
        
		return true;
		
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
    	switch(item.getItemId()) {
        
	    	case R.id.action_refresh:
	    		
	    		cd = new ConnectionDetector(getApplicationContext());
	            
	            // Check for Internet connection	
	            if (!cd.isConnectingToInternet()) {
	            	// Internet Connection is not present
	                alert.showAlertDialog(TypesListActivity.this, " ", "Pas de connection Internet", false);
	                // Stop executing code by return
	                return false;
	            }
	            
	    		Toast.makeText(this.getApplicationContext(), "Mise à jour...", Toast.LENGTH_SHORT).show();
	    		
	    		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		
	            ImageView iv = (ImageView)inflater.inflate(R.layout.iv_refresh, null);
	            
	            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
	            
	            rotation.setRepeatCount(Animation.INFINITE);
	            iv.startAnimation(rotation);
	            item.setActionView(iv);
	            
	            new LoadTypes().execute();
	            
	            // Get listView
	    	    ListView lv = getListView();
	    	         
	    	    lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	    	    
	    	    	@Override
	    	    	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
	    	              
	    	    		// On selecting a beer
	                    // BeerActivity will be launched to show the beer description
	    	    		Intent i = new Intent(getApplicationContext(), BeerActivity.class);
	    	            
	    	    		// Send beer id to BeerActivity to get the right beer
	    	    		String beer_id = ((TextView) view.findViewById(R.id.beer_id)).getText().toString();
	    	    		i.putExtra("beer_id", beer_id);  
	    	    		
	    	    		startActivity(i);

	    	    	}
	    	    	
	    	    });
	        	
	            return true;
	    		
    	}
    	
    	return super.onOptionsItemSelected(item);
    	
    }
    
    public void resetUpdating()
    {

    	MenuItem m = myMenu.findItem(R.id.action_refresh);
        
    	if(m.getActionView() != null)
        {
            m.getActionView().clearAnimation();
            m.setActionView(null);
        }
    	
    }


 	// Background AsyncTask to Load beer category by making HTTP Request
    class LoadTypes extends AsyncTask<String, String, String> {
	 
        // Getting beer category JSON
        protected String doInBackground(String... args) {
        	
        	// Temporary list to continue using the previous
        	ArrayList<HashMap<String, String>> typesList2 = new ArrayList<HashMap<String, String>>();
        	
           // Building Parameters
 		   List<NameValuePair> params = new ArrayList<NameValuePair>();
 	       
 		    // Getting JSON string from URL
		    String json = jsonParser.makeHttpRequest(URL_TYPES, "GET", params);
	 
		    // Check log cat for JSON response
	        Log.d("types JSON: ", "> " + json);
	 
	        try {               
	    	   
	    	    types = new JSONArray(json);
	    	    
	    	    	if (types != null) {

	    	    		// Looping through beers category
	    	   			for (int i = 0; i < types.length(); i++) {
	    	   				
	                        JSONObject c = types.getJSONObject(i);
	 
	                        // Storing each JSON item values in variable
	                        String id = c.getString(TAG_ID);
	                        String name = c.getString(TAG_NAME);
	                        String category = c.getString(TAG_CATEGORY);
	                        String buveur = c.getString(TAG_BUVEUR);
	                        String category_id = c.getString(TAG_CATEGORY_ID);
	                       
	                        // Taking only beers corresponding to the category selected
	                        if(id_category.equals(category_id)) {
	                        	
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
		                        typesList2.add(map);
		                        
	                        }
	                        
	                        typesList.clear();
	                        typesList = new ArrayList<HashMap<String, String>>();
	                        typesList.addAll(typesList2);
	                        
	    	   			}
	    	   			
	                }else {
	                    Log.d("types: ", "null");
	                }
	 
	        }catch (JSONException e) {
	       		e.printStackTrace();
	        }
	 
	        return null;
	            
        }

     	// After completing background task Dismiss the progress dialog
	    protected void onPostExecute(String file_url) {

	    	// Reset animation refresh
	    	resetUpdating();
	    			
		 	// Updating UI from Background Thread
		    runOnUiThread(new Runnable() {
	       
		    	public void run() {
	                    
		    		ListAdapter adapter = new SimpleAdapter(TypesListActivity.this, typesList,
                   		 R.layout.list_item_all, new String[] {TAG_ID, TAG_NAME, TAG_CATEGORY, TAG_BUVEUR}, 
                   			 new int[] {R.id.beer_id, R.id.beer_name, R.id.beer_category, R.id.beer_buveur});

		    		// Updating listView
		    		setListAdapter(adapter);
				   
		    	}
			   
		    });
	 
	    }
	   
	}

}