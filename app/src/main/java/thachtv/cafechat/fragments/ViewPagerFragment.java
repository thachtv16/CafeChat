package thachtv.cafechat.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import thachtv.cafechat.R;
import thachtv.cafechat.activities.LogInActivity;
import thachtv.cafechat.adapter.PagerAdapter;
import thachtv.cafechat.base.BaseFragment;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.fragments.contact.SearchUserFragment;

/**
 * Created by Thinkpad on 10/13/2017.
 */

public class ViewPagerFragment extends BaseFragment implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    private TextView tvTitleMain;
    private ImageView ivPlusMain;
    private ImageView ivSearchMain;
    private ImageView ivRequestMain;
    private ImageView ivMenuMain;

    private ImageView ivChatMain;
    private ImageView ivContactMain;
    private ImageView ivInformationMain;
    private ViewPager viewPager;

    private PagerAdapter pagerAdapter;

    public static ViewPagerFragment newInstance() {
        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        return viewPagerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        ivChatMain = (ImageView) rootView.findViewById(R.id.iv_chat_main);
        ivChatMain.setOnClickListener(this);
        ivContactMain = (ImageView) rootView.findViewById(R.id.iv_contact_main);
        ivContactMain.setOnClickListener(this);
        ivInformationMain = (ImageView) rootView.findViewById(R.id.iv_information_main);
        ivInformationMain.setOnClickListener(this);
        tvTitleMain = (TextView) rootView.findViewById(R.id.tv_title_main);
        ivPlusMain = (ImageView) rootView.findViewById(R.id.iv_plus_main);
        ivPlusMain.setOnClickListener(this);
        ivSearchMain = (ImageView) rootView.findViewById(R.id.iv_search_main);
        ivSearchMain.setOnClickListener(this);
        ivRequestMain = (ImageView) rootView.findViewById(R.id.iv_add_person_main);
        ivRequestMain.setOnClickListener(this);
        ivMenuMain = (ImageView) rootView.findViewById(R.id.iv_menu_main);
        ivMenuMain.setOnClickListener(this);
        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        showViewPager();
    }

    private void showViewPager() {
        pagerAdapter = new PagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        showChatMain();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        showChatMain();
                        break;
                    case 1:
                        showContactMain();
                        break;
                    case 2:
                        showInformationMain();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showChatMain() {
        tvTitleMain.setText("Chat");
        ivChatMain.setImageResource(R.drawable.ic_chat_selected);
        ivContactMain.setImageResource(R.drawable.ic_contact_unselected);
        ivInformationMain.setImageResource(R.drawable.ic_information_unselected);
        ivPlusMain.setVisibility(View.VISIBLE);
        ivSearchMain.setVisibility(View.VISIBLE);
        ivRequestMain.setVisibility(View.INVISIBLE);
        ivMenuMain.setVisibility(View.INVISIBLE);
    }

    private void showContactMain() {
        tvTitleMain.setText("Contact");
        ivChatMain.setImageResource(R.drawable.ic_chat_unselected);
        ivContactMain.setImageResource(R.drawable.ic_contact_selected);
        ivInformationMain.setImageResource(R.drawable.ic_information_unselected);
        ivPlusMain.setVisibility(View.INVISIBLE);
        ivSearchMain.setVisibility(View.VISIBLE);
        ivRequestMain.setVisibility(View.VISIBLE);
        ivMenuMain.setVisibility(View.INVISIBLE);
    }

    private void showInformationMain() {
        tvTitleMain.setText("Information");
        ivChatMain.setImageResource(R.drawable.ic_chat_unselected);
        ivContactMain.setImageResource(R.drawable.ic_contact_unselected);
        ivInformationMain.setImageResource(R.drawable.ic_information_selected);
        ivPlusMain.setVisibility(View.INVISIBLE);
        ivSearchMain.setVisibility(View.INVISIBLE);
        ivRequestMain.setVisibility(View.INVISIBLE);
        ivMenuMain.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_chat_main:
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(0);
                }
                break;
            case R.id.iv_contact_main:
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
                break;
            case R.id.iv_information_main:
                if (viewPager.getCurrentItem() != 2) {
                    viewPager.setCurrentItem(2);
                }
                break;
            case R.id.iv_plus_main:
                break;
            case R.id.iv_search_main:
                break;
            case R.id.iv_add_person_main:
                replaceFragmentFromFragment(SearchUserFragment.newInstance(), getActivity().getSupportFragmentManager());
                break;
            case R.id.iv_menu_main:
                showMenuMain();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showMenuMain() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), ivMenuMain);
        popupMenu.getMenuInflater().inflate(R.menu.popup_main_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_log_out:
                        mAuth.signOut();
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.SharePref.CAFE_CHAT_PREF, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(getActivity(), LogInActivity.class));
                        getActivity().finish();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

}
