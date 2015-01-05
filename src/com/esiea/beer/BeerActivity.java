package com.esiea.beer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
import com.esiea.beer.helper.AlertDialogManager;
import com.esiea.beer.helper.ConnectionDetector;
import com.esiea.beer.helper.JSONParser;
 
public class BeerActivity extends Activity {
	
	private Menu myMenu;

	// Connection detector
	private ConnectionDetector cd;
	// Alert dialog manager
	private AlertDialogManager alert = new AlertDialogManager();
    
    ImageView img;
    Bitmap bitmap = null;
 
    // Creating JSON Parser object
    private JSONParser jsonParser = new JSONParser();
    private String beer_id = null;
    private String beer_name, country_name = "null", category, description, buveur, url, note;
    
    // beer JSON URL
    private static final String URL_BEER = "http://binouze.fabrigli.fr/bieres/";
    // JSON node names
    private static final String TAG_NAME = "name";
    private static final String TAG_COUNTRY = "country";
    private static final String TAG_COUNTRY_NAME = "name";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_BUVEUR = "buveur";
    private static final String TAG_NOTE = "note";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_IMAGE = "image";
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer);
        
        // Get beer id
        Intent i = getIntent();
        beer_id = i.getStringExtra("beer_id");
        
        setTitle("N° : " + beer_id + " -");
        
        img = (ImageView) findViewById(R.id.img);
        
        cd = new ConnectionDetector(getApplicationContext());
        
        // Check for Internet connection	
        if (!cd.isConnectingToInternet()) {
        	// Internet Connection is not present
            alert.showAlertDialog(BeerActivity.this, " ", "Pas de connection Internet", false);
            // Stop executing code by return
            return;
        }
        
        Toast.makeText(this.getApplicationContext(), "Bière n° : " + beer_id, Toast.LENGTH_SHORT).show();
        
        new LoadBeer().execute();
         
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
	                alert.showAlertDialog(BeerActivity.this, " ", "Pas de connection Internet", false);
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
	            
	            img = (ImageView) findViewById(R.id.img);
	            
	            new LoadBeer().execute();
	        	
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
    
    
    // Background AsyncTask to Load the beer by making HTTP Request
    class LoadBeer extends AsyncTask<String, String, Bitmap> {
    	
     	// Getting the beer JSON
        protected Bitmap doInBackground(String... args) {

        	List<NameValuePair> params = new ArrayList<NameValuePair>();
        	
        	// Getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL_BEER + beer_id + ".json", "GET", params);
 
         	// Check log cat for JSON response
            Log.d("The beer JSON: ", "> " + json);
 
            try {
            	
                JSONObject jObj = new JSONObject(json);
                JSONObject country = jObj.optJSONObject(TAG_COUNTRY);
                JSONObject image = jObj.getJSONObject(TAG_IMAGE);
                
            	// Storing each JSON item values in variable
                if(jObj != null){
                	
                    beer_name = jObj.getString(TAG_NAME);
                    
                    if(country != null)
                    	country_name = country.optString(TAG_COUNTRY_NAME);
                    
                    category = jObj.getString(TAG_CATEGORY);
                    description = jObj.getString(TAG_DESCRIPTION);
                    buveur = jObj.getString(TAG_BUVEUR);
                    note = jObj.getString(TAG_NOTE);
                    
                    // Getting beer image
                    url = image.getJSONObject(TAG_IMAGE).getString("url");
                    bitmap = BitmapFactory.decodeStream((InputStream)new URL("http://binouze.fabrigli.fr" + url).getContent());
                
                }           
 
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
 
            return bitmap;
            
        }
 
     	// After completing background task Dismiss the progress dialog
        protected void onPostExecute(final Bitmap image) {
        	
        	// Reset animation refresh
        	resetUpdating();
        	
            runOnUiThread(new Runnable() {
            	
                public void run() {
                    // Updating parsed JSON data into ListView
                    // Storing each JSON item in variable
                    TextView txt_beer_name = (TextView) findViewById(R.id.beer);
                    TextView txt_category = (TextView) findViewById(R.id.category);
                    TextView txt_country_name = (TextView) findViewById(R.id.country);
                    TextView txt_buveur = (TextView) findViewById(R.id.buveur);
                    TextView txt_note = (TextView) findViewById(R.id.note);
                    TextView txt_description = (TextView) findViewById(R.id.description);
                    
                    if(image != null)
                        img.setImageBitmap(image);
                 
                    // Displaying all data in textView
                    txt_beer_name.setText(beer_name);
                    
                    if(category.equals("null"))
                    	txt_category.setText(Html.fromHtml("<b>Catégorie :</b> Autre"));
                    else
                    	txt_category.setText(Html.fromHtml("<b>Catégorie :</b> " + category));
                    
                    if(country_name.equals("null"))
                    	txt_country_name.setText(Html.fromHtml("<b>Pays :</b> " + "Inconnu"));
                    else
                    	txt_country_name.setText(Html.fromHtml("<b>Pays :</b> " + country_name));
                    
                    if(buveur.equals("null"))
                    	txt_buveur.setText(Html.fromHtml("<b>Buveur :</b> Inconnu (ou mort de soif !)"));
                    else
                    	txt_buveur.setText(Html.fromHtml("<b>Buveur :</b> " + buveur));
                    
                    if(note.equals("null"))
                    	txt_note.setText(Html.fromHtml("<b>Note :</b> Inconnue"));
                    else
                    	txt_note.setText(Html.fromHtml("<b>Note :</b> " + note));
                    
                    if(buveur.equals("null"))
                    	txt_description.setText(Html.fromHtml("<b>Description :</b> Faut boire pour savoir !"));
                    else
                    	txt_description.setText(Html.fromHtml("<b>Description :</b> " + description));
                    
                    setTitle("N° : " + beer_id + " - " + beer_name);
                    
                }
                
            });
 
        }
 
    }
    
}