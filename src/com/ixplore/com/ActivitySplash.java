package com.ixplore.com;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.jwetherell.augmented_reality.activity.Demo;

import ResponseParser.GlobalVariable;
import ResponseParser.JSONParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ActivitySplash extends Activity {
	ProgressDialog Asycdialog;
	
	private double cur_lat ;
	private double cur_lng ;
	private String radius,key,tag ;

	private String country ;
	
	private Context getDialogContext() {
	    Context context;
	    if (getParent() != null) 
	    	context = getParent();
	    else 
	    	context = this;
	    
	    return context;
	}
	
	private class GetPositionTask extends AsyncTask<Void, Void, Void >{
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            
        }
		
		@Override
        protected Void doInBackground(Void... arg0) {			
			JSONParser a = new JSONParser() ;
			List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("kind", getIntent().getExtras().getString("category_id")));
            
            GlobalVariable.featured_positions = a.getJSONFromUrl("http://ixplorecanada.canadaworldapps.com/locations.php", params) ;
            
            if ( Asycdialog.isShowing() )
				Asycdialog.dismiss() ;
			
			Intent myIntent = new Intent(ActivitySplash.this, Demo.class);
			ActivitySplash.this.startActivity(myIntent);
			
			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
			
			
			
			super.onPostExecute(result);
		}
	}
	
	private class GetGooglePositionTask extends AsyncTask<Void, Void, Void >{
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();

            Asycdialog = new ProgressDialog(getDialogContext());
            Asycdialog.setMessage("Please wait. Loading...");
            Asycdialog.show();
        }
		
		@Override
        protected Void doInBackground(Void... arg0) {			
			JSONParser a = new JSONParser() ;
			if ( GlobalVariable.f_currentLocation )
    		{
				List<NameValuePair> params = new LinkedList<NameValuePair>();
	            params.add(new BasicNameValuePair("types", tag.toLowerCase()));
	            params.add(new BasicNameValuePair("lat", String.valueOf(cur_lat)));
	            params.add(new BasicNameValuePair("lng", String.valueOf(cur_lng)));
	            params.add(new BasicNameValuePair("key", key));
	            params.add(new BasicNameValuePair("radius", radius));
	            
            	GlobalVariable.reference_google_positions = a.getJSONFromUrl("http://ixplorecanada.canadaworldapps.com/phone/google_place.php", params) ;
            	new GetPositionTask().execute() ;
    		}
			else
			{
				List<NameValuePair> params = new LinkedList<NameValuePair>();
	            params.add(new BasicNameValuePair("key", key));
	            country = country.replace(" ", "%20") ;
	            params.add(new BasicNameValuePair("query", tag.toLowerCase() + "+in+" + country));
	            
	            GlobalVariable.reference_google_positions = a.getJSONFromUrl("http://ixplorecanada.canadaworldapps.com/phone/google_place_country.php", params) ;
	            
	            if ( Asycdialog.isShowing() )
					Asycdialog.dismiss() ;
				
				GlobalVariable.featured_positions = "" ;
				Intent myIntent = new Intent(ActivitySplash.this, Demo.class) ;
				ActivitySplash.this.startActivity(myIntent) ;
			}
            			
			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
			
			super.onPostExecute(result);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//new GetPositionTask().execute() ;
		tag = getIntent().getExtras().getString("category_tag") ;
		radius = "20000" ;
		key = "AIzaSyDyWCoV5_luhS16_S3_ARn5qA29t8_k-V8" ;
		GPSTracker gps = new GPSTracker(this);
		
		if(gps.canGetLocation()){
			cur_lat = gps.getLatitude(); // returns latitude
			cur_lng = gps.getLongitude(); // returns longitude
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		country = prefs.getString("country_name", "") ;
		if ( country.length() < 1 )
			country = "United States" ;
		
		new GetGooglePositionTask().execute() ;
	}	
	
}
