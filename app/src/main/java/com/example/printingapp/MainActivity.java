package com.example.printingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    SignatureView signatureView ;
    ImageButton imgErager,imgColor,imgSave,imgSingleErager,imgPen;
    SeekBar seekBar;
    TextView textPenSize;
    int defultcolor;
    int pensize=5;

    private static String myFile;
    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Panting");
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signatureView = findViewById(R.id.signature_view);
        imgErager =findViewById(R.id.btnErazer);
        imgColor = findViewById(R.id.btnColor);
        imgSave = findViewById(R.id.btnSave);
        imgSingleErager = findViewById(R.id.btnSingleErase);
        imgPen= findViewById(R.id.btnPen);
        seekBar = findViewById(R.id.pensize);
        textPenSize = findViewById(R.id.textPenSize);

        askpermission();

        SimpleDateFormat format = new SimpleDateFormat("yyMMdd_HHmmss", Locale.getDefault());
        String date = format.format(new Date());
        myFile = file+"/"+date+".png";
        if (!file.exists()){
            file.mkdirs();
            Toast.makeText(MainActivity.this, "crate dicectroy", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(MainActivity.this, "not create", Toast.LENGTH_SHORT).show();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textPenSize.setText(i+"dp");
                signatureView.setPenSize(i);
                pensize = i;
                seekBar.setMax(50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        defultcolor = ContextCompat.getColor(MainActivity.this,R.color.black);
        imgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });
        imgErager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureView.clearCanvas();
            }
        });
        imgSingleErager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color = signatureView.getBackgroundColor();
                signatureView.setPenColor(color);
                signatureView.setPenSize(25);

            }
        });
        imgPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureView.setPenColor(defultcolor);

                signatureView.setPenSize(pensize);
            }
        });
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!signatureView.isBitmapEmpty()){
                    try {
                        saveImag();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Image not save", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void saveImag() throws IOException {
        File safefile = new File(myFile);
        Bitmap bitmap = signatureView.getSignatureBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,bos);
        byte[] bitmapData = bos.toByteArray();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
    }

    private void openColorPicker() {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(MainActivity.this, defultcolor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defultcolor = color;
                signatureView.setPenColor(defultcolor);
            }
        });
        ambilWarnaDialog.show();
    }


    private void askpermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        Toast.makeText(getApplicationContext(), "Permission Grented", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
}