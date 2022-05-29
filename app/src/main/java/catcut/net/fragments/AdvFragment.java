package catcut.net.fragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import catcut.net.R;


public class AdvFragment extends Fragment {

    SurfSiteListFragment surfSiteListFragment = new SurfSiteListFragment();

    private OnFragmentInteractionListener mListener;



    public AdvFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_advertise, container, false);

        TabHost tabHost = view.findViewById(R.id.advTabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.adv_tab_layout1);
        tabSpec.setIndicator(getString(R.string.action_web));
        tabHost.addTab(tabSpec);

//        tabSpec = tabHost.newTabSpec("tag2");
//        tabSpec.setContent(R.id.adv_tab_layout2);
//        tabSpec.setIndicator("Video");
//        tabHost.addTab(tabSpec);
//
//        tabSpec = tabHost.newTabSpec("tag3");
//        tabSpec.setContent(R.id.adv_tab_layout3);
//        tabSpec.setIndicator("Context");
//        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(surfSiteListFragment);
        fragmentTransaction.replace(R.id.detail_layout_adv, surfSiteListFragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                                            @Override
                                            public void onTabChanged(String tabId) {
                                                FragmentManager fragmentManager = getFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                                                switch (tabId) {

                                                    case "tag1":

                                                        fragmentTransaction.replace(R.id.detail_layout_adv, surfSiteListFragment);
                                                        break;
                                                    case "tag2":

//                                                        fragmentTransaction.replace(R.id.detail_layout_adv, surfSiteListFragment);
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

        void onFragmentInteraction(Uri uri);
    }
}
