package catcut.net.modes;

//import androidx.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import catcut.net.R;
import catcut.net.fragments.SurfSiteDetailFragment;
import catcut.net.network.entity.SurfSiteListItem;
import androidx.fragment.app.FragmentActivity;

public enum SurfSiteItemListMode {


    SELECT {
        @Override
        public void doProcessing(SurfSiteListItem surfSiteListItem,
                                 FragmentActivity activity) {

            Fragment surfSiteListFragment;

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            surfSiteListFragment = SurfSiteDetailFragment.newInstance(surfSiteListItem, SurfSiteDetailMode.EDIT);
            fragmentTransaction.remove(surfSiteListFragment);
            fragmentTransaction.replace(R.id.detail_layout_adv, surfSiteListFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    };

    public abstract void doProcessing(SurfSiteListItem surfSiteListItem,
                                      FragmentActivity activity);
}
