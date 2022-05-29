package catcut.net.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import catcut.net.R;
import catcut.net.adapters.ShortLinkRecyclerViewAdapter;
import catcut.net.helpers.NetworkExceptionUI;
import catcut.net.helpers.SharedPreferencesHelper;
import catcut.net.network.ShortLinkNetwork;
import catcut.net.network.ShortLinkNetworkImpl;
import catcut.net.network.entity.LinkStat;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;


public class ShortLinkListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    View mProgressView;
    GetMyLinkStatsTask getMyLinkStatsTask;
    RecyclerView recyclerView;


    public ShortLinkListFragment() {
    }

    private OnListFragmentInteractionListener mListener = new OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(ShortLinkRecyclerViewAdapter.ViewHolder holder, String mode) {

            switch (mode) {
                case "share":

                    String shareBody = "http://ccl.su/" + holder.mItem.shortlink;

                    Bundle params = new Bundle();
                    params.putString("url", shareBody);
                    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
                    mFirebaseAnalytics.logEvent("share_short_link", params);

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.title_share_using));
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.title_share_using)));
                    break;

                case "select": {
                    OnListFragmentInteractionListener callback = (OnListFragmentInteractionListener) getActivity();
                    callback.onListFragmentInteraction(holder, mode);
                }
                break;
                case "unselect": {
                    OnListFragmentInteractionListener callback = (OnListFragmentInteractionListener) getActivity();
                    callback.onListFragmentInteraction(holder, mode);

                }
                break;
                case "selected": {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    ShortLinkDetailFragment shortLinkDetailFragment = ShortLinkDetailFragment.newInstance(holder.mItem);
                    fragmentTransaction.remove(shortLinkDetailFragment);
                    fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkDetailFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
                case "copy": {
                    String copyText = "http://ccl.su/" + holder.mItem.shortlink;
                    ClipboardManager clipboard = (ClipboardManager) getActivity()
                            .getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("url", copyText);
                    clipboard.setPrimaryClip(clip);


                    Toast.makeText(getActivity(), getString(R.string.message_copied_to_clipboard),
                            Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    };


    @SuppressWarnings("unused")
    public static ShortLinkListFragment newInstance(int columnCount) {
        ShortLinkListFragment fragment = new ShortLinkListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shortlink_list, container, false);

        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.my_link_list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        mProgressView = view.findViewById(R.id.get_my_link_stat_progressbar);
        getMyLinkStatsTask = new GetMyLinkStatsTask();
        getMyLinkStatsTask.execute((Void) null);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ShortLinkRecyclerViewAdapter.ViewHolder item, String mode);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        try {

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
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {

        }
    }


    public class GetMyLinkStatsTask extends AsyncTask<Void, Void, Boolean> {

        List<LinkStat> links = new ArrayList<>();

        GetMyLinkStatsTask() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                ShortLinkNetwork networkImpl = new ShortLinkNetworkImpl(SharedPreferencesHelper.getStoredToken(getActivity()));

                links = networkImpl.getList(1);
            } catch (Exception e) {
                NetworkExceptionUI.showMessageNoInternetConnection(getActivity(), e.getMessage());
                showProgress(false);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            getMyLinkStatsTask = null;
            showProgress(false);
            recyclerView.setAdapter(new ShortLinkRecyclerViewAdapter(links, mListener));
        }

        @Override
        protected void onCancelled(final Boolean success) {
            getMyLinkStatsTask = null;

        }
    }
}
