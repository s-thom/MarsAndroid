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

    interface OnDevicesUpdatedListener {
        void onDevicesUpdated(List<RemoteSystem> devices);
    }
}
