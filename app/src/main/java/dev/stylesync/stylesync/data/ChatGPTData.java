package dev.stylesync.stylesync.data;

public class ChatGPTData implements Data {
    public Choice[] choices;

    public static class Choice {
        private Message message;
    }

    public static class Message {
        private String content;
    }

    public String getContent() {
        return choices[0].message.content;
    }
}
