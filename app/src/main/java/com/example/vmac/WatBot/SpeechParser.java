package com.example.vmac.WatBot;

import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;

/**
 * Created by Dongwook on 2017-02-11.
 */
public class SpeechParser {

    private SpeechToText speechService;
    private String result = null;

    private MicrophoneInputStream capture;
    private boolean parsing = false;

    public SpeechParser() {

        speechService = initSpeechToTextService();

    }

    private SpeechToText initSpeechToTextService() {
        SpeechToText service = new SpeechToText();
        String username = "9de80df8-6b01-4b5c-838a-c96bf0be384b";
        String password = "3sp5uaggxvXt";
        service.setUsernameAndPassword(username, password);
        service.setEndPoint("https://stream.watsonplatform.net/speech-to-text/api");
        return service;
    }

    public void startParsing() {
        result = null;
        parsing = true;
        Log2.log(2,this,"Starting MicrophoneInputStream");
        capture = new MicrophoneInputStream(true);
        Log2.log(2,this,"MicrophoneInputStream initialized.");
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    Log2.log(2,this,"Starting recognizeUsingWebSocket");
                    speechService.recognizeUsingWebSocket(capture, getRecognizeOptions(), new MicrophoneRecognizeDelegate());
                } catch (Exception e) {
                    Log2.log(e);
                }
            }
        }).start();
    }

    public boolean isParsing() {
        return parsing;
    }

    public String stopParsing() {
        parsing = false;
        try {
            capture.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private RecognizeOptions getRecognizeOptions() {
        return new RecognizeOptions.Builder()
                .continuous(true)
                .contentType(ContentType.OPUS.toString())
                .model("en-US_BroadbandModel")
                .interimResults(true)
                .inactivityTimeout(2000)
                .build();
    }

    private class MicrophoneRecognizeDelegate implements RecognizeCallback {

        @Override
        public void onTranscription(SpeechResults speechResults) {
            System.out.println(speechResults);
            result = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
        }

        @Override public void onConnected() {

        }

        @Override public void onError(Exception e) {

        }

        @Override public void onDisconnected() {

        }

    }



}




