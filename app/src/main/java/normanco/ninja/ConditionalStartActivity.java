package normanco.ninja;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class ConditionalStartActivity extends AppCompatActivity {
    private static final String TAG = "LogMessage";
    Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedData.setSharedPref(getApplicationContext());
        if (!SharedData.getDataFlag()) {
            Image.setDefImageUri();
            image = Image.getDefImageUri();
            Log.i(TAG, "App starting for first time: " + image);

            Intent intentEdit = new Intent(getApplicationContext(), EditActivity.class);
            intentEdit.putExtra("image", image);
            startActivity(intentEdit);

        }
        else {
            if (!SharedData.getImagePath().contains("false")) {
                Image.setImageUri(getApplicationContext(), SharedData.getImagePath());
                image = Image.getImageUri();
                Log.i(TAG, "Setting Saved URI: " + image);
            } else {
                Image.setDefImageUri();
                image = Image.getDefImageUri();
                Log.i(TAG, "Setting default URI: " + image);
            }

            Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
            intentMain.putExtra("image", image);
            startActivity(intentMain);
        }
        finish();
    }
}
