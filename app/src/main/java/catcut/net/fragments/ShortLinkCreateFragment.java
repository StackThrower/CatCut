package catcut.net.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import catcut.net.MainActivity;
import catcut.net.R;
import catcut.net.helpers.KeyboardHelper;
import catcut.net.listener.KeyboardEnterListener;
import catcut.net.processing.ShortLinkProcessing;
import catcut.net.processing.ProgressCallBack;
import catcut.net.tasks.ShortLinkTask;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ShortLinkCreateFragment extends Fragment implements ProgressCallBack, ShortLinkProcessing {


    private static final String ARG_URL = "url";


    ShortLinkTask shortLinkTask;
    private Button mShareLinkButton;
    private Button mGenLinkButton;
    private EditText urlToEncode;
    private OnFragmentInteractionListener mListener;
    private TextView mGenLinkState;
    private View mProgressView;
    private String url;
    private AdView mAdView;

    final Pattern VALID_URL_REGEX =
            Pattern.compile("(?:http(s)?:\\/\\/).+", Pattern.CASE_INSENSITIVE);

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShortLinkCreateFragment.
     */
    public static ShortLinkCreateFragment newInstance(String url) {
        ShortLinkCreateFragment fragment = new ShortLinkCreateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);

        fragment.setArguments(args);
        return fragment;
    }

    public ShortLinkCreateFragment() {
        url = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            url = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_shortlink_create, container, false);

        mGenLinkButton = view.findViewById(R.id.generate_link_button);
        mShareLinkButton = view.findViewById(R.id.share_link_button);
        urlToEncode = view.findViewById(R.id.create_link_edit_text);
        mGenLinkState = view.findViewById(R.id.create_link_edit_label);
        mProgressView = view.findViewById(R.id.create_link_progressbar);
        mAdView = view.findViewById(R.id.adView);

        if (url != null && !url.isEmpty())
            urlToEncode.setText(url);
        else
            urlToEncode.setText("");

        urlToEncode.setOnKeyListener(new KeyboardEnterListener(getActivity()));
        urlToEncode.setOnFocusChangeListener(new KeyboardEnterListener(getActivity()));

        mGenLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                urlToEncode.setError(null);

                String url = "" + urlToEncode.getText().toString(); // "" added in order to avoid SpannableString exception to String
                if (TextUtils.isEmpty(url)) {
                    urlToEncode.setError(getString(R.string.error_field_required));

                } else if (!isUrlValid(url)) {
                    urlToEncode.setError(getString(R.string.error_invalid_url));

                } else {
                    KeyboardHelper.hideKeyBoard(view, getActivity());

                    Bundle params = new Bundle();
                    params.putString("urlToEncode", url);
                    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
                    mFirebaseAnalytics.logEvent("create_short_link", params);


                    mGenLinkButton.setVisibility(View.GONE);
                    urlToEncode.setVisibility(View.GONE);
                    mGenLinkState.setVisibility(View.GONE);
                    mShareLinkButton.setVisibility(View.GONE);
                    mAdView.setVisibility(View.GONE);

                    showProgress(true);

                    shortLinkTask =
                            new ShortLinkTask(ShortLinkTask.CurrentTask.EncodeShortLink, url);

                    shortLinkTask.setTask(shortLinkTask);
                    shortLinkTask.setActivity(getActivity());
                    shortLinkTask.setProcessing(ShortLinkCreateFragment.this);
                    shortLinkTask.setShowProgressCallback(ShortLinkCreateFragment.this);

                    shortLinkTask.execute((Void) null);
                }
            }
        });

        mShareLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shareBody = (String) mGenLinkState.getText();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.title_share_using));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.title_share_using)));
            }
        });

        setupActionBar();
        initAd(view);

        return view;
    }

    void initAd(View view) {
        if (MainActivity.isAdEnabled()) {
            AdView mAdView = view.findViewById(R.id.adView);
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.

                    Bundle params = new Bundle();
                    FirebaseAnalytics mFirebaseAnalytics =
                            FirebaseAnalytics.getInstance(getActivity());
                    mFirebaseAnalytics.logEvent("ad_load", params);

                }


                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                }

                @Override
                public void onAdOpened() {
                    Bundle params = new Bundle();
                    FirebaseAnalytics mFirebaseAnalytics =
                            FirebaseAnalytics.getInstance(getActivity());
                    mFirebaseAnalytics.logEvent("ad_opened", params);
                }


                public void onAdLeftApplication() {
                    Bundle params = new Bundle();
                    FirebaseAnalytics mFirebaseAnalytics =
                            FirebaseAnalytics.getInstance(getActivity());
                    mFirebaseAnalytics.logEvent("ad_left_app", params);
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
            mAdView.loadAd(adRequest);

        }
    }

    private boolean isUrlValid(String email) {
        Matcher matcher = VALID_URL_REGEX.matcher(email);
        return matcher.find();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void setupActionBar() {
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onShowProgress(boolean show) {
        showProgress(show);
    }


    @Override
    public void onProcessShortLinkResponse(final String shortUrl) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                mGenLinkState.setText(shortUrl);
                urlToEncode.setText("");
                url = null;

                mShareLinkButton.setVisibility(View.VISIBLE);
                mGenLinkState.setVisibility(View.VISIBLE);
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }
}
