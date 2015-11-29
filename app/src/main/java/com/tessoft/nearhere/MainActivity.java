package com.tessoft.nearhere;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kakao.auth.ISessionCallback;

public class MainActivity extends AppCompatActivity {

    ISessionCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
