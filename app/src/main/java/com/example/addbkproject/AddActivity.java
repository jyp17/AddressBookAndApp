package com.example.addbkproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddActivity extends AppCompatActivity {
    TextView titleLabel;
    TextView firstLabel;
    TextView lastLabel;
    TextView addressLabel;
    TextView phoneLabel;
    EditText addFirst;
    EditText addLast;
    EditText addAddress;
    EditText addPhone;
    Button addContact;
    Button returnButton;
    private final String ADD_URL = "http://addressbook-env.eba-kzew8bem.us-east-1.elasticbeanstalk.com/address/addservice";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        titleLabel = (TextView) findViewById(R.id.addTitleLabel);
        firstLabel = (TextView) findViewById(R.id.addFirstLabel);
        lastLabel = (TextView) findViewById(R.id.addLastLabel);
        addressLabel = (TextView) findViewById(R.id.addAddressLabel);
        phoneLabel = (TextView) findViewById(R.id.addPhoneLabel);

        addFirst = (EditText) findViewById(R.id.firstName);
        addLast = (EditText) findViewById(R.id.lastName);
        addAddress = (EditText) findViewById(R.id.address);
        addPhone = (EditText) findViewById(R.id.phone);

        addContact = (Button) findViewById(R.id.saveButton);
        returnButton = (Button) findViewById(R.id.returnButton);

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onAddContactClick(v);
                } catch (IOException e) {
                    Log.e("AddBkException", "Unable to create contact.");
                    e.printStackTrace();
                }
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(back);
            }
        });
    }

    private void onAddContactClick(View v) throws IOException {
        if(isFormEmpty()) {
            Toast.makeText(getApplicationContext(), "Cannot create contact. One or more entries are blank.", Toast.LENGTH_LONG).show();
            return;
        }

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Contact c = new Contact(addFirst.getText().toString(), addLast.getText().toString(),
                addAddress.getText().toString(), addPhone.getText().toString());

        if (!c.isPhoneValid(c.getPhone())) {
            Toast.makeText(getApplicationContext(), "Invalid phone number entered. Setting contact's phone to 'N/A'.", Toast.LENGTH_LONG).show();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("first_name", c.getFirstName());
        params.put("last_name", c.getLastName());
        params.put("address", c.getAddress());
        params.put("phone", c.getPhone());

        JSONObject parameter = new JSONObject(params);

        executeTask(ADD_URL, parameter, JSON);

        addFirst.setText("");
        addLast.setText("");
        addAddress.setText("");
        addPhone.setText("");

        Toast.makeText(getApplicationContext(), "Contact successfully created.", Toast.LENGTH_LONG).show();
    }

    private void executeTask (String url, JSONObject parameter, MediaType m) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                RequestBody body = RequestBody.create(parameter.toString(), m);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code: " + response);
                } catch (IOException e) {
                    Log.e("AddBkException", "Failed to post query to database.");
                    e.printStackTrace();
                }
            }
        });

        executor.shutdown();
    }

    private boolean isFormEmpty() {
        if((addFirst.getText().toString()).trim().length() > 0 &&
                (addLast.getText().toString()).trim().length() > 0 &&
                (addAddress.getText().toString()).trim().length() > 0 &&
                (addPhone.getText().toString()).trim().length() > 0) {
            return false;
        }
        return true;
    }

}

