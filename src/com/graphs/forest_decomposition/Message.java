package com.graphs.forest_decomposition;

public class Message {

    private String content;
    private long senderId;

    public Message(String content, long senderId) {
        this.content = content;
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public long getSenderId() {
        return senderId;
    }
}
