package dev.stylesync.stylesync.utility;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.StringCallback;

public class ChatGPT {
    public static JSONObject makeTextRequest(String prompt) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("model", "gpt-3.5-turbo");
            jsonRequest.put("messages", new JSONArray().put(new JSONObject().put("role", "user").put("content", prompt)));
            return jsonRequest;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject makeImageRequest(String prompt, String imageURL) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("model", "gpt-4-turbo");
            JSONArray content = new JSONArray();
            content.put(new JSONObject().put("type", "text").put("text", prompt));
            content.put(new JSONObject().put("type", "image_url").put("image_url", new JSONObject().put("url", imageURL)));
            jsonRequest.put("messages", new JSONArray().put(new JSONObject().put("role", "user").put("content", content)));
            return jsonRequest;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPrompt(MainActivity context, JSONObject JSONRequest, final StringCallback callback) {

        String url = Secrets.CHATGPT_API_URL;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, JSONRequest,
                response -> {
                    if (response != null) {
                        callback.onStringReceived(getContent(response));
                    } else {
                        callback.onError("Failed to parse response");
                    }
                }, error -> callback.onError("Error in network request: " + error.getMessage())) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + Secrets.CHATGPT_API_KEY);
                return headers;
            }
        };

        context.volleyRequestQueue.add(jsonObjectRequest);
    }

    private static String getContent(JSONObject response) {
        try {
            return response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } catch (JSONException e) {
            System.err.println("Invalid ChatGPT response");
            return null;
        }
    }
}
