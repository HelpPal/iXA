package com.jwetherell.augmented_reality.data;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.jwetherell.augmented_reality.ui.IconMarker;
import com.jwetherell.augmented_reality.ui.Marker;

/**
 * This class should be used as a example local data source. It is an example of
 * how to add data programatically. You can add data either programatically,
 * SQLite or through any other source.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class LocalDataSource extends DataSource {

    private List<Marker> cachedMarkers = new ArrayList<Marker>();
    
    public LocalDataSource(Resources res) {
        if (res == null) throw new NullPointerException();

        createIcon(res);
    }

    protected void createIcon(Resources res) {
        if (res == null) throw new NullPointerException();

        //icon = BitmapFactory.decodeResource(res, R.drawable.waitor);
    }

    public List<Marker> getMarkers(String name, double lat, double lng, Bitmap icon, String businessName, String call, String description) {
    	
    	if ( icon != null )
        {
    		Marker atl = new IconMarker(name, lat, lng, 0, Color.RED, businessName, call, description, icon) ;
    		cachedMarkers.add(atl);
        }
    	else
    	{
    		//Marker home = new Marker(name, lat, lng, 0, Color.RED);
    		Marker home = new Marker(name,lat,lng,0,icon,businessName,call,description) ;
            cachedMarkers.add(home);
    	}
        
        /*
         * 
         * Marker lon = new IconMarker(
         * "I am a really really long string which should wrap a number of times on the screen."
         * , 39.95335, -74.9223445, 0, Color.MAGENTA, icon);
         * cachedMarkers.add(lon); Marker lon2 = new IconMarker(
         * "2: I am a really really long string which should wrap a number of times on the screen."
         * , 39.95334, -74.9223446, 0, Color.MAGENTA, icon);
         * cachedMarkers.add(lon2);
         */

        /*
         * float max = 10; for (float i=0; i<max; i++) { Marker marker = null;
         * float decimal = i/max; if (i%2==0) marker = new Marker("Test-"+i,
         * 39.99, -75.33+decimal, 0, Color.LTGRAY); marker = new
         * IconMarker("Test-"+i, 39.99+decimal, -75.33, 0, Color.LTGRAY, icon);
         * cachedMarkers.add(marker); }
         */

        return cachedMarkers;
    }

	@Override
	public List<Marker> getMarkers() {
		// TODO Auto-generated method stub
		return null;
	}
}
