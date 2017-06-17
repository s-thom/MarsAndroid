package kiwi.sthom.mars;

import com.microsoft.connecteddevices.RemoteSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static store for {@link RemoteSystem}, since they can not be passed though bundles
 */
class DeviceStorage {
    private static Map<String, RemoteSystem> _devices = new HashMap<>();

    static boolean addDevice(RemoteSystem device) {
        if (_devices.containsKey(device.getId())) {
            return false;
        } else {
            _devices.put(device.getId(), device);
            return true;
        }
    }

    static void removeDevice(String id) {
        _devices.remove(id);
    }

    static RemoteSystem getDevice(String id) {
        if (_devices.containsKey(id)) {
            return _devices.get(id);
        } else {
            return null;
        }
    }

    static List<RemoteSystem> getAll() {
        return new ArrayList<>(_devices.values());
    }

    static int getDrawableId(RemoteSystem device) {
        switch (device.getKind()) {
            case DESKTOP:
                return R.drawable.ic_desktop_windows_black_24dp;
            case PHONE:
                return R.drawable.ic_smartphone_black_24dp;
            case XBOX:
                return R.drawable.ic_xbox_black_24dp;
            case HUB:
            case HOLOGRAPHIC:
                return R.drawable.ic_devices_other_black_24dp;
            case UNKNOWN:
            default:
                return R.drawable.ic_help_circle_black_24dp;
        }
    }
}
