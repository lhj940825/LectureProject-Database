package com.example.hojun.db_project;

/**
 * Created by HoJun on 2017-05-23.
 */

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
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
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.io.Serializable;
import java.util.ArrayList;


public class BarChartActivity extends Activity {

    private BarChart barChart;
    private ArrayList<WeatherContainer> weathers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);
        Intent intent = getIntent();
        Serializable list = intent.getExtras().getSerializable("data");
        weathers = (ArrayList<WeatherContainer>) list;
        setTitle("Today's Temperature");
        //barChart = (BarChart) findViewById(R.id.bargraph);
        HorizontalBarChart barChart = (HorizontalBarChart) findViewById(R.id.bargraph);
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        for(int i = 0; i < weathers.size(); i++){
            barEntries.add(new BarEntry(weathers.get(i).getTmx(),i));
        }
     /*   barEntries.add(new BarEntry(44f, 0));
        barEntries.add(new BarEntry(88f, 1));
        barEntries.add(new BarEntry(66f, 2));
        barEntries.add(new BarEntry(12f, 3));
        barEntries.add(new BarEntry(19f, 4));
        barEntries.add(new BarEntry(91f, 5));

        barEntries.add(new BarEntry(91f, 6));
        barEntries.add(new BarEntry(91f, 7));
        barEntries.add(new BarEntry(91f, 8));
        barEntries.add(new BarEntry(91f, 9));
        barEntries.add(new BarEntry(91f, 10));
        barEntries.add(new BarEntry(91f, 11));
        barEntries.add(new BarEntry(91f, 12));
        barEntries.add(new BarEntry(91f, 13));
        barEntries.add(new BarEntry(91f, 14));
        barEntries.add(new BarEntry(91f, 15));
        barEntries.add(new BarEntry(91f, 16));
        barEntries.add(new BarEntry(91f, 17));
        barEntries.add(new BarEntry(91f, 18));
        barEntries.add(new BarEntry(91f, 19));*/


        BarDataSet barDataSet = new BarDataSet(barEntries, "Temperatures");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);


        ArrayList<String> theDates = new ArrayList<String>();
        for(int i = 0; i <weathers.size(); i++){
            theDates.add(weathers.get(i).getCity());
        }
/*        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");
        theDates.add("서울");

        theDates.add("April");
        theDates.add("May");
        theDates.add("June");
        theDates.add("July");
        theDates.add("August");
        theDates.add("September");*/

        BarData theData = new BarData(theDates, barDataSet);
        barChart.setData(theData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setDescription("Bar Chart of Weather");
        barChart.setHorizontalFadingEdgeEnabled(true);
        barChart.getRendererXAxis().getPaintAxisLabels().setLetterSpacing(0.00005f);

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