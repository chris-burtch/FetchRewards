package com.chrisburtch.fetchrewards;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends Activity {
    static final String TAG = "FetchRewards";
    Button getItemsBtn;
    Button clearBtn;
    itemAdapter adapter;
    RecyclerView recyclerView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getItemsBtn = (Button)findViewById(R.id.get_items);
        clearBtn = (Button)findViewById(R.id.clear);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        ArrayList<itemData> itemList = new ArrayList<>();
        adapter = new itemAdapter(itemList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

    }

    public void getItems(View view){
        Toast.makeText(this, "GetItems", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"pre execute");
        new JSONTask().execute();
    }

    public void clear(View view){
        Toast.makeText(this, "Clear", Toast.LENGTH_SHORT).show();
    }

    private class JSONTask extends AsyncTask<Void, Void, String>{
        String jsonAddress = "https://fetch-hiring.s3.amazonaws.com/hiring.json";
        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            Log.d(TAG, "executing..");
            try{
                URL url = new URL(jsonAddress);
                //open the connection and get everything ready to read the data
                conn = (HttpURLConnection)url.openConnection();
                conn.connect();
                InputStream stream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer stringBuffer = new StringBuffer();
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s == null || s.isEmpty()){
                Toast.makeText(getBaseContext(), "No data to display. Try again.",Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<itemData> list = new ArrayList<>();
            try{
                JSONArray baseJson = new JSONArray(s);
                for(int i = 0; i < baseJson.length(); i++){
                    JSONObject item = baseJson.getJSONObject(i);
                    int id = item.getInt("id");
                    int listId = item.getInt("listId");
                    String name = item.getString("name");

                    if(name != null && !name.isEmpty() && name.compareToIgnoreCase("null") != 0) {
                        Log.d(TAG, "adding item index "+i);
                        itemData data = new itemData();
                        data.setId(id);
                        data.setListID(listId);
                        data.setName(name);
                        list.add(data);
                    }
                }

                if(list.size() > 0){
                    Collections.sort(list, new itemData.SortByListIDAndName());
                   adapter.updateDataList(list);
                }
            }catch (JSONException e){
                Log.e(TAG,"JSON Error. Reason: "+e.getMessage(), e);
            }
        }
    }

}