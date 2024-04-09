package dev.stylesync.stylesync.service;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.ImgBBData;

public class ImageService implements Service {
    private final MainActivity context;

    public ImageService(MainActivity context) {
        this.context = context;
    }

    public void captureImage() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            System.err.println("Camera permission not granted");
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(context, takePictureIntent, MainActivity.REQUEST_CODE_CAPTURE_IMAGE, null);
    }

    public void uploadImage(String imageBase64) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.imgbb.com/1/upload";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Gson gson = new Gson();
                    ImgBBData imageData = gson.fromJson(response, ImgBBData.class);
                    System.out.println("Image URL: " + imageData.getData().getUrl());
                }, error -> System.err.println("Failed to upload the image to ImgBB")) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", "34e2c7376d77be5f4837ab45ba6accb2");
                params.put("image", imageBase64);
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
