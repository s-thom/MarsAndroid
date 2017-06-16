package kiwi.sthom.mars;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.connecteddevices.ConnectedDevicesException;
import com.microsoft.connecteddevices.IRemoteSystemDiscoveryListener;
import com.microsoft.connecteddevices.RemoteSystem;
import com.microsoft.connecteddevices.RemoteSystemDiscovery;
import com.microsoft.connecteddevices.RemoteSystemDiscoveryType;
import com.microsoft.connecteddevices.RemoteSystemDiscoveryTypeFilter;
import com.microsoft.connecteddevices.RemoteSystemKindFilter;
import com.microsoft.connecteddevices.RemoteSystemKinds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnDeviceSelectedListener}
 * interface.
 */
public class DeviceListFragment extends Fragment implements OnRefreshListener {
    private static final String ARG_COLUMN_COUNT = "column-count";

    private OnDeviceSelectedListener _listener;
    private SwipeRefreshLayout _swiper = null;
    private DeviceRecyclerViewAdapter _adapter = null;

    private List<RemoteSystem> _devices = null;
    private int _columnCount = 2;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DeviceListFragment() {
    }

    public static DeviceListFragment newInstance(int columnCount) {
        DeviceListFragment fragment = new DeviceListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            _columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);

        _swiper = (SwipeRefreshLayout) view;
        _swiper.setOnRefreshListener(this);

        // Set RecyclerView adapter and layout manager
        RecyclerView list = view.findViewById(R.id.list);
        Context context = view.getContext();
        if (_columnCount <= 1) {
            list.setLayoutManager(new LinearLayoutManager(context));
        } else {
            list.setLayoutManager(new GridLayoutManager(context, _columnCount));
        }
        _adapter = new DeviceRecyclerViewAdapter(_listener);
        list.setAdapter(_adapter);

        RemoteSystemDiscovery.Builder discBuilder = new RemoteSystemDiscovery.Builder()
            .setListener(new IRemoteSystemDiscoveryListener() {
                @Override
                public void onRemoteSystemAdded(RemoteSystem remoteSystem) {
                    _adapter.addDevice(remoteSystem);
                }

                @Override
                public void onRemoteSystemUpdated(RemoteSystem remoteSystem) {
                    _adapter.updateDevice(remoteSystem);
                }

                @Override
                public void onRemoteSystemRemoved(String s) {
                    _adapter.removeDevice(s);
                }

                @Override
                public void onComplete() {
                    // TODO: 16/06/2017 Something here?
                }
            });
            // TODO: 17/06/2017 filtering options?

        RemoteSystemDiscovery disc = null;

        try {
            disc = discBuilder.getResult();
        } catch (UnsatisfiedLinkError ex) {
            // If this fragment is created before the auth code is actually saved,
            // then lots of weird stuff happens. This is to help prevent weird stuff.
            getActivity().recreate();
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

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDeviceSelectedListener) {
            _listener = (OnDeviceSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnDeviceSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    @Override
    public void onRefresh() {
        // TODO: 16/06/2017 Do something on refresh
        _swiper.setRefreshing(false);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnDeviceSelectedListener {
        void OnDeviceSelected(RemoteSystem device);
    }
}
