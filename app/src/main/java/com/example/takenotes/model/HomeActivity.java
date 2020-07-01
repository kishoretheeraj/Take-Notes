package com.example.takenotes.model;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takenotes.R;
import com.example.takenotes.ViewActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {
    EditText inputsearch;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<image> options;
    FirebaseRecyclerAdapter<image,MyViewHolder> adapter;
    DatabaseReference ref;
    FirebaseUser user;
    FirebaseAuth fauth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar t=findViewById(R.id.toolbar);
        setSupportActionBar(t);
        fauth=FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user=fauth.getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference().child(user.getUid());
        inputsearch=findViewById(R.id.search);
        recyclerView=findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(view.getContext(), ImageActivity.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);


            }
        });
        LoadData("");
        inputsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString()!=null)
                {
                    LoadData(s.toString());
                }
                else
                {
                    LoadData("");
                }

            }
        });

    }

    private void LoadData(String data) {
        Query query=ref.orderByChild("ImageName").startAt(data).endAt(data+"\uf8ff");

        options=new FirebaseRecyclerOptions.Builder<image>().setQuery(query,image.class).build();
        adapter=new FirebaseRecyclerAdapter<image, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull image model) {
                holder.textView.setText(model.getImageName());
                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(HomeActivity.this, ViewActivity.class);
                        intent.putExtra("imagekey",getRef(position).getKey());
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view,parent,false);
                return new MyViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
