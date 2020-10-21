package com.example.comicsprime;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicsprime.Adapters.titleRecyclerAdapter;
import com.example.comicsprime.Model.Comic;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePageLoggedInActivity extends AppCompatActivity implements titleRecyclerAdapter.OnComicListener {

    private static final String TAG = "HomePageLoggedInActivity";
    private static final int FACTS = 59;

    FloatingActionButton addComicButton;
    androidx.appcompat.widget.Toolbar toolbar;
    EditText searchEditText;
    ImageButton searchButton;
    RecyclerView comicListView;
    TextView factsTextView;

    //NAVIGATION BAR
    private DrawerLayout drawer;
    NavigationView navigationView;
    View headerView;
    TextView navWelcome;

    //FIREBASE

    FirebaseDatabase database;
    DatabaseReference comicsReference;
    DatabaseReference factsReference;

    //GET DATA
    String username;


    //RECYCLER VIEW
    ArrayList<String> list = new ArrayList<>();


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeloggedin_page);


        searchEditText = (EditText) findViewById(R.id.search_bar);
        searchButton = (ImageButton) findViewById(R.id.search_btn);

        factsTextView = (TextView) findViewById(R.id.facts);
        //SCROLL
        factsTextView.setMovementMethod(new ScrollingMovementMethod());

        comicListView = (RecyclerView) findViewById(R.id.comic_recyclerView);
        comicListView.setHasFixedSize(true);
        comicListView.setLayoutManager(new LinearLayoutManager(this));

        //GET DATA
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");


        //FIREBASE

        database = FirebaseDatabase.getInstance();
        comicsReference = database.getReference().child("Comics").child(username);
        factsReference = database.getReference().child("Facts");


        //TOOLBAR
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Comics"); //SETTING OTHER TITLE
        setSupportActionBar(toolbar);

        //NAVIGATION BAR
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState(); //NAVIGTION BAR DONE, NOW NAVIGATION SETTINGS
        navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_mycomics);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //NAVIGATION HEADER
        headerView = navigationView.getHeaderView(0);
        navWelcome = (TextView) headerView.findViewById(R.id.welcome_userText);
        navWelcome.setText(navWelcome.getText() + " " + username);





        //RECYCLER VIEW
        final titleRecyclerAdapter mTitleRecyclerAdapter = new titleRecyclerAdapter(list, this);
        comicListView.setAdapter(mTitleRecyclerAdapter);

        comicsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    list.add(dataSnapshot.getKey().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //EDITS

        addComicButton = (FloatingActionButton) findViewById(R.id.addComic);

        addComicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), AddComicsActivity.class);

                //PASS DATA
                s.putExtra("username", username);

                startActivity(s);
            }
        });


        //SEARCH BUTTON

        final titleRecyclerAdapter mTitleRecyclerAdapter2 = new titleRecyclerAdapter(list, this);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String searchText = searchEditText.getText().toString().trim().toUpperCase();
                list.clear();

                comicListView.setAdapter(mTitleRecyclerAdapter2);

                comicsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            String keys = dataSnapshot.getKey().toString().toUpperCase();
                            if(keys.contains(searchText)){
                                list.add(dataSnapshot.getKey().toString());
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

                        factsTextView.setText(facts_text);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    //WHAT DOES CLICKING COMIC DOES
    @Override
    public void onComicClick(int position) {
        Intent intent = new Intent(this, VolumesActivity.class);

        //PASS DATA
        intent.putExtra("username", username);
        intent.putExtra("title_name", list.get(position));

        startActivity(intent);


    }

    //SET WHAT BACK BUTTON DOES
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            moveTaskToBack(true); //QUIT APP
        }
    }

    //    @Override
//    protected void onStart() {
//        super.onStart();
//
//        //private void firebaseComicSearch(String searchText) {
//
//            //Query query = comicsReference.orderByChild("title").startAt(searchText).endAt(searchText + "\uf8ff");
//
//
//
//            FirebaseRecyclerOptions<Comic> options =
//                    new FirebaseRecyclerOptions.Builder<Comic>()
//                            .setQuery(comicsReference, Comic.class)
//                            .build();
//
//
//            FirebaseRecyclerAdapter<Comic, ComicsViewHolder> adapter = new FirebaseRecyclerAdapter<Comic, ComicsViewHolder>(options) {
//
//                @Override
//                public ComicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
//
//
//                    return new ComicsViewHolder(view);
//                }
//
//                @Override
//                protected void onBindViewHolder(ComicsViewHolder holder, int position, Comic model) {
//                    // Bind the image_details object to the BlogViewHolder
//                    // ...
//                    holder.setDetails(model.getTitle());
//
//
//                }
//            };
//
////                Comic.class,
////                R.layout.list_layout,
////                ComicsViewHolder.class,
////                comicsReference
//
//
//
//            comicListView.setAdapter(adapter);
//            adapter.startListening();
//        //}
//    }


}
