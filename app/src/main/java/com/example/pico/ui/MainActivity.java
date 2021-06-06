package com.example.pico.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.example.pico.databinding.ActivityMainBinding;
import com.example.pico.util.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

/**
 * Home activity of Pico app.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    /**
     * User-permission request
     */
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        /**
         * Initialize google ads
         */
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build(); //request for ads
        binding.adView.loadAd(adRequest);                      //load ads for view

        /**
         * Open gallery to choose photo
         */
        binding.ivEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Constants.IMAGE_REQUEST_CODE);
            }
        });

        /**
         * Open Camera to take photo
         */
        binding.ivCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!allPermissionsGranted()) {
                    ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST_CODE);
                }
            }
        });
    }

    /**
     * Get user-permission for camera and storage
     */
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * CropActivity is opened for editing photo chosen from gallery.
         * 'com.theartofdev.edmodo:android-image-cropper:2.4.+' lib is used for the purpose
         */
        if (data != null) {
            if (requestCode == Constants.IMAGE_REQUEST_CODE) {
                if (data.getData() != null) {
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMultiTouchEnabled(true)
                            .start(this);
                }
            }
        }

        /**
         * The resultant photo is sent to ResultActivity.
         */
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.setData(result.getUri());
                startActivity(intent);
            }
        }

        /**
         * CropActivity is opened for editing photo taken from camera.
         * 'com.theartofdev.edmodo:android-image-cropper:2.4.+' lib is used for the purpose
         */
        if (data != null) {
            if (requestCode == Constants.CAMERA_REQUEST_CODE) {
                if (data.getExtras() != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri uri = getImageUri(photo);

                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMultiTouchEnabled(true)
                            .start(this);
                }
            } else {
                onResume();
            }
        }
    }

    /**
     * Convert bitmat to uri image.
     */
    public Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "title",
                "edited_pic"
        );
        return Uri.parse(path);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}









