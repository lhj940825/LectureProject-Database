package com.example.hojun.db_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HoJun on 2017-05-24.
 */

public class RecordActivity extends Activity {
    private ArrayList<String> recordList;
    private ListViewAdapter adapter;
    private HashMap<String, Integer> markerIcon = new HashMap<String, Integer>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        setContentView(R.layout.record_list);
        android.view.WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        android.view.WindowManager.LayoutParams selayoutParams = this.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.y = 200;
        Intent intent = getIntent();
        Serializable list = intent.getExtras().getSerializable("records");
        recordList = (ArrayList<String>) list;
       /* recordList = new ArrayList<String>();
        recordList.add("12344");*/


        markerIcon.put("흐림", R.drawable.blur);
        markerIcon.put("구름조금", R.drawable.little_cloudy);
        markerIcon.put("구름많음", R.drawable.cloudy);
        markerIcon.put("비", R.drawable.rain);
        markerIcon.put("흐리고 비", R.drawable.rain);
        markerIcon.put("비 또는 눈", R.drawable.rain_snow);
        markerIcon.put("눈 또는 비", R.drawable.rain_snow);
        markerIcon.put("눈", R.drawable.snow);
        markerIcon.put("맑음", R.drawable.sunny);


        adapter = new ListViewAdapter(recordList);
        ListView listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);


    }

    public class ListViewAdapter extends BaseAdapter {
        private ArrayList<String> recordList = new ArrayList<String>();

        public ListViewAdapter() {

        }

        public ListViewAdapter(ArrayList<String> temp) {
            this.recordList = temp;
        }

        @Override
        public int getCount() {
            return recordList.size();
        }

        @Override
        public Object getItem(int position) {
            return recordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_element, parent, false);
            }

            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1);
            TextView descTextView = (TextView) convertView.findViewById(R.id.textView2);

            String item = recordList.get(position);

            try {
                JSONObject json = new JSONObject(item);
                iconImageView.setImageResource(markerIcon.get((String)json.get("sky")));
                titleTextView.setText((String) json.get("place"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            titleTextView.setTextColor(Color.WHITE);
            descTextView.setText("");
            return convertView;
        }
    }
}
