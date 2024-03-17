package dev.stylesync.stylesync.data;

public class WeatherData implements Data {
    public Temperature temperature = new Temperature();
    public double humidity;
    public double wind_speed;
    public String weather;

    public static class Temperature {
        public double temp;
        public double temp_feels_like;
        public double temp_min;
        public double temp_max;
    }
}
