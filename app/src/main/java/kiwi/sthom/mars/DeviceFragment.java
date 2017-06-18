package kiwi.sthom.mars;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.connecteddevices.RemoteSystem;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceFragment extends Fragment {
    private static final String ARG_DEVICE = "device";

    private RemoteSystem _device;


    public DeviceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param device Device to display
     * @return A new instance of fragment DeviceFragment.
     */
    public static DeviceFragment newInstance(RemoteSystem device) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DEVICE, device.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String id = getArguments().getString(ARG_DEVICE);
            _device = DeviceStorage.getDevice(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_device, container, false);

        TextView title = v.findViewById(R.id.device_name);
        title.setText(_device.getDisplayName());

        return v;
    }

}
