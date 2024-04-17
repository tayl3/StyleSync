package dev.stylesync.stylesync.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.DataCallback;
import dev.stylesync.stylesync.data.WeatherData;
import dev.stylesync.stylesync.utility.Secrets;

public class WeatherService implements Service {
    private final MainActivity context;
    private final LocationManager locationManager;

    public WeatherService(MainActivity context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private double[] getCoordinate() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                return new double[]{location.getLatitude(), location.getLongitude()};
            }
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                return new double[]{location.getLatitude(), location.getLongitude()};
            }
        }
        return null;
    }

    public void getWeatherData(final DataCallback callback) {
        double[] coordinate = getCoordinate();
        if (coordinate == null) {
            callback.onError("Location data unavailable");
            return;
        }
        double latitude = coordinate[0];
        double longitude = coordinate[1];

        String url = Secrets.WEATHER_API_URL + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + Secrets.WEATHER_API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    WeatherData weatherData = parseWeatherData(response.toString());
                    if (weatherData != null) {
                        callback.onDataReceived(weatherData);
                    } else {
                        callback.onError("Failed to parse weather data");
                    }
                }, error -> callback.onError("Network connection failed"));

        context.volleyRequestQueue.add(jsonObjectRequest);
    }

    private WeatherData parseWeatherData(String JSONData) {
        try {
            JSONObject obj = new JSONObject(JSONData);
            WeatherData data = new WeatherData();
            data.getTemperature().setTemp(obj.getJSONObject("main").getDouble("temp"));
            data.getTemperature().setTempFeelsLike(obj.getJSONObject("main").getDouble("feels_like"));
            data.getTemperature().setTempMin(obj.getJSONObject("main").getDouble("temp_min"));
            data.getTemperature().setTempMax(obj.getJSONObject("main").getDouble("temp_max"));
            data.setHumidity(obj.getJSONObject("main").getInt("humidity"));
            data.setWindSpeed(obj.getJSONObject("wind").getDouble("speed"));
            data.setWeather(obj.getJSONArray("weather").getJSONObject(0).getString("main"));
            return data;
        } catch (Exception e) {
            System.err.println("Invalid weather JSON data: " + e.getMessage());
            return null;
        }
    }
}
