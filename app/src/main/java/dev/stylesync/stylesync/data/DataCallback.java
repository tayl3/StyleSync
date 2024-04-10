package dev.stylesync.stylesync.data;

public interface DataCallback {
    void onDataReceived(Data data);

    void onError(String message);
}
