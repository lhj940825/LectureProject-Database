package com.example.hojun.db_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by HoJun on 2017-05-22.
 */

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PieChartActivity extends Activity {
    private PieChart mChart;
    private RelativeLayout mainLayout;

    private float[] yData = {5, 10, 25, 30, 40};
    private String[] xData = {"sony", "huawei", "LG", "Apple", "Samsung"};

    ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    HashMap<Integer, String> map2 = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map.put("서울",0);
        map.put("인천",1);
        map.put("수원",2);
        map.put("파주",3);
        map.put("춘천",4);
        map.put("원주",5);
        map.put("강릉",6);
        map.put("청주",7);
        map.put("대전",8);
        map.put("서산",9);
        map.put("세종",10);
        map.put("전주",11);
        map.put("군산",12);
        map.put("광주",13);
        map.put("목포",14);
        map.put("여수",15);
        map.put("대구",16);
        map.put("안동",17);
        map.put("포항",18);
        map.put("부산",19);
        map.put("울산",20);
        map.put("창원",21);
        map.put("제주",22);
        map.put("서귀포",23);


        map2.put(0,"서울");
        map2.put(1,"인천");
        map2.put(2,"수원");
        map2.put(3,"파주");
        map2.put(4,"춘천");
        map2.put(5,"원주");
        map2.put(6,"강릉");
        map2.put(7,"청주");
        map2.put(8,"대전");
        map2.put(9,"서산");
        map2.put(10,"세종");
        map2.put(11,"전주");
        map2.put(12,"군산");
        map2.put(13,"광주");
        map2.put(14,"목포");
        map2.put(15,"여수");
        map2.put(16,"대구");
        map2.put(17,"안동");
        map2.put(18,"포항");
        map2.put(19,"부산");
        map2.put(20,"울산");
        map2.put(21,"창원");
        map2.put(22,"제주");
        map2.put(23,"서귀포");

        for(int i = 0 ; i < 24; i++){
            dataList.add(new ArrayList<String>());
        }
        Intent intent = getIntent();
        String temp = intent.getStringExtra("data");
        JSONObject json = null;
        try {
            json = new JSONObject(temp);
            JSONArray datas = json.getJSONArray("weatherInfoArray");
            for(int i = 0; i < datas.length(); i++){
                JSONObject temp2 = (JSONObject) datas.get(i);
                dataList.get(map.get(temp2.get("city"))).add((String)temp2.get("wf"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }







        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        mChart = new PieChart(this);
        // add pie chart to main layout
        //mainLayout.addView(mChart);
        mainLayout.addView(mChart, 1400, 1800);
        mainLayout.setBackgroundColor(Color.LTGRAY);

        // configure pie chart
        mChart.setUsePercentValues(true);
        mChart.setDescription("Sunny Ratio of Citys");

        //enable hole and configure
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(10);

        //enable rotation of the chart by touch
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        //set a chart value selected listener
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                //display msg when value selected
                if(entry == null)
                    return;

                Toast.makeText(PieChartActivity.this, xData[entry.getXIndex()]+" = "+ entry.getVal()+"%", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
        //add data
        addData();

        //customize legends
        Legend I = mChart.getLegend();
        I.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        I.setXEntrySpace(7);
        I.setYEntrySpace(5);
    }
    public void addData(){
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
/*        for(int i = 0; i < yData.length; i++) {
            yVals1.add(new Entry(yData[i], i));
        }*/
        for(int i = 0; i < dataList.size(); i++){
            float cnt = 0f;
            for(int j = 0; j < dataList.get(i).size(); j++){
                if(dataList.get(i).get(j).equals("맑음")||dataList.get(i).get(j).equals("구름조금")){
                    cnt++;
                }
            }
            float per = (cnt/dataList.get(i).size())*100;
            yVals1.add(new Entry(per,i));
        }

        ArrayList<String> xVals = new ArrayList<String>();
        for(int i = 0; i < map2.size(); i++){
            xVals.add(map2.get(i));
        }
/*        for(int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);*/

        //create pie data set
        PieDataSet dataset = new PieDataSet(yVals1, "City Name");
        dataset.setSliceSpace(3);
        dataset.setSelectionShift(5);

        //add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for(int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for(int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for(int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for(int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for(int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataset.setColors(colors);

        //instantiate pie data object now
        PieData data = new PieData(xVals, dataset);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);

        //undo all highlights
        mChart.highlightValue(null);

        //update  pie chart
        mChart.invalidate();

        // now it's time for demo!!

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return true;
    }





}