package com.jaay.bridge;
import com.unity3d.player.UnityPlayer;

public class Bridge {
    public static void call(String object, String method, String message) {
        UnityPlayer.UnitySendMessage(object, method, message);
    }
}

