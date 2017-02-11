package com.example.vmac.WatBot;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;

/**
 * Created by Dongwook on 2017-02-11.
 */
public class SpeechParser {

    private SpeechToText speechService;

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

}
