package normanco.ninja;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity implements DatePickerFragment.OnDateSetListener {
    private static String TAG = "LogMessage";
    ConstraintSet constraintSet1 = new ConstraintSet();
    ConstraintSet constraintSet2 = new ConstraintSet();
    DatePickerFragment datePicker = new DatePickerFragment();
    ConstraintLayout mConstraintLayout;
    ImageButton btnPhoto;
    ImageButton btnGallery;
    ImageButton btnCamera;
    ImageButton btnAccept;
    TextView txvDueDate;
    TextView txvBornDate;
    ImageView imvBgEdit;
    Uri image = null;
    boolean collapsed = true;               //stores the state of the button stack.
    int dateDue;                            //stores the due date entered
    int dateBorn;                           //stores the birth date entered
    int viewClicked;                        //stores which date was selected to be edited
    int usingDefImage = Image.DEFAULT_IMAGE;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_state1);
        final Context mContext = this;
        SharedData.setSharedPref(mContext);

        //initialise the widgets
        btnPhoto = findViewById(R.id.btn_photo);
        btnGallery  = findViewById(R.id.btn_gallery);
        btnCamera = findViewById(R.id.btn_camera);
        btnAccept = findViewById(R.id.btn_ok);
        txvDueDate = findViewById(R.id.txv_due_date);
        txvBornDate = findViewById(R.id.txv_birth_date);
        imvBgEdit = findViewById(R.id.imv_photo_edit);

        //set the defaults;
        setDefaults(savedInstanceState);

        //Photo button is clicked to expand the userImage buttons
        btnPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                buttonCollapse(mContext);
            }
        });

        //Due date OnClick Listeners.
        txvDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewClicked = 1;
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

        //Born date on click listener
        txvBornDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewClicked = 2;
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

        //Gallery button on click listener
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        //Camera button on click listen. usding to reset shared preferences
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image.setDefImageUri();
                image = Image.getDefImageUri();
                Matrix matrix = Image.rotateImage(mContext, image, Image.DEFAULT_IMAGE);
                imvBgEdit.setScaleType(ImageView.ScaleType.MATRIX);
                imvBgEdit.setImageMatrix(matrix);
                imvBgEdit.setImageURI(image);
            }
        });

        // Ok button onclick listener
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedData.setSharedPref(getApplicationContext());
                SharedData.setBornDate(dateBorn);
                SharedData.setDueDate(dateDue);
                SharedData.setImagePath(mContext, image);
                SharedData.applyData();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("image", image);
                intent.putExtra("flag", 1);
                intent.putExtra("usingDefImage", usingDefImage);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Log.i(TAG, "Edit Activity passed image URI: " + image);
                finish();
                startActivity(intent);
            }
        });
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
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int date = year*10000 + month*100 + dayOfMonth;

        if(viewClicked == 1){
            dateDue = date;
            txvDueDate.setText(DatesToWords.parseDateToWord(date));
        }
        else if(viewClicked == 2) {
            dateBorn = date;
            txvBornDate.setText(DatesToWords.parseDateToWord(date));
        }
    }

    //Saves the Uri of the userImage in the Uri variable and saves userImage to folder
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            image = data.getData();
            usingDefImage = 0;
            Matrix matrix = Image.rotateImage(this, image, usingDefImage);
            imvBgEdit.setScaleType(ImageView.ScaleType.MATRIX);
            imvBgEdit.setImageMatrix(matrix);
            imvBgEdit.setImageURI(image);
        }
    }

    private void buttonCollapse(Context mContext) {
        constraintSet2.clone(mContext, R.layout.activity_edit_state2);
        constraintSet1.clone(mContext, R.layout.activity_edit_state1);
        mConstraintLayout = findViewById(R.id.activity_edit_state1);
        TransitionManager.beginDelayedTransition(mConstraintLayout);

        if (collapsed) {
            constraintSet2.applyTo(mConstraintLayout);
            collapsed = false;
        } else {
            constraintSet1.applyTo(mConstraintLayout);
            collapsed = true;
        }
    }

    private void setDefaults(Bundle savedInstanceState){
        if(savedInstanceState != null){
            dateDue = savedInstanceState.getInt("dateDue");
            dateBorn = savedInstanceState.getInt("dateBorn");
            image = savedInstanceState.getParcelable("image");
            usingDefImage = savedInstanceState.getInt("usingDefImage");
            txvDueDate.setText(DatesToWords.parseDateToWord(dateDue));
            txvBornDate.setText(DatesToWords.parseDateToWord(dateBorn));
            Matrix matrix = Image.rotateImage(this, image, usingDefImage);
            imvBgEdit.setScaleType(ImageView.ScaleType.MATRIX);
            imvBgEdit.setImageMatrix(matrix);
            imvBgEdit.setImageURI(image);

            Log.i(TAG, "Image Uri 1: " + image);
        }
        else if (SharedData.getDataFlag()){
            Intent intent = getIntent();
            dateDue = SharedData.getDueDate();
            dateBorn = SharedData.getBornDate();
            image = intent.getParcelableExtra("image");
            txvDueDate.setText(DatesToWords.parseDateToWord(dateDue));
            txvBornDate.setText(DatesToWords.parseDateToWord(dateBorn));
            Matrix matrix = Image.rotateImage(this, image, usingDefImage);
            imvBgEdit.setScaleType(ImageView.ScaleType.MATRIX);
            imvBgEdit.setImageMatrix(matrix);
            imvBgEdit.setImageURI(image);

            Log.i(TAG, "Image Uri 2: " + image);
        }
        else {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            Intent intent = getIntent();
            dateDue = dateBorn = year * 10000 + month * 100 + day;
            image = intent.getParcelableExtra("image"); Log.i(TAG, "Received Image");

            txvDueDate.setText(DatesToWords.parseDateToWord(dateDue));
            txvBornDate.setText(DatesToWords.parseDateToWord(dateBorn));
            Matrix matrix = Image.rotateImage(this, image, usingDefImage);
            imvBgEdit.setScaleType(ImageView.ScaleType.MATRIX);
            imvBgEdit.setImageMatrix(matrix);
            imvBgEdit.setImageURI(image);

            Log.i(TAG, "Image Uri 3: " + image);
        }
    }

    private void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

}
