package com.claucookie.trendinground;

import android.widget.TextView;
import android.app.Activity;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity
{

    @ViewById
    TextView hello;

    @AfterViews
    void initViews() {
        // TODO: start coding!
    }

}
