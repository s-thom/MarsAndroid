package kiwi.sthom.mars;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        RemoteSystem device = _devices.get(position);

        holder.item = device;
        holder.nameView.setText(device.getDisplayName());
        holder.typeView.setText(device.getKind().getValue());
        holder.imageView.setImageResource(DeviceStorage.getDrawableId(device));

        holder.root.setOnClickListener((View v) -> {
            if (null != _listener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                _listener.OnDeviceSelected(holder.item);
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

    void setDevices(List<RemoteSystem> devices) {
        _devices = devices;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View root;
        final TextView nameView;
        final TextView typeView;
        final ImageView imageView;
        RemoteSystem item;

        ViewHolder(View view) {
            super(view);
            root = view;
            nameView = view.findViewById(R.id.device_name);
            typeView = view.findViewById(R.id.device_type);
            imageView = view.findViewById(R.id.device_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + typeView.getText() + "'";
        }
    }
}
