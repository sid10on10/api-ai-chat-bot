package edu.purdue.kroppt.chatbot;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import ai.api.AIConfiguration;
import ai.api.AIListener;
import ai.api.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener {
    private static final String TAG = "ChatActivity";
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 12;
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 35;

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private FloatingActionButton sendButton;
    private FloatingActionButton listenButton;
    private AIService aiService;
    private Animation pop_in_anim;
    private Animation pop_out_anim;

    private boolean rightSide = true; //true if you want message on right rightSide

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = (FloatingActionButton) findViewById(R.id.btn_send);
        listView = (ListView) findViewById(R.id.msgview);
        listenButton = (FloatingActionButton) findViewById(R.id.btn_mic);
        chatText = (EditText) findViewById(R.id.msg);

        pop_in_anim = AnimationUtils.loadAnimation(this, R.anim.pop_in);
        pop_out_anim = AnimationUtils.loadAnimation(this, R.anim.pop_out);
        sendButton.setAnimation(pop_out_anim);
        sendButton.setAnimation(pop_in_anim);
        listenButton.setAnimation(pop_in_anim);
        listenButton.setAnimation(pop_out_anim);
        sendButton.clearAnimation();
        listenButton.clearAnimation();

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);

        listView.setAdapter(chatArrayAdapter);

        // Get INTERNET permission

        while (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

                showExplanation("Permission Needed", "Rationale", Manifest.permission.INTERNET,
                        MY_PERMISSIONS_REQUEST_INTERNET);

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.

                requestPermission(Manifest.permission.INTERNET,
                        MY_PERMISSIONS_REQUEST_INTERNET);

                // MY_PERMISSIONS_REQUEST_INTERNET is an
                // app-defined int constant. The callback method gets the
                // result of the request (onRequestPermissionsResult).
            }
        }

        /**
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage(chatText.getText().toString());
                }
                return false;
            }
        });
        **/

        chatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 && listenButton.getVisibility() == View.GONE) {
                    sendButton.clearAnimation();
                    sendButton.startAnimation(pop_out_anim);
                    sendButton.setVisibility(View.GONE);
                    sendButton.setEnabled(false);
                    listenButton.clearAnimation();
                    listenButton.setVisibility(View.VISIBLE);
                    listenButton.startAnimation(pop_in_anim);
                    listenButton.setEnabled(true);

                } else if (s.length() > 0 && sendButton.getVisibility() == View.GONE) {
                    listenButton.clearAnimation();
                    listenButton.startAnimation(pop_out_anim);
                    listenButton.setVisibility(View.GONE);
                    listenButton.setEnabled(false);
                    sendButton.clearAnimation();
                    sendButton.setVisibility(View.VISIBLE);
                    sendButton.startAnimation(pop_in_anim);
                    sendButton.setEnabled(true);
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage(chatText.getText().toString());
            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getParent(),
                            Manifest.permission.RECORD_AUDIO)) {

                        showExplanation("Permission Needed", "Rationale", Manifest.permission.RECORD_AUDIO,
                                MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed, we can request the permission.

                        requestPermission(Manifest.permission.RECORD_AUDIO,
                                MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                        // MY_PERMISSIONS_REQUEST_RECORD_AUDIO is an
                        // app-defined int constant. The callback method gets the
                        // result of the request (onRequestPermissionsResult).
                    }

                } else {
                    aiService.startListening();
                }
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

        final AIConfiguration config = new AIConfiguration("937174fa0798485bbe75e8ecc391082f",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);
    }

    private boolean sendResponse(String text) {
        if (text.length() == 0)
            return false;
        chatArrayAdapter.add(new ChatMessage(!rightSide, text));
        return true;
    }

    private boolean sendChatMessage(String text) {
        if (text.length() == 0)
            return false;
        chatArrayAdapter.add(new ChatMessage(rightSide, text));
        chatText.setText("");
        return true;
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                    // whether Permissions were granted
                    aiService.startListening();
                return;
            } case MY_PERMISSIONS_REQUEST_INTERNET: {
            } default: return;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onResult(final AIResponse response) { // here process response
        Result result = response.getResult();
        Log.i(TAG, "Action: " + result.getAction());
        // process response object
        sendChatMessage(response.getResult().getResolvedQuery());

        sendResponse(result.getFulfillment().getSpeech());


        /**

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        // Show results in TextView.
        sendChatMessage("Query:" + result.getResolvedQuery() +
                        "\nAction: " + result.getAction() +
                        "\nParameters: " + parameterString);

        **/
    }

    @Override
    public void onError(AIError error) { // here process error
        sendResponse(error.toString());
    }

    @Override
    public void onAudioLevel(float level) { // callback for sound level visualization

    }

    @Override
    public void onListeningStarted() { // indicate start listening here

    }

    @Override
    public void onListeningCanceled() { // indicate stop listening here

    }

    @Override
    public void onListeningFinished() { // indicate stop listening here

    }
}
