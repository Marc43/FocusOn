package com.example.focuson;

import android.content.pm.PackageManager;
import android.media.Image;
import android.media.ImageReader;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Map;

public class CameraInteractionToServer {

    private String URL_IMAGE_SERVER;
    private static final String UPLOAD_IMAGE = "upload_image";
    /*  Available requests in the server
        /upload_image
     */

    private ImageReader imageReader;
    private RequestQueue queue;

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
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
                    System.out.println("Sending stuff to fucking Mafe server");
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

    public CameraInteractionToServer(String URL_IMAGE_SERVER, ImageReader imageReader, RequestQueue queue) {
        this.URL_IMAGE_SERVER = URL_IMAGE_SERVER;
        this.imageReader = imageReader;
        this.imageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);
        System.out.println("setOnImageAvailable");
        this.queue = queue;
    }

    private void sendDataToServer(byte[] data) {

        final String encodedImage = Base64.encodeToString(data, Base64.DEFAULT);

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_IMAGE_SERVER+UPLOAD_IMAGE,
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
                    System.out.println("Upload image request queued");
                }
    }
}

