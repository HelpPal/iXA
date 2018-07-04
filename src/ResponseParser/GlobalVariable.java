package ResponseParser;

import android.app.Application;
import android.content.Context;

public class GlobalVariable extends Application {
    private static GlobalVariable singleton ;
    
    public static Boolean f_currentLocation = true ;
    
    public static String web_url ;
    public static String web_title ;
    public static String[] array_Category_name ;
    public static String[] array_Category_tag ;
    public static String[] array_Category_id ;
    public static String cur_code ;
    public static String cur_country ;
    
    public static String cur_category ;
    
    public static String reference_google_positions ;
    public static String featured_positions ;
    
    public static double cur_lat ;
    public static double cur_lng ;
    
    public static GlobalVariable getInstance() {
        return singleton;
    }
    
    public static int getResourceID(Context _context, String fileName) {
    	int resID = _context.getResources().getIdentifier("@drawable/" + fileName, "drawable", _context.getPackageName());
		return resID ;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}