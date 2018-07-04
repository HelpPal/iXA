package com.ixplore.com;


import java.util.ArrayList;

import ImageCache.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author javatechig {@link http://javatechig.com}
 * 
 */
public class CategoryListViewAdapter extends ArrayAdapter<CategoryImageItem> {
	private Context context;
	private int layoutResourceId;
	private ArrayList<CategoryImageItem> data = new ArrayList<CategoryImageItem>();

	public CategoryListViewAdapter(Context context, int layoutResourceId,
			ArrayList<CategoryImageItem> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new ViewHolder();
			convertView.setTag(holder);			
		} 
		else
	    {
	        holder = (ViewHolder) convertView.getTag();
	    }

		holder.imageTitle = (TextView) convertView.findViewById(R.id.itemTitle);
		holder.image = (ImageView)convertView.findViewById(R.id.itemImgView) ;
		holder.imageUrl = data.get(position).getImageUrl() ;
		
		CategoryImageItem item = data.get(position);
		holder.imageTitle.setText(item.getTitle());
		
		ImageLoader imgLoader = new ImageLoader(getContext());
		imgLoader.DisplayImage(holder.imageUrl, holder.image ) ;
		
		if ( position % 2 == 0 )
			convertView.setBackgroundColor(Color.argb(255, 240, 240, 240)) ;
		else
			convertView.setBackgroundColor(Color.WHITE) ;
		
		return convertView;
	}

	static class ViewHolder {
		TextView imageTitle;
		String imageUrl ;
		ImageView image;
		Bitmap bm ;
	}
	
}