package normanco.ninja;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    ImageButton btnDefault;
    TextView txvDueDate;
    TextView txvBornDate;
    ImageView imvBackground;
    Image image;
    Uri imageBackground;
    String mCurrentPhotoPath;
    boolean collapsed = true;               //stores the state of the button stack.
    int dateDue;                            //stores the due date entered
    int dateBorn;                           //stores the birth date entered
    int viewClicked;                        //stores which date was selected to be edited
    int usingDefImage;
    private static final int PICK_IMAGE = 100;
    private static final int CAMERA_REQUEST = 150;

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
        imvBackground = findViewById(R.id.imv_photo_edit);

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
                if(Permissions.checkPermissionREAD_EXTERNAL_STORAGE(mContext))
                openGallery();
            }
        });

        //Camera button on click listener
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Permissions.checkPermissionCAMERA(mContext)){
                    openCamera();
                }
            }
        });

        //Default image button on click listen.
        //btnDefault.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        usingDefImage = Image.DEFAULT_IMAGE;
        //        imageBackground = image.getDefImageUri();
        //       Matrix matrix = Image.rotateImage(mContext, imageBackground, usingDefImage);
        //        imvBackground.setScaleType(ImageView.ScaleType.MATRIX);
        //        imvBackground.setImageMatrix(matrix);
        //        imvBackground.setImageURI(imageBackground);
        //        SharedData.setImagePath("default");
        //        SharedData.applyData();
        //    }
        //});

        // Ok button onclick listener
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedData.setSharedPref(getApplicationContext());
                SharedData.setBornDate(dateBorn);
                SharedData.setDueDate(dateDue);
                SharedData.setFirstFlag();
                if(usingDefImage == 0){
                    SharedData.setImagePath(mContext, imageBackground);
                }

                SharedData.applyData();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("image", imageBackground);
                intent.putExtra("flag", 1);
                intent.putExtra("usingDefImage", usingDefImage);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Log.i(TAG, "Edit Activity passed imageBackground URI: " + imageBackground);
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
        outState.putParcelable("image", imageBackground);
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
            imageBackground = data.getData();
            usingDefImage = 0;
            Matrix matrix = Image.rotateImage(this, imageBackground, usingDefImage);
            imvBackground.setScaleType(ImageView.ScaleType.MATRIX);
            imvBackground.setImageMatrix(matrix);
            imvBackground.setImageURI(imageBackground);
        }

        if(resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST) {
            Log.i(TAG, "In Camera on action");
            usingDefImage = 0;
            image = new Image(this, mCurrentPhotoPath);
            imageBackground = image.getImageUri();
            Log.i(TAG, "Parse photo Uri:" + imageBackground);
            Matrix matrix = Image.rotateImage(this, imageBackground, usingDefImage);
            imvBackground.setScaleType(ImageView.ScaleType.MATRIX);
            imvBackground.setImageMatrix(matrix);
            imvBackground.setImageURI(imageBackground);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        openGallery();
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
            imageBackground = savedInstanceState.getParcelable("image");
            usingDefImage = savedInstanceState.getInt("usingDefImage");
            txvDueDate.setText(DatesToWords.parseDateToWord(dateDue));
            txvBornDate.setText(DatesToWords.parseDateToWord(dateBorn));
            Matrix matrix = Image.rotateImage(this, imageBackground, usingDefImage);
            imvBackground.setScaleType(ImageView.ScaleType.MATRIX);
            imvBackground.setImageMatrix(matrix);
            imvBackground.setImageURI(imageBackground);

            Log.i(TAG, "Image Uri 1: " + imageBackground);
        }
        else if (!SharedData.getFirstFlag()){
            dateDue = SharedData.getDueDate();
            dateBorn = SharedData.getBornDate();

            if(SharedData.getImagePath().equals("default")) {
                Log.i(TAG, "Edit Activity - Setting image to default");
                image = new Image();
                imageBackground = image.getDefImageUri();
                usingDefImage = Image.DEFAULT_IMAGE;
            }
            else {
                Log.i(TAG, "Edit Activity - Setting image to user");
                image = new Image(this, SharedData.getImagePath());
                imageBackground = image.getImageUri();
                usingDefImage = 0;
            }

            txvDueDate.setText(DatesToWords.parseDateToWord(dateDue));
            txvBornDate.setText(DatesToWords.parseDateToWord(dateBorn));

            Matrix matrix = Image.rotateImage(this, imageBackground, usingDefImage);
            imvBackground.setScaleType(ImageView.ScaleType.MATRIX);
            imvBackground.setImageMatrix(matrix);
            imvBackground.setImageURI(imageBackground);

            Log.i(TAG, "Image Uri 2: " + imageBackground);
        }
        else {
            image = new Image();
            Matrix matrix = Image.rotateImage(this, imageBackground, Image.DEFAULT_IMAGE);
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            dateDue = dateBorn = year * 10000 + month * 100 + day;
            imageBackground = image.getDefImageUri();
            txvDueDate.setText(DatesToWords.parseDateToWord(dateDue));
            txvBornDate.setText(DatesToWords.parseDateToWord(dateBorn));
            imvBackground.setScaleType(ImageView.ScaleType.MATRIX);
            imvBackground.setImageMatrix(matrix);
            imvBackground.setImageURI(imageBackground);

            Log.i(TAG, "Image Uri 3: " + imageBackground);
        }
    }

    private void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch(IOException e) {
                Log.i(TAG, "Camera file creation error: " + e);
            }

            if(photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "normanco.ninja.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                Log.i(TAG, "Photo Path: " + photoFile);    Log.i(TAG, "Photo Uri: " + photoUri);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagePath = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = imagePath.getAbsolutePath();

        return imagePath;
    }

}
