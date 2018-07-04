package com.ixplore.com;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListFeaturedBusiness extends ArrayAdapter<String>{
	
	private final Context context;
	public ArrayList<String> titles ;
	public ArrayList<String> addresses ;
	public ArrayList<String> icons;
	
	public ListFeaturedBusiness(Context context, ArrayList<String> titles) {
		
		super(context,R.layout.item_places);
		
		this.context = context;
		this.titles = titles;
		
		//this.imageId = imageId;
	}
	
	public void updateList(ArrayList<String> titles, ArrayList<String> addresses, ArrayList<String> icons) {
		this.titles = titles;
		this.addresses = addresses;
		this.icons = icons ;
		
	}
	
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		Log.d("LIST COUNT", String.valueOf(addresses.size())) ;
		
        View itemView = inflater.inflate(R.layout.item_places, parent, false);
        TextView txtTitle = (TextView) itemView.findViewById(R.id.txtName);
        TextView txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
		ImageView imageView = (ImageView) itemView.findViewById(R.id.place_icon);
		
		txtTitle.setText(titles.get(position).toString()) ;
		txtAddress.setText(addresses.get(position).toString()) ;
		
		Picasso.with(context).load(Uri.parse(icons.get(position).toString())).into(imageView) ;
		
		if ( position % 2 == 1 )
		{
			itemView.setBackgroundColor(Color.rgb(250, 250, 250)) ;
		}
		else
		{
			itemView.setBackgroundColor(Color.rgb(255, 255, 255)) ;
		}

		return itemView;
	}
	
	@Override
	public int getCount() {
		return this.titles.size() ;		
	}

	@Override
	public String getItem(int arg0) {
		return this.titles.get(arg0) ;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
}