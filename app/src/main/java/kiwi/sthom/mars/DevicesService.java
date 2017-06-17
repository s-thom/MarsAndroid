package kiwi.sthom.mars;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.microsoft.connecteddevices.ConnectedDevicesException;
import com.microsoft.connecteddevices.IAuthCodeProvider;
import com.microsoft.connecteddevices.IPlatformInitializationHandler;
import com.microsoft.connecteddevices.IRemoteSystemDiscoveryListener;
import com.microsoft.connecteddevices.Platform;
import com.microsoft.connecteddevices.PlatformInitializationStatus;
import com.microsoft.connecteddevices.RemoteSystem;
import com.microsoft.connecteddevices.RemoteSystemDiscovery;

public class DevicesService extends Service {
    // Not the best way to do this, need to investigate later
    private static boolean _isRunning = false;

    public DevicesService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _isRunning = true;
        Log.d("DEVSERV", "Device service is running");


        Platform.initialize(
            getApplicationContext(),
            new IAuthCodeProvider() {
                @Override
                public void fetchAuthCodeAsync(String s, Platform.IAuthCodeHandler iAuthCodeHandler) {
                    throw new UnsupportedOperationException("Service can't initialize");
                }

                @Override
                public String getClientId() {
                    return BuildConfig.MS_CLIENT_ID;
                }
            },
            new IPlatformInitializationHandler() {
                @Override
                public void onDone() {
                    Log.d("DEVSERVP", "Platform done");

                    startReceiver();
                }

                @Override
                public void onError(PlatformInitializationStatus platformInitializationStatus) {
                    switch (platformInitializationStatus) {
                        case PLATFORM_FAILURE:
                            Log.e("P", "Error initializing platform");
                        case TOKEN_ERROR:
                            Log.e("P", "Error refreshing tokens");
                    }

                    stopSelf();
                }
            });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        _isRunning = false;
    }

    void startReceiver() {
        RemoteSystemDiscovery.Builder discBuilder = new RemoteSystemDiscovery.Builder()
            .setListener(new IRemoteSystemDiscoveryListener() {
                @Override
                public void onRemoteSystemAdded(RemoteSystem remoteSystem) {
                    DeviceStorage.addDevice(remoteSystem);
                }

                @Override
                public void onRemoteSystemUpdated(RemoteSystem remoteSystem) {
                    // TODO: 17/06/2017 Something here?
                    Log.d("P", "Device " + remoteSystem.getDisplayName() + " updated");
                }

                @Override
                public void onRemoteSystemRemoved(String s) {
                    DeviceStorage.removeDevice(s);
                }

                @Override
                public void onComplete() {
                    // TODO: 16/06/2017 Something here?
                    Log.d("P", "Device search complete");
                }
            });

        RemoteSystemDiscovery disc = null;

        try {
            disc = discBuilder.getResult();
        } catch (UnsatisfiedLinkError ex) {
            // If this fragment is created before the auth code is actually saved,
            // then lots of weird stuff happens. This is to help prevent weird stuff.
            stopSelf();
        }

        try {
            if (disc != null) {
                disc.start();
            } else {
                Log.d("ERR", "Discovery was null");
            }
        } catch (ConnectedDevicesException ex) {
            Log.e("CDE", ex.getLocalizedMessage());
        }
    }

    static boolean isRunning() {
        return _isRunning;
    }
}
