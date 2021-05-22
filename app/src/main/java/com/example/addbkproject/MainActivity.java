package com.example.addbkproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button add;
    Button listAll;
    TextView title;
    TextView instruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.title);
        instruction = (TextView) findViewById(R.id.instruction);
        add = (Button) findViewById(R.id.addButton);
        listAll = (Button) findViewById(R.id.listButton);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClick(v);
            }
        });

        listAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListAllClick(v);
            }
        });
    }

    private void onListAllClick(View v) {
        Intent list = new Intent(getApplicationContext(), ListAllActivity.class);
        startActivity(list);
    }

    private void onAddClick(View v) {
        Intent add = new Intent(getApplicationContext(), AddActivity.class);
        startActivity(add);
    }
}