package edu.purdue.kroppt.chatbot;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import ai.api.AIConfiguration;
import ai.api.AIListener;
import ai.api.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;

public class MainActivity extends AppCompatActivity implements AIListener {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    FloatingActionButton fab;
    private boolean side = true; //true if you want message on right side

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = (Button) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.msgview);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        chatText = (EditText) findViewById(R.id.msg);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);

        listView.setAdapter(chatArrayAdapter);

        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });


        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        final AIConfiguration config = new AIConfiguration("937174fa0798485bbe75e8ecc391082f",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        AIService aiService = AIService.getService(this, config);
        aiService.setListener(this);
    }

    private boolean sendChatMessage() {
        if (chatText.getText().length() == 0)
            return false;
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        chatText.setText("");
        return true;
    }

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
