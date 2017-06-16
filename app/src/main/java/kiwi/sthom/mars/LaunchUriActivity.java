package kiwi.sthom.mars;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.microsoft.connecteddevices.ConnectedDevicesException;
import com.microsoft.connecteddevices.RemoteLaunchUriStatus;
import com.microsoft.connecteddevices.RemoteLauncher;
import com.microsoft.connecteddevices.RemoteSystem;
import com.microsoft.connecteddevices.RemoteSystemConnectionRequest;

public class LaunchUriActivity extends AppCompatActivity {
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
                }
            });
        } catch (ConnectedDevicesException ex) {
            Log.e("CDE", ex.getLocalizedMessage());
        }
    }

}
