package com.chrisburtch.fetchrewards;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;



public class MainActivity extends Activity implements IDataUpdateListener {
    static final String TAG = "FetchRewards";
    Button getItemsBtn;
    Button clearBtn;
    Button exitBtn;
    ItemAdapter adapter;
    RecyclerView recyclerView;
    TextView introTv;
    TextView countTextView;

    /*********************************************************************************
     Purpose: OnCreate Override. Setup UI.
     *********************************************************************************/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<ItemData> itemList = new ArrayList<>();
        //buttons - click behavior defined in layout
        getItemsBtn = (Button)findViewById(R.id.get_items);
        clearBtn = (Button)findViewById(R.id.clear);
        exitBtn = (Button)findViewById(R.id.exit);
        //For demonstration purposes - we will set an OnClickListener here
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        //recylcerview infrastructure
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        adapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        //TextViews
        countTextView = (TextView)findViewById(R.id.item_count_tv);
        String countText = "Item count: "+adapter.getItemCount();
        countTextView.setText(countText);
        introTv = (TextView)findViewById(R.id.intro_textview);
        String introText = "Tap \"GET ITEMS\" to begin"+"\n"+" Tap \"CLEAR\" to clear items"+"\n"+"Tap \"EXIT \" to exit app";
        introTv.setText(introText);

    }

    /*********************************************************************************
     Purpose: Execute the JSONTask to retrieve data. Hide intro message.
     Parameter: View to associate to layout onClick behavior
    *********************************************************************************/
    public void getItems(View view){
        introTv.setVisibility(View.GONE);
        new JSONTask().execute();

    }

    /*********************************************************************************
     Purpose: Clear recyclerview. Show intro message.
     Parameter: View to associate to layout onClick behavior
    *********************************************************************************/
    public void clear(View view){
        introTv.setVisibility(View.VISIBLE);
        adapter.clearDataList();
        String countText = "Item count: "+adapter.getItemCount();
        countTextView.setText(countText);
    }

    /*********************************************************************************
     Purpose: Exit application
    *********************************************************************************/
    public void exit(){
        this.finish();
    }

    /*********************************************************************************
     Purpose: OnDataUpdated override. Update the item counter view.
     Parameter: Updated list to get item count.
     *********************************************************************************/
    @Override
    public void OnDataUpdated(ArrayList<ItemData> list) {
        String countText = "Item count: "+adapter.getItemCount();
        countTextView.setText(countText);
    }

    /*********************************************************************************
     Purpose: Private class to asynchronously get JSON data from endpoint
    *********************************************************************************/
    private class JSONTask extends AsyncTask<Void, Void, String>{
        final String jsonAddress = "https://fetch-hiring.s3.amazonaws.com/hiring.json";

        /*********************************************************************************
         Purpose: doInBackground override.
         1.) Create connection to web endpoint
         2.) Pull data
         4.) Send data to postExecute as String
        *********************************************************************************/
        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(jsonAddress);
                //open the connection and get everything ready to read the data
                conn = (HttpURLConnection)url.openConnection();
                conn.connect();
                InputStream stream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder stringBuffer = new StringBuilder();
                String readLine;

                //read data
                while((readLine = reader.readLine()) != null){
                    stringBuffer.append(readLine).append("\n");
                }

                if(stringBuffer.length() == 0)
                    return null;

                return stringBuffer.toString();
            }catch(IOException e){
                Log.e(TAG, "Connection error. Reason: "+e.getMessage(), e);
            }finally {
                if(conn != null) conn.disconnect();
                if(reader != null){
                    try{
                        reader.close();
                    }catch(IOException e){
                        Log.e(TAG, "Reader close failed. Reason: "+e.getMessage(), e);
                    }
                }
            }
            return null;
        }

        /*********************************************************************************
         Purpose: doInBackground override.
         1.) Receive string
         2.) Create JSON array/objects
         3.) Load into list
         4.) Sort list by ListID then Name length then Name value
         5.) Send sorted data to recylcerview via adapter.
         *********************************************************************************/
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s == null || s.isEmpty()){
                //notify user of no results
                Toast.makeText(getBaseContext(), "No data to display. Try again.",Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<ItemData> list = new ArrayList<>();
            try{
                JSONArray baseJson = new JSONArray(s);
                for(int i = 0; i < baseJson.length(); i++){
                    JSONObject item = baseJson.getJSONObject(i);
                    int id = item.getInt("id");
                    int listId = item.getInt("listId");
                    String name = item.getString("name");

                    if(!name.isEmpty() && name.compareToIgnoreCase("null") != 0) {
                        ItemData data = new ItemData();
                        data.setId(id);
                        data.setListID(listId);
                        data.setName(name);
                        list.add(data);
                    }
                }

                if(list.size() > 0){
                    Collections.sort(list, new ItemData.SortByListIDAndName());
                    adapter.updateDataList(list);
                }
            }catch (JSONException e){
                Log.e(TAG,"JSON Error. Reason: "+e.getMessage(), e);
            }
        }
    }

}