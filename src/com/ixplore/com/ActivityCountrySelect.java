package com.ixplore.com;

import ResponseParser.GlobalVariable;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ActivityCountrySelect extends Activity {
	private String[] countries ;
	ListView listView ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.activity_country) ;
		
		countries = getResources().getStringArray(R.array.CountryCodes);
		listView = (ListView) findViewById(R.id.list_country) ;
		
		ListCountry adapter = new ListCountry(ActivityCountrySelect.this, countries) ;
		 
		listView.setAdapter(adapter); 
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
                
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ActivityCountrySelect.this.getApplicationContext());
				SharedPreferences.Editor editor = prefs.edit() ;
                editor.putString("country_name", countries[arg2]);
                editor.commit();
                
                if ( GlobalVariable.cur_country.equals(countries[arg2]) )
                	GlobalVariable.f_currentLocation = true ;
                else
                	GlobalVariable.f_currentLocation = false ;
                
                finish() ;
			}
        });
        
        Button btnCurrentCountry = (Button)findViewById(R.id.btnUseCurrentCountry) ;
        btnCurrentCountry.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GlobalVariable.f_currentLocation = true ;
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ActivityCountrySelect.this.getApplicationContext());
				SharedPreferences.Editor editor = prefs.edit() ;
                editor.putString("country_name", GlobalVariable.cur_country);
                editor.commit();
                
                finish() ;
                
			}
		}) ;
	}
	
}
