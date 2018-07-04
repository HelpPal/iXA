package com.jwetherell.augmented_reality.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ResponseParser.GlobalVariable;
import ResponseParser.JSONParser;
import ResponseParser.XMLParser;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ixplore.com.ActivityDistance;
import com.ixplore.com.ActivityMain;
import com.ixplore.com.ActivityMap;
import com.ixplore.com.ActivityPlaceList;
import com.ixplore.com.R;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.data.NetworkDataSource;
import com.jwetherell.augmented_reality.ui.IconMarker;
import com.jwetherell.augmented_reality.ui.Marker;
import com.jwetherell.augmented_reality.widget.VerticalTextView;

@SuppressLint("HandlerLeak")
public class Demo extends AugmentedReality {
	
    private static final String TAG = "Demo";
    private static final String locale = Locale.getDefault().getLanguage();
    private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
    private static final ThreadPoolExecutor exeService = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, queue);
    private static final Map<String, NetworkDataSource> sources = new ConcurrentHashMap<String, NetworkDataSource>();
    
    private static Toast myToast = null;
    private static VerticalTextView text = null;
    private String call_number ;
    private String shareBody ;
    
    private double curLat ;
    private double curLng ;
    private double tarLat ;
    private double tarLng ;
    	
    private ImageView imgBizType, imgCloseDetail ;
    private TextView label_bizType, label_bizName, label_bizDescription ;
    private Button btnCall, btnMap, btnShare ;
    
    public static Collection<Marker> markers_featured ;
    RelativeLayout view_detail ;
    
    public static Collection<Marker> markers_google ;
    Handler mHandler ;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    
        curLat = 0.0f ;
        curLng = 0.0f ;
        
        tarLat = 0.0f ;
        tarLng = 0.0f ;
        
        shareBody = "" ;
        
        WindowManager.LayoutParams attrs = getWindow().getAttributes(); 
        attrs.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN; 
        getWindow().setAttributes(attrs); 
        
        // Create toast
        myToast = new Toast(getApplicationContext());
        myToast.setGravity(Gravity.CENTER, 0, 0);
        // Creating our custom text view, and setting text/rotation
        text = new VerticalTextView(getApplicationContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        text.setLayoutParams(params);
        text.setBackgroundResource(android.R.drawable.toast_frame);
        text.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Small);
        text.setShadowLayer(2.75f, 0f, 0f, Color.parseColor("#BB000000"));
        myToast.setView(text);
        myToast.setDuration(Toast.LENGTH_SHORT);
        
        // Marker Detail View
        LayoutInflater inflater = LayoutInflater.from(Demo.this);
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
        
        setDetailView() ;
        // ARMenu - ARView, ListView, MapView
        
        RelativeLayout view_armenu = (RelativeLayout)inflater.inflate(R.layout.item_armenu, null);
        RelativeLayout.LayoutParams param_armenu = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) ;
        getWindow().addContentView(view_armenu, param_armenu) ;
        
        final ImageView btnArView = (ImageView)view_armenu.findViewById(R.id.btnArView) ;
        final ImageView btnListView = (ImageView)view_armenu.findViewById(R.id.btnListView) ;
        final ImageView btnMapView = (ImageView)view_armenu.findViewById(R.id.btnMapView) ;
        final ImageView btnFoldView = (ImageView)view_armenu.findViewById(R.id.btnFoldView) ;
        
        btnArView.setVisibility(View.GONE) ;
        btnListView.setVisibility(View.GONE) ;
        btnMapView.setVisibility(View.GONE) ;
        btnFoldView.setImageResource(R.drawable.fold_view_show) ;
        
        btnFoldView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if ( btnArView.getVisibility() == View.GONE )
				{
					btnArView.setVisibility(View.VISIBLE) ;
					btnListView.setVisibility(View.VISIBLE) ;
					btnMapView.setVisibility(View.VISIBLE) ;
					
					btnFoldView.setImageResource(R.drawable.fold_view_hide) ;
				}
				else
				{
					btnArView.setVisibility(View.GONE) ;
					btnListView.setVisibility(View.GONE) ;
					btnMapView.setVisibility(View.GONE) ;
					
					btnFoldView.setImageResource(R.drawable.fold_view_show) ;
				}
			}
		}) ;
        
        btnMapView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Demo.this, ActivityMap.class);
				intent.putExtra("featured_positions", GlobalVariable.featured_positions) ;
				intent.putExtra("references", GlobalVariable.reference_google_positions) ;
				startActivity(intent);
			}
		}) ;
        
        btnListView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Demo.this, ActivityPlaceList.class);
				intent.putExtra("featured_positions", GlobalVariable.featured_positions) ;
				intent.putExtra("references", GlobalVariable.reference_google_positions) ;
				
				GlobalVariable.cur_lat = curLat ;
				GlobalVariable.cur_lng = curLng ;
				
				startActivity(intent) ;
			}
		}) ;
        
        markers_google = new ArrayList<Marker>() ;
        
        ARData.deleteMarkers() ;
        markers_featured = new ArrayList<Marker>() ;
        
        if ( GlobalVariable.f_currentLocation )
        	LoadFeaturedBiz() ;
    }
    
    private class GetGooglePositionInfoTask extends AsyncTask<String, Void, Void >{
		
    	private String position ;
    	Marker marker ;
    	
		@Override
        protected void onPreExecute() {
			super.onPreExecute();
        }
		
		@Override
        protected Void doInBackground(String... arg0) {			
			JSONParser a = new JSONParser() ;
			String url_detail = "https://maps.googleapis.com/maps/api/place/details/xml?reference=" + arg0[0] + "&sensor=false&key=" + "AIzaSyDyWCoV5_luhS16_S3_ARn5qA29t8_k-V8" ;
			position = a.getJSONFromUrl(url_detail, null) ;
            position = position.replace("&", "") ;
            
			XMLParser parser = new XMLParser();
        	Document doc = parser.getDomElement(position); // getting DOM element
            NodeList nl = doc.getElementsByTagName("result");
            
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        	Bitmap bm = null ;
            
            for ( int i = 0 ; i < nl.getLength() ; i++ )
            {
            	Element e = (Element) nl.item(i);
            	String iconFileUrl = parser.getValue(e, "icon") ;
            	
            	try {
                    InputStream in = new java.net.URL(iconFileUrl).openStream();
                    bm = BitmapFactory.decodeStream(in);
            		//bm = Picasso.with(Demo.this).load(Uri.parse(iconFileUrl)).get() ; 
                   // bm = Picasso.with(getApplicationContext()).load(Uri.parse(iconFileUrl)).get() ;
                } catch (Exception ex) {
                    Log.e("Error", ex.getMessage());
                    ex.printStackTrace();
                }
            	
            	String _description = "International Phone Number " + parser.getValue(e, "international_phone_number") + "\n" +
            							"Website " + parser.getValue(e, "website") + "\n" ;
            							
            	marker = new IconMarker(parser.getValue(e, "name"), Double.parseDouble(parser.getValue(e,"lat")), 
            			Double.parseDouble(parser.getValue(e,"lng")), 
            			0,  Color.RED, parser.getValue(e, "formatted_address"), 
            			parser.getValue(e, "formatted_phone_number"), 
            			_description, bm) ;
            	marker.setAddress(parser.getValue(e, "address")) ;
            	//_markers.add(marker) ;
            }
			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			Message msg=new Message();
            msg.obj=marker;
            mHandler.sendMessage(msg);
		}
	}

    public void LoadGoogleReferences()
    {
    	if (GlobalVariable.reference_google_positions.length() > 1)
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
    	
    	mHandler = new Handler() { 
            @Override public void handleMessage(Message msg) { 
            	
            	markers_google.add((Marker) msg.obj) ;
    			ARData.addMarkers(markers_google) ;

            }
        };
    }
    
    public void LoadFeaturedBiz()
    {
    	if ( GlobalVariable.featured_positions == null )
    		return ;
    	
    	if ( GlobalVariable.featured_positions.length() > 1 )
        {
        	XMLParser parser = new XMLParser();
        	Document doc = parser.getDomElement(GlobalVariable.featured_positions); // getting DOM element
            NodeList nl = doc.getElementsByTagName("pagecontent");
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        	
        	ARData.deleteMarkers() ;
            markers_featured = new ArrayList<Marker>() ;
            
            Bitmap bm = null ;
            String tempUrl = "" ;
            
            for ( int i = 0 ; i < nl.getLength() ; i++ )
            {
            	Element e = (Element) nl.item(i);
            	String iconFileUrl = parser.getValue(e, "icon") ;
            	
            	if ( !tempUrl.equals(iconFileUrl) )
            	{
            		try {
                        InputStream in = new java.net.URL(iconFileUrl).openStream();
                        bm = BitmapFactory.decodeStream(in);
            			//bm = Picasso.with(Demo.this).load(Uri.parse(iconFileUrl)).get() ;
                    } catch (Exception ex) {
                        Log.e("Error", ex.getMessage());
                        ex.printStackTrace();
                    }
            		tempUrl = iconFileUrl ;
            	}
            	
            	Marker marker = new IconMarker(parser.getValue(e, "name"), Double.parseDouble(parser.getValue(e,"lat")), 
            			Double.parseDouble(parser.getValue(e,"lng")), 
            			0,  Color.RED, parser.getValue(e, "address"), 
            			parser.getValue(e, "call_num"), 
            			parser.getValue(e, "description"), bm) ;

            	marker.setAddress(parser.getValue(e, "address")) ;
            	markers_featured.add(marker) ;            	
            }      
            
            ARData.addMarkers(markers_featured) ;
        }
    	
    	if (GlobalVariable.f_currentLocation)
    		LoadGoogleReferences() ;
    }
    
    public static Bitmap getBitmapFromURL(String src) {
    	try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream is = connection.getInputStream();
            Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);

            if(options.outHeight * options.outWidth * 2 >= 100*100*2){
            	double sampleSize = scaleByHeight
            			? options.outHeight / 50
                        : options.outWidth / 50;
                options.inSampleSize = 
                        (int)Math.pow(2d, Math.floor(
                        Math.log(sampleSize)/Math.log(2d)));
            }

            // Do the actual decoding
            options.inJustDecodeBounds = false;
            is.close();
            is = url.openStream() ;
            Bitmap img = BitmapFactory.decodeStream(is, null, options);
            is.close();
  
            return img ;
              
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
	public void onBackPressed() {
    	Intent intent = new Intent(Demo.this, ActivityMain.class);
    	startActivity(intent) ;    	
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "onOptionsItemSelected() item=" + item);
        switch (item.getItemId()) {
            case R.id.showRadar:
                showRadar = !showRadar;
                item.setTitle(((showRadar) ? "Hide" : "Show") + " Radar");
                break;
            case R.id.showZoomBar:
                showZoomBar = !showZoomBar;
                item.setTitle(((showZoomBar) ? "Hide" : "Show") + " Zoom Bar");
                zoomLayout.setVisibility((showZoomBar) ? LinearLayout.VISIBLE : LinearLayout.GONE);
                break;
            case R.id.exit:
                //finish();
                break;
        }
        return true;
    }
    
    @Override
    public void onStart() {
        super.onStart();

        Location last = ARData.getCurrentLocation();
        updateData(last.getLatitude(), last.getLongitude(), last.getAltitude());
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        updateData(location.getLatitude(), location.getLongitude(), location.getAltitude());
        curLat = location.getLatitude() ;
        curLng = location.getLongitude() ;
    }
    
    public void setDetailView()
    {
    	btnCall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				
				String str = "" ;
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
				Intent mapIntent = new Intent(Demo.this, ActivityDistance.class);
				
				GlobalVariable.web_title = "Distance" ;
    			GlobalVariable.web_url = String.format("http://maps.google.com/maps?saddr=%g,%g&daddr=%g,%g", 
    					curLat, curLng, tarLat, tarLng);
    			
    			startActivity(mapIntent);				
			}
		});

        btnShare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		        sharingIntent.setType("text/plain");
		        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "iXploreCanada");
		        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		        startActivity(Intent.createChooser(sharingIntent, "Please choose..."));		
			}
		});
    }

    @Override
    protected void markerTouched(Marker marker) {
    	view_detail.setVisibility(View.VISIBLE) ;
        
    	tarLat = marker.getLatitude() ;
        tarLng = marker.getLongitude() ;
        
        call_number = marker.getCall() ;
        shareBody = marker.getBusinessName() + "\n" + marker.getName() + "\n" + marker.getDescription() ;
        
        imgBizType.setImageBitmap(marker.getColor()) ;
        label_bizType.setText(marker.getBusinessName()) ;
        label_bizName.setText(marker.getName()) ;
        label_bizDescription.setText(marker.getDescription()) ;
        
    }

    @Override
    protected void updateDataOnZoom() {
        super.updateDataOnZoom();
        Location last = ARData.getCurrentLocation();
        curLat = last.getLatitude() ;
        curLng = last.getLongitude() ;
        updateData(last.getLatitude(), last.getLongitude(), last.getAltitude());
    }

    private void updateData(final double lat, final double lon, final double alt) {
        try {
            exeService.execute(new Runnable() {
                @Override
                public void run() {
                    for (NetworkDataSource source : sources.values())
                        download(source, lat, lon, alt);
                }
            });
        } catch (RejectedExecutionException rej) {
            Log.w(TAG, "Not running new download Runnable, queue is full.");
        } catch (Exception e) {
            Log.e(TAG, "Exception running download Runnable.", e);
        }
    }

    private static boolean download(NetworkDataSource source, double lat, double lon, double alt) {
        if (source == null) return false;

        String url = null;
        try {
            url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);
        } catch (NullPointerException e) {
            return false;
        }

        List<Marker> markers = null;
        try {
            markers = source.parse(url);
        } catch (NullPointerException e) {
            return false;
        }

        ARData.addMarkers(markers);
        return true;
    }

}