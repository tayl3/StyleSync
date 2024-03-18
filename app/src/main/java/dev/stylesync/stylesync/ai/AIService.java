package dev.stylesync.stylesync.ai;

import com.google.gson.Gson;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.InferData;
import dev.stylesync.stylesync.data.PlanData;
import dev.stylesync.stylesync.database.UserDataService;
import dev.stylesync.stylesync.weather.WeatherService;

public class AIService {
    private final MainActivity context;

    // Weather data
    private final WeatherService weatherService;
    // User data
    private final UserDataService userDataService;

    public AIService(MainActivity context) {
        this.context = context;
        this.weatherService = new WeatherService(context);
        this.userDataService = new UserDataService(context);
    }

    public void infer() {
        new Thread(() -> {
            InferData inferData = new InferData();
            inferData.weatherData = weatherService.getData();
            inferData.userData = userDataService.getData();

            if (inferData.weatherData == null){
                System.err.println("Unable to retrieve weather data");
                context.planData = null;
                context.planDataAvailable = true;
                return;
            }
            if (inferData.userData == null){
                System.err.println("Unable to retrieve userData data");
                context.planData = null;
                context.planDataAvailable = true;
                return;
            }

            String json = new Gson().toJson(inferData);
            String planDataJSON = ChatGPT.sendPrompt(makePrompt(json));
            if (planDataJSON == null){
                infer();
            }

            context.planData = new Gson().fromJson(planDataJSON, PlanData.class);
            context.planDataAvailable = true;
        }).start();
    }

    private String makePrompt(String json) {
        return "Please generate an output JSON file with the following input: " + json
                + "Fill out the following output with complete recommended outfit plans based on the weather conditions, favorite colors, schedules, and available clothes. User preference adds some weight to the plan but is not required. You do not have to use up every possible cloth for each plan: "
                + "{\"plan1\":[],\"plan2\":[],\"plan3\":[]}"
                + "DO NOT HAVE EXTRA TEXT ASIDE FROM THE JSON OUTPUT, INCLUDING NEW LINES";
    }
}