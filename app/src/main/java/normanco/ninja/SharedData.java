package normanco.ninja;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

/**
 * Created by toman on 2/05/2018.
 */

public class SharedData {
    private static final String TAG = "LogMessage";
    private static  SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;

    public SharedData(){}

    public static void setSharedPref(Context mContext){
        sharedPref = mContext.getSharedPreferences("normanco.ninja", mContext.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public static void setImagePath(String string) {
        editor.putString("image", string);
    }

    public static void setImagePath(Context mContext, Uri uri){
        GetPathFromUri getPathFromUri = new GetPathFromUri();
        String path = getPathFromUri.getUriRealPath(mContext, uri);
        editor.putString("image", path);
        Log.i(TAG, "Shared Pref saved imageBackground path: " + path);
    }

    public static void setDueDate(int dateDue){
        // sets the flag that indicates the the due date has been set.
        editor.putInt("dateDue", dateDue);
    }

    public static void setBornDate(int dateBorn){
        //sets a flag that the born date has been set.
        editor.putInt("dateBorn", dateBorn);
    }

    public static void setFirstFlag(){
        editor.putBoolean("firstStart", false);
    }

    public static boolean getFirstFlag(){
        //looks for the Shared Preference flags due_date & born_date to see if the dates have been assigned;
        return sharedPref.getBoolean("firstStart", true);
    }

    public static String getImagePath(){
        return sharedPref.getString("image", "default");
    }

    public static int getDueDate(){
        return sharedPref.getInt("dateDue", 0);
    }

    public static int getBornDate(){
        return sharedPref.getInt("dateBorn", 0);
    }

    public static void applyData(){
        //applies the shared preference settings
        editor.apply();
    }

    public static void removeAll(){
        editor.clear();
        editor.commit();
    }
}
