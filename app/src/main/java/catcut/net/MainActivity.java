package catcut.net;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import catcut.net.adapters.ShortLinkRecyclerViewAdapter;
import catcut.net.fragments.*;
import catcut.net.modes.SurfSiteDetailMode;
import catcut.net.modes.SurfSiteItemListMode;
import catcut.net.network.NetworkAsync;
import catcut.net.network.entity.AccountInfo;
import catcut.net.network.entity.SurfSiteListItem;
import catcut.net.processing.AccountProcessing;
import catcut.net.processing.FragmentHomeInteraction;
import catcut.net.tasks.AccountTask;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        SurfSiteListFragment.OnListFragmentInteractionListener,
        EarnFragment.OnFragmentInteractionListener,
        AdvFragment.OnFragmentInteractionListener,
        ShortLinkListFragment.OnListFragmentInteractionListener,
        TasksFragment.OnFragmentInteractionListener,
        ShortLinkCreateFragment.OnFragmentInteractionListener,
        FragmentHomeInteraction,
        ShortLinkDetailFragment.OnFragmentInteractionListener,
        AccountFragment.OnFragmentInteractionListener,
        AccountProcessing {


    Fragment advFragment = new AdvFragment();
    Fragment earnFragment = new EarnFragment();
    Fragment accountFragment = new AccountFragment();
    Fragment surfSiteListFragment = new SurfSiteListFragment();
    Fragment shortLinkListFragment = new ShortLinkListFragment();
    Fragment shortLinkCreateFragment = new ShortLinkCreateFragment();
    Fragment shortLinkDetailFragment = new ShortLinkDetailFragment();

    AccountTask accountTask = null;
    NetworkAsync mNetworkAsync;
    Menu optionsMenu;

    BottomNavigationView navigation;

    List<ShortLinkRecyclerViewAdapter.ViewHolder> selectedLinks = new ArrayList<>();


    enum ServiceMode {
        ADV_WEB,
        EARN_LINK,
        ACCOUNT
    }

    public enum SubServiceMode {
        SHORTLINK_LIST,
        SHORTLINK_DETAIL,
        SHORTLINK_CREATE
    }

    ServiceMode currentTab = ServiceMode.ADV_WEB;

    // Needed for restore state after rotation.
    SubServiceMode subServiceMode = SubServiceMode.SHORTLINK_LIST;

    public Double adsbalance = 0.0;
    public Double workbalance = 0.0;

    private static boolean IS_AD_ENABLED = false;
    private static boolean IS_STAT_ENABLED = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_advertise:

                    advFragment = new AdvFragment();

                    fragmentTransaction.remove(advFragment);
                    fragmentTransaction.replace(R.id.main_form, advFragment);
                    fragmentTransaction.addToBackStack(null);

                    currentTab = ServiceMode.ADV_WEB;

                    setTitle(adsbalance + " RUB");
                    optionsMenu.getItem(1).setVisible(true);
                    break;
                case R.id.navigation_earn:

                    earnFragment = new EarnFragment();

                    fragmentTransaction.remove(earnFragment);
                    fragmentTransaction.replace(R.id.main_form, earnFragment);
                    fragmentTransaction.addToBackStack(null);

                    currentTab = ServiceMode.EARN_LINK;

                    setTitle(workbalance + " RUB");
                    optionsMenu.getItem(1).setVisible(true);
                    break;

                case R.id.navigation_account:

                    accountFragment = AccountFragment.newInstance(String.valueOf(adsbalance), String.valueOf(workbalance));

                    currentTab = ServiceMode.ACCOUNT;

                    fragmentTransaction.remove(accountFragment);
                    fragmentTransaction.replace(R.id.main_form, accountFragment);
                    fragmentTransaction.addToBackStack(null);

                    setTitle("");

                    optionsMenu.getItem(1).setVisible(false);

                    break;
            }

            fragmentTransaction.commit();

            showHomeActionBar(false);

            return true;
        }
    };

    void showHomeActionBar(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(show);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRemoteConfig();

        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        String mString = mPrefs.getString("token", "");

        if (mString.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        } else {

            navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        MobileAds.initialize(this);
    }


    private synchronized void initRemoteConfig() {

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {


                            String versionKey = BuildConfig.VERSION_NAME.replaceAll("\\.", "_");

                            IS_AD_ENABLED = mFirebaseRemoteConfig.getBoolean(String.format("v%s_ad_enabled", versionKey));
                            IS_STAT_ENABLED = mFirebaseRemoteConfig.getBoolean(String.format("v%s_stat_enabled", versionKey));

                            setStateForFirebase(IS_STAT_ENABLED);

                            handleStartFragments();

                            accountTask = new AccountTask(AccountTask.CurrentTask.Info);
                            accountTask.setProcessing(MainActivity.this);
                            accountTask.setActivity(MainActivity.this);
                            accountTask.setTask(accountTask);
                            accountTask.execute((Void) null);

                        } else {
                            Toast.makeText(MainActivity.this, "Configuration has been not retrieved!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     *  IMPORTANT!!! This method can be called/restored when the device is rotated.
     */
    private void handleStartFragments() {
        boolean shareInput = false;

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                shareInput = true;
                navigation.setSelectedItemId(R.id.navigation_earn);
            }
        }

        if (!shareInput) {

            switch (currentTab) {

                case ADV_WEB:
                    fragmentTransaction.remove(advFragment);
                    fragmentTransaction.replace(R.id.main_form, advFragment);
                    break;

                case EARN_LINK:

                    switch (subServiceMode) {
                        case SHORTLINK_CREATE:
                            fragmentTransaction.remove(shortLinkCreateFragment);
                            fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkCreateFragment);
                            break;
                        case SHORTLINK_DETAIL:

                            fragmentTransaction.remove(shortLinkDetailFragment);
                            fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkDetailFragment);
                            break;
                        case SHORTLINK_LIST:
                        default:
                            fragmentTransaction.remove(earnFragment);
                            fragmentTransaction.replace(R.id.main_form, earnFragment);
                    }

                    break;
            }
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setStateForFirebase(boolean enabled) {

        FirebaseAnalytics mFirebaseAnalytics =
                FirebaseAnalytics.getInstance(MainActivity.this);

        mFirebaseAnalytics.setAnalyticsCollectionEnabled(enabled);
    }

    public static synchronized boolean isAdEnabled() {
        return IS_AD_ENABLED;
    }

    public static synchronized boolean isStatEnabled() {
        return IS_STAT_ENABLED;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionsMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings_link) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        } else if (id == R.id.action_create) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (currentTab) {
                case ADV_WEB:

                    Fragment surfSiteDetailFragment =
                            SurfSiteDetailFragment.newInstance(new SurfSiteListItem(), SurfSiteDetailMode.ADD);

                    fragmentTransaction.remove(surfSiteDetailFragment);
                    fragmentTransaction.replace(R.id.detail_layout_adv, surfSiteDetailFragment);
                    setTitle(adsbalance + " RUB");
                    break;
                case EARN_LINK:
                    subServiceMode = SubServiceMode.SHORTLINK_CREATE;

                    fragmentTransaction.remove(shortLinkCreateFragment);
                    fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkCreateFragment);
                    setTitle(workbalance + " RUB");
                    break;
            }
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            return true;
        } else if (id == R.id.action_hide_link) {

            StringBuilder sb = new StringBuilder();
            for (ShortLinkRecyclerViewAdapter.ViewHolder view : selectedLinks) {
                if (!sb.toString().isEmpty())
                    sb.append(",");

                sb.append(view.mItem.id);
            }

            mNetworkAsync = new NetworkAsync(this);
            mNetworkAsync.hideUrls(sb.toString());
            mNetworkAsync.execute((Void) null);


            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction  fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.remove(shortLinkListFragment);
            fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkListFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            selectedLinks.clear();

            MenuItem menuItem = optionsMenu.findItem(R.id.action_hide_link);
            menuItem.setVisible(false);
        } else if (id == android.R.id.home) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


            switch (currentTab) {
                case ADV_WEB:
                    fragmentTransaction.remove(surfSiteListFragment);
                    fragmentTransaction.replace(R.id.detail_layout_adv, surfSiteListFragment);
                    setTitle(adsbalance + " RUB");
                    break;
                case EARN_LINK:
                    subServiceMode = SubServiceMode.SHORTLINK_LIST;

                    fragmentTransaction.remove(shortLinkListFragment);
                    fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkListFragment);
                    setTitle(workbalance + " RUB");
                    break;
            }

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            showHomeActionBar(false);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onListFragmentInteraction(ShortLinkRecyclerViewAdapter.ViewHolder holder, String mode) {

        switch (mode) {
            case "select": {

                selectedLinks.add(holder);

                MenuItem menuItem = optionsMenu.findItem(R.id.action_hide_link);
                menuItem.setVisible(selectedLinks.size() > 0);
            }
            break;
            case "unselect": {

                selectedLinks.remove(holder);

                MenuItem menuItem = optionsMenu.findItem(R.id.action_hide_link);
                menuItem.setVisible(selectedLinks.size() > 0);
            }
            break;
            case "share":

                String shareBody = "http://ccl.su/" + holder.mItem.shortlink;

                Bundle params = new Bundle();
                params.putString("url", shareBody);
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
                mFirebaseAnalytics.logEvent("share_short_link", params);

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.title_share_using));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.title_share_using)));
                break;

            case "selected": {

                subServiceMode = SubServiceMode.SHORTLINK_DETAIL;

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                shortLinkDetailFragment = ShortLinkDetailFragment.newInstance(holder.mItem);
                fragmentTransaction.remove(shortLinkDetailFragment);
                fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkDetailFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            break;
            case "copy": {
                String copyText = "http://ccl.su/" + holder.mItem.shortlink;
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("url", copyText);
                clipboard.setPrimaryClip(clip);


                Toast.makeText(MainActivity.this, getString(R.string.message_copied_to_clipboard),
                        Toast.LENGTH_LONG).show();
            }
            break;
        }
    }


    @Override
    public void onListFragmentInteraction(SurfSiteListItem surfSiteListItem, SurfSiteItemListMode mode) {

        SurfSiteItemListMode.valueOf(mode.toString()).doProcessing(surfSiteListItem, MainActivity.this);

    }

    @Override
    public void onBackPressed() {

        showHomeActionBar(false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (currentTab) {
            case ADV_WEB:
                // TODO needed accoding to opened tab on adv category

                fragmentTransaction.remove(surfSiteListFragment);
                fragmentTransaction.replace(R.id.detail_layout_adv, surfSiteListFragment);
                setTitle(adsbalance + " RUB");
                break;
            case EARN_LINK:

                subServiceMode = SubServiceMode.SHORTLINK_LIST;

                // TODO needed accoding to opened tab on earn category
                fragmentTransaction.remove(shortLinkListFragment);
                fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkListFragment);
                setTitle(workbalance + " RUB");
                break;
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentHomeInteraction(boolean show) {
        showHomeActionBar(show);
    }


    @Override
    public void onProcessAccountInfoResponse(AccountInfo accountInfo) {

        if (accountInfo != null) {

            SharedPreferences mPrefs = getSharedPreferences("label", 0);
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putString("accountInfo", accountInfo.toString()).apply();

            try {
                adsbalance = Double.valueOf(accountInfo.adsbalance);
                workbalance = Double.valueOf(accountInfo.workbalance);
            } catch (NumberFormatException e) {
                adsbalance = 0.0;
                workbalance = 0.0;
            }

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    setTitle(adsbalance + " RUB");
                }
            });
        }
    }
}
