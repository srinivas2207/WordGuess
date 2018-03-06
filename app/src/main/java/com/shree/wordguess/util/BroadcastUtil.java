package com.shree.wordguess.util;

import android.content.Intent;

import com.shree.wordguess.WordGuessApplication;

/**
 * Created by SrinivasDonapati on 12/19/2017.
 */

public class BroadcastUtil  {

    private static BroadcastUtil broadcastUtil;

    public static BroadcastUtil getInstance() {
        if (broadcastUtil == null) {
            broadcastUtil = new BroadcastUtil();
        }
        return broadcastUtil;
    }

    public void broadcast(int type, String data) {
        Intent intent = new Intent(ApplicationConstants.UI_NOTIFIER);
        intent.putExtra(ApplicationConstants.NOTIFICATION_TYPE, type);
        intent.putExtra(ApplicationConstants.NOTIFICATION_DATA, data);
        WordGuessApplication.getInstance().sendBroadcast(intent);
    }
}
