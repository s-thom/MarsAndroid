package kiwi.sthom.mars;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.microsoft.connecteddevices.ConnectedDevicesException;
import com.microsoft.connecteddevices.RemoteLaunchUriStatus;
import com.microsoft.connecteddevices.RemoteLauncher;
import com.microsoft.connecteddevices.RemoteSystem;
import com.microsoft.connecteddevices.RemoteSystemConnectionRequest;

public class LaunchUriActivity extends Activity implements DeviceListFragment.OnDeviceSelectedListener {
    String _url = null;
    String _title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if (i.getExtras() == null) {
            Log.d("URI", "No extras found");
            finish();
        }
        if (!i.hasExtra(Intent.EXTRA_TEXT)) {
            finish();
        }

        _url = i.getStringExtra(Intent.EXTRA_TEXT);

        if (i.hasExtra(Intent.EXTRA_SUBJECT)) {
            _title = i.getStringExtra(Intent.EXTRA_SUBJECT);
        }

        if (i.hasExtra(DeviceChooserService.KEY_DEVICE_ID)) {
            sendUrl(
                DeviceStorage.getDevice(i.getStringExtra(DeviceChooserService.KEY_DEVICE_ID)),
                _url,
                _title
            );
        } else {
            // Show dialog
            showDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void OnDeviceSelected(RemoteSystem device) {
        sendUrl(
            device,
            _url,
            _title
        );
    }

    void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_device_list_dialog, null);
        // Set RecyclerView adapter and layout manager

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        DeviceRecyclerViewAdapter adapter = new DeviceRecyclerViewAdapter(this);
        adapter.setDevices(DeviceStorage.getAll());

        RecyclerView list =  (RecyclerView) view;
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        new AlertDialog.Builder(this)
            .setTitle(R.string.send_choose_destination)
            .setView(view)
            .setCancelable(true)
            .setOnCancelListener((DialogInterface d) ->
                this.finish()
            )
            .create()
            .show();
    }

    void sendUrl(RemoteSystem device, String url, String pageName) {
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
            });
        } catch (ConnectedDevicesException ex) {
            Log.e("CDE", ex.getLocalizedMessage());
        }

        finish();
    }
}
