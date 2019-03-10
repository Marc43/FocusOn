package com.example.focuson;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.util.Range;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class CameraInteractionToServer {

    protected CameraDevice cameraDevice;

    private String URL_IMAGE_SERVER;
    private static final String UPLOAD_IMAGE = "upload_image";
    /*  Available requests in the server
        /upload_image
     */

    private ImageReader imageReader;
    private RequestQueue queue;

    int videoWidth = 320;
    int videoHeight = 240;

    public final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            try (Image image = reader.acquireNextImage()) {
                Image.Plane[] planes = image.getPlanes();
                if (planes.length > 0) {
                    ByteBuffer buffer = planes[0].getBuffer();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                    sendDataToServer(data);
                }
            }
        }

    };

    public CameraInteractionToServer() {

    }

    public CameraInteractionToServer(ImageReader imageReader) {
        this.imageReader = imageReader;
        this.imageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);
    }

    public CameraInteractionToServer(ImageReader imageReader, RequestQueue queue) {
        this.imageReader = imageReader;
        this.queue = queue;
    }

    public CameraInteractionToServer(String URL_IMAGE_SERVER, ImageReader imageReader, RequestQueue queue, MainActivity activity) {
        this.URL_IMAGE_SERVER = URL_IMAGE_SERVER;
        this.imageReader = imageReader;
        this.imageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);

        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            String pickedCamera = getCamera(manager);
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.openCamera(pickedCamera, cameraStateCallback, null);
            imageReader = ImageReader.newInstance(videoWidth, videoHeight, 0x00000001 /*ImageFormat.YUV_420_888*/, 2 /* images buffered */);
            imageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);
            Log.i(TAG, "imageReader created");
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }
        System.out.println("setOnImageAvailable");
        this.queue = queue;
    }

    protected CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.i(TAG, "CameraDevice.StateCallback onOpened");
            cameraDevice = camera;
            actOnReadyCameraDevice();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.w(TAG, "CameraDevice.StateCallback onDisconnected");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.e(TAG, "CameraDevice.StateCallback onError " + error);
        }
    };

    protected CaptureRequest createCaptureRequest() {
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            builder.addTarget(imageReader.getSurface());
            builder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range(1,5));
            return builder.build();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public void actOnReadyCameraDevice()
    {
        try {
            cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()), sessionStateCallback, null);
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }
    }

    protected CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            Log.i(TAG, "CameraCaptureSession.StateCallback onConfigured see se se callback se se seee");
            try {
                session.setRepeatingRequest(createCaptureRequest(), null, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void onConfigureFailed( CameraCaptureSession session) {
        }
    };


    public String getCamera(CameraManager manager) {
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CameraCharacteristics.LENS_FACING_FRONT) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    String encodedImage;
    private void sendDataToServer(byte[] data) {

        encodedImage = Base64.encodeToString(data, Base64.DEFAULT);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_IMAGE_SERVER+UPLOAD_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        return;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        return;
                    }
                })
                {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new Hashtable<String, String>();

                    params.put("image", encodedImage);
                    return params;
                }
                };
                {
                    queue.add(stringRequest);
                    System.gc();
                    System.out.println("Upload image request queued");
                }
    }
}

