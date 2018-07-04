package com.ixplore.com;

import java.util.ArrayList;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ResponseParser.GlobalVariable;
import ResponseParser.JSONParser;
import ResponseParser.XMLParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import com.revmob.RevMob;

public class ActivityMain extends Activity {
	ProgressDialog Asycdialog;
	String jsonContent ;
	SharedPreferences prefs ;
	Locale cur_locale ;
	ImageView imgSelectCountry ;
	int n = 0 ;
	double cur_lat, cur_lng ;
	
	private Context getDialogContext() {
	    Context context;
	    if (getParent() != null) 
	    	context = getParent();
	    else 
	    	context = this;
	    
	    return context;
	}
	
	private class GetCountryTask extends AsyncTask<Void, Void, Void >{
		
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
			
        	String res = a.getJSONFromUrl("http://maps.googleapis.com/maps/api/geocode/xml?latlng=" + String.valueOf(cur_lat) + ","
                    + String.valueOf(cur_lng) + "&sensor=true", null) ;
            
        	res = res.replace("&", "") ;
        	XMLParser parser = new XMLParser();
        	Document doc = parser.getDomElement(res); // getting DOM element
            NodeList nl = doc.getElementsByTagName("result");
            
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        	
            for ( int i = 0 ; i < nl.getLength() ; i++ )
            {
            	Element e = (Element) nl.item(i);
            	String address = "" ;
            	
            	if ( parser.getValue(e, "type").equals("country") )
            	{
            		address = parser.getValue(e, "formatted_address") ;
            		GlobalVariable.cur_country = address ;
            		Log.d("ADDRESSES", address) ;
            	}            	
            }
            
			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
			
			if ( Asycdialog.isShowing() )
				Asycdialog.dismiss();
			
			if ( GlobalVariable.cur_country == null || GlobalVariable.cur_country.length() < 1)
			{
				GlobalVariable.cur_country = "United States" ;				
			}
			
			if (prefs.getString("country_name", "").length() < 1 )
			{
				SharedPreferences.Editor editor = prefs.edit() ;
	            editor.putString("country_name", GlobalVariable.cur_country);
	            editor.commit();
	            
	            GlobalVariable.f_currentLocation = true ;
	            
		        imgSelectCountry.setImageResource(GlobalVariable.getResourceID(ActivityMain.this, GlobalVariable.cur_country.replace(" ", "").toLowerCase())) ;	
		    	GlobalVariable.f_currentLocation = true ;
		    }
		    else
		    {
		    	imgSelectCountry.setImageResource(GlobalVariable.getResourceID(ActivityMain.this, prefs.getString("country_name", "").replace(" ", "").toLowerCase())) ;
		    	if (prefs.getString("country_name", "").equals(GlobalVariable.cur_country))
		    	{
		    		GlobalVariable.f_currentLocation = true ;
		    	}
		    	else
		    		GlobalVariable.f_currentLocation = false ;
		    }				
			
			if ( n == 0 )
		    	new GetCategoryTask().execute() ;
			
			super.onPostExecute(result);
		}
	}

	private class GetCategoryTask extends AsyncTask<Void, Void, ArrayList<CategoryImageItem> >{

		@Override
        protected void onPreExecute() {
            //set message of the dialog
            Asycdialog.setMessage("Please wait. Loading...");
            Asycdialog.show();
            
            super.onPreExecute();
        }
		
		@Override
        protected ArrayList<CategoryImageItem> doInBackground(Void... arg0) {
        	JSONParser a = new JSONParser() ;
            jsonContent = a.getJSONFromUrl("http://ixplorecanada.canadaworldapps.com/phone/get_category_names.php", null);
            
            XMLParser parser = new XMLParser();
            
            if ( jsonContent.length() > 1 )
            {
            	Document doc = parser.getDomElement(jsonContent); // getting DOM element
                NodeList nl = doc.getElementsByTagName("pagecontent");
                
                final ArrayList<CategoryImageItem> categoryItems = new ArrayList<CategoryImageItem>();
                GlobalVariable.array_Category_id = new String[nl.getLength()] ;
                GlobalVariable.array_Category_name = new String[nl.getLength()] ;
                GlobalVariable.array_Category_tag = new String[nl.getLength()] ;
                for ( int i = 0 ; i < nl.getLength() ; i++ )
                {
                	Element e = (Element) nl.item(i);
                	
                	GlobalVariable.array_Category_id[i] = parser.getValue(e, "id") ;
                	GlobalVariable.array_Category_name[i] = parser.getValue(e, "name") ;
                	GlobalVariable.array_Category_tag[i] = parser.getValue(e, "tag") ;
                	
                	String imageFileName = "http://ixplorecanada.canadaworldapps.com/images/category_icons/" + parser.getValue(e,"icon") ;
                	categoryItems.add(new CategoryImageItem(imageFileName, parser.getValue(e,"name")));
                }
                
                return categoryItems ;
            }
            return null;
        }
		
		@Override
        protected void onPostExecute(ArrayList<CategoryImageItem> result) {
			if ( Asycdialog.isShowing() )
				Asycdialog.dismiss();
			
            super.onPostExecute(result);
            CategoryListViewAdapter customGridAdapter = null;
            final ListView listView = (ListView)findViewById(R.id.listviewCategory) ;
            
            if ( result == null )
    		{
    			Toast.makeText(ActivityMain.this,"Faild. Couldn't get any data from the url.", Toast.LENGTH_SHORT).show();
    		}
    		else
    		{
    			customGridAdapter = new CategoryListViewAdapter(ActivityMain.this, R.layout.item, result);
    			listView.setAdapter(customGridAdapter);
    			
    			n = 1 ;
    		}	
            
            LayoutParams lp = (LayoutParams) listView.getLayoutParams() ;
            lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40 * 6, ActivityMain.this.getResources().getDisplayMetrics());
            //lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40 * 2, MainActivity.this.getResources().getDisplayMetrics());
    		listView.setLayoutParams(lp);
    	    
    		listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					Intent myIntent = new Intent(ActivityMain.this, ActivitySplash.class);
					myIntent.putExtra("category_id", GlobalVariable.array_Category_id[position]) ;
					myIntent.putExtra("category_name", GlobalVariable.array_Category_name[position]) ;
					myIntent.putExtra("category_tag", GlobalVariable.array_Category_tag[position]) ;
					
					GlobalVariable.cur_category = GlobalVariable.array_Category_name[position] ;
	    			ActivityMain.this.startActivity(myIntent);					
				}
    		});
    		
    		if (result != null)
    			customGridAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = PreferenceManager.getDefaultSharedPreferences(ActivityMain.this.getApplicationContext());
		GPSTracker gps = new GPSTracker(this);
		
		if(gps.canGetLocation()){
			cur_lat = gps.getLatitude(); // returns latitude
			cur_lng = gps.getLongitude(); // returns longitude
		}
		
		@SuppressWarnings("deprecation")
		RevMob revmob = RevMob.start(this, "5356b7e9b9cf64ef1f132ef3"); 
		revmob.setTimeoutInSeconds(5);
		revmob.showBanner(this, Gravity.TOP) ;
		
		Asycdialog = new ProgressDialog(getDialogContext());
		
		ListView settingList = (ListView)findViewById(R.id.ListViewSetting) ;
		String[] listContent = {"Add New Business", "Settings"} ;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item2, R.id.itemSettingTitle, listContent);
		settingList.setAdapter(adapter);
		
		LayoutParams lp = (LayoutParams) settingList.getLayoutParams();
		lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, ActivityMain.this.getResources().getDisplayMetrics());
	    settingList.setLayoutParams(lp);
	    
	    settingList.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view,
	                int position, long id) {
	    		
	    		if ( position == 0 )
	    		{
	    			Intent myIntent = new Intent(ActivityMain.this, ActivityAddNewBiz.class);
	    			ActivityMain.this.startActivity(myIntent);
	    		}
	    		else if ( position == 1 )
	    		{
	    			Intent myIntent = new Intent(ActivityMain.this, ActivitySetting.class);
	    			ActivityMain.this.startActivity(myIntent);
	    		}	       
	        }

	    });
	    
	    imgSelectCountry = (ImageView)findViewById(R.id.imgSelectCountry) ;
	    String _country_name = prefs.getString("country_name", "") ;
	    
	    if (_country_name.length() < 1 )
	    {
	    	new GetCountryTask().execute() ;
	    }
	    else
	    {
	    	if ( GlobalVariable.cur_country == null || GlobalVariable.cur_country.length() < 1 )
	    		new GetCountryTask().execute() ;
	    	else
	    	{
	    		imgSelectCountry.setImageResource(GlobalVariable.getResourceID(this, prefs.getString("country_name", "").replace(" ", "").toLowerCase())) ;
		    	new GetCategoryTask().execute() ;
	    	}
	    }
	    
	    //if ( GlobalVariable.cur_country == null || GlobalVariable.cur_country.length() < 1 )
	    //	new GetCountryTask().execute() ;
	   // else
	    //{
	    	/*
	    	if (prefs.getString("country_name", "").length() < 1 )
		    {
	    		if ( GlobalVariable.cur_country != null && GlobalVariable.cur_country.length() > 1 )
	    			imgSelectCountry.setImageResource(GlobalVariable.getResourceID(this, GlobalVariable.cur_country.replace(" ", "").toLowerCase())) ;	
	    		else
	    			imgSelectCountry.setImageResource(GlobalVariable.getResourceID(this, "United States")) ;
		    }
		    else
		    {
		    	imgSelectCountry.setImageResource(GlobalVariable.getResourceID(this, prefs.getString("country_name", "").replace(" ", "").toLowerCase())) ;
		    }*/
	    	
	    //	new GetCategoryTask().execute() ;
	   // }
	    
	    imgSelectCountry.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(ActivityMain.this, ActivityCountrySelect.class);
				ActivityMain.this.startActivityForResult(myIntent, 101);	
			}
		}) ;
	    	    
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == 101) {
	    	if (prefs.getString("country_name", "").length() < 1 )
    	    {
    	        imgSelectCountry.setImageResource(GlobalVariable.getResourceID(this, GlobalVariable.cur_country.replace(" ", "").toLowerCase())) ;	
    	    }
    	    else
    	    {
    	    	imgSelectCountry.setImageResource(GlobalVariable.getResourceID(this, prefs.getString("country_name", "").replace(" ", "").toLowerCase())) ;
    	    }
	    }
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (Asycdialog.isShowing()) {
	    	Asycdialog.dismiss();
	    	Asycdialog = null;
	    }
	}
		
}
