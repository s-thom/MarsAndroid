package kiwi.sthom.mars;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.microsoft.connecteddevices.ConnectedDevicesException;
import com.microsoft.connecteddevices.RemoteLaunchUriStatus;
import com.microsoft.connecteddevices.RemoteLauncher;
import com.microsoft.connecteddevices.RemoteSystem;
import com.microsoft.connecteddevices.RemoteSystemConnectionRequest;

public class LaunchUriActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if (i.getExtras() == null) {
            Log.d("URI", "No extras found");
            finish();
        }

        RemoteSystem device = DeviceStorage.getDevice(i.getStringExtra(DeviceChooserService.KEY_DEVICE_ID));
        String url = i.getStringExtra(Intent.EXTRA_TEXT);
        String pageName = i.getStringExtra(Intent.EXTRA_SUBJECT);

        RemoteSystemConnectionRequest connReq = new RemoteSystemConnectionRequest(device);

        try {
            RemoteLauncher.LaunchUriAsync(connReq, url, status -> {
                if (status == RemoteLaunchUriStatus.SUCCESS) {
                    runOnUiThread(() -> {
                        String message = getString(R.string.send_success, pageName, device.getDisplayName());
                        Toast
                            .makeText(this, message, Toast.LENGTH_LONG)
                            .show();
                    });
                } else {
                    runOnUiThread(() -> {
                        String message = getString(R.string.send_error, device.getDisplayName());
                        Toast
                            .makeText(this, message, Toast.LENGTH_LONG)
                            .show();
                    });
                }

                finish();
            });
        } catch (ConnectedDevicesException ex) {
            Log.e("CDE", ex.getLocalizedMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        finish();
    }
}
