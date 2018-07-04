package com.ixplore.com;

import com.ixplore.com.R;

import ResponseParser.GlobalVariable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class ActivityDistance extends Activity {
	
	public class myWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_distance);	
		
		ImageView btnBack = (ImageView)findViewById(R.id.btnBack) ;
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish() ;
			}
		}) ;
		
		TextView title = (TextView)findViewById(R.id.webview_title) ;
		title.setText(GlobalVariable.web_title) ;
		WebView mWebView = (WebView) findViewById(R.id.webView1);

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new myWebViewClient());
		mWebView.loadUrl(GlobalVariable.web_url) ;
	}
}
