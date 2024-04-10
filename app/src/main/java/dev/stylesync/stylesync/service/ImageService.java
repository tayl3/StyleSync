package dev.stylesync.stylesync.service;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.DataCallback;
import dev.stylesync.stylesync.data.ImgBBData;
import dev.stylesync.stylesync.data.StringCallback;
import dev.stylesync.stylesync.utility.ChatGPT;
import dev.stylesync.stylesync.utility.Secrets;

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

    public void uploadImage(String imageBase64, final DataCallback callback) {
        String url = "https://api.imgbb.com/1/upload";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    ImgBBData imageData = new Gson().fromJson(response, ImgBBData.class);
                    callback.onDataReceived(imageData);
                }, error -> callback.onError("Failed to upload the image to ImgBB")) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", Secrets.IMGBB_API_KEY);
                params.put("image", imageBase64);
                return params;
            }
        };

        context.volleyRequestQueue.add(stringRequest);
    }

    public void identifyImage(String url, StringCallback callback) {
        ChatGPT.sendPrompt(context, ChatGPT.makeImageRequest(makePrompt(), url), new StringCallback() {
            @Override
            public void onStringReceived(String string) {
                if (string.lastIndexOf('.') == string.length() - 1) {
                    string = string.substring(0, string.length() - 1);
                }
                callback.onStringReceived(string);
            }

            @Override
            public void onError(String message) {
                callback.onError("Failed to identify the image");
            }
        });
    }

    private static String makePrompt() {
        return "Provide a detailed description of the clothing, accessory, or other wearing item in this image, including its color, style, pattern, shape, text, and any distinctive features present. The description should contain a few qualifiers and the type of the item. The description should not be a list of keywords and avoid using commas or other separators. Identify specific brand and model if possible. The length should be no more than 10 words. Output \"False\" if you cannot identify any item. Output \"Multiple\" if you can identify multiple items.";
    }
}
