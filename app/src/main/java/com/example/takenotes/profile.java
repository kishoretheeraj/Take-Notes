package com.example.takenotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.takenotes.auth.Register;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {
    Button logout, verify, resetpassword;
    TextView verifymsg, fname, femail;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userid;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        logout = findViewById(R.id.logout);
        verifymsg = findViewById(R.id.textView5);
        fname = findViewById(R.id.textView6);
        femail = findViewById(R.id.textView7);
        resetpassword = findViewById(R.id.resetpassword);
         verify = findViewById(R.id.verifybtn);
        fauth = FirebaseAuth.getInstance();
        user = fauth.getCurrentUser();
        user.reload();


        fstore = FirebaseFirestore.getInstance();

        userid = fauth.getCurrentUser().getUid();

        if (user.isAnonymous()) {
            femail.setText("temporaryuser@gmail.com");
            fname.setText("Temporary User");
        } else {
            femail.setText(user.getEmail());
            fname.setText(user.getDisplayName());
        }


        if (!user.isEmailVerified()) {
            verifymsg.setVisibility(View.VISIBLE);
            verify.setVisibility(View.VISIBLE);
            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    FirebaseUser user = fauth.getCurrentUser();
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verfication Email has been sent successfully", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(profile.this, "Error!!" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }


        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetpassword = new EditText(v.getContext());
                final AlertDialog.Builder passworddialog = new AlertDialog.Builder(v.getContext());
                passworddialog.setTitle("Reset Password");
                passworddialog.setMessage("Enter your New Password :-");
                passworddialog.setView(resetpassword);

                passworddialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get email and send link
                        String newpassword = resetpassword.getText().toString();
                        user.updatePassword(newpassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(profile.this, "Password Reset Successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(profile.this, "Error in Resetting password", Toast.LENGTH_SHORT).show();

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

    }

    public void logout(View view) {
        checkuser();


    }

    private void checkuser() {
        if (user.isAnonymous()) {
            Displayalert();

        } else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), splash.class));
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            finish();
        }
    }

    private void Displayalert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("You are logged in with Temporary Account.Logging out will Delete all the Notes")
                .setPositiveButton("Sync note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                        finish();
                    }
                }).setNegativeButton("logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // To delete all the notes by anonymous user

                        //and delete anonymous user

                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), splash.class));
                                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                                finish();

                            }
                        });

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
