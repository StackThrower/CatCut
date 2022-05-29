package catcut.net.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import catcut.net.MainActivity;
import catcut.net.R;
import catcut.net.listener.KeyboardEnterListener;
import catcut.net.modes.SurfSiteDetailMode;
import catcut.net.network.entity.SurfSite;
import catcut.net.network.entity.SurfSiteListItem;
import catcut.net.processing.FragmentHomeInteraction;
import catcut.net.processing.SurfSiteDetailProcessing;
import catcut.net.processing.ProgressCallBack;
import catcut.net.tasks.SurfSiteDetailTask;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import static catcut.net.modes.SurfSiteDetailMode.ADD;
import static catcut.net.modes.SurfSiteDetailMode.EDIT;
import static catcut.net.tasks.SurfSiteDetailTask.CurrentTask.AddSurfSite;
import static catcut.net.tasks.SurfSiteDetailTask.CurrentTask.EditSurfSite;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class SurfSiteDetailFragment extends Fragment
        implements ProgressCallBack, SurfSiteDetailProcessing {

    private static final String ARG_SURFSITELISTITEM = "surfsitelistitem";
    private static final String ARG_SURFSITEDETAILMODE = "surfsitedetailmode";


    private View mProgressView;
    private SurfSiteDetailTask surfSiteDetailSubmitTask;
    private SurfSiteDetailTask surfSiteDetailStartPriceTask;
    private SurfSiteDetailTask surfSiteDetailAudienceTask;

    private TextView onePriceView;
    private TextView extendedAuditoryDescView;
    private TextView audienceView;
    private EditText url;
    private TextView budgedText;

    private Spinner urlTypeSpin;
    private Spinner countrySpin;
    private Spinner geoTargetTypeSpin;
    private Spinner budgedTypeSpin;

    Switch enabled;

    private ImageButton decAudienceText;
    private ImageButton incAudienceText;

    private SurfSite surfSite = new SurfSite();
    private SurfSiteListItem surfSiteListItem = new SurfSiteListItem();

    private SurfSiteDetailMode surfSiteDetailMode;

    public SurfSiteDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SurfSiteDetailFragment.
     */
    public static SurfSiteDetailFragment newInstance(SurfSiteListItem surfSiteListItem, SurfSiteDetailMode mode) {
        SurfSiteDetailFragment fragment = new SurfSiteDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SURFSITELISTITEM, surfSiteListItem);
        args.putSerializable(ARG_SURFSITEDETAILMODE, mode);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            surfSiteListItem = (SurfSiteListItem) getArguments().getSerializable(ARG_SURFSITELISTITEM);
            surfSiteDetailMode = (SurfSiteDetailMode)getArguments().getSerializable(ARG_SURFSITEDETAILMODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_surfsite_detail, container, false);
        setupActionBar(true);

        urlTypeSpin = view.findViewById(R.id.urlType);
        countrySpin = view.findViewById(R.id.countries);
        geoTargetTypeSpin = view.findViewById(R.id.geotarget_type);
        budgedTypeSpin = view.findViewById(R.id.budged_type);
        decAudienceText = view.findViewById(R.id.decrease_audience);
        incAudienceText = view.findViewById(R.id.increase_audience);
        extendedAuditoryDescView = view.findViewById(R.id.extended_auditory_description);
        audienceView = view.findViewById(R.id.audience);
        onePriceView = view.findViewById(R.id.one_view_price);
        url = initUrlEditView(view);
        budgedText = initBudgedEditView(view);
        enabled = view.findViewById(R.id.enable_after_verification);
        mProgressView = view.findViewById(R.id.progressbar);

        initAudienceText();
        initSubmitButton(view);
        initTargetingSpinners();

        updateAudienceUI();
        initStartPrice();
        if (surfSiteDetailMode == ADD) {
            setDefaultFieldsValues();
        } else if (surfSiteDetailMode == EDIT){
            updateAllFieldsValues();
        }

        initAd(view);

        return view;
    }


    private void initAd(View view) {


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

    private void updateAllFieldsValues() {

        url.setText(surfSiteListItem.site_url);

        try {
            urlTypeSpin.setSelection(Math.abs(Short.valueOf(surfSiteListItem.site_type)));
        } catch (NumberFormatException e) {
            urlTypeSpin.setSelection(0);
        }

        try {
            budgedTypeSpin.setSelection(Math.abs(Short.valueOf(surfSiteListItem.site_type_budget)));
        } catch (NumberFormatException e) {
            urlTypeSpin.setSelection(0);
        }

        if(surfSiteListItem.site_off != null)
            enabled.setChecked(surfSiteListItem.site_off.equals("0"));
    }


    private void initTargetingSpinners() {
        ArrayAdapter<?> adapterUrlType =
                ArrayAdapter.createFromResource(getActivity(), R.array.array_surfsite_type,
                        android.R.layout.simple_spinner_item);
        adapterUrlType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        urlTypeSpin.setAdapter(adapterUrlType);
        urlTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String value = getResources().getStringArray(R.array.array_surfsite_type_values)[position];

                switch (value) {
                    case "0":
                        url.setHint("domain.com");
                        break;
                    case "-1":
                    case "-2":
                    case "-4":
                        url.setHint("11111111");
                        break;
                    case "-3":
                        url.setHint("https://facebook.com/example_group");
                        break;
                    case "-5":
                        url.setHint("https://www.instagram.com/p/AAAAAAAA/");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


        ArrayAdapter<?> adapterCountry =
                ArrayAdapter.createFromResource(getActivity(), R.array.array_countries,
                        android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpin.setAdapter(adapterCountry);


        ArrayAdapter<?> geoTargetTypeCountry =
                ArrayAdapter.createFromResource(getActivity(), R.array.array_geotarget_type,
                        android.R.layout.simple_spinner_item);
        geoTargetTypeCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        geoTargetTypeSpin.setAdapter(geoTargetTypeCountry);
        geoTargetTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                switch (position) {
                    case 0:
                        countrySpin.setVisibility(View.GONE);
                        break;
                    default:
                        countrySpin.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<?> budgedType =
                ArrayAdapter.createFromResource(getActivity(), R.array.array_budged_type,
                        android.R.layout.simple_spinner_item);
        budgedType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        budgedTypeSpin.setAdapter(budgedType);
        budgedTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                switch (position) {
                    case 0:
                        budgedText.setVisibility(View.GONE);
                        break;
                    default:
                        budgedText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }

    private void initAudienceText() {
        incAudienceText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAudience(++surfSiteListItem.site_rate);
            }
        });

        decAudienceText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (surfSiteListItem.site_rate > 1) {
                    updateAudience(--surfSiteListItem.site_rate);
                }
            }
        });

    }

    private void initStartPrice() {
        try {
            surfSiteDetailStartPriceTask = new SurfSiteDetailTask(SurfSiteDetailTask.CurrentTask.GetStartPrice);
            surfSiteDetailStartPriceTask.setTask(surfSiteDetailStartPriceTask);
            surfSiteDetailStartPriceTask.setActivity(getActivity());
            surfSiteDetailStartPriceTask.setProcessing(SurfSiteDetailFragment.this);
            surfSiteDetailStartPriceTask.setShowProgressCallback(SurfSiteDetailFragment.this);
            surfSiteDetailStartPriceTask.execute((Void) null);
        } catch (Exception e) {

        }
    }

    private EditText initUrlEditView(View view) {

        final EditText url = view.findViewById(R.id.site_url);

        url.setOnKeyListener(new KeyboardEnterListener(getActivity()));
        url.setOnFocusChangeListener(new KeyboardEnterListener(getActivity()));

        url.setHint("domain.com");


        return url;
    }

    private TextView initBudgedEditView(View view) {

        TextView budgedText = view.findViewById(R.id.budged);

        budgedText.setOnKeyListener(new KeyboardEnterListener(getActivity()));
        budgedText.setOnFocusChangeListener(new KeyboardEnterListener(getActivity()));

        return budgedText;
    }

    private void initSubmitButton(View view) {

        final Button button = view.findViewById(R.id.submit_button);

        if (surfSiteDetailMode == ADD) {
            button.setText(getString(R.string.action_add));
        } else if (surfSiteDetailMode == EDIT) {
            button.setText(getString(R.string.action_edit));

        }

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean cancel = false;
                url.setError(null);

                Double budged = 0.0;

                final int GENERAL_BUDGED_TYPE = 0;

                if(budgedTypeSpin.getSelectedItemId() !=
                        GENERAL_BUDGED_TYPE) {
                    try {
                        budged = (double) Integer.valueOf(budgedText.getText().toString());
                    } catch (NumberFormatException e) {

                        budgedText.setError(getString(R.string.error_not_number));
                        cancel = true;
                    }
                } else {
                    budged = 0.0;
                }


                if (TextUtils.isEmpty(url.getText())) {
                    url.setError(getString(R.string.error_field_required));
                    cancel = true;
                }

                if (!cancel) {


                    Short siteType;
                    try {
                        siteType = Short.valueOf(
                                getResources().getStringArray(R.array.array_surfsite_type_values)[(int) urlTypeSpin.getSelectedItemId()]);
                    } catch (NumberFormatException e) {
                        siteType = 0;
                    }


                    List<Short> geoCountry = new ArrayList<>();
                    try {
                        geoCountry.add(Short.valueOf(
                                getResources().getStringArray(R.array.array_countries_values)[(int)countrySpin.getSelectedItemId()]));


                    } catch (NumberFormatException e) {
                        siteType = 0;
                    }


                    surfSite = new SurfSite(url.getText().toString(),
                            surfSiteListItem.site_id,
                            siteType,
                            (short) -1,
                            surfSiteListItem.site_rate,
                            (short) budgedTypeSpin.getSelectedItemId(),
                            budged/surfSiteListItem.adssite_start_price,
                            enabled.isChecked(),
                            (short) geoTargetTypeSpin.getSelectedItemId(),
                            geoCountry.toArray(new Short[geoCountry.size()]),
                            null,
                            null

                    );


                    SurfSiteDetailTask.CurrentTask currentTaskMode = AddSurfSite;
                    switch (surfSiteDetailMode) {

                        case ADD: {
                            Bundle params = new Bundle();
                            params.putString("url", surfSite.site_url);
                            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
                            mFirebaseAnalytics.logEvent("create_surf_site", params);

                            setDefaultFieldsValues();
                        }
                        break;

                        case EDIT: {
                            currentTaskMode = EditSurfSite;

                        }
                        break;
                    }

                    surfSiteDetailSubmitTask = new SurfSiteDetailTask(currentTaskMode, surfSite);

                    surfSiteDetailSubmitTask.setTask(surfSiteDetailSubmitTask);
                    surfSiteDetailSubmitTask.setActivity(getActivity());
                    surfSiteDetailSubmitTask.setProcessing(SurfSiteDetailFragment.this);
                    surfSiteDetailSubmitTask.setShowProgressCallback(SurfSiteDetailFragment.this);
                    surfSiteDetailSubmitTask.execute((Void) null);
                }
            }
        });

    }

    private void updateAudience(short rate) {

        SurfSiteListItem surfSiteListItem = new SurfSiteListItem();
        surfSiteListItem.site_rate = rate;

        surfSiteDetailAudienceTask =
                new SurfSiteDetailTask(SurfSiteDetailTask.CurrentTask.ChangeAudience, surfSiteListItem);

        surfSiteDetailAudienceTask.setTask(surfSiteDetailAudienceTask);
        surfSiteDetailAudienceTask.setActivity(getActivity());
        surfSiteDetailAudienceTask.setProcessing(SurfSiteDetailFragment.this);
        surfSiteDetailAudienceTask.setShowProgressCallback(SurfSiteDetailFragment.this);

        surfSiteDetailAudienceTask.execute((Void) null);
    }

    private void updateAudienceUI() {

        String factor = (surfSiteListItem.site_audience != null) ?
                surfSiteListItem.site_audience + "x" :
                "";

        audienceView.setText(factor);


        String onePrice = (surfSiteListItem.site_rate != null &&
                surfSiteListItem.adssite_start_price != null &&
                surfSiteListItem.site_rate > 0) ?
                String.format("%.3f", surfSiteListItem.adssite_start_price *
                surfSiteListItem.site_rate) + " RUB" :
                "";
        onePriceView.setText(onePrice);


        if(surfSiteListItem.site_audience != null) {
            String description = getString(R.string.title_extend_auditory);
            extendedAuditoryDescView.setText(String.format(description.toString(),
                    surfSiteListItem.site_audience));
        }
    }

    private void setupActionBar(boolean show) {
        Activity activity = getActivity();
        if(activity instanceof FragmentHomeInteraction) {
            ((FragmentHomeInteraction) activity).onFragmentHomeInteraction(show);
        }
    }

    private void setDefaultFieldsValues() {
        surfSiteListItem.site_rate = 1;
        surfSiteListItem.site_audience = 1;
        surfSiteListItem.adssite_start_price = 0.0;

        url.setText("");
        budgedText.setText("");

        urlTypeSpin.setSelection(0);
        countrySpin.setSelection(0);
        geoTargetTypeSpin.setSelection(0);
        budgedTypeSpin.setSelection(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
    public void onProcessSurfSiteResponse(SurfSite surfSite, SurfSiteDetailTask.CurrentTask mode) {

        switch (mode) {
            case AddSurfSite:
            case EditSurfSite:

                Fragment webAdvFragment = new SurfSiteListFragment();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.remove(webAdvFragment);
                fragmentTransaction.replace(R.id.detail_layout_adv, webAdvFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                setupActionBar(false);
                break;
        }
    }

    @Override
    public void onProcessSurfSiteItemListResponse(SurfSiteListItem surfSiteListItem, SurfSiteDetailTask.CurrentTask mode) {

        switch (mode) {
            case GetStartPrice:
                this.surfSiteListItem.adssite_start_price = surfSiteListItem.adssite_start_price;
                updateAudienceUI();
                if(this.surfSiteListItem.site_rate != null && this.surfSiteListItem.site_rate > 0) {
                    updateAudience(this.surfSiteListItem.site_rate);

                    if(this.surfSiteListItem.site_val_budget != null &&
                            this.surfSiteListItem.adssite_start_price != null) {

                        long val = Math.round(this.surfSiteListItem.site_val_budget * this.surfSiteListItem.adssite_start_price);

                        if(val > 0)
                            budgedText.setText(String.valueOf(
                                    val)
                            );
                    }
                }

                break;

            case ChangeAudience:
                this.surfSiteListItem.site_audience = surfSiteListItem.site_audience;
                updateAudienceUI();
                break;
        }
    }
}
