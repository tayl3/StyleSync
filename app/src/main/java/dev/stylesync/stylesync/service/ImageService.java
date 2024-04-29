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

    private static String makePrompt(String url) {
        return "Provide a detailed description and proper tagging of all clothing, accessory, or other wearing items in this image and fill the following JSON template, including its color, style, pattern, shape, text, and any distinctive features present. The description should contain a few qualifiers and the type of the item. The description should not be a list of keywords and avoid using commas or other separators. Identify specific brand and model in the description if possible. The length of the description should be no more than 10 words. DO NOT HAVE EXTRA TEXT BESIDES THE JSON FILE, ONLY LIST THE ITEMS WITHOUT ANY ADDITIONAL EXPLANATIONS OR SUGGESTIONS. The original url is: " + url + "\n" +
                "[\n" +
                "  {\n" +
                "    \"description\": \"Description for item 1\",\n" +
                "    \"tags\": [\"tag1\", \"tag2\", \"tag3\"],\n" +
                "    \"url\": \"https://original-url-of-item-1\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"description\": \"Description for item 2\",\n" +
                "    \"tags\": [\"tag1\", \"tag2\", \"tag3\"],\n" +
                "    \"url\": \"https://original-url-of-item-2\"\n" +
                "  }\n" +
                "]";
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
        String url = "https://freeimage.host/api/1/upload";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    ImgBBData imageData = new Gson().fromJson(response, ImgBBData.class);
                    callback.onDataReceived(imageData);
                }, error -> callback.onError("Failed to upload the image to ImgBB, error message: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", Secrets.IMGBB_API_KEY);
                params.put("action", "upload");
                params.put("source", imageBase64);
                return params;
            }
        };

        context.volleyRequestQueue.add(stringRequest);
    }

    public void identifyImage(String url, StringCallback callback) {
        ChatGPT.sendPrompt(context, ChatGPT.makeImageRequest(makePrompt(url), url), new StringCallback() {
            @Override
            public void onStringReceived(String string) {
                callback.onStringReceived(string);
            }

            @Override
            public void onError(String message) {
                callback.onError("Failed to identify the image");
            }
        });
    }
}
