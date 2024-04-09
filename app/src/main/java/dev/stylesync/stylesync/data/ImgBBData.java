package dev.stylesync.stylesync.data;

public class ImgBBData implements Data {
    private Data data;
    private boolean success;
    private int status;

    public Data getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public class Data {
        private String url;

        public String getUrl() {
            return url;
        }
    }
}
