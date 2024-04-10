package dev.stylesync.stylesync.utility;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dev.stylesync.stylesync.MainActivity;

public class ChatGPT {

    private static RequestQueue queue;

    public static void sendPrompt(Context context, String prompt, final VolleyResponseListener listener) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }

        String url = Secrets.CHATGPT_API_URL;

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("model", "gpt-3.5-turbo");
            jsonRequest.put("messages", new JSONArray().put(new JSONObject().put("role", "user").put("content", prompt)));
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onError("Failed to create JSON request");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,
                response -> {
                    String parsedResponse = parseResponse(response.toString());
                    if (parsedResponse != null) {
                        listener.onResponse(parsedResponse);
                    } else {
                        listener.onError("Failed to parse response");
                    }
                }, error -> listener.onError("Error in network request: " + error.getMessage())) {
            @Override
            public java.util.Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + Secrets.CHATGPT_API_KEY);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    private static String parseResponse(String response) {
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(response);
            return jsonObj.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface VolleyResponseListener {
        void onResponse(String response);
        void onError(String message);
    }
}
