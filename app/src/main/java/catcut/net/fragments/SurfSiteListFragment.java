package catcut.net.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import catcut.net.R;
import catcut.net.adapters.SurfSiteRecyclerViewAdapter;
import catcut.net.helpers.NetworkExceptionUI;
import catcut.net.helpers.SharedPreferencesHelper;
import catcut.net.modes.SurfSiteItemListMode;
import catcut.net.network.SurfSiteNetwork;
import catcut.net.network.SurfSiteNetworkImpl;
import catcut.net.network.entity.SurfSiteListItem;

import java.util.ArrayList;
import java.util.List;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SurfSiteListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private OnListFragmentInteractionListener mListener = new OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(SurfSiteListItem item, SurfSiteItemListMode mode) {
            SurfSiteItemListMode.valueOf(mode.toString()).doProcessing(item, getActivity());
        }
    };


    GetMySurfSiteStatsTask getMySurfSiteStatsTask;
    RecyclerView recyclerView;
    View mProgressView;
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SurfSiteListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SurfSiteListFragment newInstance(int columnCount) {
        SurfSiteListFragment fragment = new SurfSiteListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surfsite_list, container, false);

        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.web_adv_list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mProgressView = view.findViewById(R.id.progressbar);

        getMySurfSiteStatsTask = new GetMySurfSiteStatsTask();
        getMySurfSiteStatsTask.execute((Void) null);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (context instanceof OnListFragmentInteractionListener) {
                mListener = (OnListFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnListFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                mListener = (OnListFragmentInteractionListener) getActivity();
            } catch (ClassCastException e) {
                throw new ClassCastException(getActivity().toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

        void onListFragmentInteraction(SurfSiteListItem item, SurfSiteItemListMode mode);
    }


    public class GetMySurfSiteStatsTask extends AsyncTask<Void, Void, Boolean> {

        List<SurfSiteListItem> items = new ArrayList<>();

        GetMySurfSiteStatsTask() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                SurfSiteNetwork networkImpl = new SurfSiteNetworkImpl(SharedPreferencesHelper.getStoredToken(getActivity()));

                items = networkImpl.getList(1);
            } catch (Exception e) {
                NetworkExceptionUI.showMessageNoInternetConnection(getActivity(), e.getMessage());
                showProgress(false);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            getMySurfSiteStatsTask = null;
            showProgress(false);

            recyclerView.setAdapter(new SurfSiteRecyclerViewAdapter(items, mListener));
        }

        @Override
        protected void onCancelled(final Boolean success) {
            getMySurfSiteStatsTask = null;
        }
    }


}
