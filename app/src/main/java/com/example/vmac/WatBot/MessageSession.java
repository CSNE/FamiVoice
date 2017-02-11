package com.example.vmac.WatBot;

import java.util.ArrayList;

/**
 * Created by danny on 2017-02-11.
 */

public class MessageSession {


    ArrayList<MessageListener> mls=new ArrayList<>();

    ArrayList<Message> messages=new ArrayList<>();

    public ArrayList<Message> getMessagesArrayList(){
        return messages;
    }

    public void messageFromUser(String msg){
        messages.add(new Message());
    }

    public void messageFromBot(String msg){

    }
    public void addMessageListener(MessageListener ml){
        mls.add(ml);
    }
}
