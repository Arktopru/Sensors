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
import java.util.List;

public class SensorBarometerService extends Service implements SensorEventListener {

    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private DecimalFormat formatter = new DecimalFormat("0.00");
    public static final int GET_PRESSURE = 1;
    public static final int STOP_PRESSURE = 6;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private String pressureInMmhg = "000.00";

    @Override
    public void onCreate(){
        super.onCreate();
    }

    public class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case GET_PRESSURE:
                    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
                    sensorManager.registerListener(SensorBarometerService.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case STOP_PRESSURE:

                    if(sensorManager != null) {
                        sensorManager.unregisterListener(SensorBarometerService.this);
                        stopSelf();
                    }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void sendBroadcast(){
        Intent intent = new Intent("M_BROADCAST_R");
        intent.putExtra("PRESSURE", pressureInMmhg);
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
        float[] pressures = event.values;

        if (pressures != null && pressures[0] > 0) {
            pressureInMmhg = formatter.format(pressures[0] * 0.750062);
        }

        SensorBarometerService.this.sendBroadcast();
    }
}
