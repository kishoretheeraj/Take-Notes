package com.example.takenotes.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.takenotes.MainActivity;
import com.example.takenotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {
    EditText rusername,ruseremail,ruserpass,ruserconfpass;
    Button syncaccount;
    TextView loginact;
    ProgressBar progressBar;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rusername=findViewById(R.id.userName);
        ruseremail=findViewById(R.id.userEmail);
        ruserpass=findViewById(R.id.password);
        ruserconfpass=findViewById(R.id.passwordConfirm);
        syncaccount=findViewById(R.id.createAccount);
        loginact=findViewById(R.id.login);
        progressBar=findViewById(R.id.progressBar4);
        fauth=FirebaseAuth.getInstance();

        loginact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        syncaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String username=rusername.getText().toString();
                final String email=ruseremail.getText().toString();
               final String pass=ruserpass.getText().toString();
                final String confpass=ruserconfpass.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    rusername.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    ruseremail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    ruserpass.setError("Password is required");
                    return;
                }
                if (pass.length() < 6) {
                    ruserpass.setError("password should be greater than six letters");
                    return;
                }

                if(!pass.equals(confpass)){
                    ruserconfpass.setError("Password Do not Match");
                
                }
                progressBar.setVisibility(View.VISIBLE);


                AuthCredential credential= EmailAuthProvider.getCredential(email,pass);
                fauth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Register.this, "Notes are Synced", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        FirebaseUser usr=fauth.getCurrentUser();
                        usr.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Register.this,"Verification Email has been sent successfully", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, "Error!!"+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });


                        UserProfileChangeRequest request=new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();
                        usr.updateProfile(request);

                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Failed to Connect, Try again.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        progressBar.setVisibility(View.GONE);

                    }
                });
                
                
                


            }
        });




    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
        finish();
        return super.onOptionsItemSelected(item);
    }
}