package arktop.ru.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Arcady on 04.03.2017.
 */

public class SensorsBroadcastReceiver extends BroadcastReceiver {

    MainActivity activity;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getAction().equals("M_BROADCAST_R")) {

            String pressure = intent.getStringExtra("PRESSURE");
            String gyroValue = intent.getStringExtra("GYROSCOPE");
            String mField = intent.getStringExtra("MAGNET_FIELD");
            String accelField = intent.getStringExtra("ACCELEROMETER");

            if (pressure != null && activity != null) {
                activity.getPressureView().setText(pressure + " mmhg");
            }

            if (mField != null && activity != null) {
                activity.getMagnetView().setText(mField);
            }

            if (gyroValue != null && activity != null) {
                activity.getGyroView().setText(gyroValue);
            }

            if (accelField != null && activity != null) {
                activity.getAccelView().setText(accelField);
            }
        }
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }
}