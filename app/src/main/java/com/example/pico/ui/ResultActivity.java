package com.example.pico.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.example.pico.R;
import com.example.pico.databinding.ActivityResultBinding;
import com.google.android.gms.ads.AdRequest;
import java.io.File;
import java.io.FileOutputStream;

public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding binding;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.ivEditedPhoto.setImageURI(getIntent().getData());

        binding.ibSave.setOnClickListener(v -> {
            if(!isSaved) {
                saveToGallery();
            } else {
                Toast.makeText(this, "Your photo is already saved...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.ibBack.setOnClickListener(v -> {
            onBackPressed();
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
    }

    private void saveToGallery() {
        binding.pbDownloading.setVisibility(View.VISIBLE);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.ivEditedPhoto.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        FileOutputStream outputStream = null;

        File file = Environment.getExternalStorageDirectory();
        String path = file.getAbsolutePath();
        File dir = new File(path + "/MyPics");
        dir.mkdirs();

        String filename = String.format("%d.png", System.currentTimeMillis());
        File outFile = new File(dir, filename);
        try {
            outputStream = new FileOutputStream(outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        try {
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            outputStream.close();
            Uri uri = Uri.fromFile(file);
            getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            Toast.makeText(this, "Hurry!! Photo is saved in your Gallery.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.pbDownloading.setVisibility(View.GONE);
        binding.ibSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_downloaded, null));
        isSaved = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}