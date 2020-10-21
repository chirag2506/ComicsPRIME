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

import com.example.comicsprime.Adapters.issueRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class IssuesActivity extends AppCompatActivity implements issueRecyclerAdapter.OnComicIssueListener{

    private static final String TAG = "IssuesActivity";
    private static final int FACTS = 59;

    FloatingActionButton addComicButtonIssue;
    androidx.appcompat.widget.Toolbar toolbarIssue;
    EditText searchEditTextIssue;
    ImageButton searchButtonIssue;
    RecyclerView comicListViewIssue;
    TextView factsTextViewIssue;
    DatabaseReference factsReference;

    //FIREBASE

    FirebaseDatabase database;
    DatabaseReference comicsIssueReference;

    //GET DATA
    String username;
    String title_name;
    String volume_name;

    //RECYCLER VIEW
    ArrayList<String> listIssues = new ArrayList<>();

    //NAVIGATION BAR
    private DrawerLayout drawerIssue;
    NavigationView navigationViewIssue;
    View headerViewIssue;
    TextView navWelcomeIssue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issues_page);

        //GET DATA
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        title_name = bundle.getString("title_name");
        volume_name = bundle.getString("volume_name");

        searchEditTextIssue = (EditText) findViewById(R.id.search_barIssue);
        searchButtonIssue = (ImageButton) findViewById(R.id.search_btnIssue);

        factsTextViewIssue = (TextView) findViewById(R.id.factsIssue);
        //SCROLL
        factsTextViewIssue.setMovementMethod(new ScrollingMovementMethod());

        comicListViewIssue = (RecyclerView) findViewById(R.id.comic_recyclerViewIssue);
        comicListViewIssue.setHasFixedSize(true);
        comicListViewIssue.setLayoutManager(new LinearLayoutManager(this));

        //FIREBASE

        database = FirebaseDatabase.getInstance();
        comicsIssueReference = database.getReference().child("Comics").child(username).child(title_name).child(volume_name);
        factsReference = database.getReference().child("Facts");


        //TOOLBAR
        toolbarIssue = findViewById(R.id.toolbarIssue);
        toolbarIssue.setTitle(title_name + " Volume " +  volume_name); //SETTING OTHER TITLE
        setSupportActionBar(toolbarIssue);

        //NAVIGATION BAR
        drawerIssue = findViewById(R.id.drawer_layoutIssue);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerIssue, toolbarIssue, R.string.navigation_drawer_open, R.string.navigation_drawer_close); //1
        drawerIssue.addDrawerListener(toggle); //2
        toggle.syncState(); //NAVIGTION BAR DONE, NOW NAVIGATION SETTINGS (//1 and //2 for NAVIGATION TOGGLE BUTTON ON TOOLBAR)
        navigationViewIssue = findViewById(R.id.nav_viewIssue);
        navigationViewIssue.setCheckedItem(R.id.nav_mycomics);
        navigationViewIssue.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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

                drawerIssue.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //NAVIGATION HEADER
        headerViewIssue = navigationViewIssue.getHeaderView(0);
        navWelcomeIssue = (TextView) headerViewIssue.findViewById(R.id.welcome_userText);
        navWelcomeIssue.setText(navWelcomeIssue.getText() + " " + username);


        //RECYCLER VIEW
        issueRecyclerAdapter mIssueRecyclerAdapter = new issueRecyclerAdapter(listIssues, this);
        comicListViewIssue.setAdapter(mIssueRecyclerAdapter);

        comicsIssueReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIssues.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    listIssues.add(dataSnapshot.getKey().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //EDITS

        addComicButtonIssue = (FloatingActionButton) findViewById(R.id.addComicIssue);

        addComicButtonIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), AddComicsActivity.class);

                //PASS DATA
                s.putExtra("username", username);
                s.putExtra("title_name", title_name);
                s.putExtra("volume_name", volume_name);

                startActivity(s);
            }
        });


        //SEARCH BUTTON

        final issueRecyclerAdapter mIssueRecyclerAdapter2 = new issueRecyclerAdapter(listIssues, this);

        searchButtonIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String searchText = searchEditTextIssue.getText().toString().trim().toUpperCase();
                listIssues.clear();

                comicListViewIssue.setAdapter(mIssueRecyclerAdapter2);

                comicsIssueReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listIssues.clear();

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            String keys = dataSnapshot.getKey().toString().toUpperCase();
                            if(keys.contains(searchText)){
                                listIssues.add(dataSnapshot.getKey().toString());
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

                        factsTextViewIssue.setText(facts_text);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onComicIssueClick(int position) {

        Intent intent = new Intent(this, ComicDetails.class);

        //PASS DATA
        intent.putExtra("username", username);
        intent.putExtra("title_name", title_name);
        intent.putExtra("volume_name", volume_name);
        intent.putExtra("issue_name", listIssues.get(position));

        startActivity(intent);

    }

    //SET WHAT BACK BUTTON DOES
    @Override
    public void onBackPressed() {
        if(drawerIssue.isDrawerOpen(GravityCompat.START)){
            drawerIssue.closeDrawer(GravityCompat.START);
        }else {
            Intent intent = new Intent(this, VolumesActivity.class);

            //PASS DATA
            intent.putExtra("username", username);
            intent.putExtra("title_name", title_name);


            startActivity(intent);
        }
    }
}
