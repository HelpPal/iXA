package com.ixplore.com;

import ImageCache.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryItemAdapter extends BaseAdapter {
    
    private Activity activity;
    private String[] imageUrl;
    private String[] itemName;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public CategoryItemAdapter(Activity a, String[] imageUrl, String[] itemName) {
        activity = a;
        this.imageUrl = imageUrl ;
        this.itemName = itemName ;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        imageLoader = new ImageLoader(activity.getApplicationContext()) ;
    }

    public int getCount() {
        return imageUrl.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView ;
        
        if( convertView == null )
            vi = inflater.inflate( R.layout.item, null ) ;
 
        TextView text = (TextView)vi.findViewById( R.id.itemTitle ) ;
        ImageView image = (ImageView)vi.findViewById( R.id.itemImgView ) ;
        
        text.setText( itemName[position] ) ;
        imageLoader.DisplayImage( imageUrl[position], image ) ;
        
        return vi;
    }
}