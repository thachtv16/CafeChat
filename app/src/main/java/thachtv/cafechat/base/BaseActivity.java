package thachtv.cafechat.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Thinkpad on 10/05/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());

        initialViews(savedInstanceState);
    }

    protected abstract int setLayout();
    protected abstract int getFragmentContainerResId();
    protected abstract void initialViews(@Nullable Bundle savedInstanceState);

    public void replaceFragmentFromActivity(Fragment fragment, FragmentManager manager) {
        if(null != manager) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(getFragmentContainerResId(), fragment);
            ft.commit();
            manager.executePendingTransactions();
        }
    }

    public void replaceFragment(Fragment fragment, FragmentManager manager) {
        if(null != manager) {
            String nameFragment = fragment.getClass().getName();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(getFragmentContainerResId(), fragment);
            ft.addToBackStack(nameFragment);
            ft.commit();
            manager.executePendingTransactions();
        }
    }

}
