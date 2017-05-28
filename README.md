# BarcodeReaderSample
Barcode Reader


I used google vision api for barcode reader. Can you find basic barcode reader example. I added flash light open and close during when camera preview open.


You would need two permissions Manifest.permission:

<uses-permission android:name="android.permission.CAMERA" />
    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus"/>
    
    This is MainActivity;
    -------------------------------------------
    
    public class MainActivity extends Activity{
    TextView barcodeResult;

    @Override
-    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barcodeResult = (TextView) findViewById(R.id.barcode_result);

    }

    public void scanBarcode (View v){
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(intent, 0);
-    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0){
            if(resultCode== CommonStatusCodes.SUCCESS){
                if(data!=null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    barcodeResult.setText("Barcode Value : " + barcode.displayValue);
                }else{
                    barcodeResult.setText("No Barcode Found");
                }
            }

        }else
        super.onActivityResult(requestCode, resultCode, data);
    }
    
}
    
    
    This is ScanBarcodeActivity ;
    -------------------------------
    
    public class ScanBarcodeActivity extends Activity implements View.OnClickListener {
    SurfaceView cameraPreview;
    CameraSource cameraSource;
    ImageView flashbtn;
    android.hardware.Camera.Parameters params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        flashbtn = (ImageView) findViewById(R.id.flashbtn);
        flashbtn.setOnClickListener(this);


        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        createCameraSource();

    }

    @Override
    public void onClick(View v) {
        flashOnButton();
    }

    private Camera camera = null;
    boolean flashmode=false;
    private void flashOnButton() {
        camera=getCamera(cameraSource);
        if (camera != null) {
            try {
                Camera.Parameters param = camera.getParameters();
                param.setFlashMode(!flashmode?Camera.Parameters.FLASH_MODE_TORCH :Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(param);
                flashmode = !flashmode;
                if(flashmode){
                    Toast.makeText(this, "Flash Switched ON", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Flash Switched Off", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    private static Camera getCamera(@NonNull CameraSource cameraSource) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        return camera;
                    }
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return null;
    }

        public void createCameraSource(){

            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
            cameraSource = new CameraSource.Builder(this,barcodeDetector)
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


