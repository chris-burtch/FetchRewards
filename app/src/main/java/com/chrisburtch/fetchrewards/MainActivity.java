package com.chrisburtch.fetchrewards;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
    LinearLayout loadingDialog;

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

        //loading dialog
        loadingDialog = (LinearLayout)findViewById(R.id.loadingLayout);
        loadingDialog.setVisibility(View.GONE);

        //recylcerview infrastructure
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        adapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        //TextViews
        countTextView = (TextView)findViewById(R.id.item_count_tv);
        String countText =  getString(R.string.item_count)+adapter.getItemCount();
        countTextView.setText(countText);
        introTv = (TextView)findViewById(R.id.intro_textview);
        String introText = getString(R.string.intro_text);
        introTv.setText(introText);

        //Loading dialog


    }

    /*********************************************************************************
     Purpose: Execute the JSONTask to retrieve data. Hide intro message.
     Parameter: View to associate to layout onClick behavior
    *********************************************************************************/
    public void getItems(View view){
        if(isOnline()) {
            introTv.setVisibility(View.GONE);
            new JSONTask().execute();
        }else{
            Toast.makeText(this, getString(R.string.no_network), Toast.LENGTH_LONG).show();
        }

    }

    /*********************************************************************************
     Purpose: Clear recyclerview. Show intro message.
     Parameter: View to associate to layout onClick behavior
    *********************************************************************************/
    public void clear(View view){
        introTv.setVisibility(View.VISIBLE);
        adapter.clearDataList();
    }

    /*********************************************************************************
     Purpose: Exit application
    *********************************************************************************/
    public void exit(){
        this.finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /*********************************************************************************
     Purpose: OnDataUpdated override. Update the item counter view.
     Parameter: Updated list to get item count.
     *********************************************************************************/
    @Override
    public void OnDataUpdated(ArrayList<ItemData> list) {
        String countText = getString(R.string.item_count)+adapter.getItemCount();
        countTextView.setText(countText);
    }

    /*********************************************************************************
     Purpose: Private class to asynchronously get JSON data from endpoint
    *********************************************************************************/
    private class JSONTask extends AsyncTask<Void, Void, String>{
        final String jsonAddress = "https://fetch-hiring.s3.amazonaws.com/hiring.json";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(loadingDialog != null){
                loadingDialog.setVisibility(View.VISIBLE);
            }
        }

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
                Toast.makeText(getBaseContext(), getString(R.string.no_data_issue),Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<ItemData> list = new ArrayList<>();
            try{
                JSONArray baseJson = new JSONArray(s);
                for(int i = 0; i < baseJson.length(); i++){
                    JSONObject item = baseJson.getJSONObject(i);
                    int id = -1;
                    int listId = -1;
                    String name = "";

                    //ensure value exists before loading
                    if (item.has("id"))  id= item.getInt("id");
                    if (item.has("listId")) listId = listId = item.getInt("listId");
                    if (item.has("name")) name= item.getString("name");

                    //filter the list here to limit the O(n) cost to sort
                    if(!name.isEmpty() && name.compareToIgnoreCase("null") != 0 && id >= 0 && listId >= 0) {
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

            if(loadingDialog != null){
                loadingDialog.setVisibility(View.GONE);
            }
        }
    }

}