package com.example.comicsprime;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comicsprime.Model.Comic;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddComicsActivity extends AppCompatActivity {

    private static final String TAG = "AddComicsActivity";

    String title_name = ""; //IF ACTIVITY OPENS FROM NOT HOME PAGE
    String volume_name = "";   //IF ACTIVITY OPENS FROM NOT HOME PAGE

    Button btnAdd;
    TextView editTitle, editVolume, editIssue, editEvent;
    RadioGroup radioGroup;
    RadioButton radioButton;


    //FIREBASE

    FirebaseDatabase database;
    DatabaseReference comics;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_comic_page);

        btnAdd = (Button) findViewById(R.id.addnewcomic);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        editTitle = (TextView) findViewById(R.id.edittextTitle);
        editVolume = (TextView) findViewById(R.id.edittextVolume);
        editIssue = (TextView) findViewById(R.id.edittextIssue);
        editEvent = (TextView) findViewById(R.id.editTextEventName);

        //TO ENABLE BUTTON AFTER TEXT IS WRITTEN

        editTitle.addTextChangedListener(loginTextWatch);
        editVolume.addTextChangedListener(loginTextWatch);
        editIssue.addTextChangedListener(loginTextWatch);;

        
        //GET DATA AND PASS TO INTENT
        Intent thisIntent = getIntent();
        Bundle bundle = thisIntent.getExtras();
        final String username_2 = bundle.getString("username");
        if (thisIntent.hasExtra("title_name")) {
            title_name = bundle.getString("title_name");
            editTitle.setText(title_name);
        }
        if (thisIntent.hasExtra("volume_name")) {
            volume_name = bundle.getString("volume_name");
            editVolume.setText(volume_name);
        }


        //FIREBASE

        database = FirebaseDatabase.getInstance();
        comics = database.getReference().child("Comics").child(username_2);

        //RadioButtons
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);


        //BUTTON

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(radioButton.getText().toString().equals("Yes")){


                    if(editEvent.getText().toString().trim().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Event name cannot be empty. Select 'No'  if this issue is not a part of an event", Toast.LENGTH_LONG).show();
                    }else {
                        final Comic newComic = new Comic(editTitle.getText().toString().trim(),
                                editVolume.getText().toString().trim(),
                                editIssue.getText().toString().trim(),
                                true,
                                editEvent.getText().toString().trim());

                        final String title_name = editTitle.getText().toString().trim();
                        final String volume_name = editVolume.getText().toString().trim();
                        final String issue_name = editIssue.getText().toString().trim();

                        //ADD

                        comics.child(title_name).child(volume_name).child(issue_name).setValue(newComic).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Added Successfully !", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }else{

                    final Comic newComic = new Comic(editTitle.getText().toString().trim(),
                            editVolume.getText().toString().trim(),
                            editIssue.getText().toString().trim(),
                            false);

                    final String title_name = editTitle.getText().toString().trim();
                    final String volume_name = editVolume.getText().toString().trim();
                    final String issue_name = editIssue.getText().toString().trim();

                    //ADD

                    comics.child(title_name).child(volume_name).child(issue_name).setValue(newComic).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Added Successfully !", Toast.LENGTH_LONG).show();
                        }
                    });

                }

                openHomePageActivity(username_2, title_name, volume_name);

            }
        });

    }

    //FOR ENABLING AND DISABLING EVENT NAME EDITTEXT
    public void checkButton(View v){

        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        if(radioButton.getText().toString().equals("Yes")){

            editEvent.setVisibility(View.VISIBLE);

        }else{

            editEvent.setVisibility(View.INVISIBLE);

        }
    }


    //FOR OPENINIG OTHER PAGE

    private void openHomePageActivity(String username_2, String title_name, String volume_name){




        if(!title_name.isEmpty()){
            if(!volume_name.isEmpty()){

                Intent s = new Intent(getApplicationContext(), IssuesActivity.class);

                //PASS DATA
                String username = username_2;
                s.putExtra("username", username);
                s.putExtra("title_name",title_name);
                s.putExtra("volume_name", volume_name);

                startActivity(s);

            }else{

                Intent s = new Intent(getApplicationContext(), VolumesActivity.class);

                //PASS DATA
                String username = username_2;
                s.putExtra("username", username);
                s.putExtra("title_name",title_name);

                startActivity(s);

            }
        }else{
            Intent s = new Intent(getApplicationContext(), HomePageLoggedInActivity.class);

            //PASS DATA
            String username = username_2;
            s.putExtra("username", username);

            startActivity(s);
        }


    }


    //TO ENABLE BUTTON AFTER TEXT IS WRITTEN

    private TextWatcher loginTextWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String titleInput = editTitle.getText().toString().trim();
            String volumeInput = editVolume.getText().toString().trim();
            String issueInput = editIssue.getText().toString().trim();

            btnAdd.setEnabled(!titleInput.isEmpty()
                    && !volumeInput.isEmpty()
                    && !issueInput.isEmpty());


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
