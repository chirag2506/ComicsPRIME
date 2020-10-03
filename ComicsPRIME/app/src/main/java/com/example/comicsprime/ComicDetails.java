package com.example.comicsprime;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comicsprime.Model.Comic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ComicDetails extends AppCompatActivity {

    private final String TAG = "ComicDetails";

    androidx.appcompat.widget.Toolbar toolbarComicDetails;
    TextView details;

    Comic comic = new Comic();


    //FIREBASE

    FirebaseDatabase database;
    DatabaseReference comicDetailsReference;

    //GET DATA
    String username;
    String title_name;
    String volume_name;
    String issue_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comic_details_page);

        details = (TextView) findViewById(R.id.details);
        //SCROLL
        details.setMovementMethod(new ScrollingMovementMethod());

        //GET DATA
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        title_name = bundle.getString("title_name");
        volume_name = bundle.getString("volume_name");
        issue_name = bundle.getString("issue_name");

        //FIREBASE

        database = FirebaseDatabase.getInstance();
        comicDetailsReference = database.getReference().child("Comics").child(username).child(title_name).child(volume_name).child(issue_name);


        //TOOLBAR
        toolbarComicDetails = findViewById(R.id.toolbarComicsDetails);
        toolbarComicDetails.setTitle(title_name + " Vol " +  volume_name + " " + issue_name); //SETTING OTHER TITLE
        setSupportActionBar(toolbarComicDetails);

        //EDITS

        comicDetailsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comic = snapshot.getValue(Comic.class);
                if(comic.isPartOfEvent()){
                    details.setText(comic.getComicName() + " is a part of " + comic.getEventName() + " event. \n");
                }else{
                    details.setText("");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
