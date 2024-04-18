package dev.stylesync.stylesync.data;

public class ImgBBData implements Data {
    private Data image;

    public Data getData() {
        return image;
    }

    public static class Data {
        private String url;

        public String getUrl() {
            return url;
        }
    }
}
