package com.manrique.exameniiparcialsergiorios;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("aplication/json; charset=UTF-8");

}