package thachtv.cafechat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import thachtv.cafechat.base.BaseActivity;
import thachtv.cafechat.fragments.chat.ChatFragment;
import thachtv.cafechat.fragments.contact.ContactFragment;
import thachtv.cafechat.fragments.information.MenuFragment;

/**
 * Created by Thinkpad on 09/30/2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    private BaseActivity baseActivity;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ChatFragment.newInstance();
            case 1:
                return ContactFragment.newInstance();
            case 2:
                return MenuFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
