package com.example.hojun.db_project;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 107;
    private static final int REQUEST_ANALYSIS_VIEW = 4;
    private static final int REQUEST_RECORD_VIEW = 5;
    private static final int REQUEST_Line_VIEW = 6;
    private GoogleMap mMap;
    private LatLng current_location;

    static private NetworkService netService = null;
    private String ipAddress = "163.180.116.125"; //Server ip address
    private int port = 3000;
    private boolean serverConnected = false;

    private ArrayList<WeatherContainer> weathers = new ArrayList<WeatherContainer>();

    private ArrayList<String> records = new ArrayList<String>();

    private HashMap<String, Integer> markerIcon = new HashMap<String, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActionBar ab = getActionBar();
        ab.show();
        current_location = new LatLng(37.2394869, 127.0838281);
        checkPermission();
        service_init();
        setTitle("DB");


        markerIcon.put("흐림", R.drawable.blur);
        markerIcon.put("구름조금", R.drawable.little_cloudy);
        markerIcon.put("구름많음", R.drawable.cloudy);
        markerIcon.put("비", R.drawable.rain);
        markerIcon.put("흐리고 비", R.drawable.rain);
        markerIcon.put("구름많고 비", R.drawable.rain);
        markerIcon.put("비 또는 눈", R.drawable.rain_snow);
        markerIcon.put("눈 또는 비", R.drawable.rain_snow);
        markerIcon.put("눈", R.drawable.snow);
        markerIcon.put("맑음", R.drawable.sunny);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initialize();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                final String temp = new String("WeatherPlanet " + String.valueOf(latLng.latitude) + " " + String.valueOf(latLng.longitude));
                Thread thread = new Thread() {

                    @Override
                    public void run() {
                        netService.requestWeatherInfo(temp);
                    }

                };
                thread.start();

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(final Marker marker) {
                        marker.remove();

                    }
                });
                return false;
            }
        });

    }

    public void initialize() {
        mMap.setMyLocationEnabled(true); //현재위치로 카메라를 이동시키는 버튼을 사용가능하게함
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 17));
        mMap.clear();
    }

    public void drawMarker() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 5));
        mMap.clear();
        if (weathers != null) {
            for (int i = 0; i < this.weathers.size(); i++) {
                WeatherContainer cityWeather = this.weathers.get(i);
                LatLng latLng = new LatLng(cityWeather.getLatitude(), cityWeather.getLongitude());
                MarkerOptions opt = new MarkerOptions();
                opt.position(latLng);
                opt.title(cityWeather.getCity());
                opt.snippet("기온:" + cityWeather.getTmn() + "~" + cityWeather.getTmx() + "'C");
                opt.icon(BitmapDescriptorFactory.fromResource(markerIcon.get(cityWeather.getWf())));
                mMap.addMarker(opt);
            }
        }
    }

    public void drawWeatherPlanetMarker(JSONObject json) {
        try {
            LatLng latLng = new LatLng(Double.parseDouble((String) json.get("latitude")), Double.parseDouble((String) json.get("longitude")));
            MarkerOptions opt = new MarkerOptions();
            opt.position(latLng);
            opt.title((String) json.get("place"));
            opt.snippet("기온:" + json.get("tmin") + "~" + json.get("tmax") + "'C");
            opt.icon(BitmapDescriptorFactory.fromResource(markerIcon.get(json.get("sky"))));
            mMap.addMarker(opt);
            records.add(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void checkPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(MainActivity.this, "권한 사용을 동의해주셔야 합니다.", Toast.LENGTH_LONG).show();
                    this.finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.serverConnectItem:
               /*  Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("Latitude", gps.getLatitude());
                intent.putExtra("longitude", gps.getlongitude());
                intent.putExtra("markers", markers);
                startActivityForResult(intent, REQUEST_MAP_VIEW);*/
                Log.d("XXX", "Connect");
                try {
                    NetworkService.callBack callback = new NetworkService.callBack() {
                        @Override
                        public void callbackMethod(ArrayList<WeatherContainer> markerlist) {
                            weathers = markerlist;
                        }
                    };
                    netService.setCallBack(callback);
                    netService.setting(ipAddress, port);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.PieActivity:
                final String temp = new String("recentAllWeather");
                Thread thread = new Thread() {

                    @Override
                    public void run() {
                        netService.requestWeatherInfo(temp);
                    }

                };
                thread.start();


                return true;
            case R.id.barActivity:
                //Intent intent = new Intent(MainActivity.this, PieChartActivity.class);
                intent = new Intent(MainActivity.this, BarChartActivity.class);
                intent.putExtra("data", weathers);
                startActivityForResult(intent, REQUEST_ANALYSIS_VIEW);

                return true;
            case R.id.recordActivity:
                intent = new Intent(MainActivity.this, RecordActivity.class);
                intent.putExtra("records", records);
                startActivityForResult(intent, REQUEST_RECORD_VIEW);
                return true;
            case R.id.lineActivity:
                onCreateDialog();
                Log.d("XXX", "클릭");
                //스레드로 최근날씨 요청
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected Dialog onCreateDialog() {
        // TODO Auto-generated method stub
        final String[] items = {"서울", "인천", "수원", "파주", "춘천", "원주", "강룽", "청주", "대전", "서산", "세종", "전주", "군산", "광주", "목포", "여수", "대구", "안동", "포항", "부산", "울산", "창원", "제주", "서귀포"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("확인하고 싶은 시(도)를 선택하세요");
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, items[which], Toast.LENGTH_SHORT).show();
                final String temp = new String("recentWeather " + items[which]);
                Thread thread = new Thread() {

                    @Override
                    public void run() {
                        netService.requestWeatherInfo(temp);
                    }

                };
                thread.start();
                dialog.dismiss();
            }
        });
        builder.show();
        return builder.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ServiceConnection netServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            netService = ((NetworkService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            netService = null;
        }
    };

    private void service_init() {
        Intent bindIntent = new Intent(this, NetworkService.class);
        bindService(bindIntent, netServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(StatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NetworkService.ACTION_SERVER_CONNECT);
        intentFilter.addAction(NetworkService.WeatherContainer_ARRIVED);
        intentFilter.addAction(NetworkService.NOTIFICATION_ARRIVED);
        intentFilter.addAction(NetworkService.WeatherPlanet_ARRIVED);
        intentFilter.addAction(NetworkService.RecentWeather_ARRIVED);
        intentFilter.addAction(NetworkService.RecentAllWeather_ARRIVED);
        return intentFilter;
    }


    private final BroadcastReceiver StatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;

            if (action.equals(NetworkService.ACTION_SERVER_CONNECT)) {
                serverConnected = true;
            }
            if (action.equals(NetworkService.WeatherContainer_ARRIVED)) {
                Toast.makeText(MainActivity.this, "오늘의 날씨 데이터가 수신되었습니다.", Toast.LENGTH_LONG).show();
                drawMarker();
            }
            if (action.equals(NetworkService.NOTIFICATION_ARRIVED)) {
                String notificationInfo = mIntent.getExtras().getString("data");
                final String[] Infos = notificationInfo.split(" ");
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("MarkerCreator")
                        .setMessage("Is it the " + Infos[2] + " where you have been?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // markers.add(new MarkerContainer(Double.parseDouble(Infos[0]),Double.parseDouble(Infos[1]),Infos[2],"",Infos[2]));
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();

            }
            if (action.equals(NetworkService.WeatherPlanet_ARRIVED)) {
                String data = mIntent.getExtras().getString("data");
                try {
                    JSONObject json = new JSONObject(data);
                    drawWeatherPlanetMarker(json);
                    String[] judge = new String[4];
                    if (Double.parseDouble((String) json.get("tmin")) < Double.parseDouble((String) json.get("mColdWave")))
                        judge[0] = "O";
                    else
                        judge[0] = "X";

                    if (Double.parseDouble((String) json.get("tmax")) > Double.parseDouble((String) json.get("mHeatWave")))
                        judge[1] = "O";
                    else
                        judge[1] = "X";

                    if (Double.parseDouble((String) json.get("wspd")) > Double.parseDouble((String) json.get("mWindSpeed")))
                        judge[2] = "O";
                    else
                        judge[2] = "X";

                    if (Double.parseDouble((String) json.get("humidity")) > Double.parseDouble((String) json.get("mHumidity")))
                        judge[3] = "O";
                    else
                        judge[3] = "X";


                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    alert.setTitle("지역 날씨 정보(" + json.get("latitude") + ", " + json.get("longitude") + ")");
                    alert.setMessage("지역:" + json.get("place") + "\n날씨:" + json.get("sky") + "\n기온:" + json.get("tmin") + "~" + json.get("tmax") + "'C\n풍속:" + json.get("wspd") + "\n습도:" + json.get("humidity") + "%\n\n *특보\n한파:" + judge[0] + ", 폭염:" + judge[1] + ", 강풍:" + judge[2] + ", 건조:" + judge[3]);
                    alert.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //dialog만들어 정보 처리


            }
            if (action.equals(NetworkService.RecentWeather_ARRIVED)) {
                String data = mIntent.getExtras().getString("data");
                intent = new Intent(MainActivity.this, LineChartActivity.class);
                intent.putExtra("data", data);
                startActivityForResult(intent, REQUEST_Line_VIEW);

            }
            if (action.equals(NetworkService.RecentAllWeather_ARRIVED)){
                String data = mIntent.getExtras().getString("data");
                intent = new Intent(MainActivity.this, PieChartActivity.class);
                intent.putExtra("data", data);
                startActivityForResult(intent, REQUEST_Line_VIEW);
            }
        }
    };


}