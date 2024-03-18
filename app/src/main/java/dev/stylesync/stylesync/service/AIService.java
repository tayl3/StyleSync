package dev.stylesync.stylesync.service;

import com.google.gson.Gson;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.DataCallback;
import dev.stylesync.stylesync.data.InferData;
import dev.stylesync.stylesync.data.PlanData;
import dev.stylesync.stylesync.utility.ChatGPT;

public class AIService implements Service {
    private final MainActivity context;

    public AIService(MainActivity context) {
        this.context = context;
    }

    public void generatePlan(DataCallback callback) {
        new Thread(() -> {
            InferData inferData = new InferData();
            inferData.weatherData = context.weatherService.getData();
            inferData.userData = context.userDataService.getData();

            if (inferData.weatherData == null) {
                callback.OnError("Failed to retrieve weather data");
                return;
            }
            if (inferData.userData == null) {
                callback.OnError("Failed to retrieve user data");
                return;
            }

            String json = new Gson().toJson(inferData);
            String planDataJSON = ChatGPT.sendPrompt(makePrompt(json));
            if (planDataJSON == null) {
                callback.OnError("Invalid response from ChatGPT");
                return;
            }

            try {
                callback.OnDataReceived(new Gson().fromJson(planDataJSON, PlanData.class));
            } catch (Exception e){
                callback.OnError("Invalid response from ChatGPT: " + planDataJSON);
            }
        }).start();
    }

    private String makePrompt(String json) {
        return "Please generate an output JSON file with the following input: " + json
                + "Fill out the following output with complete recommended outfit plans based on the weather conditions, favorite colors, schedules, and available clothes. User preference adds some weight to the plan but is not required. Make sure to generate a comprehensive plan that is suitable to go outside: "
                + "{\"plan1\":[],\"plan2\":[],\"plan3\":[]}"
                + "DO NOT HAVE EXTRA TEXT ASIDE FROM THE JSON OUTPUT, INCLUDING NEW LINES";
    }
}