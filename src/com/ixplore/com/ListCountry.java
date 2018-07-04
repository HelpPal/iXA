package com.ixplore.com;

import ResponseParser.GlobalVariable;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListCountry extends ArrayAdapter<String>{
	
	private final Context context;
	private final String[] country;
	
	public ListCountry(Context context,
		String[] country) {
		
		super(context,R.layout.item_country);
		
		this.context = context;
		this.country = country;
		//this.imageId = imageId;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
        View itemView = inflater.inflate(R.layout.item_country, parent, false);
        TextView txtTitle = (TextView) itemView.findViewById(R.id.label_country);
		ImageView imageView = (ImageView) itemView.findViewById(R.id.imgCountry);
		
		txtTitle.setText(country[position]);
		imageView.setImageResource(GlobalVariable.getResourceID(context, country[position].replace(" ", "").toLowerCase()));
		
		if ( position % 2 == 1 )
		{
			itemView.setBackgroundColor(Color.rgb(230, 230, 230)) ;
		}
		else
		{
			itemView.setBackgroundColor(Color.rgb(250, 250, 250)) ;
		}
		
		return itemView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return country.length;
	}

	@Override
	public String getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
