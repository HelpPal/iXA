package com.ixplore.com;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ResponseParser.GlobalVariable;
import ResponseParser.JSONParser;
import ResponseParser.XMLParser;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ixplore.com.R;

public class ActivityMap extends Activity {
	// Google Map
    private GoogleMap googleMap;
    Handler mHandler ;
    boolean f_firstStatus ;
    LatLngBounds.Builder builder ;
    Button btnGetDirection ;
    ArrayList<MarkerOptions> markers ;
    
    double tar_lat, tar_lng ;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview);
        
        markers = new ArrayList<MarkerOptions>() ;
        //@SuppressWarnings("deprecation")
		//RevMob revmob = RevMob.start(this, "5356b7e9b9cf64ef1f132ef3");  
		//revmob.showFullscreen(this);
        //revmob.showBanner(ActivityMap.this, Gravity.BOTTOM) ;
		
        f_firstStatus = false ;
        
        try {
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
                
        ImageView imgBack = (ImageView)findViewById(R.id.btnBack) ;
        imgBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish() ;
			}
		}) ;
 
        btnGetDirection = (Button)findViewById(R.id.btnGetDirection) ;
        btnGetDirection.setVisibility(View.GONE) ;
        
        btnGetDirection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				btnGetDirection.setVisibility(View.GONE) ;
				Intent mapIntent = new Intent(ActivityMap.this, ActivityDistance.class);
				
				GlobalVariable.web_title = "Distance" ;
				
				GPSTracker gps = new GPSTracker(ActivityMap.this);
				
				double cur_lat, cur_lng ;
				cur_lat = 0 ;
				cur_lng = 0 ;
				
				if(gps.canGetLocation()){
					cur_lat = gps.getLatitude(); // returns latitude
					cur_lng = gps.getLongitude(); // returns longitude
				}
				
    			GlobalVariable.web_url = String.format("http://maps.google.com/maps?saddr=%g,%g&daddr=%s,%s", 
    					cur_lat, cur_lng, String.valueOf(tar_lat), String.valueOf(tar_lng)) ;
    			
    			startActivity(mapIntent);	
			}
		}) ;
        
    }
 
    private class GetGooglePositionInfoTask extends AsyncTask<String, Void, Void >{
		
    	String name="", address="" ;
    	double lat=0.0, lng=0.0 ;
    	
    	@Override
        protected void onPreExecute() {
			super.onPreExecute();
        }
		
		@Override
        protected Void doInBackground(String... arg0) {			
			JSONParser a = new JSONParser() ;
			String url_detail = "https://maps.googleapis.com/maps/api/place/details/xml?reference=" + arg0[0] + "&sensor=false&key=" + "AIzaSyDyWCoV5_luhS16_S3_ARn5qA29t8_k-V8" ;
			String position = a.getJSONFromUrl(url_detail, null) ;
            position = position.replace("&", "") ;
			
			if ( position != null && position.length() > 1 )
			{
				XMLParser parser = new XMLParser();
	        	Document doc = parser.getDomElement(position); // getting DOM element
	            NodeList nl = doc.getElementsByTagName("result");
	            
	            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        	StrictMode.setThreadPolicy(policy);
	        	
	            for ( int i = 0 ; i < nl.getLength() ; i++ )
	            {
	            	Element e = (Element) nl.item(i);
	            	
	            	if ( parser.getValue(e, "name").length() > 0 )
	            		name = parser.getValue(e, "name") ;
	            	
	            	if ( parser.getValue(e, "formatted_address").length() > 0 )
	            		address = parser.getValue(e, "formatted_address") ;
	            	
	            	if ( parser.getValue(e, "lat").length() > 0 )
	            		lat = Double.parseDouble(parser.getValue(e, "lat")) ;
	            	
	            	if ( parser.getValue(e, "lng").length() > 0 )
	            		lng = Double.parseDouble(parser.getValue(e, "lng")) ;
	            	
	            }
			}
			
			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			if ( name.length() > 1 && address.length() > 1 )
				addMapMarker(2, name, address, lat, lng) ;
		}
	}

    public void addMapMarker(int n, String name, String address, double lat, double lng)
    {
    	MarkerOptions map_marker = new MarkerOptions().position(new LatLng(lat, lng)).title( name ) ; // + " : " + address );
		map_marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
		map_marker.snippet(address) ;
		
		googleMap.addMarker(map_marker);
		
		if ( n==2 )
			markers.add(map_marker) ;        
        
        animageGoogleMap() ;
    }
    
    private void animageGoogleMap() {
    	int k1 = 0 ;
    	for ( MarkerOptions marker : markers )
		{
			builder.include(marker.getPosition());
			k1 = k1 + 1 ;
		}
		
    	if ( k1 > 0 )
    	{
    		LatLngBounds bounds = builder.build();
        	CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 30);
        	googleMap.animateCamera(cu);
    	}
		
    }
        
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            
            builder = new LatLngBounds.Builder();
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            
            String str_featured_positions = getIntent().getExtras().getString("featured_positions") ;
            String str_references = getIntent().getExtras().getString("references") ;

            if ( str_featured_positions != null && str_featured_positions.length() > 1 )
            {
            	XMLParser parser = new XMLParser();
            	Document doc = parser.getDomElement(GlobalVariable.featured_positions); // getting DOM element
                NodeList nl = doc.getElementsByTagName("pagecontent");
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            	StrictMode.setThreadPolicy(policy);
            	
            	for ( int i = 0 ; i < nl.getLength() ; i++ )
                {
                	Element e = (Element) nl.item(i);
                	String name="", address="" ;
                	double lat=0.0, lng=0.0 ;
                	
                	if ( parser.getValue(e, "name").length() > 0 )
                		name = parser.getValue(e, "name") ;
                	
                	if ( parser.getValue(e, "address").length() > 0 )
                		address = parser.getValue(e, "address") ;
                	
                	if ( parser.getValue(e, "lat").length() > 0 )
                		lat = Double.parseDouble(parser.getValue(e, "lat")) ;
                	
                	if ( parser.getValue(e, "lng").length() > 0 )
                		lng = Double.parseDouble(parser.getValue(e, "lng")) ;
                	
                	
                	addMapMarker(1, name, address, lat, lng) ;
                }
            }
            
            if (str_references.length() > 1)
        	{
        		XMLParser parser = new XMLParser();
            	Document doc = parser.getDomElement(GlobalVariable.reference_google_positions); // getting DOM element
                NodeList nl = doc.getElementsByTagName("pagecontent");
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            	StrictMode.setThreadPolicy(policy);
            	
            	for ( int i = 0 ; i < nl.getLength() ; i++ )
                {
            		Element e = (Element) nl.item(i);
            		String reference = parser.getValue(e, "reference") ;
            		
            		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            		    new GetGooglePositionInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, reference);
            		} else {
            		    new GetGooglePositionInfoTask().execute(reference);
            		}            		
                }
        	}            
            googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View v = getLayoutInflater().inflate(R.layout.marker, null);
                    TextView title= (TextView) v.findViewById(R.id.marker_title);
                    TextView address= (TextView) v.findViewById(R.id.marker_address);
                    title.setText(marker.getTitle());
                    address.setText(marker.getSnippet()) ;
                    
                    btnGetDirection.setVisibility(View.VISIBLE) ;
                    tar_lat = marker.getPosition().latitude ; 
                    tar_lng = marker.getPosition().longitude ;
                    
                    return v;
                }
            });
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }
}