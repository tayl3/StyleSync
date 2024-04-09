package dev.stylesync.stylesync.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.PlanData;
import dev.stylesync.stylesync.data.WeatherData;
import dev.stylesync.stylesync.data.PromptData;
import dev.stylesync.stylesync.utility.ChatGPT;

public class PlanService implements Service {
    private final MainActivity context;
    private PlanData planData;
    private boolean planDataChanged;

    // Constructor
    public PlanService(MainActivity context) {
        this.context = context;
    }

    // Interface for callbacks when plan data is fetched or error occurs
    public interface PlanDataCallback {
        void onPlanDataFetched(PlanData planData);
        void onError(String message);
    }

    // Method to generate plan asynchronously
    public void generatePlan(PlanDataCallback callback) {
        PromptData promptData = new PromptData();

        context.weatherService.getWeatherData(new WeatherService.WeatherDataCallback() {
            @Override
            public void onWeatherDataFetched(WeatherData weatherData) {
                promptData.setWeatherData(weatherData);
                promptData.setUserData(context.userService.getUserData());

                if (promptData.getUserData() == null) {
                    System.err.println("Failed to retrieve user data");
                    callback.onError("Failed to retrieve user data");
                    return;
                }

                String promptDataJSON = new Gson().toJson(promptData);
                String prompt = makePrompt(promptDataJSON);

                ChatGPT.sendPrompt(context, prompt, new ChatGPT.VolleyResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            PlanData fetchedPlanData = new Gson().fromJson(response, PlanData.class);
                            setPlanData(fetchedPlanData);
                            setPlanDataChanged(true);
                            callback.onPlanDataFetched(fetchedPlanData);
                        } catch (JsonSyntaxException e) {
                            System.err.println("Invalid response from ChatGPT: " + response);
                            callback.onError("Invalid response format from ChatGPT");
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.onError("An error occurred processing the response");
                        }
                    }

                    @Override
                    public void onError(String message) {
                        System.err.println("Failed to send prompt: " + message);
                        callback.onError(message);
                    }
                });
            }

            @Override
            public void onError(String message) {
                System.err.println("Failed to retrieve weather data");
                callback.onError("Failed to retrieve weather data");
            }
        });
    }


    // Helper method to create prompt
    private String makePrompt(String json) {
        return "Please generate an output JSON file with the following input: " + json
                + " Fill out the following output with recommended outfit plans based on the weather conditions, "
                + "favorite colors, schedules, and available clothes. The arrays should contain only string literals. "
                + "User preference adds some weight to the plan but is not required. Make sure to generate a comprehensive plan "
                + "that is suitable to go outside: {\"plan1\":[],\"plan2\":[],\"plan3\":[]}"
                + " DO NOT HAVE EXTRA TEXT ASIDE FROM THE JSON OUTPUT, INCLUDING NEW LINES";
    }

    // Getters and setters
    public PlanData getPlanData() {
        return planData;
    }

    public void setPlanData(PlanData planData) {
        this.planData = planData;
    }

    public boolean isPlanDataChanged() {
        return planDataChanged;
    }

    public void setPlanDataChanged(boolean planDataChanged) {
        this.planDataChanged = planDataChanged;
    }
}
