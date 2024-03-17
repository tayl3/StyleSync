package dev.stylesync.stylesync.ai;

import com.google.gson.Gson;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.Data;
import dev.stylesync.stylesync.data.DataCallback;
import dev.stylesync.stylesync.data.InferData;
import dev.stylesync.stylesync.data.PlanData;
import dev.stylesync.stylesync.data.UserData;
import dev.stylesync.stylesync.data.WeatherData;
import dev.stylesync.stylesync.database.UserDataService;
import dev.stylesync.stylesync.weather.WeatherService;

public class AIService {
    private final MainActivity context;

    // Weather data
    private final WeatherService weatherService;
    private final DataCallback weatherDataCallback;
    private WeatherData weatherData;
    private boolean weatherDataAvailable;

    // User preference data
    private final UserDataService userDataService;
    private final DataCallback userDataCallback;
    private UserData userData;
    private boolean userDataAvailable;

    private final Object lock = new Object();

    public AIService(MainActivity context) {
        this.context = context;
        this.weatherService = new WeatherService(context);
        this.weatherDataCallback = new DataCallback() {
            @Override
            public void onDataReceived(Data data) {
                synchronized (lock) {
                    weatherData = (WeatherData) data;
                    weatherDataAvailable = true;
                    lock.notify();
                }
            }

            @Override
            public void onError(String error) {
                System.err.println(error);
            }
        };

        this.userDataService = new UserDataService(context);
        this.userDataCallback = new DataCallback() {
            @Override
            public void onDataReceived(Data data) {
                synchronized (lock) {
                    userData = (UserData) data;
                    userDataAvailable = true;
                    lock.notify();
                }
            }

            @Override
            public void onError(String error) {
                System.err.println(error);
            }
        };
    }

    public void infer() {
        new Thread(() -> {
            InferData inferData = new InferData();
            weatherService.getData(weatherDataCallback);
            userDataService.getData(userDataCallback);

            waitForData();
            weatherDataAvailable = false;
            userDataAvailable = false;

            inferData.weatherData = weatherData;
            inferData.userData = userData;
            String json = new Gson().toJson(inferData);

            String planDataJSON = ChatGPT.sendPrompt(makePrompt(json));
            if (planDataJSON == null){
                infer();
            }
            PlanData planData = new Gson().fromJson(planDataJSON, PlanData.class);

            context.planData = planData;
            context.planDataAvailable = true;
        }).start();
    }

    private void waitForData() {
        synchronized (lock) {
            while (!weatherDataAvailable || !userDataAvailable) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private String makePrompt(String json) {
        return "Please generate an output JSON file with the following input: " + json
                + "Fill out the following output with complete recommended outfit plans based on the weather conditions, favorite colors, schedules, and available clothes. User preference adds some weight to the plan but is not required. You do not have to use up every possible cloth for each plan: "
                + "{\"plan1\":[],\"plan2\":[],\"plan3\":[]}"
                + "DO NOT HAVE EXTRA TEXT ASIDE FROM THE JSON OUTPUT, INCLUDING NEW LINES";
    }
}