package ru.arktop.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class SensorAccelService extends Service implements SensorEventListener {

    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private DecimalFormat formatter = new DecimalFormat("0.00");
    public static final int GET_ACCEL = 7;
    public static final int STOP_ACCEL = 8;
    private final Messenger mMessenger = new Messenger(new SensorAccelService.IncomingHandler());
    private int countOfMesure = 1;
    private final int maxCountOfMesure = 8;
    private Map<Integer, float[]> accelMap;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case GET_ACCEL:
                    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    sensorManager.registerListener(SensorAccelService.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    accelMap = new HashMap<>();
                    countOfMesure = 0;
                    break;
                case STOP_ACCEL:

                    if (sensorManager != null) {
                        sensorManager.unregisterListener(SensorAccelService.this);
                        stopSelf();
                        countOfMesure = 0;
                        accelMap = new HashMap<>();
                    }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void sendBroadcast(String accelValues) {
        Intent intent = new Intent("M_BROADCAST_R");
        intent.putExtra("ACCELEROMETER", accelValues);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] accelVal = event.values;
        accelMap.put(countOfMesure, accelVal);
        countOfMesure++;

        if(countOfMesure == maxCountOfMesure) {
            String accelValues = "";

            if (accelMap != null && !accelMap.isEmpty()) {
                Float xMesure = 0F;
                Float yMesure = 0F;
                Float zMesure = 0F;

                for (Integer key:accelMap.keySet()) {
                    xMesure += accelMap.get(key)[0];
                    yMesure += accelMap.get(key)[1];
                    zMesure += accelMap.get(key)[2];
                }
                accelVal = new float[]{xMesure/countOfMesure, yMesure/countOfMesure, zMesure/countOfMesure};

                for (int i = 0; i < accelVal.length; i++) {
                    String coordName;

                    switch (i) {
                        case 0:
                            coordName = "X";
                            break;
                        case 1:
                            coordName = "Y";
                            break;
                        case 2:
                            coordName = "Z";
                            break;
                        default:
                            coordName = "";
                    }
                    accelValues += coordName + formatter.format(accelVal[i]) + " ";
                }
            }
            accelMap = new HashMap<>();
            countOfMesure = 0;
            sendBroadcast(accelValues);
        }
    }
}

