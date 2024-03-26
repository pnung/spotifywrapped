package com.example.spotifywrapped;

public class ReauthExceptionHandlerSingleton {
    private static ReauthExceptionHandlerSingleton singletonObject = null;

    private ReauthExceptionHandlerSingleton() {

    }

    public static ReauthExceptionHandlerSingleton getInstance() {
        if (singletonObject == null) {
            singletonObject = new ReauthExceptionHandlerSingleton();
        }
        return singletonObject;
    }

    public void handleReauthExceptions(String TAG, Exception e) {

    }
}
