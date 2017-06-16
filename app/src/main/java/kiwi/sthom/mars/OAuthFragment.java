package kiwi.sthom.mars;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnOAuthCodeListener} interface
 * to handle interaction events.
 * Use the {@link OAuthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OAuthFragment extends Fragment {
    private static final String REDIRECT_URI = "https://login.live.com/oauth20_desktop.srf";

    private static final String ARG_OAUTH_URL = "oauth-url";

    private String _oauthUrl;

    private OnOAuthCodeListener _listener;

    public OAuthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url URL to load initially
     * @return A new instance of fragment OAuthFragment.
     */
    public static OAuthFragment newInstance(String url) {
        OAuthFragment fragment = new OAuthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OAUTH_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _oauthUrl = getArguments().getString(ARG_OAUTH_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_oauth, container, false);

        WebView wv = v.findViewById(R.id.oauth_webview);
        wv.setWebChromeClient(new WebChromeClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);

        // Load OAuth URL
        WebViewClient client = new WebViewClient() {
            // Make sure we don't send the auth code out twice
            boolean done = false;

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.startsWith(REDIRECT_URI)) {
                    // extract the auth code from the url
                    Uri uri = Uri.parse(url);
                    String code = uri.getQueryParameter("code");
                    String error = uri.getQueryParameter("error");

                    if (code != null && !done) {
                        done = true;
                        onAuthCodeFetched(code);
                    } else if (error != null) {
                        // Handle error case
                        // For now, just log
                    }
                }
            }
        };
        wv.setWebViewClient(client);

        wv.loadUrl(_oauthUrl);

        return v;
    }

    public void onAuthCodeFetched(String code) {
        if (_listener != null) {
            _listener.onOAuthCode(code);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOAuthCodeListener) {
            _listener = (OnOAuthCodeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnOAuthCodeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnOAuthCodeListener {
        void onOAuthCode(String string);
    }
}
