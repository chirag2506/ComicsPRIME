package com.example.comicsprime;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicsprime.Adapters.volumeRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VolumesActivity extends AppCompatActivity implements volumeRecyclerAdapter.OnComicVolumeListener {

    private static final String TAG = "VolumesActivity";
    private static final int FACTS = 59;

    FloatingActionButton addComicButtonVolume;
    androidx.appcompat.widget.Toolbar toolbarVolume;
    EditText searchEditTextVolume;
    ImageButton searchButtonVolume;
    RecyclerView comicListViewVolume;
    TextView factsTextViewVolume;

    //FIREBASE

    FirebaseDatabase database;
    DatabaseReference comicsVolumeReference;
    DatabaseReference factsReference;

    //GET DATA
    String username;
    String title_name;

    //RECYCLER VIEW
    ArrayList<String> listVolumes = new ArrayList<>();

    //NAVIGATION BAR
    private DrawerLayout drawerVolume;
    NavigationView navigationViewVolume;
    View headerViewVolume;
    TextView navWelcomeVolume;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volumes_page);

        //GET DATA
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        title_name = bundle.getString("title_name");


        searchEditTextVolume = (EditText) findViewById(R.id.search_barVolume);
        searchButtonVolume = (ImageButton) findViewById(R.id.search_btnVolume);

        factsTextViewVolume = (TextView) findViewById(R.id.factsVolume);
        //SCROLL
        factsTextViewVolume.setMovementMethod(new ScrollingMovementMethod());

        comicListViewVolume = (RecyclerView) findViewById(R.id.comic_recyclerViewVolume);
        comicListViewVolume.setHasFixedSize(true);
        comicListViewVolume.setLayoutManager(new LinearLayoutManager(this));

        //FIREBASE

        database = FirebaseDatabase.getInstance();
        comicsVolumeReference = database.getReference().child("Comics").child(username).child(title_name);
        factsReference = database.getReference().child("Facts");


        //TOOLBAR
        toolbarVolume = findViewById(R.id.toolbarVolume);
        toolbarVolume.setTitle(title_name); //SETTING OTHER TITLE
        setSupportActionBar(toolbarVolume);

        //NAVIGATION BAR
        drawerVolume = findViewById(R.id.drawer_layoutVolume);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerVolume, toolbarVolume, R.string.navigation_drawer_open, R.string.navigation_drawer_close); //1
        drawerVolume.addDrawerListener(toggle); //2
        toggle.syncState(); //NAVIGTION BAR DONE, NOW NAVIGATION SETTINGS (//1 and //2 for NAVIGATION TOGGLE BUTTON ON TOOLBAR)
        navigationViewVolume = findViewById(R.id.nav_viewVolume);
        navigationViewVolume.setCheckedItem(R.id.nav_mycomics);
        navigationViewVolume.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_mycomics:
                        Intent intent1 = new Intent(getApplicationContext(), HomePageLoggedInActivity.class);

                        //PASS DATA
                        intent1.putExtra("username", username);

                        startActivity(intent1);
                        break;
                    case R.id.nav_upcoming:
                        Intent intent2 = new Intent(getApplicationContext(), UpcomingComicsActivity.class);

                        //PASS DATA
                        intent2.putExtra("username", username);

                        startActivity(intent2);

                }

                drawerVolume.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //NAVIGATION HEADER
        headerViewVolume = navigationViewVolume.getHeaderView(0);
        navWelcomeVolume = (TextView) headerViewVolume.findViewById(R.id.welcome_userText);
        navWelcomeVolume.setText(navWelcomeVolume.getText() + " " + username);



        //RECYCLER VIEW
        volumeRecyclerAdapter mVolumeRecyclerAdapter = new volumeRecyclerAdapter(listVolumes, this);
        comicListViewVolume.setAdapter(mVolumeRecyclerAdapter);

        comicsVolumeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listVolumes.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    listVolumes.add(dataSnapshot.getKey().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //EDITS

        addComicButtonVolume = (FloatingActionButton) findViewById(R.id.addComicVolume);

        addComicButtonVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), AddComicsActivity.class);

                //PASS DATA
                s.putExtra("username", username);
                s.putExtra("title_name", title_name);

                startActivity(s);
            }
        });


        //SEARCH BUTTON

        final volumeRecyclerAdapter mVolumeRecyclerAdapter2 = new volumeRecyclerAdapter(listVolumes, this);


        searchButtonVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String searchText = searchEditTextVolume.getText().toString().trim().toUpperCase();
                listVolumes.clear();

                comicListViewVolume.setAdapter(mVolumeRecyclerAdapter2);

                comicsVolumeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listVolumes.clear();

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            String keys = dataSnapshot.getKey().toString().toUpperCase();
                            if(keys.contains(searchText)){
                                listVolumes.add(dataSnapshot.getKey().toString());
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        //FACTS

        final String posi = Integer.toString((int) Math.floor(Math.random() * FACTS));
        factsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.getKey().toString().equals(posi)){

                        String facts_text = "Did you know? \n" + dataSnapshot.getValue().toString();

                        factsTextViewVolume.setText(facts_text);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onComicVolumeClick(int position) {

        Intent intent = new Intent(this, IssuesActivity.class);

        //PASS DATA
        intent.putExtra("username", username);
        intent.putExtra("title_name", title_name);
        intent.putExtra("volume_name", listVolumes.get(position));

        startActivity(intent);

    }

    //SET WHAT BACK BUTTON DOES
    @Override
    public void onBackPressed() {
        if(drawerVolume.isDrawerOpen(GravityCompat.START)){
            drawerVolume.closeDrawer(GravityCompat.START);
        }else {
            Intent intent = new Intent(this, HomePageLoggedInActivity.class);

            //PASS DATA
            intent.putExtra("username", username);

            startActivity(intent);
        }
    }
}
