package com.example.takenotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.takenotes.model.HomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewActivity extends AppCompatActivity {
    private ImageView imageView;
    TextView textView;
    Button btndelete;
    DatabaseReference ref,dataref;
    StorageReference storageReference;
    FirebaseUser user;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        fauth=FirebaseAuth.getInstance();
        user=fauth.getCurrentUser();
        imageView=findViewById(R.id.viewimage);
        textView=findViewById(R.id.viewtext);
        btndelete=findViewById(R.id.deletebtn);
        ref= FirebaseDatabase.getInstance().getReference().child(user.getUid());
        String imagekey=getIntent().getStringExtra("imagekey");
        dataref=FirebaseDatabase.getInstance().getReference().child("image").child(imagekey);
        storageReference= FirebaseStorage.getInstance().getReference().child(user.getUid()).child(imagekey+".jpg");


ref.child(imagekey).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists())
        {
            String imagename=dataSnapshot.child("ImageName").getValue().toString();
            String imageurl=dataSnapshot.child("ImageUrl").getValue().toString();

            Picasso.get().load(imageurl).into(imageView);
            textView.setText(imagename);
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
btndelete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        dataref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ViewActivity.this, "Image  Deleted", Toast.LENGTH_SHORT).show();
             storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                     startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                     overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                     finish();
                     
                     
                 }
             });
                
            }
        });

    }
});

    }
}