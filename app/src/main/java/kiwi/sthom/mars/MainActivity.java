package kiwi.sthom.mars;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.microsoft.connecteddevices.IAuthCodeProvider;
import com.microsoft.connecteddevices.IPlatformInitializationHandler;
import com.microsoft.connecteddevices.Platform;
import com.microsoft.connecteddevices.PlatformInitializationStatus;

public class MainActivity extends AppCompatActivity
    implements OAuthFragment.OnOAuthCodeListener
{
    private static final int STATE_INIT = 974;
    private static final int STATE_OAUTH = 662;

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

            }

            @Override
            public void onError(PlatformInitializationStatus platformInitializationStatus) {

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
            default:
                Log.d("STATE", "Unknown state " + newState);
        }

        _state = newState;
    }
}
