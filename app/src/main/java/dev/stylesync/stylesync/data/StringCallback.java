package dev.stylesync.stylesync.data;

public interface StringCallback {
    void onStringReceived(String string);

    void onError(String message);
}
