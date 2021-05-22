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


public class ChangeActivity extends AppCompatActivity {
    TextView titleLabel;
    TextView firstLabel;
    TextView lastLabel;
    TextView addressLabel;
    TextView phoneLabel;
    EditText newFirst;
    EditText newLast;
    EditText newAddress;
    EditText newPhone;
    Button editContact;
    Button deleteContact;
    Button returnButton;
    private int id;
    private final String EDIT_URL = "http://addressbook-env.eba-kzew8bem.us-east-1.elasticbeanstalk.com/address/editservice";
    private final String DELETE_URL = "http://addressbook-env.eba-kzew8bem.us-east-1.elasticbeanstalk.com/address/deleteservice";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        Bundle extras = getIntent().getExtras();

        titleLabel = (TextView) findViewById(R.id.changeTitleLabel);
        firstLabel = (TextView) findViewById(R.id.changeFirstLabel);
        lastLabel = (TextView) findViewById(R.id.changeLastLabel);
        addressLabel = (TextView) findViewById(R.id.changeAddressLabel);
        phoneLabel = (TextView) findViewById(R.id.changePhoneLabel);

        newFirst = (EditText) findViewById(R.id.firstName);
        newLast = (EditText) findViewById(R.id.lastName);
        newAddress = (EditText) findViewById(R.id.address);
        newPhone = (EditText) findViewById(R.id.phone);

        editContact = (Button) findViewById(R.id.saveButton);
        deleteContact = (Button) findViewById(R.id.deleteContact);
        returnButton = (Button) findViewById(R.id.returnButton);

        newFirst.setText(extras.getString("firstName"));
        newLast.setText(extras.getString("lastName"));
        newAddress.setText(extras.getString("address"));
        newPhone.setText(extras.getString("phone"));
        id = extras.getInt("id");

        editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onEditContactClick(v);
                } catch (Exception e) {
                    Log.e("AddBkException", "Unable to edit contact.");
                    e.printStackTrace();
                }
            }
        });

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onDeleteContactClick(v);
                } catch (Exception e) {
                    Log.e("AddBkException", "Unable to delete contact.");
                    e.printStackTrace();
                }
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(getApplicationContext(), ListAllActivity.class);
                startActivity(back);
            }
        });
    }

   private void onEditContactClick(View v) throws IOException {
       if(isFormEmpty()) {
           Toast.makeText(getApplicationContext(), "Cannot edit contact. One or more entries are blank.", Toast.LENGTH_LONG).show();
           return;
       }

       Contact c = new Contact(newFirst.getText().toString(), newLast.getText().toString(),
               newAddress.getText().toString(), newPhone.getText().toString());

       if (!c.isPhoneValid(c.getPhone())) {
           Toast.makeText(getApplicationContext(), "Invalid phone number entered. Setting contact's phone to 'N/A'.", Toast.LENGTH_LONG).show();
       }

       Map<String, String> params = new HashMap<String, String>();
       params.put("first_name", c.getFirstName());
       params.put("last_name", c.getLastName());
       params.put("address", c.getAddress());
       params.put("phone", c.getPhone());
       params.put("id", Integer.toString(id));

       JSONObject parameter = new JSONObject(params);

       executeTask(EDIT_URL, parameter, JSON);

       Toast.makeText(getApplicationContext(), "Contact successfully edited.", Toast.LENGTH_LONG).show();
    }

    private void onDeleteContactClick(View v) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("id", Integer.toString(id));

        JSONObject parameter = new JSONObject(param);

        executeTask(DELETE_URL, parameter, JSON);

        Toast.makeText(getApplicationContext(), "Contact successfully deleted.", Toast.LENGTH_LONG).show();
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
        if((newFirst.getText().toString()).trim().length() > 0 &&
                (newLast.getText().toString()).trim().length() > 0 &&
                (newAddress.getText().toString()).trim().length() > 0 &&
                (newPhone.getText().toString()).trim().length() > 0) {
            return false;
        }
        return true;
    }
}
