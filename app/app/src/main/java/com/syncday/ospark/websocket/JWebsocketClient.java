package com.syncday.ospark.websocket;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class JWebsocketClient extends WebSocketClient {

    public JWebsocketClient(URI uri) {
        super(uri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.e("DEBUG", "onopen()");
    }

    @Override
    public void onMessage(String s) {
        Log.e("DEBUG", "onMessage()");
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        Log.e("DEBUG", "onclose()");
    }

    @Override
    public void onError(Exception e) {
        Log.e("DEBUG", e.toString());
    }

}
