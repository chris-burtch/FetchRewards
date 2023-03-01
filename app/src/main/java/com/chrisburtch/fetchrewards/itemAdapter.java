package com.chrisburtch.fetchrewards;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class itemAdapter extends RecyclerView.Adapter<itemViewHolder> {
    ArrayList<itemData> itemDataList = new ArrayList<>();

    public itemAdapter(ArrayList<itemData> list){
        itemDataList = list;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_card, parent, false);
        itemViewHolder holder =  new itemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        String sListID = "ListID: "+itemDataList.get(position).listID;
        String sID = "ID:" +itemDataList.get(position).id;
        String sName = "Name: "+itemDataList.get(position).name;
        holder.listID.setText(itemDataList.get(position).toString());
        //holder.id.setText(sID);
        //holder.name.setText(sName);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void updateDataList(ArrayList<itemData> newList){
        Log.d("itemAdapter","new list received: "+newList.size());
        if(newList.size() > 0){
            itemDataList.clear();
            //itemDataList.addAll(newList);
            for(itemData item : newList){
                itemDataList.add(item);
            }
            Log.d("itemAdapter","notifying data set changed "+itemDataList.size());
            notifyDataSetChanged();
            Log.d("itemAdapter","data set changed");
        }
    }
}
