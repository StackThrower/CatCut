package catcut.net.fragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import catcut.net.MainActivity;
import catcut.net.R;


/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link EarnFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class EarnFragment extends Fragment {

    private static final String ARG_SUBSERVICE_MODE = "subservicemode";

    MainActivity.SubServiceMode mode = MainActivity.SubServiceMode.SHORTLINK_LIST;

    ShortLinkCreateFragment shortLinkCreateFragment = new ShortLinkCreateFragment();
    ShortLinkListFragment shortLinkListFragment = new ShortLinkListFragment();
    TasksFragment tasksFragment = new TasksFragment();

    private OnFragmentInteractionListener mListener;

    public EarnFragment() {
        // Required empty public constructor
    }

    public static EarnFragment newInstance(MainActivity.SubServiceMode mode) {
        EarnFragment fragment = new EarnFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SUBSERVICE_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mode = (MainActivity.SubServiceMode) getArguments().getSerializable(ARG_SUBSERVICE_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earn, container, false);

        TabHost tabHost = view.findViewById(R.id.tabHost1);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.earn_tab_layout1);
        tabSpec.setIndicator(getString(R.string.action_links));
        tabHost.addTab(tabSpec);

//        tabSpec = tabHost.newTabSpec("tag2");
//        tabSpec.setContent(R.id.earn_tab_layout2);
//        tabSpec.setIndicator("Tasks");
//        tabHost.addTab(tabSpec);
//
//        tabSpec = tabHost.newTabSpec("tag3");
//        tabSpec.setContent(R.id.earn_tab_layout3);
//        tabSpec.setIndicator("Partnership");
//        tabHost.addTab(tabSpec);
//
//        tabSpec = tabHost.newTabSpec("tag4");
//        tabSpec.setContent(R.id.earn_tab_layout3);
//        tabSpec.setIndicator("Web");
//        tabHost.addTab(tabSpec);
//
//        tabSpec = tabHost.newTabSpec("tag5");
//        tabSpec.setContent(R.id.earn_tab_layout4);
//        tabSpec.setIndicator("Referals");
//        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        boolean share = false;
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                share = true;
                ShortLinkCreateFragment shortLinkCreateFragment =
                        ShortLinkCreateFragment.newInstance(intent.getStringExtra(Intent.EXTRA_TEXT));
                fragmentTransaction.remove(shortLinkCreateFragment);
                fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkCreateFragment);
            }
        }

        if(!share && mode == MainActivity.SubServiceMode.SHORTLINK_LIST) {
            fragmentTransaction.remove(shortLinkListFragment);
            fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkListFragment);
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                                            @Override
                                            public void onTabChanged(String tabId) {
                                                FragmentManager fragmentManager = getFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                                switch (tabId) {
                                                    case "tag1":

                                                        fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkListFragment);
                                                        break;
                                                    case "tag2":

                                                        fragmentTransaction.replace(R.id.detail_layout_earn, tasksFragment);
                                                        break;
                                                    case "tag3":

                                                        fragmentTransaction.replace(R.id.detail_layout_earn, shortLinkCreateFragment);
                                                        break;
                                                    default:

                                                }

                                                fragmentTransaction.addToBackStack(null);
                                                fragmentTransaction.commit();
                                            }
                                        }
        );


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.

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
