package edu.purdue.kroppt.chatbot;

import ai.api.AIListener;
import ai.api.model.AIError;
import ai.api.model.AIResponse;

/**
 * Created by kroppt on 10/6/2016.
 */

public class ChatAIListener implements AIListener {
    public void onResult(AIResponse result) { // here process response

    }
    public void onError(AIError error) { // here process error

    }
    public void onAudioLevel(float level) { // callback for sound level visualization

    }
    public void onListeningStarted() { // indicate start listening here

    }
    public void onListeningCanceled() { // indicate stop listening here

    }
    public void onListeningFinished() { // indicate stop listening here

    }
}
