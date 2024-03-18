package dev.stylesync.stylesync.utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import dev.stylesync.stylesync.MainActivity;

public class ChatGPT {
    public static String sendPrompt(String prompt) {
        try {
            String response = send(prompt);
            return parseResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String send(String prompt) throws IOException {
        URL url = new URL(MainActivity.CHATGPT_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + MainActivity.CHATGPT_API_KEY);
            connection.setDoOutput(true);

            String jsonInputString = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt.replace("\"", "\\\"") + "\"}]}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();

            // On success
            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
                return response.toString();
            }
            // On fail
            else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.err.println("Error Response: " + response);
                    return response.toString();
                }
            }
        } finally {
            connection.disconnect();
        }
    }

    private static String parseResponse(String response) {
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(response);
            JSONArray choices = jsonObj.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                return message.getString("content");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
