package dev.stylesync.stylesync.utility;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.ChatGPTData;
import dev.stylesync.stylesync.data.DataCallback;

public class ChatGPT {

    public static void sendPrompt(MainActivity context, String prompt, final DataCallback callback) {

        String url = Secrets.CHATGPT_API_URL;

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("model", "gpt-3.5-turbo");
            jsonRequest.put("messages", new JSONArray().put(new JSONObject().put("role", "user").put("content", prompt)));
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Failed to create JSON request");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,
                response -> {
                    ChatGPTData data = new Gson().fromJson(response.toString(), ChatGPTData.class);
                    if (data != null) {
                        callback.onDataReceived(data);
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
}
