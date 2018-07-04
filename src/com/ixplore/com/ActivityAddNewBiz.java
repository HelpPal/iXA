package com.ixplore.com;

import java.util.LinkedList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ixplore.com.R;
import com.revmob.RevMob;

import ResponseParser.GlobalVariable;
import ResponseParser.JSONParser;
import ResponseParser.XMLParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ActivityAddNewBiz extends Activity {	
	ProgressDialog Asycdialog;
	static final String URL = "http://ixplorecanada.canadaworldapps.com/phone/add_business.php";
	String jsonContent ;
	ToggleButton toggleFeature ;
	String user_name, email, phone, bizName, featured, categoryid ;
	double lat, lng ;
	
	TextView btnBizCategory ;
	
	private Context getDialogContext() {
	    Context context;
	    if (getParent() != null) context = getParent();
	    else context = this;
	    return context;
	}
	
	private class SubmitPositionTask extends AsyncTask<Void, Void, Void>{

		@Override
        protected void onPreExecute() {
            //set message of the dialog
            Asycdialog.setMessage("Please wait. Submitting...");
            Asycdialog.show();
            
            super.onPreExecute();
        }
		
		@Override
        protected Void doInBackground(Void... arg0) {
        	
            JSONParser a = new JSONParser() ;
            
            List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("user_name", user_name));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("phone_number", phone));
            params.add(new BasicNameValuePair("business_name", bizName));
            params.add(new BasicNameValuePair("category_id", categoryid));
            params.add(new BasicNameValuePair("featured", featured));
            params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
            params.add(new BasicNameValuePair("lng", String.valueOf(lng)));
            
            jsonContent = a.getJSONFromUrl("http://ixplorecanada.canadaworldapps.com/phone/add_business.php", params) ;
            
            return null;
        }
		
		@Override
        protected void onPostExecute(Void result) {
            Asycdialog.dismiss();
            super.onPostExecute(result);
            XMLParser parser = new XMLParser();
            if (jsonContent == null)
            	return ;
            
            if ( jsonContent.length() > 1 )
            {
            	Document doc = parser.getDomElement(jsonContent); // getting DOM element
                
                NodeList nl = doc.getElementsByTagName("pagecontent");
                Element e = (Element) nl.item(0);
                if ( parser.getValue(e,"message").equals("1") )
                	Toast.makeText(ActivityAddNewBiz.this, "Your request has submitted successfully.", Toast.LENGTH_SHORT).show();
                else
                	Toast.makeText(ActivityAddNewBiz.this, "Failed. Internet Connection Error.", Toast.LENGTH_SHORT).show();
            }
            else
            	Toast.makeText(ActivityAddNewBiz.this, "Failed. Internet Connection Error.", Toast.LENGTH_SHORT).show();            
		}		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addbusiness);		
		
		@SuppressWarnings("deprecation")
		RevMob revmob = RevMob.start(this, "5356b7e9b9cf64ef1f132ef3");  
		revmob.showFullscreen(this);
		
		GPSTracker gps = new GPSTracker(ActivityAddNewBiz.this);
		lat = 0.0 ;
		lng = 0.0 ;
		categoryid = "" ;
		
		if(gps.canGetLocation()){
            lat = gps.getLatitude();
            lng = gps.getLongitude();
             
        }else{
            gps.showSettingsAlert();
        }
        
		btnBizCategory = (TextView)findViewById(R.id.btnBizCategory);
		btnBizCategory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				CharSequence[] items = new String[GlobalVariable.array_Category_name.length] ;
				for ( int i = 0 ; i < GlobalVariable.array_Category_name.length ; i++ )
					items[i] = GlobalVariable.array_Category_name[i] ;
				
				AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddNewBiz.this);
				builder.setTitle("Make your selection");
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	categoryid = GlobalVariable.array_Category_id[item] ;
				    	btnBizCategory.setText("Business Category" + " " + GlobalVariable.array_Category_name[item]) ;
				    }
				});
				
				AlertDialog alert = builder.create();
				alert.show();
			}
		}) ;
		
		Asycdialog = new ProgressDialog(getDialogContext());
		
		ImageView btnBack = (ImageView)findViewById(R.id.btnBack) ;
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish() ;
				
			}
		}) ;
	}	
	
	public void onSubmit(View v)
	{
		user_name = ((EditText)findViewById(R.id.txtName)).getText().toString() ;
		email = ((EditText)findViewById(R.id.txtEmail)).getText().toString() ;
		phone = ((EditText)findViewById(R.id.txtPhone)).getText().toString() ;
		bizName = ((EditText)findViewById(R.id.txtBizName)).getText().toString() ;
		
		toggleFeature = (ToggleButton)findViewById(R.id.toggleFeature) ;
		
		if ( toggleFeature.isChecked() )
			featured = "1" ;
		else
			featured = "0" ;
			
		if(user_name == null || user_name.trim().length() < 1) 
        {
			Toast.makeText(ActivityAddNewBiz.this, "Please enter a Name.", Toast.LENGTH_SHORT).show();
        }
		else if(email == null || email.trim().length() < 1) 
        {
        	Toast.makeText(ActivityAddNewBiz.this, "Please enter a Email.", Toast.LENGTH_SHORT).show();
        }
		else if(phone == null || phone.trim().length() < 1) 
        {
        	Toast.makeText(ActivityAddNewBiz.this, "Please enter a Phone Number.", Toast.LENGTH_SHORT).show();
        }
		else if(bizName == null || bizName.trim().length() < 1) 
        {
        	Toast.makeText(ActivityAddNewBiz.this, "Please enter a Business Name.", Toast.LENGTH_SHORT).show();
        }
		else if ( categoryid.length() < 1 )
			Toast.makeText(ActivityAddNewBiz.this, "Please select business category.", Toast.LENGTH_SHORT).show();
		else
			new SubmitPositionTask().execute() ;
	}
		
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (Asycdialog != null) {
	    	Asycdialog.dismiss();
	    	Asycdialog = null;
	    }
	}
}
