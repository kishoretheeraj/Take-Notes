package com.example.takenotes.model;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takenotes.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Adaptar extends RecyclerView.Adapter<Adaptar.ViewHolder> {
    List<String> titles;
    List<String> content;

    public Adaptar(List<String>title,List<String> content){
        this.titles=title;
        this.content=content;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
                holder.noteTitle.setText(titles.get(position));
                holder.noteContent.setText(content.get(position));
                final int code=getRandomcolor();
                holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(code,null));
                
                
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(view.getContext(), NoteDetails.class);
                        i.putExtra("title",titles.get(position));
                        i.putExtra("content",content.get(position));
                        i.putExtra("code",code);

                        view.getContext().startActivity(i);
                    }
                });

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
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView noteTitle,noteContent;
         View view;
         CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle=itemView.findViewById(R.id.titles);
            noteContent=itemView.findViewById(R.id.content);
            mCardView=itemView.findViewById(R.id.noteCard);
            view=itemView;
        }
    }
}
