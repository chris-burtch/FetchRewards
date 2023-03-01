package com.chrisburtch.fetchrewards;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;

public class itemViewHolder extends RecyclerView.ViewHolder{
    TextView id;
    TextView listID;
    TextView name;

    public itemViewHolder(@NonNull View itemView) {
        super(itemView);
        //id = (TextView)itemView.findViewById(R.id.id);
        listID = (TextView)itemView.findViewById(R.id.listID);
       // name = (TextView)itemView.findViewById(R.id.name);
    }
}
