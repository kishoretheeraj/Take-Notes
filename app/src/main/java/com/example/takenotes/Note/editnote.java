package com.example.takenotes.Note;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.takenotes.MainActivity;
import com.example.takenotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class editnote extends AppCompatActivity {
    Intent data;
    EditText editnotetitle,editnotecontent;
    FirebaseFirestore fstore;
    ProgressBar spinner;
    FirebaseUser user;
    TextToSpeech textToSpeech;
    private static final int RECOGNIZER_RESULT=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);
        Toolbar t=findViewById(R.id.toolbar);
        setSupportActionBar(t);

        fstore=FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();

        editnotetitle=findViewById(R.id.editnote);
        editnotecontent=findViewById(R.id.editnotecontent);
        spinner=findViewById(R.id.progressBar2);
        data=getIntent();

        String noteTitle=data.getStringExtra("title");
        String noteContent=data.getStringExtra("content");

        editnotetitle.setText(noteTitle);
        editnotecontent.setText(noteContent);

        FloatingActionButton fab = findViewById(R.id.saveeditednote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nTitle=editnotetitle.getText().toString();
                final String nContent=editnotecontent.getText().toString();

                if(nTitle.isEmpty()||nContent.isEmpty())
                {
                    Toast.makeText(editnote.this, "Cannot Save the note with Empty Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                spinner.setVisibility(view.VISIBLE);
                //save note
                DocumentReference docref=fstore.collection("notes").document(user.getUid()).collection("mynotes").document(data.getStringExtra("noteid"));

                Map<String,Object> note=new HashMap<>();
                note.put("title",nTitle);
                note.put("content",nContent);



                docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(editnote.this, "Note Updated.", Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(editnote.this, "Error, Try again", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.VISIBLE);

                    }
                });


            }
        });
        FloatingActionButton fab1 = findViewById(R.id.speak);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text");
                startActivityForResult(intent,RECOGNIZER_RESULT);





            }
        });
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "This language is not supported!",
                                Toast.LENGTH_SHORT);
                    } else {
                        // btnSpeak.setEnabled(true);
                        textToSpeech.setPitch(0.6f);
                        textToSpeech.setSpeechRate(1.0f);

                       // speak();
                    }
                }
            }
        });
        FloatingActionButton fab2 = findViewById(R.id.text);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Init TextToSpeech
                speak();

            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultcode, @Nullable Intent data) {

        if (requestCode == RECOGNIZER_RESULT && resultcode == RESULT_OK)
        {
            ArrayList<String> matches=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            editnotecontent.setText(matches.get(0).toString());

        }
        super.onActivityResult(requestCode, resultcode, data);
    }




    private void speak() {
        String text = editnotecontent.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.close_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.close) {
            Toast.makeText(this, "Not Saved", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}