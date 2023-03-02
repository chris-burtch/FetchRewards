package com.chrisburtch.fetchrewards;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    ArrayList<ItemData> itemDataList = new ArrayList<>();
    IDataUpdateListener listener;

    /*********************************************************************************
     Purpose: Constructor
     *********************************************************************************/
    public ItemAdapter(ArrayList<ItemData> list, IDataUpdateListener listener){
        itemDataList = list;
        this.listener = listener;
    }

    /*********************************************************************************
     Purpose: onCreateViewHolder override. Returns view object.
     *********************************************************************************/
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_card, parent, false);
        return new ItemViewHolder(itemView);
    }
    /*********************************************************************************
     Purpose: onBindViewHolder override.
     *********************************************************************************/
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.itemResult.setText(itemDataList.get(position).toString());
    }

    /*********************************************************************************
     Purpose: onBindViewHolder override. Returns integer value of list size.
     *********************************************************************************/
    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    /*********************************************************************************
     Purpose: onAttachedToRecyclerView override.
     *********************************************************************************/
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /*********************************************************************************
     Purpose: onAttachedToRecyclerView override. Update recylerview and notify MainActivity of count change.
     *********************************************************************************/
    public void updateDataList(ArrayList<ItemData> newList){
        if(newList.size() > 0){
            itemDataList.clear();
            itemDataList.addAll(newList);
            notifyDataSetChanged();
            listener.OnDataUpdated(itemDataList);
        }
    }

    /*********************************************************************************
     Purpose: Clear data and update recylerview and notify MainActivity of count change.
     *********************************************************************************/
    public void clearDataList(){
        int size = itemDataList.size();
        itemDataList.clear();
        notifyItemRangeRemoved(0, size);
        listener.OnDataUpdated(itemDataList);
    }
}
