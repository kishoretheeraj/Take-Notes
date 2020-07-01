package com.example.takenotes.model;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.takenotes.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText imagetitle;
    private TextView percent;
    private ProgressBar progressBar;
    private Button uploadbtn;
    private static final int REQUEST_CODE_IMAGE=101;
    Uri imageUri;
    boolean  isImageAdded=false;
DatabaseReference ref;
StorageReference storageReference;
    FirebaseUser user;
    FirebaseAuth fauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView=findViewById(R.id.imageviewadd);
        imagetitle=findViewById(R.id.Imagetitle);
        percent=findViewById(R.id.percent);
        progressBar=findViewById(R.id.progress);
        uploadbtn=findViewById(R.id.upload);

        percent.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        fauth=FirebaseAuth.getInstance();
        user=fauth.getCurrentUser();

        ref= FirebaseDatabase.getInstance().getReference().child(user.getUid());
        storageReference= FirebaseStorage.getInstance().getReference().child(user.getUid());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODE_IMAGE);
            }
        });
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              final String imagename=  imagetitle.getText().toString();
              if(isImageAdded!=false&& imagename!=null)
              {
                  uploadimage(imagename);
              }

            }
        });


    }

    private void uploadimage(final String imagename) {
        progressBar.setVisibility(View.VISIBLE);
        percent.setVisibility(View.VISIBLE);


        final String key=ref.push().getKey();
        storageReference.child(key+".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap=new HashMap();
                        hashMap.put("ImageName",imagename);
                        hashMap.put("ImageUrl",uri.toString());
                        ref.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ImageActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                                finish();
                            }
                        });

                    }
                });


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress=(taskSnapshot.getBytesTransferred()*100)/taskSnapshot.getTotalByteCount();
                progressBar.setProgress( (int) progress);
                percent.setText((int) progress+"%");

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_IMAGE && data!=null)
        {
            imageUri=data.getData();
            isImageAdded=true;
            imageView.setImageURI(imageUri);
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}