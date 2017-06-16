package kiwi.sthom.mars;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.IBinder;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import android.util.Log;

import com.microsoft.connecteddevices.RemoteSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceChooserService extends ChooserTargetService {
    static final String KEY_DEVICE_ID = "kiwi.sthom.mars.DEVICE_ID";
    @Override
    public List<ChooserTarget> onGetChooserTargets(ComponentName componentName, IntentFilter intentFilter) {
        // The list of Direct Share items. The system will show the items the way they are sorted
        // in this list.
        ArrayList<ChooserTarget> targets = new ArrayList<>();

        for (RemoteSystem device : DeviceStorage.getAll()) {
            Bundle extras = new Bundle();
            extras.putString(KEY_DEVICE_ID, device.getId());

            targets.add(new ChooserTarget(
                device.getDisplayName(),
                null,
                0.5f,
                componentName,
                extras
            ));

            Log.d("SHARE", "Added " + device.getDisplayName() + " to list");
        }

        return targets;
    }
}
