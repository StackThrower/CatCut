package catcut.net.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import catcut.net.MainActivity;
import catcut.net.R;

import catcut.net.fragments.ShortLinkListFragment.OnListFragmentInteractionListener;
import catcut.net.network.entity.LinkStat;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

/**
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ShortLinkRecyclerViewAdapter extends
        RecyclerView.Adapter<ShortLinkRecyclerViewAdapter.ViewHolder> {


    private static final int AD_TYPE = 1;
    private static final int CONTENT_TYPE = 0;
    private static final int LIST_AD_DELTA = 4;

    private final List<LinkStat> mValues;
    private final OnListFragmentInteractionListener mListener;


    public ShortLinkRecyclerViewAdapter(List<LinkStat> items,
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
            adView.setAdUnitId("ca-app-pub-3579118192425679/8432486618");

            return new ViewHolder(adView);

        } else
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_shortlink_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (getItemViewType(position) == CONTENT_TYPE) {

            holder.mItem = mValues.get(position);
            holder.mCount.setText(mValues.get(position).count);
            holder.mShortLink.setText("http://ccl.su/" + mValues.get(position).shortlink);
            holder.mDate.setText(mValues.get(position).createtime);
            holder.mPrice.setText(mValues.get(position).money + " RUB");
            holder.mLongLink.setText(mValues.get(position).longurl);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(holder, "selected");
                    }
                }
            });

            holder.mShare.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(holder, "share");
                    }
                }
            });


            holder.mCopy.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(holder, "copy");
                    }
                }
            });


            holder.mSelect.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {


                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.

                        mListener.onListFragmentInteraction(holder, isChecked ? "select" : "unselect");
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
        public final TextView mCount;
        public final TextView mDate;
        public final TextView mShortLink;
        public final TextView mPrice;
        public final TextView mLongLink;
        public LinkStat mItem;
        public ImageView mShare;
        public ImageView mCopy;
        public CheckBox mSelect;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mCount = view.findViewById(R.id.count);
            mShortLink = view.findViewById(R.id.shortlink);

            mDate = view.findViewById(R.id.date);
            mShare = view.findViewById(R.id.share);
            mCopy = view.findViewById(R.id.copy);
            mSelect = view.findViewById(R.id.select);
            mPrice = view.findViewById(R.id.price);
            mLongLink = view.findViewById(R.id.longlink);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mShortLink.getText() + "'";
        }
    }
}
