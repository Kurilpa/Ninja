package normanco.ninja;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "LogMessage";
    int dateBorn;
    int dateDue;
    int usingDefImage;
    long ageActual;
    long ageCorrected;
    Baby baby;
    Uri image;
    ImageView imvBG;
    TextView txv1;
    TextView txv2;
    TextView txv3;
    TextView txv4;
    TextView txv5;
    TextView txv6;
    TextView txv7;
    TextView txv8;
    TextView txvGest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        SharedData.setSharedPref(getApplicationContext());          //set ups the shared preferences for the application.
        setDefaults(savedInstanceState);                            //set the defaults of the view and ages.

        baby = new Baby(dateDue, dateBorn);
        imvBG = findViewById(R.id.imv_photo_main);
        txv1 = findViewById(R.id.txv_main_top_1);
        txv2 = findViewById(R.id.txv_main_top_2);
        txv3 = findViewById(R.id.txv_main_top_3);
        txv4 = findViewById(R.id.txv_main_top_4);
        txv5 = findViewById(R.id.txv_main_bott_1);
        txv6 = findViewById(R.id.txv_main_bott_2);
        txv7 = findViewById(R.id.txv_main_bott_3);
        txv8 = findViewById(R.id.txv_main_bott_4);
        txvGest = findViewById(R.id.txv_gestation);

        imvBG.setScaleType(ImageView.ScaleType.MATRIX);
        imvBG.setImageMatrix(Image.rotateImage(this, image, usingDefImage));

        registerForContextMenu(imvBG);       //tells the context menu to attach to the list view
    }

    @Override
    protected void onStart() {
        super.onStart();
        imvBG.setImageURI(image);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ageActual = baby.getAgeActual();
        ageCorrected = baby.getAgeCorrected();
        txv1.setText(Integer.toString(baby.getYears(ageCorrected)));
        txv2.setText(Integer.toString(baby.getMonths(ageCorrected)));
        txv3.setText(Integer.toString(baby.getAgeWeeks(ageCorrected)));
        txv4.setText(Integer.toString(baby.getAgeDays(ageCorrected)));
        txv5.setText(Integer.toString(baby.getYears(ageActual)));
        txv6.setText(Integer.toString(baby.getMonths(ageActual)));
        txv7.setText(Integer.toString(baby.getAgeWeeks(ageActual)));
        txv8.setText(Integer.toString(baby.getAgeDays(ageActual)));
        txvGest.setText(baby.getGestation());

        Log.i(TAG, "OnResume Called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("dateDue", dateDue);
        outState.putInt("dateBorn", dateBorn);
        outState.putParcelable("image", image);
        outState.putInt("usingDefImage", usingDefImage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("Edit");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Edit")) {
            Intent intent = new Intent(getApplicationContext(), EditActivity.class);
            intent.putExtra("image", image);
            startActivity(intent);
        }
        return super.onContextItemSelected(item);
    }

    private void setDefaults(Bundle savedInstanceState){
        if(savedInstanceState != null){
            dateDue = savedInstanceState.getInt("dateDue");
            dateBorn = savedInstanceState.getInt("dateBorn");
            image = savedInstanceState.getParcelable("image");
            usingDefImage = savedInstanceState.getInt("usingDefImage");
        }
        else if(SharedData.getDataFlag()){
            dateBorn = SharedData.getBornDate();
            dateDue = SharedData.getDueDate();
            Intent intent = getIntent();
            Image.setImageUri(getApplicationContext(), SharedData.getImagePath());
            Image.setDefImageUri();

            if(intent.getIntExtra("flag", 0) == 1){
                image = intent.getParcelableExtra("image");
                usingDefImage = intent.getIntExtra("usingDefImage", Image.DEFAULT_IMAGE);
                Log.i(TAG, "Main Activity uri from intent: " + image + "Default Image Value: " + usingDefImage);
            }
            else if (Image.getImageUri() != null){
                image = Image.getImageUri();
                usingDefImage = 0;
                Log.i(TAG, "Main Activity uri from user image: " + image);
            }
            else{
                image = Image.getDefImageUri();
                usingDefImage = Image.DEFAULT_IMAGE;
                Log.i(TAG, "Main Activity uri from default: " + image);
            }
        }
        else{
            Intent intent = new Intent(getApplicationContext(), EditActivity.class);
            startActivity(intent);
        }
    }
}
