package kiwi.sthom.mars;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends PreferenceFragment {
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case "prefs_sign_out":
                new AlertDialog.Builder(getContext())
                    .setTitle(R.string.prefs_sing_out_dialog_title)
                    .setMessage(R.string.prefs_sign_out_dialog_content)
                    .setPositiveButton(R.string.prefs_sign_out_dialog_action, (DialogInterface dialogInterface, int i) -> {
                        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                        Intent intent = new Intent()
                            .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(uri);
                        startActivity(intent);
                    })
                    .setNegativeButton(R.string.prefs_sign_out_dialog_cancel, (DialogInterface dialogInterface, int i) -> {})
                    .setCancelable(true)
                    .create()
                    .show();
                return true;
            default:
                return super.onPreferenceTreeClick(preferenceScreen, preference);
        }


    }
}
