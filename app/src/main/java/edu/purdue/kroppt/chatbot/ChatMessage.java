package edu.purdue.kroppt.chatbot;

/**
 * Created by kroppt on 10/5/2016.
 */

public class ChatMessage {
    public boolean rightSide;
    public String message;

    public ChatMessage(boolean rightSide, String message) {
        super();
        this.rightSide = rightSide;
        this.message = message;
    }
}