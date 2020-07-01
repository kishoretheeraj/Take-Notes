package com.example.takenotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.takenotes.Note.AddNotes;
import com.example.takenotes.Note.editnote;
import com.example.takenotes.auth.Login;
import com.example.takenotes.auth.Register;
import com.example.takenotes.model.Adaptar;
import com.example.takenotes.model.HomeActivity;
import com.example.takenotes.model.Note;
import com.example.takenotes.model.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView noteLists;
    Adaptar adapter;
    FirebaseFirestore fstore;
    FirestoreRecyclerAdapter<Note,NoteViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fstore=FirebaseFirestore.getInstance();
        fauth=FirebaseAuth.getInstance();
        user=fauth.getCurrentUser();


        Query query=fstore.collection("notes").document(user.getUid()).collection("mynotes").orderBy("title",Query.Direction.DESCENDING);

        //query notes> uuid > mynotes
FirestoreRecyclerOptions<Note> allNotes=new FirestoreRecyclerOptions.Builder<Note>()
        .setQuery(query,Note.class)
        .build();

noteAdapter=new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {
        noteViewHolder.noteTitle.setText(note.getTitle());
        noteViewHolder.noteContent.setText(note.getContent());
        final int code=getRandomcolor();
        noteViewHolder.mCardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code,null));
        final String docid=noteAdapter.getSnapshots().getSnapshot(i).getId();

        noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(view.getContext(), NoteDetails.class);
                i.putExtra("title",note.getTitle());
                i.putExtra("content",note.getContent());
                i.putExtra("code",code);
                i.putExtra("noteid",docid);

                view.getContext().startActivity(i);
            }
        });

        ImageView menuicon=noteViewHolder.view.findViewById(R.id.menuIcon);
        menuicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String docid=noteAdapter.getSnapshots().getSnapshot(i).getId();
                PopupMenu menu=new PopupMenu(view.getContext(),view);
                menu.setGravity(Gravity.END);
                menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Intent i=new Intent(view.getContext(), editnote.class);
                        i.putExtra("title",note.getTitle());
                        i.putExtra("content",note.getContent());
                        i.putExtra("noteid",docid);
                        startActivity(i);
                        return false;
                    }
                });
                menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        DocumentReference docref=fstore.collection("notes").document(user.getUid()).collection("mynotes").document(docid);
                        docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Notes Deleted.", Snackbar.LENGTH_LONG);
                                snackbar.show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Error, Try Again", Snackbar.LENGTH_LONG);
                                snackbar.show();

                            }
                        });
                        return false;
                    }
                });
                menu.show();

            }
        });

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);


        return new NoteViewHolder(view);
    }
};
         drawerLayout=findViewById(R.id.drawer);
         noteLists=findViewById(R.id.notelist);
         nav_view=findViewById(R.id.nav_view);
         nav_view.setNavigationItemSelectedListener(this);
         toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
         toggle.setDrawerIndicatorEnabled(true);
         toggle.syncState();

         noteLists.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
         noteLists.setAdapter(noteAdapter);

        View headerView=nav_view.getHeaderView(0);
        TextView username=headerView.findViewById(R.id.username);
        TextView email=headerView.findViewById(R.id.useremail);
        if(user.isAnonymous()){
            email.setVisibility(View.GONE);
            username.setText("Temporary User");
        }
        else{
            email.setText(user.getEmail());
            username.setText(user.getDisplayName());

        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(view.getContext(), AddNotes.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (menuItem.getItemId()){
            case R.id.notes:
                startActivity(new Intent(getApplicationContext(),profile.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;
            case R.id.addnote:
                startActivity(new Intent(this,AddNotes.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;
            case R.id.rating:
                final EditText sendmail=new EditText(nav_view.getContext());
                final AlertDialog.Builder maildialog=new AlertDialog.Builder(nav_view.getContext());
                maildialog.setTitle("FEEDBACK");
                maildialog.setMessage("Enter your Feed back about our Take Notes App :-");
                maildialog.setView(sendmail);

                maildialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get email and send link
                        String mailcontent=sendmail.getText().toString();
                        String mailid="kishoretheeraj.developer@gmail.com";
                        String mailsub="Feed Back for Take Notes App from"+" "+user.getEmail();

                        Intent email = new Intent(Intent.ACTION_SEND);


                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ mailid});
                        email.putExtra(Intent.EXTRA_SUBJECT, mailsub);
                        email.putExtra(Intent.EXTRA_TEXT, mailcontent);
                        email.setType("text/html");
                        email.setPackage("com.google.android.gm");
                        startActivity(Intent.createChooser(email, "Send mail"));

                        //need this to prompts email client only
                        email.setType("message/rfc822");

                        //startActivity(Intent.createChooser(email, "Choose an Email client :")); // no permission needed for mail



                    }
                });
                maildialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                maildialog.show();
                break;

            case R.id.shareapp:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/drive/folders/1mBpIwoBCeXMpA4t1HCxlMXVDX2xhHwVH?usp=sharing")).setPackage("com.google.android.apps.docs");
                startActivity(browserIntent);
                break;


            case R.id.logout:
                checkuser();
                break;

             case R.id.sync:
              if(user.isAnonymous()){
                  startActivity(new Intent(getApplicationContext(), Login.class));
                  overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

              }
              else{
                  Toast.makeText(this, "You are Connected", Toast.LENGTH_SHORT).show();
              }
                break;

            default:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkuser() {
        if(user.isAnonymous())
        {
            Displayalert();

            }
        else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), splash.class));
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            finish();
        }
        }

    private void Displayalert() {
        AlertDialog.Builder warning=new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("You are logged in with Temporary Account.Logging out will Delete all the Notes")
                .setPositiveButton("Sync note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(),Register.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
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
                                startActivity(new Intent(getApplicationContext(),splash.class));
                                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                                finish();

                            }
                        });

                    }
                });
warning.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.settings){
                startActivity(new Intent(getApplicationContext(),profile.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);


        }
        if(item.getItemId()==R.id.logout1)
        {
            checkuser();

        }
        if(item.getItemId()==R.id.image);
        {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
        }
        return super.onOptionsItemSelected(item);

    }
    public  class NoteViewHolder extends  RecyclerView.ViewHolder{
        TextView noteTitle,noteContent;
        View view;
        CardView mCardView;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle=itemView.findViewById(R.id.titles);
            noteContent=itemView.findViewById(R.id.content);
            mCardView=itemView.findViewById(R.id.noteCard);
            view=itemView;
        }
    }

    private int getRandomcolor() {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.blue);
        colorcode.add(R.color.yellow);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.lightPurple);
        colorcode.add(R.color.lightGreen);
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.red);
        colorcode.add(R.color.greenlight);
        colorcode.add(R.color.notgreen);

        Random randomcolor = new Random();
        int number = randomcolor.nextInt(colorcode.size());
        return colorcode.get(number);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null)
        {
            noteAdapter.stopListening();
        }

    }
}
