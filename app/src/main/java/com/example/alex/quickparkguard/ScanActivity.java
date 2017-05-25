package com.example.alex.quickparkguard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by Alex on 22/05/2017.
 */

public class ScanActivity extends AppCompatActivity {
    String usuario;
    SurfaceView cameraView;
    BarcodeDetector barcode;
    CameraSource cameraSource;
    SurfaceHolder holder;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_screen);

        ImageButton flecha = (ImageButton) findViewById(R.id.button2);
        title = (TextView)findViewById(R.id.textView);
        usuario = getIntent().getStringExtra("user");

        flecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gomaps = new Intent(ScanActivity.this, ScreenVigilancia.class);
                gomaps.putExtra("user", usuario);
                startActivity(gomaps);
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        cameraView = (SurfaceView) findViewById(R.id.cameraView2);
        cameraView.setZOrderMediaOverlay(true);
        holder = cameraView.getHolder();
        barcode = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();



        if(!barcode.isOperational()){
            Toast.makeText(this, "No se pudo iniciar el detector", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        cameraSource = new CameraSource.Builder(this, barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920,1024)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(ContextCompat.checkSelfPermission(ScanActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    try {

                        try{
                            Thread.sleep(500);
                        }catch (Exception ex)
                        {

                        }
                        cameraSource.start(cameraView.getHolder());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        barcode.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcode = detections.getDetectedItems();
                try{
                    Thread.sleep(500);
                }catch (Exception ex)
                {

                }
                if(barcode.size() > 0)
                {
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcode.valueAt(0));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    /*@Override
    public void onBackPressed() {
        Intent goregistro = new Intent(ScanActivity.this, ScreenVigilancia.class);
        goregistro.setFlags(goregistro.FLAG_ACTIVITY_NEW_TASK | goregistro.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goregistro);
        finish();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }*/
}

