package com.example.vmac.WatBot;

/**
 * Created by Chan on 2/11/2017.
 */

public interface MessageListener {
    void newMessageFromUser(String msg);
    void newMessageFromBot(String msg);
}
