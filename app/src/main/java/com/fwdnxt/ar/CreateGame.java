package com.fwdnxt.ar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class CreateGame extends Activity {

    NativeProcessor nativeAPI;

    boolean learner = true;
    int screenwidth, screenheight;
    int nativeerror;
    int imageSideNN = 128; // input image size for neural network
    Camera myCamera;

    MyCameraSurfaceView myCameraSurfaceView;

    static int currentCameraId;
    FrameLayout myCameraPreview;

    Button[] protos;
    BitmapDrawable[][] protobmps;
    String[] objectnames;
    int editingobject;
    TextView resultField1;
    TextView resultField2;
    TextView fpsField;
    EditText objectName;

    RelativeLayout mainlayout;

    boolean recording = true;

    Typeface bebasNeue;
    Typeface openSans;
    Typeface telegrafico;

    static
    {
        System.loadLibrary("NativeProcessor");
    }


    /**
     *
     */
    public void initialize() {
        nativeAPI = new NativeProcessor();
        //nativeerror = nativeAPI.init(getAssets(), "Networks/generic", imageSideNN, learner);
        nativeerror = nativeAPI.init(null, "/sdcard/neural-nets", imageSideNN, learner);

        telegrafico = Typeface.createFromAsset(getAssets(), "telegrafico.ttf");
        mainlayout = (RelativeLayout) findViewById(R.id.background);

        resultField1 = (TextView) findViewById(R.id.category1);
        resultField2 = (TextView) findViewById(R.id.category2);

        fpsField = (TextView) findViewById(R.id.fps);

        bebasNeue = Typeface.createFromAsset(getAssets(), "BebasNeue.otf");
        openSans = Typeface.createFromAsset(getAssets(), "OpenSans.ttf");

        objectName = (EditText) findViewById(R.id.editTextObjectName);
        objectnames = new String[5];

        resultField1.setTypeface(bebasNeue);
        resultField2.setTypeface(bebasNeue);
        fpsField.setTypeface(openSans);
        protos = new Button[]{
                (Button)findViewById(R.id.proto1),
                (Button)findViewById(R.id.proto2),
                (Button)findViewById(R.id.proto3),
                (Button)findViewById(R.id.proto4),
                (Button)findViewById(R.id.proto5),
        };
        if (learner) {
            protobmps = new BitmapDrawable[5][];
            for (int i = 0; i < 5; i++) {
                protos[i].setHeight(protos[i].getWidth());
                protos[i].setOnClickListener(protosOnClickListener);
            }
        } else {
            for (int i = 0; i < 5; i++)
                protos[i].setVisibility(View.GONE);
        }
        objectName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public  boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    v.setVisibility(View.GONE);
                    objectnames[editingobject] = ((EditText)v).getText().toString();
                    v.setText("");
                    recording = true;
                    handled = true;
                }
                return handled;
            }
        });
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

        myCamera = Camera.open(currentCameraId); // attempt to get a Camera instance
        myCamera.setDisplayOrientation(90);

        myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);
        myCameraPreview = (FrameLayout) findViewById(R.id.surface_camera);
        myCameraPreview.addView(myCameraSurfaceView);

        initialize();

        /*******GET SIZE OF PHONE SCREEN******/
        Display screenDisplay = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        screenDisplay.getSize(size);
        screenwidth = size.x;
        screenheight = size.y;

    }



    /**
     * 	Class that controls Camera View
     */
    public class MyCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Camera.PreviewCallback {

        SurfaceHolder mHolder;
        Camera mCamera;
        long prevtm = 0;

        public MyCameraSurfaceView(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);

            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int weight, int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            Parameters parameters;
            parameters = mCamera.getParameters();
            Camera.Size csize = parameters.getPreviewSize();
            // Take the smallest acceptable resolution
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            for(Camera.Size size : previewSizes) {
                if (size.height > 360 && size.height < csize.height) {
                    csize.width = size.width;
                    csize.height = size.height;
                }
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }
            parameters.setPreviewFormat(ImageFormat.NV21);
            parameters.setPreviewSize(csize.width, csize.height);
            mCamera.setParameters(parameters);

            // make any resize, rotate or reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.setPreviewCallback(this);
                mCamera.startPreview();

            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();

                mCamera.setDisplayOrientation(90);

                /*if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
                	mCamera.setDisplayOrientation(90);
                else
                	mCamera.setDisplayOrientation(270);*/
            } catch (Exception e) {
                mCamera.release();
                mCamera = null;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
        }

        LiveMode liveMode = new LiveMode(CreateGame.this);

        @Override
        public void onPreviewFrame(byte[] data, Camera arg1) {
            if(recording) {
                liveMode.startProcess(data, mCamera, nativeAPI);
            }
        }
    }

    public void ProtoFound(int idx, float distance, float max, float fps)
    {
        float threshold = (float) 0.5;
        if(distance < max*threshold)
        {
            resultField1.setText(objectnames[idx]);
            resultField2.setText(String.format("Distance: %.3f", distance));
        } else {
            resultField1.setText("");
            resultField2.setText("");
        }
        fpsField.setText(String.format("%.2f", fps) + " fps");
    }

    public void SaveProto(int idx)
    {
        myCameraSurfaceView.liveMode.saveproto = idx;
    }

    public void SetProtoImage(int idx, Bitmap bmp)
    {
        bmp = LiveMode.getResizedBitmap(bmp, protos[0].getWidth(), protos[0].getWidth());
        protos[idx].setBackground(new BitmapDrawable(getResources(), bmp));
        int side = bmp.getWidth();
        int[] pixels = new int[side*side];
        bmp.getPixels(pixels, 0, side, 0, 0, side, side);
        for(int j = 0; j < side; j++)
            for(int i = 0; i < 5; i++)
            {
                pixels[i*side + j] = Color.RED;
                pixels[(side-1-i)*side + j] = Color.RED;
                pixels[i + j*side] = Color.RED;
                pixels[(side-1-i) + j*side] = Color.RED;
            }
        protobmps[idx] = new BitmapDrawable[]{
                new BitmapDrawable(getResources(), bmp),
                new BitmapDrawable(getResources(), Bitmap.createBitmap(pixels, side, side, Bitmap.Config.ARGB_8888))
        };
    }

    /**
     *
     */
    Button.OnClickListener protosOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(recording) {
                editingobject = Integer.parseInt(((Button) v).getText().toString()) - 1;
                CreateGame.this.SaveProto(Integer.parseInt(((Button) v).getText().toString()) - 1);
                objectName.setVisibility(View.VISIBLE);
                recording = false;
            }
        }
    };

}