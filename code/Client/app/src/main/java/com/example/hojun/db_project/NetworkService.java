package com.example.hojun.db_project;

/**
 * Created by HoJun on 2017-05-02.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class NetworkService extends Service {

    private String dstAddress;
    private int dstPort;
    private Socket socket = null;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private static Context context;
    private IBinder mBinder = new LocalBinder();
    private ArrayList<WeatherContainer> weathers = new ArrayList<WeatherContainer>();
    public callBack m_callback;
    public final static String ACTION_SERVER_CONNECT = "com.nordicsemi.nrfUART.ACTION_SERVER_CONNECT";
    public final static String WeatherContainer_ARRIVED = "com.nordicsemi.nrfUART.MARKER_ARRIVED";
    public final static String NOTIFICATION_ARRIVED = "com.nordicsemi.nrfUART.NOTIFICATION_ARRIVED";
    public final static String WeatherPlanet_ARRIVED = "com.nordicsemi.nrfUART.WeatherPlanet_ARRIVED";
    public final static String RecentWeather_ARRIVED = "com.nordicsemi.nrfUART.RecentWeather_ARRIVED";
    public final static String RecentAllWeather_ARRIVED = "com.nordicsemi.nrfUART.RecentAllWeather_ARRIVED";

    interface callBack {
        void callbackMethod(ArrayList<WeatherContainer> weathers);
    }

    public void setCallBack(callBack callback) {
        this.m_callback = callback;
    }

    class LocalBinder extends Binder {
        NetworkService getService() {
            return NetworkService.this;
        }
    }

    private Thread receiveData = new Thread() {
        @Override
        public void run() {
            super.run();
            while (isSocketConnected()) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof ArrayList) {
                        weathers = (ArrayList<WeatherContainer>) obj;
                        m_callback.callbackMethod(weathers);
                        broadcastUpdate(NetworkService.WeatherContainer_ARRIVED);
                    } else if (obj instanceof String) {

                        String data = (String) obj;
                        try {
                            JSONObject json = new JSONObject(data);
                            if (json.get("tag").equals("WeatherPlanet")) {
                                broadcastUpdate(NetworkService.WeatherPlanet_ARRIVED, data);
                            } else if (json.get("tag").equals("recentWeather")) {
                                broadcastUpdate(NetworkService.RecentWeather_ARRIVED, data);
                            } else if (json.get("tag").equals("recentAllWeather")) {
                                broadcastUpdate(NetworkService.RecentAllWeather_ARRIVED, data);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();


    }

    public void setting(String add, int port) throws IOException {
        this.dstAddress = add;
        this.dstPort = port;
        Thread t = new Thread() { //network 연결과 관련된 부분은 별도의 쓰레드에서 실행해야 하므로 생성
            @Override
            public void run() {
                Log.d("XXX", "서버접속");
                super.run();
                try {
                    socket = new Socket(dstAddress, dstPort);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());
                    broadcastUpdate(NetworkService.ACTION_SERVER_CONNECT);
                    receiveData.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, String notificationInfo) {
        final Intent intent = new Intent(action);
        intent.putExtra("data", notificationInfo);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public boolean isSocketConnected() {
        return this.socket.isConnected();
    }


    public void requestWeatherInfo(String request) {
        try {
            oos.writeObject(request);
            oos.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disConnectServer() {
        socket = null;
    }


}
