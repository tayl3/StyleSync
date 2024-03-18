package dev.stylesync.stylesync.data;

public interface DataCallback {
    void OnDataReceived(Data data);

    void OnError(String msg);
}
