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

public class SensorGyroscopeService extends Service implements SensorEventListener {

    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private DecimalFormat formatter = new DecimalFormat("0.00");
    public static final int GET_GYRO = 3;
    public static final int STOP_GYRO = 4;
    private final Messenger mMessenger = new Messenger(new SensorGyroscopeService.IncomingHandler());
    private String gyroscopeValues = "X00.00 Y00.00 Z00.00";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case GET_GYRO:
                    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                    sensorManager.registerListener(SensorGyroscopeService.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case STOP_GYRO:

                    if (sensorManager != null) {
                        sensorManager.unregisterListener(SensorGyroscopeService.this);
                        stopSelf();
                    }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void sendBroadcast() {
        Intent intent = new Intent("M_BROADCAST_R");
        intent.putExtra("GYROSCOPE", gyroscopeValues);
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
        float[] gyroscopeVal = event.values;
        gyroscopeValues = "";

        if (gyroscopeVal != null) {

            for (int i = 0; i < gyroscopeVal.length; i++) {
                String coordName;

                switch (i){
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
                gyroscopeValues += coordName + formatter.format(gyroscopeVal[i]) + " ";
            }
        }
        sendBroadcast();
    }
}
