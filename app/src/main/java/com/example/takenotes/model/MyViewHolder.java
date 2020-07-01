package com.example.takenotes.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takenotes.R;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView textView;
    View v;



    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imagesingle);
        textView=itemView.findViewById(R.id.imagetext);
        v=itemView;

    }
}
