package arktop.ru.sensors;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ru.arktop.services.SensorAccelService;
import ru.arktop.services.SensorBarometerService;
import ru.arktop.services.SensorGyroscopeService;
import ru.arktop.services.SensorMagnetometerService;

public class MainActivity extends AppCompatActivity {
    private Messenger mServicePressure;
    private Messenger mServiceMagnet;
    private Messenger mServiceGyro;
    private Messenger mServiceAccel;
    private boolean mBoundPressure = false;
    private boolean mBoundMagnet = false;
    private boolean mBoundGyro = false;
    private boolean mBoundAccel = false;
    private TextView pressureView;
    private TextView magnetView;
    private TextView gyroView;
    private TextView accelView;
    private SensorsBroadcastReceiver mBroadcastReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBroadcastReciever = new SensorsBroadcastReceiver();
        mBroadcastReciever.setActivity(this);
        setContentView(R.layout.activity_main);
        pressureView = (TextView) findViewById(R.id.pressure_view);
        magnetView = (TextView) findViewById(R.id.magnet_view);
        gyroView = (TextView) findViewById(R.id.gyro_view);
        accelView = (TextView) findViewById(R.id.accel_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public TextView getPressureView() {
        return pressureView;
    }

    public TextView getMagnetView() {
        return magnetView;
    }

    public TextView getGyroView() {
        return gyroView;
    }

    public TextView getAccelView() {
        return accelView;
    }

    public void getPressure(View view) {

        if (!mBoundPressure) {
            return;
        }
        Message msg = Message.obtain(null, SensorBarometerService.GET_PRESSURE, 0, 0);

        try {
            mServicePressure.send(msg);
        } catch (RemoteException e) {
            Log.e("MAIN", e.getMessage());
        }
    }

    public void stopPressure(View view) {

        if (!mBoundPressure) {
            return;
        }
        Message msg = Message.obtain(null, SensorBarometerService.STOP_PRESSURE, 0, 0);

        try {
            mServicePressure.send(msg);
        } catch (RemoteException e) {
            Log.e("MAIN", e.getMessage());
        }
    }

    public void getMagnet(View view) {

        if (!mBoundMagnet) {
            return;
        }
        Message msg = Message.obtain(null, SensorMagnetometerService.GET_MAGNET_FIELD, 0, 0);

        try {
            mServiceMagnet.send(msg);
        } catch (RemoteException e) {
            Log.e("MAIN", e.getMessage());
        }
    }

    public void stopMagnet(View view) {

        if (!mBoundMagnet) {
            return;
        }
        Message msg = Message.obtain(null, SensorMagnetometerService.STOP_MAGNET_FIELD, 0, 0);

        try {
            mServiceMagnet.send(msg);
        } catch (RemoteException e) {
            Log.e("MAIN", e.getMessage());
        }
    }

    public void getGyro(View view) {

        if (!mBoundGyro) {
            return;
        }
        Message msg = Message.obtain(null, SensorGyroscopeService.GET_GYRO, 0, 0);

        try {
            mServiceGyro.send(msg);
        } catch (RemoteException e) {
            Log.e("MAIN", e.getMessage());
        }
    }

    public void stopGyro(View view) {

        if (!mBoundGyro) {
            return;
        }
        Message msg = Message.obtain(null, SensorGyroscopeService.STOP_GYRO, 0, 0);

        try {
            mServiceGyro.send(msg);
        } catch (RemoteException e) {
            Log.e("MAIN", e.getMessage());
        }
    }


    public void getAccel(View view) {

        if (!mBoundAccel) {
            return;
        }
        Message msg = Message.obtain(null, SensorAccelService.GET_ACCEL, 0, 0);

        try {
            mServiceAccel.send(msg);
        } catch (RemoteException e) {
            Log.e("MAIN", e.getMessage());
        }
    }

    public void stopAccel(View view) {

        if (!mBoundAccel) {
            return;
        }
        Message msg = Message.obtain(null, SensorAccelService.STOP_ACCEL, 0, 0);

        try {
            mServiceAccel.send(msg);
        } catch (RemoteException e) {
            Log.e("MAIN", e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mBroadcastReciever, new IntentFilter("M_BROADCAST_R"));
        bindService(new Intent(this, SensorBarometerService.class), mConnectionPressure,
                Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, SensorMagnetometerService.class), mConnectionMagnet,
                Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, SensorGyroscopeService.class), mConnectionGyro,
                Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, SensorAccelService.class), mConnectionAccel,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReciever);

        if (mBoundPressure) {
            unbindService(mConnectionPressure);
            mBoundPressure = false;
        }

        if (mBoundMagnet) {
            unbindService(mConnectionMagnet);
            mBoundMagnet = false;
        }

        if (mBoundGyro) {
            unbindService(mConnectionGyro);
            mBoundGyro = false;
        }

        if (mBoundAccel) {
            unbindService(mConnectionAccel);
            mBoundAccel = false;
        }
    }

    private ServiceConnection mConnectionAccel = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mServiceAccel = new Messenger(service);
            mBoundAccel = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mServiceAccel = null;
            mBoundAccel = false;
        }
    };

    private ServiceConnection mConnectionPressure = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mServicePressure = new Messenger(service);
            mBoundPressure = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mServicePressure = null;
            mBoundPressure = false;
        }
    };


    private ServiceConnection mConnectionMagnet = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            mServiceMagnet = new Messenger(service);
            mBoundMagnet = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mServiceMagnet = null;
            mBoundMagnet = false;
        }
    };

    private ServiceConnection mConnectionGyro = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            mServiceGyro = new Messenger(service);
            mBoundGyro = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mServiceGyro = null;
            mBoundGyro = false;
        }
    };
}
