package dev.stylesync.stylesync.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.Data;
import dev.stylesync.stylesync.data.DataCallback;
import dev.stylesync.stylesync.data.PlanData;
import dev.stylesync.stylesync.data.PromptData;
import dev.stylesync.stylesync.data.StringCallback;
import dev.stylesync.stylesync.data.WeatherData;
import dev.stylesync.stylesync.utility.ChatGPT;

public class PlanService implements Service {
    private final MainActivity context;

    // Constructor
    public PlanService(MainActivity context) {
        this.context = context;
    }

    // Method to generate plan asynchronously
    public void generatePlan(DataCallback callback) {
        PromptData promptData = new PromptData();

        context.weatherService.getWeatherData(new DataCallback() {
            @Override
            public void onDataReceived(Data data) {
                promptData.setWeatherData((WeatherData) data);
                promptData.setUserData(context.userService.getUserData());

                if (promptData.getUserData() == null) {
                    callback.onError("Failed to retrieve user data");
                    return;
                }

                String promptDataJSON = new Gson().toJson(promptData);
                String prompt = makePrompt(promptDataJSON);

                ChatGPT.sendPrompt(context, ChatGPT.makeTextRequest(prompt), new StringCallback() {
                    @Override
                    public void onStringReceived(String string) {
                        try {
                            PlanData planData = new Gson().fromJson(string, PlanData.class);
                            callback.onDataReceived(planData);
                        } catch (JsonSyntaxException e) {
                            callback.onError("Invalid response from ChatGPT: " + string);
                        } catch (Exception e) {
                            callback.onError("An error occurred processing the response: " + e);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        callback.onError("Failed to send prompt: " + message);
                    }
                });
            }

            @Override
            public void onError(String message) {
                callback.onError("Failed to retrieve weather data: " + message);
            }
        });
    }

    // Helper method to create prompt
    private static String makePrompt(String json) {
        return "Please generate an output JSON file with the following input: " + json
                + " Fill out the following output with recommended outfit plans based on the weather conditions, "
                + "favorite colors, schedules, celebrity and available clothes. The arrays should contain only string literals that are clothing items "
                + "If celebrity is \"None\", do not use the celebrity in the plan. Otherwise, use what the celebrity wears as style inspiration" +
                " User preference adds some weight to the plan but is not required. Make sure to generate a comprehensive plan "
                + "that is suitable to go outside: {\"plan1\":[],\"plan2\":[],\"plan3\":[]}"
                + " DO NOT HAVE EXTRA TEXT ASIDE FROM THE JSON OUTPUT, INCLUDING NEW LINES";
    }
}
