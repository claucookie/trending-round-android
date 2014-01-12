package com.claucookie.trendinground;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("App started!");
    }

}
