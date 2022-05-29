package catcut.net.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import catcut.net.MainActivity;
import catcut.net.R;
import catcut.net.fragments.SurfSiteListFragment.OnListFragmentInteractionListener;
import catcut.net.modes.SurfSiteItemListMode;
import catcut.net.network.entity.SurfSiteListItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

/**
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SurfSiteRecyclerViewAdapter extends
        RecyclerView.Adapter<SurfSiteRecyclerViewAdapter.ViewHolder> {

    private final List<SurfSiteListItem> mValues;
    private final OnListFragmentInteractionListener mListener;


    private static final int AD_TYPE = 1;
    private static final int CONTENT_TYPE = 0;
    private static final int LIST_AD_DELTA = 4;

    public SurfSiteRecyclerViewAdapter(List<SurfSiteListItem> items,
                                       OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view;

        if (viewType == AD_TYPE) {
            AdView adView = new AdView(parent.getContext());
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId("ca-app-pub-3579118192425679/5718731353");

            return new ViewHolder(adView);

        } else
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_surfsite_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (getItemViewType(position) == CONTENT_TYPE) {

            holder.mItem = mValues.get(position);

            switch (mValues.get(position).site_valid) {
                case "1":
                    holder.mStatusVerifiedView.setVisibility(View.VISIBLE);
                    break;
                case "2":
                    holder.mStatusDeclinedView.setVisibility(View.VISIBLE);
                    break;
                default:
                    holder.mStatusInProgressView.setVisibility(View.VISIBLE);
            }

            holder.mUrlView.setText(mValues.get(position).site_url);
            holder.mShowsView.setText(String.valueOf(mValues.get(position).site_shows));
            holder.mClickBackView.setText(String.valueOf(mValues.get(position).site_clickback));
            holder.mStartPriceView.setText(String.valueOf(mValues.get(position).adssite_start_price));
            holder.mRBudgedView.setText(String.valueOf(mValues.get(position).site_r_budget));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(holder.mItem, SurfSiteItemListMode.SELECT);
                    }
                }
            });

        } else {


            AdRequest adRequest = new AdRequest.Builder().build();

            float density = holder.mView.getContext().getResources().getDisplayMetrics().density;
            int height = Math.round(AdSize.BANNER.getHeight() * (int)(density * 1.75));
            AbsListView.LayoutParams params =
                    new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, height);

            if (holder.mView instanceof AdView) {

                holder.mView.setLayoutParams(params);
                ((AdView) holder.mView).setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        // Code to be executed when an ad finishes loading.

                        Bundle params = new Bundle();
                        FirebaseAnalytics mFirebaseAnalytics =
                                FirebaseAnalytics.getInstance(holder.mView.getContext());
                        mFirebaseAnalytics.logEvent("ad_load", params);

                    }


                    public void onAdFailedToLoad(int errorCode) {
                        // Code to be executed when an ad request fails.
                    }

                    @Override
                    public void onAdOpened() {
                        Bundle params = new Bundle();
                        FirebaseAnalytics mFirebaseAnalytics =
                                FirebaseAnalytics.getInstance(holder.mView.getContext());
                        mFirebaseAnalytics.logEvent("ad_opened", params);
                    }


                    public void onAdLeftApplication() {
                        Bundle params = new Bundle();
                        FirebaseAnalytics mFirebaseAnalytics =
                                FirebaseAnalytics.getInstance(holder.mView.getContext());
                        mFirebaseAnalytics.logEvent("ad_left_app", params);
                    }

                    @Override
                    public void onAdClosed() {
                        // Code to be executed when when the user is about to return
                        // to the app after tapping on an ad.
                    }
                });
                ((AdView) holder.mView).loadAd(adRequest);
            }
        }
    }


    @Override
    public int getItemViewType(int position) {

        if(MainActivity.isAdEnabled()) {
            if (position > 0 && position % LIST_AD_DELTA == 0 || position == 1)
                return AD_TYPE;
        }

        return CONTENT_TYPE;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mShowsView;
        public final TextView mClickBackView;
        public final TextView mStartPriceView;
        public final TextView mRBudgedView;
        public final TextView mUrlView;
        public final ImageView mStatusVerifiedView;
        public final ImageView mStatusDeclinedView;
        public final ImageView mStatusInProgressView;
        public SurfSiteListItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUrlView = (TextView) view.findViewById(R.id.site_url);
            mShowsView = (TextView) view.findViewById(R.id.site_shows);
            mClickBackView = (TextView) view.findViewById(R.id.site_clickback);
            mStartPriceView = (TextView) view.findViewById(R.id.adssite_start_price);
            mRBudgedView = (TextView) view.findViewById(R.id.site_r_budget);
            mStatusVerifiedView = (ImageView) view.findViewById(R.id.status_verified);
            mStatusDeclinedView = (ImageView) view.findViewById(R.id.status_declined);
            mStatusInProgressView = (ImageView) view.findViewById(R.id.status_inprogress);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUrlView.getText() + "'";
        }
    }
}
