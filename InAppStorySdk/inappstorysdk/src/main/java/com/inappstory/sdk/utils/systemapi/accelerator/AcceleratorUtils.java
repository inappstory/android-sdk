package com.inappstory.sdk.utils.systemapi.accelerator;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.inappstory.sdk.core.IASCore;

import java.util.HashSet;
import java.util.Set;

public class AcceleratorUtils implements IAcceleratorUtils {
    private final Set<IAcceleratorSubscriber> subscribers = new HashSet<>();
    private final Object lock = new Object();
    private final IASCore core;

    public AcceleratorUtils(IASCore core) {
        this.core = core;
    }


    @Override
    public void init(double frequency, IAcceleratorInitCallback callback) {
        try {
            Context context = core.appContext();
            double localFrequency = frequency == 0 ? 30 : frequency;
            double delay = 1000000 / localFrequency;
            if (delay < 10) delay = 10;
            SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
            sensorManager.registerListener(
                    listener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                    (int) delay
            );
            callback.onSuccess();
        } catch (Exception e) {
            callback.onError("InitError", e.getMessage());
        }
    }

    @Override
    public void subscribe(IAcceleratorSubscriber subscriber) {
        synchronized (lock) {
            subscribers.add(subscriber);
        }
    }

    @Override
    public void unsubscribe(IAcceleratorSubscriber subscriber) {
        synchronized (lock) {
            subscribers.remove(subscriber);
        }
    }

    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                Set<IAcceleratorSubscriber> localSubscribers = new HashSet<>();
                synchronized (lock) {
                    localSubscribers.addAll(subscribers);
                }
                sendSensorEvent(localSubscribers, event);
            }
        }
    };


    private void sendSensorEvent(Set<IAcceleratorSubscriber> subscriberList, SensorEvent event) {
        for (IAcceleratorSubscriber subscriber : subscriberList) {
            subscriber.onEvent(event.values[0], event.values[1], event.values[2]);
        }
    }
}
