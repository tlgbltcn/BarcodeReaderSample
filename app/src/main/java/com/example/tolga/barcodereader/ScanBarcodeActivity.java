package com.example.tolga.barcodereader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import java.io.IOException;

public class ScanBarcodeActivity extends Activity {
    SurfaceView cameraPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);


        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        createCameraSource();

    }

        public void createCameraSource(){

            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
            final CameraSource cameraSource = new CameraSource.Builder(this,barcodeDetector)
                    .setRequestedPreviewSize(1600,1024)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedFps(30.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {

                public static final int REQUEST_PERMISSION_CAMERA = 77;

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if(ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(ScanBarcodeActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
                }
            });

            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {

                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if(barcodes.size()>0){
                        Intent intent = new Intent();
                        intent.putExtra("barcode",barcodes.valueAt(0)); // son değer
                        setResult(CommonStatusCodes.SUCCESS, intent);
                        finish();


                    }


                }
            });
        }
}
