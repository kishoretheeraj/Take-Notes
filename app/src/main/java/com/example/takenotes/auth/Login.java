package com.example.takenotes.auth;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.takenotes.MainActivity;
import com.example.takenotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText lemail,lpassword;
Button loginnow;
TextView forgetpass,createacc;
FirebaseAuth fauth;
FirebaseFirestore fstore;
ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lemail=findViewById(R.id.emailid);
        lpassword=findViewById(R.id.pass);
        loginnow=findViewById(R.id.loginbtn);
        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        forgetpass=findViewById(R.id.forgetpass);
        createacc=findViewById(R.id.newuser);
        progressBar=findViewById(R.id.progressBar2);
        
        
        showwaring();

        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetmail1=new EditText(v.getContext());
                final AlertDialog.Builder passworddialog=new AlertDialog.Builder(v.getContext());
                passworddialog.setTitle("Reset Password");
                passworddialog.setMessage("Enter your Email id to receive Reset link :-");
                passworddialog.setView(resetmail1);

                passworddialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get email and send link
                        String mail=resetmail1.getText().toString();
                        fauth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Reset link as been sent", Snackbar.LENGTH_LONG);
                                snackbar.show();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error!!"+e.getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        });
                    }
                });
                passworddialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passworddialog.show();
            }
        });

createacc.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(getApplicationContext(),Register.class));
        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }
});
        loginnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String email=lemail.getText().toString();
                String password=lpassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    lemail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    lpassword.setError("Password is required");
                    return;
                }

                //delete notes first

                progressBar.setVisibility(View.VISIBLE);





                fauth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if(fauth.getCurrentUser().isAnonymous()){
                            FirebaseUser user=fauth.getCurrentUser();

                            fstore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "All Temporary Notes are Deleted.", Snackbar.LENGTH_LONG);
                                    snackbar.show();

                                }
                            });
                            // delete temp user
                            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Login.this, "Temporary User Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Log In Successful", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                
            }
        });

    }

    private void showwaring() {
        final AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are You Sure ?")
                .setMessage("Linking Existing Account Will Delete all the Temporary Notes. Create New Account To Save them.")
                .setPositiveButton("Save Notes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),Register.class));
                        finish();
                    }
                }).setNegativeButton("It's Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        warning.show();

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