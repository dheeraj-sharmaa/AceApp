package com.example.hp.markmep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

public class PresentStudents extends AppCompatActivity {
    ArrayList<String> list=new ArrayList<>();
    ListView lv;
    Firebase ref;
    String name,subselec,selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_students);
        lv=findViewById(R.id.listView);
        name=getIntent().getStringExtra("name");
        subselec=getIntent().getStringExtra("subselec");
        selectedDate=getIntent().getStringExtra("selectedDate");
        final ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(itemsAdapter);
        ref = new Firebase("https://attendance-app-3558c.firebaseio.com/" + name + "/" + subselec + "/" + selectedDate + "/present/");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String daaad = dataSnapshot.getValue().toString();
                list.add(daaad);
                itemsAdapter.notifyDataSetChanged();
                lv.setVisibility(View.VISIBLE);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
