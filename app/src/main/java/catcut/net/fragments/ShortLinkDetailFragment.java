package catcut.net.fragments;

import androidx.fragment.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.ActionBar;
import catcut.net.MainActivity;
import catcut.net.R;
import catcut.net.network.NetworkAsync;
import catcut.net.network.entity.LinkStat;
import catcut.net.processing.ShortLinkDetailProcessing;
import catcut.net.tasks.ShortLinkDetailTask;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import static android.content.Context.CLIPBOARD_SERVICE;
import static catcut.net.tasks.ShortLinkDetailTask.CurrentTask.LoadQR;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShortLinkDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShortLinkDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShortLinkDetailFragment extends Fragment implements ShortLinkDetailProcessing {

    private static final String ARG_LINKSTAT = "linkstat";

    private LinkStat linkStat = new LinkStat();
    private OnFragmentInteractionListener mListener;
    NetworkAsync mNetworkAsync;

    ImageView qrCode;

    TextView shortlink;

    private ShortLinkDetailTask shortLinkDetailTask;

    public ShortLinkDetailFragment() {
        // Required empty public constructor
    }

    public static ShortLinkDetailFragment newInstance(LinkStat param) {
        ShortLinkDetailFragment fragment = new ShortLinkDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LINKSTAT, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            linkStat = (LinkStat) getArguments().getSerializable(ARG_LINKSTAT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        View view = inflater.inflate(R.layout.fragment_shortlink_detail, container, false);

        final String shortlinkText = "http://ccl.su/" + linkStat.shortlink;
        getActivity().setTitle(shortlinkText);

        shortlink = view.findViewById(R.id.link);
        shortlink.setText(linkStat.longurl);

        TextView date = view.findViewById(R.id.date);
        date.setText(linkStat.createtime);

        TextView previewCount = view.findViewById(R.id.preview_count);
        previewCount.setText(linkStat.count);

        TextView money = view.findViewById(R.id.money);
        money.setText(linkStat.money);


        Switch enableAd = view.findViewById(R.id.enable_ad);
        enableAd.setChecked(linkStat.advsurfing != null &&
                linkStat.advsurfing.equals("1"));

        enableAd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mNetworkAsync = new NetworkAsync(getActivity());
                mNetworkAsync.hideAdForUrl(linkStat.id, isChecked);
                mNetworkAsync.execute((Void) null);
            }
        });

        initShareButton(view, shortlinkText);
        initCopyButton(view, shortlinkText);
        initAd(view);
        initQrCode(view);

        return view;
    }

    void initQrCode(View view) {
        qrCode = view.findViewById(R.id.qr_code);
        qrCode.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Bitmap bitmap = ((BitmapDrawable) qrCode.getDrawable()).getBitmap();
//
//                String bitmapPath = MediaStore.Images.Media
//                        .insertImage(getContentResolver(), bitmap, "title", null);
//                Uri bitmapUri = Uri.parse(bitmapPath);
//
//                Intent fragmentIntent = new Intent(Intent.ACTION_SEND);
//                fragmentIntent.setType("image/png");
//                fragmentIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
//                startActivity(Intent.createChooser(fragmentIntent, "Share"));
            }
        });

        shortLinkDetailTask =
                new ShortLinkDetailTask(LoadQR, "https://catcut.net/qr100x100/" + linkStat.shortlink );

        shortLinkDetailTask.setTask(shortLinkDetailTask);
        shortLinkDetailTask.setActivity(getActivity());
        shortLinkDetailTask.setProcessing(ShortLinkDetailFragment.this);
        shortLinkDetailTask.execute((Void) null);

    }

    void initCopyButton(View view, final String shortlinkText) {
        ImageView copy = view.findViewById(R.id.copy);
        copy.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                String copyText = shortlinkText;
                ClipboardManager clipboard = (ClipboardManager) getActivity()
                        .getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("url", copyText);
                clipboard.setPrimaryClip(clip);


                Toast.makeText(getActivity(), getString(R.string.message_copied_to_clipboard),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    void initShareButton(View view, final String shortlinkText) {
        ImageView share = view.findViewById(R.id.share);
        share.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareBody = shortlinkText;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "CatCut Share Link");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.title_share_using)));
            }
        });

    }

    void initAd(View view) {
        if(MainActivity.isAdEnabled()) {

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

    @Override
    public void onProcessQRResponse(Bitmap bitmap) {

        if (bitmap != null)
            qrCode.setImageBitmap(bitmap);

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
