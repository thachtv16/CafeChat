package thachtv.cafechat.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import thachtv.cafechat.R;

/**
 * Created by Thinkpad on 09/30/2017.
 */

public class BaseFragment extends Fragment {

    private static BaseFragment instance;

    public static BaseFragment getInstance() {
        if(null == instance) {
            instance = new BaseFragment();
        }
        return instance;
    }

    public void replaceFragmentFromActivity(BaseFragment fragment, FragmentManager manager) {
        if (null != fragment) {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content, fragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
    }

    public void replaceFragmentFromFragment(BaseFragment fragment, FragmentManager manager) {
        if (null != fragment) {
            String nameFragment = fragment.getClass().getName();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content, fragment);
            fragmentTransaction.addToBackStack(nameFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
//            manager.executePendingTransactions();
        }
    }
}
