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
import android.util.Log;

import java.text.DecimalFormat;

public class SensorMagnetometerService extends Service implements SensorEventListener {

    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private DecimalFormat formatter = new DecimalFormat("0.00");
    public static final int GET_MAGNET_FIELD = 2;
    public static final int STOP_MAGNET_FIELD = 5;
    private final Messenger mMessenger = new Messenger(new SensorMagnetometerService.IncomingHandler());
    private String magnetometerValue = "X00.00 Y00.00 Z00.00";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case GET_MAGNET_FIELD:
                    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                    sensorManager.registerListener(SensorMagnetometerService.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    SensorMagnetometerService.this.sendBroadcast();
                    break;
                case STOP_MAGNET_FIELD:

                    if (sensorManager != null) {
                        sensorManager.unregisterListener(SensorMagnetometerService.this);
                        stopSelf();
                    }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void sendBroadcast() {
        Intent intent = new Intent("M_BROADCAST_R");
        intent.putExtra("MAGNET_FIELD", magnetometerValue);
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
        float[] magnetField = event.values;
        magnetometerValue = "";

        if (magnetField != null) {

            for (int i = 0; i < magnetField.length; i++) {
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
                magnetometerValue += coordName + formatter.format(magnetField[i]) + " ";
            }
        }
        sendBroadcast();
    }
}
