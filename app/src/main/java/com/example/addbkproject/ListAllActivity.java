package com.example.addbkproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListAllActivity extends AppCompatActivity {
    TextView listTitle;
    ListView contactList;
    Button returnMain;
    ArrayList<Contact> contactArrayList;
    ContactListAdapter contactAdapter;
    private final String url = "http://addressbook-env.eba-kzew8bem.us-east-1.elasticbeanstalk.com/address/listservice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all);

        contactArrayList = new ArrayList<Contact>();

        listTitle = (TextView) findViewById(R.id.listAllTitle);
        contactList = (ListView) findViewById(R.id.contactList);
        returnMain = (Button) findViewById(R.id.returnMainButton);

        getList();

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact c = (Contact) contactAdapter.getItem(position);

                Intent change = new Intent(getApplicationContext(), ChangeActivity.class);
                change.putExtra("firstName", c.getFirstName());
                change.putExtra("lastName", c.getLastName());
                change.putExtra("address", c.getAddress());
                change.putExtra("phone", c.getPhone());
                change.putExtra("id", c.getId());

                startActivity(change);
            }
        });

        returnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(back);
            }
        });
    }

    private void getList() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AddBkException", "Unable to retrieve contacts from database.");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    String listResponse = response.body().string();

                    ListAllActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject result = new JSONObject(listResponse);

                                for(int i = 0; i < result.length(); i++) {
                                    JSONObject data = result.getJSONObject("contact" + (i+1));
                                    String firstName = data.getString("first_name");
                                    String lastName = data.getString("last_name");
                                    String address = data.getString("address");
                                    String phone = data.getString("phone");
                                    int id = data.getInt("id");

                                    contactArrayList.add(new Contact(firstName, lastName, address, phone, id));
                                }

                                contactAdapter = new ContactListAdapter(contactArrayList, ListAllActivity.this);
                                contactList.setAdapter(contactAdapter);
                            } catch(JSONException e) {
                                Log.e("AddBkException", "Problem with JSONObject.");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}
