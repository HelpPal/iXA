package com.ixplore.com;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ImageCache.ImageLoader;
import ResponseParser.GlobalVariable;
import ResponseParser.JSONParser;
import ResponseParser.XMLParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class ActivityPlaceList extends Activity {
	ListView list_featured ;
	RelativeLayout view_detail ;
	private ImageView imgBizType, imgCloseDetail ;
    private TextView label_bizType, label_bizName, label_bizDescription ;
    private Button btnCall, btnMap, btnShare ;
    
    private String tarLat, tarLng ;
    private String call_number, share_body ;
    
    Handler mHandler ;
    ListFeaturedBusiness adapter ;
    
    ArrayList<String> featured_names, featured_address, featured_icons, featured_call, featured_description, featured_lat, featured_lng ;
	
	private void setDetailView() {
		LayoutInflater inflater = LayoutInflater.from(ActivityPlaceList.this);
        view_detail = (RelativeLayout)inflater.inflate(R.layout.item_mark_detail, null);
        RelativeLayout.LayoutParams param_detail = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT) ;
        getWindow().addContentView(view_detail, param_detail) ;
        
        view_detail.setVisibility(View.GONE) ;
        
        imgBizType = (ImageView)view_detail.findViewById(R.id.imgBizCategory) ;
        imgCloseDetail = (ImageView)view_detail.findViewById(R.id.imgClose) ;
        label_bizDescription = (TextView)view_detail.findViewById(R.id.label_bizDescription) ;
        label_bizName = (TextView)view_detail.findViewById(R.id.label_bizName) ;
        label_bizType = (TextView)view_detail.findViewById(R.id.label_bizType) ;
        
        btnCall = (Button)view_detail.findViewById(R.id.btnCall) ;
        btnMap = (Button)view_detail.findViewById(R.id.btnMap) ;
        btnShare = (Button)view_detail.findViewById(R.id.btnShare) ;
        
        label_bizDescription.setMovementMethod(new ScrollingMovementMethod()) ;
        imgCloseDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				view_detail.setVisibility(View.GONE); 
			}
		}) ;
        
        btnCall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				
				String str = "" ;
				if (call_number==null)
					return ;
				
				for ( int i = 0 ; i < call_number.length() ; i++ )
				{
					String temp = call_number.substring(i, i+1) ;
					int n ;
					try{
						n = Integer.parseInt(temp) ;
						if ( n >= 0 && n < 10 )
							str = str + String.valueOf(n) ;
					}catch(NumberFormatException ex) {
						
			        }
				}
				
				callIntent.setData(Uri.parse("tel:"+ str));
				startActivity(callIntent);				
			}
		});
                
        btnMap.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent mapIntent = new Intent(ActivityPlaceList.this, ActivityDistance.class);
				
				GlobalVariable.web_title = "Distance" ;
    			GlobalVariable.web_url = String.format("http://maps.google.com/maps?saddr=%g,%g&daddr=%s,%s", 
    					GlobalVariable.cur_lat, GlobalVariable.cur_lng, tarLat, tarLng);
    			
    			startActivity(mapIntent);				
			}
		});

        btnShare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		        sharingIntent.setType("text/plain");
		        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "iXploreCanada");
		        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, share_body);
		        startActivity(Intent.createChooser(sharingIntent, "Please choose..."));		
			}
		});
	}
	
	private class GetGooglePositionInfoTask extends AsyncTask<String, Void, Void >{
		String[] resArray ;
		
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
			if ( position.length() > 1 )
			{
				XMLParser parser = new XMLParser();
	        	Document doc = parser.getDomElement(position); // getting DOM element
	            NodeList nl = doc.getElementsByTagName("result");
	            
	            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        	StrictMode.setThreadPolicy(policy);
	        	resArray = new String[7] ;
	        	
	            for ( int i = 0 ; i < nl.getLength() ; i++ )
	            {
	            	Element e = (Element) nl.item(i);
	            	
	            	resArray[0] = parser.getValue(e, "name") ;
	            	resArray[1] = parser.getValue(e, "formatted_address") ;
	            	resArray[2] = parser.getValue(e, "icon") ;
	            	resArray[3] = parser.getValue(e, "formatted_phone_number") ;
	            	resArray[4] = "International phone number " + parser.getValue(e, "international_phone_number") + "\n" +
	            				 	"Website " + parser.getValue(e, "website");
	            	resArray[5] = parser.getValue(e, "lat") ;
	            	resArray[6] = parser.getValue(e, "lng") ;
	            	
	            }
			}
			
			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			Message msg=new Message() ;
            msg.obj = resArray ;
            mHandler.sendMessage(msg) ;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.activity_placelist) ;
		
		call_number = "";
		share_body = "" ;
		
		TextView title = (TextView)findViewById(R.id.webview_title) ;
		title.setText(GlobalVariable.cur_category) ;
		
		LinearLayout btnAddNewBiz = (LinearLayout)findViewById(R.id.btn_AddNewBiz) ;
		btnAddNewBiz.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ActivityPlaceList.this, ActivityAddNewBiz.class);
				startActivity(intent);				
			}
			
		}) ;

		ImageView btnBack = (ImageView)findViewById(R.id.btnBack) ;
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish() ;
			}
		}) ;
		
		list_featured = (ListView)findViewById(R.id.list_featured2) ;
		
		setDetailView() ;
		/*
		if ( !GlobalVariable.f_currentLocation )
			list_featured.setVisibility(View.GONE) ;
		else
			list_featured.setVisibility(View.VISIBLE) ;
		*/
		featured_names = new ArrayList<String>();
    	featured_address = new ArrayList<String>();
    	featured_call = new ArrayList<String>();
    	featured_description = new ArrayList<String>();
    	featured_lat = new ArrayList<String>();
    	featured_lng = new ArrayList<String>();
    	featured_icons = new ArrayList<String>();
    	
		String str_featured_positions = getIntent().getExtras().getString("featured_positions") ;
		if (str_featured_positions != null)
		{
			if ( str_featured_positions.length() > 1 )
	        {
				str_featured_positions.replace("&", " ") ;
	        	XMLParser parser = new XMLParser();
	        	Document doc = parser.getDomElement(GlobalVariable.featured_positions); // getting DOM element
	            NodeList nl = doc.getElementsByTagName("pagecontent");
	            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        	StrictMode.setThreadPolicy(policy);
	        	
	        	for ( int i = 0 ; i < nl.getLength() ; i++ )
	            {
	            	Element e = (Element) nl.item(i);
	            	
	            	if ( parser.getValue(e, "name").length() > 0 )
	            		featured_names.add(parser.getValue(e, "name")) ;
	            	
	            	if ( parser.getValue(e, "call").length() > 0 )
	            		featured_call.add(parser.getValue(e, "call")) ;
	            	
	            	if ( parser.getValue(e, "description").length() > 0 )
	            		featured_description.add(parser.getValue(e,  "description")) ;
	            	
	            	if ( parser.getValue(e, "address").length() > 0 )
	            		featured_address.add(parser.getValue(e, "address")) ;
	            	
	            	if ( parser.getValue(e, "icon").length() > 0 )
	            		featured_icons.add(parser.getValue(e, "icon")) ;
	            	
	            	if ( parser.getValue(e, "lat").length() > 0 )
	            		featured_lat.add(parser.getValue(e, "lat")) ;
	            	
	            	if ( parser.getValue(e, "lng").length() > 0 )
	            		featured_lng.add(parser.getValue(e, "lng")) ;            	
	            }        	
	        }
		}
		
        String str_references = getIntent().getExtras().getString("references") ;
        if ( str_references != null ) {
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
            		
            		//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            		//    new GetGooglePositionInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, reference);
            		//} else {
            		    new GetGooglePositionInfoTask().execute(reference);
            		//}        		
                }
        	}
        }
        
    	list_featured.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
                
				view_detail.setVisibility(View.VISIBLE) ;
		        
		    	tarLat = featured_lat.get(arg2).toString() ;
		    	tarLng = featured_lng.get(arg2).toString() ;
		        
		    	if ( featured_call.size() >= arg2 + 1 )
		    		call_number = featured_call.get(arg2).toString() ;
		    	
		        share_body = featured_names.get(arg2).toString() + "\n" +featured_address.get(arg2).toString() + "\n" 
		        				+ featured_description.get(arg2).toString() ;
		        
		        ImageLoader imgLoader = new ImageLoader(ActivityPlaceList.this);
				imgLoader.DisplayImage(featured_icons.get(arg2).toString(), imgBizType ) ;
				
		        label_bizType.setText(featured_names.get(arg2).toString()) ;
		        label_bizName.setText(featured_address.get(arg2).toString()) ;
		        label_bizDescription.setText(featured_description.get(arg2).toString()) ;
			}
        });
    	
    	adapter = new ListFeaturedBusiness(ActivityPlaceList.this, featured_names) ;
    	adapter.updateList(featured_names, featured_address, featured_icons) ;
		list_featured.setAdapter(adapter); 
		
        mHandler = new Handler() { 
            @Override public void handleMessage(Message msg) { 
            	String[] _temp = (String[]) msg.obj ;
            	
            	featured_names.add(_temp[0]) ;
            	featured_address.add(_temp[1]) ;
            	featured_icons.add(_temp[2]) ;
            	featured_call.add(_temp[3]) ;
            	featured_description.add(_temp[4]) ;
            	featured_lat.add(_temp[5]) ;
            	featured_lng.add(_temp[6]) ;
            	
            	adapter = new ListFeaturedBusiness(ActivityPlaceList.this, featured_names) ;
            	adapter.updateList(featured_names, featured_address, featured_icons) ;
        		list_featured.setAdapter(adapter); 
            }
        };
	}
}
