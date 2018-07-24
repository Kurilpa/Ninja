package normanco.ninja;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class ConditionalStartActivity extends AppCompatActivity {
    private static final String TAG = "LogMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedData.setSharedPref(getApplicationContext());

        if (SharedData.getFirstFlag()) {
            Log.i(TAG, "First Start: Going to edit activity");
            Intent intentEdit = new Intent(getApplicationContext(), EditActivity.class);
            startActivity(intentEdit);
        }
        else {
            Log.i(TAG, "Previous start detected: Going to main activity");
            Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intentMain);
        }
        finish();
    }
}
