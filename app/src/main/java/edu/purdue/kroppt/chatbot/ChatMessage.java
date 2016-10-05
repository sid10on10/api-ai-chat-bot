package edu.purdue.kroppt.chatbot;

/**
 * Created by kroppt on 10/5/2016.
 */

public class ChatMessage {
    public boolean left;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }
}