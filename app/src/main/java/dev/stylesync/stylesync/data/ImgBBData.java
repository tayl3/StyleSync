package dev.stylesync.stylesync.data;

public class ImgBBData implements Data {
    private Data data;
    private boolean success;

    public Data getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public static class Data {
        private String url;

        public String getUrl() {
            return url;
        }
    }
}
