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
        messages.add(new Message("1",msg));

        for (MessageListener ml:mls){
            ml.newMessageFromUser(msg);
        }
    }

    public void messageFromBot(String msg){

        messages.add(new Message("0",msg));

        for (MessageListener ml:mls){
            ml.newMessageFromBot(msg);
        }
    }
    public void addMessageListener(MessageListener ml){
        mls.add(ml);
    }


}
