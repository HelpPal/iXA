package com.ixplore.com;


import com.revmob.RevMob;

import ResponseParser.GlobalVariable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class ActivitySetting extends Activity {
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		@SuppressWarnings("deprecation")
		RevMob revmob = RevMob.start(this, "5356b7e9b9cf64ef1f132ef3");  
		revmob.showFullscreen(this);
		
		ListView settingList = (ListView)findViewById(R.id.listSetting);
		String[] listContent = {"App Version 						1.0", "Rate on Google Play", "Send Feedback", "Google Terms and Use", "Google Privacy Policy", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item2, R.id.itemSettingTitle, listContent );
		settingList.setAdapter(adapter);
		
		ImageView btnBack = (ImageView)findViewById(R.id.btnBack) ;
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish() ;	
			}
		}) ;
		
		settingList.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    		if ( position == 1 )
	    		{
	    			//final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
	    			try {
	    				ActivitySetting.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.canadaworldapps.ixplore")));
	    			} catch (android.content.ActivityNotFoundException anfe) {
	    			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + "com.canadaworldapps.ixplore")));
	    			}
	    		}	       
	    		else if ( position == 2 )
	    		{
	    			Intent email = new Intent(Intent.ACTION_SEND);
	    			
	    			email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "feedback@canadaworldapps.com" });
		  			email.putExtra(Intent.EXTRA_SUBJECT, "Feedback Of iXplore App");
		  			//email.setType("message/rfc822");
		  			email.setType("text/plain");
		  			email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
		  			email.putExtra(Intent.EXTRA_TEXT, "Email message goes here");
		  			
		  			try {
		  				startActivity(Intent.createChooser(email, "Send mail..."));
		  				Log.i("Finished sending email...", "");
		  			} catch (android.content.ActivityNotFoundException ex) {
		  				Toast.makeText(ActivitySetting.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
		  			}	   
	    		}
	    		else if ( position == 3 )
	    		{
	    			GlobalVariable.web_title = "Google Terms and Use" ;
	    			GlobalVariable.web_url = "https://www.google.com/intl/en/policies/terms" ;
	    			
	    			Intent myIntent = new Intent(ActivitySetting.this, ActivityDistance.class) ;
					startActivity(myIntent);	
	    		}
	    		else if ( position == 4 )
	    		{
	    			GlobalVariable.web_title = "Google Privacy Policy" ;
	    			GlobalVariable.web_url = "https://www.google.com/policies/privacy" ;
	    			
	    			Intent myIntent = new Intent(ActivitySetting.this, ActivityDistance.class) ;
					startActivity(myIntent);
	    		}
	        }
	    });
	}	
	
}
