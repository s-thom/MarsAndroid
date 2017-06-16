package kiwi.sthom.mars;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.microsoft.connecteddevices.IAuthCodeProvider;
import com.microsoft.connecteddevices.IPlatformInitializationHandler;
import com.microsoft.connecteddevices.Platform;
import com.microsoft.connecteddevices.PlatformInitializationStatus;
import com.microsoft.connecteddevices.RemoteSystem;

public class MainActivity extends AppCompatActivity implements
    OAuthFragment.OnOAuthCodeListener,
    DeviceListFragment.OnDeviceSelectedListener
{
    private static final int STATE_INIT = 974;
    private static final int STATE_OAUTH = 662;
    private static final int STATE_DEVICES = 46;

    private DeviceListFragment _deviceFrag = null;

    private Platform.IAuthCodeHandler _authHandler = null;

    private int _state = STATE_INIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.main_nav);
        navigation.setOnNavigationItemSelectedListener((@NonNull MenuItem item) -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
//                mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
//                mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        });

        Platform.initialize(
            getApplicationContext(),
            new IAuthCodeProvider() {
                @Override
                public void fetchAuthCodeAsync(String s, Platform.IAuthCodeHandler iAuthCodeHandler) {
                    _authHandler = iAuthCodeHandler;
                    setFragment(OAuthFragment.newInstance(s));
                    setNavVisible(false);
                    setState(STATE_OAUTH);
                }

                @Override
                public String getClientId() {
                    return BuildConfig.MS_CLIENT_ID;
                }
            },
            new IPlatformInitializationHandler() {
            @Override
            public void onDone() {
                Log.d("P", "Platform done");

                if (_deviceFrag == null) {
                    _deviceFrag = DeviceListFragment.newInstance(2);
                }

                setState(STATE_DEVICES);
            }

            @Override
            public void onError(PlatformInitializationStatus platformInitializationStatus) {
                switch (platformInitializationStatus) {
                    case PLATFORM_FAILURE:
                        Log.e("P", "Error initializing platform");
                    case TOKEN_ERROR:
                        Log.e("P", "Error refreshing tokens");
                }
            }
        });
    }

    @Override
    public void onOAuthCode(String string) {
        if (_authHandler != null) {
            _authHandler.onAuthCodeFetched(string);
            // Reset auth handler, in case anything happens
            _authHandler = null;

            setNavVisible(true);
        } else {
            Log.d("OAUTH", "Got auth code, but handler was null");
        }
    }

    @Override
    public void OnDeviceSelected(RemoteSystem device) {
        Log.d("TEST", device.getDisplayName());
    }

    private void setNavVisible(boolean visibility) {
        View nav = findViewById(R.id.main_nav);
        if (visibility) {
            nav.setVisibility(View.VISIBLE);
        } else {
            nav.setVisibility(View.GONE);
        }
    }

    private void setFragment(Fragment frag) {
        getFragmentManager()
            .beginTransaction()
            .replace(R.id.main_frag_container, frag)
            .commit();
    }

    private void setState(int newState) {
        if (_state == newState) {
            return;
        }

        switch (newState) {
            case STATE_OAUTH:
                setNavVisible(false);
                break;
            case STATE_DEVICES:
                // TODO: 16/06/2017 Decide column count more intelligently
                setFragment(_deviceFrag);
                break;
            default:
                Log.d("STATE", "Unknown state " + newState);
        }

        _state = newState;
    }
}
