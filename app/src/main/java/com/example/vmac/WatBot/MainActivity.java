package com.example.vmac.WatBot;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//HUNGSEO

public class MainActivity extends AppCompatActivity implements MessageListener{


    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    //private EditText inputMessage;
    //private ImageButton btnSend;
    //private Map<String,Object> context = new HashMap<>();
    //StreamPlayer streamPlayer;
    //private boolean initialRequest;
    private Button recordButton;
    MessageSession ms;

    private SpeechParser sp;

    private Logic logic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ms=new MessageSession();
        ms.addMessageListener(this);

        logic=new Logic(ms);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        mAdapter = new ChatAdapter(ms.getMessagesArrayList());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recordButton = (Button) findViewById(R.id.record_button);

        sp=new SpeechParser();


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ms.messageFromUser("asdf!!!");
                if (sp.isParsing()) {
                    sp.stopParsing();

                    int counter = 0;
                    String result = null;
                    while(true) {
                        try {
                            Thread.sleep(10); counter++;
                            if(result != sp.getScript()) {
                                result = sp.getScript();
                                counter = 0;
                            }
                            if(counter > 80) break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (result!=null) {
                        ms.messageFromUser(result);
                        logic.updateMsg(result);
                    }else{
                        ms.messageFromUser("NULL!!!!");
                    }
                    recordButton.setText("Record");

                }else{
                    if (checkInternetConnection()) {
                        recordButton.setText("Stop");
                        sp.startParsing();
                    }
                }

            }
        });

        logic.restart();
    };


    /**
     * Check Internet Connection
     * @return
     */
    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected){
            return true;
        }
       else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }


    @Override
    public void newMessageFromUser(String msg) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void newMessageFromBot(String msg) {
        mAdapter.notifyDataSetChanged();
    }
}

