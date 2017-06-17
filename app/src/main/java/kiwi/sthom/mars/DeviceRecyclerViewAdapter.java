package kiwi.sthom.mars;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.connecteddevices.RemoteSystem;

import kiwi.sthom.mars.DeviceListFragment.OnDeviceSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RemoteSystem} and makes a call to the
 * specified {@link OnDeviceSelectedListener}.
 */
public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> {

    private List<RemoteSystem> _devices = new ArrayList<>();
    private final DeviceListFragment.OnDeviceSelectedListener _listener;

    public DeviceRecyclerViewAdapter(OnDeviceSelectedListener listener) {
        _listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_item_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = _devices.get(position);
        holder.mIdView.setText(_devices.get(position).getDisplayName());
        holder.mContentView.setText(_devices.get(position).getKind().getValue());

        holder.mView.setOnClickListener((View v) -> {
            if (null != _listener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                _listener.OnDeviceSelected(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (_devices == null) {
            return 0;
        }
        return _devices.size();
    }

    void addDevice(RemoteSystem device) {
        _devices.add(device);
        notifyDataSetChanged();
    }

    void removeDevice(String id) {
        RemoteSystem toRemove = null;
        for (RemoteSystem device : _devices) {
            if (device.getId() == id) {
                toRemove = device;
                break;
            }
        }
        if (toRemove != null) {
            _devices.remove(toRemove);
            notifyDataSetChanged();
        }
    }

    void updateDevice(RemoteSystem device) {
        int index = -1;
        for (RemoteSystem d : _devices) {
            if (d.getId().equals(device.getId())) {
                index = _devices.indexOf(d);
                break;
            }
        }
        if (index != -1) {
            _devices.set(index, device);
            notifyItemChanged(index);
        }
    }

    void setDevices(List<RemoteSystem> devices) {
        _devices = devices;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public RemoteSystem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
