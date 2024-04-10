package dev.stylesync.stylesync.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.PromptData;
import dev.stylesync.stylesync.data.PlanData;
import dev.stylesync.stylesync.utility.ChatGPT;

public class PlanService implements Service {
    private final MainActivity context;
    private PlanData planData;
    private boolean planDataChanged;

    public PlanService(MainActivity context) {
        this.context = context;
    }

    public void generatePlan() {
        PromptData promptData = new PromptData();
        promptData.setWeatherData(context.weatherService.getWeatherData());
        promptData.setUserData(context.userService.getUserData());

        if (promptData.getWeatherData() == null) {
            System.err.println("Failed to retrieve weather data");
            setPlanData(null);
            setPlanDataChanged(true);
            return;
        }
        if (promptData.getUserData() == null) {
            System.err.println("Failed to retrieve user data");
            setPlanData(null);
            setPlanDataChanged(true);
            return;
        }

        String promptDataJSON = new Gson().toJson(promptData);
        String prompt = makePrompt(promptDataJSON);

        ChatGPT.sendPrompt(context, prompt, new ChatGPT.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    setPlanData(new Gson().fromJson(response, PlanData.class));
                    setPlanDataChanged(true);
                } catch (JsonSyntaxException e) {
                    System.err.println("Invalid response from ChatGPT: " + response);
                    setPlanData(null);
                    setPlanDataChanged(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                System.err.println("Failed to send prompt: " + message);
                setPlanData(null);
                setPlanDataChanged(true);
            }
        });
    }

    private String makePrompt(String json) {
        return "Please generate an output JSON file with the following input: " + json
                + "Fill out the following output with recommended outfit plans based on the weather conditions, favorite colors, schedules, and available clothes. The arrays should contain only string literals. User preference adds some weight to the plan but is not required. Make sure to generate a comprehensive plan that is suitable to go outside: "
                + "{\"plan1\":[],\"plan2\":[],\"plan3\":[]}"
                + "DO NOT HAVE EXTRA TEXT ASIDE FROM THE JSON OUTPUT, INCLUDING NEW LINES";
    }

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
