package com.chrisburtch.fetchrewards;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;

public class ItemViewHolder extends RecyclerView.ViewHolder{
    final TextView itemResult;

    /*********************************************************************************
     Purpose: Constructor
     *********************************************************************************/
    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemResult = (TextView)itemView.findViewById(R.id.item_result);
    }
}
