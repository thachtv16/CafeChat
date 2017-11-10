package thachtv.cafechat.fragments.information;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import thachtv.cafechat.R;
import thachtv.cafechat.base.BaseFragment;
import thachtv.cafechat.define.Constant;

/**
 * Created by Thinkpad on 10/31/2017.
 */

public class PrivacyFragment extends BaseFragment {

    private FirebaseAuth mAuth;

    private TextView tvTitlePrivacy;
    private ImageView ivLeftBackPrivacy;

    private CheckBox cbEmailPrivacy;
    //private ImageView ivEmailPrivacy;
    private CheckBox cbPhonePrivacy;
    private CheckBox cbGenderPrivacy;

    private boolean isCheckedEmail;
    private boolean isCheckedPhone;
    private boolean isCheckedGender;

    public static PrivacyFragment newInstance() {
        PrivacyFragment privacyFragment = new PrivacyFragment();
        return privacyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_privacy, container, false);
        tvTitlePrivacy = (TextView) rootView.findViewById(R.id.tv_title_extra);
        ivLeftBackPrivacy = (ImageView) rootView.findViewById(R.id.iv_left_extra);
        cbEmailPrivacy = (CheckBox) rootView.findViewById(R.id.cb_email_privacy);
        //ivEmailPrivacy = (ImageView) rootView.findViewById(R.id.iv_email_privacy);
        cbPhonePrivacy = (CheckBox) rootView.findViewById(R.id.cb_phone_privacy);
        cbGenderPrivacy = (CheckBox) rootView.findViewById(R.id.cb_gender_privacy);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        checkedPrivacy();
        getKeyCheckedPrivacy();
    }

    private void getKeyCheckedPrivacy() {
        SharedPreferences sharedPreferencesPrivacy = getActivity().getSharedPreferences(Constant.SharePrefPrivacy.PRIVACY_PREF, Context.MODE_PRIVATE);
        isCheckedEmail = sharedPreferencesPrivacy.getBoolean(Constant.SharePrefPrivacy.EMAIL_PRIVACY_PREF, false);
        cbEmailPrivacy.setChecked(isCheckedEmail);

        isCheckedPhone = sharedPreferencesPrivacy.getBoolean(Constant.SharePrefPrivacy.PHONE_PRIVACY_PREF, false);
        cbPhonePrivacy.setChecked(isCheckedPhone);

        isCheckedGender = sharedPreferencesPrivacy.getBoolean(Constant.SharePrefPrivacy.GENDER_PRIVACY_REF, false);
        cbGenderPrivacy.setChecked(isCheckedGender);
    }

    private void checkedPrivacy() {
        final String currentUid = mAuth.getCurrentUser().getUid();
        cbEmailPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                saveSharedPref(Constant.SharePrefPrivacy.EMAIL_PRIVACY_PREF, currentUid, b);
            }
        });

        cbPhonePrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                saveSharedPref(Constant.SharePrefPrivacy.PHONE_PRIVACY_PREF, currentUid, b);
            }
        });

        cbGenderPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                saveSharedPref(Constant.SharePrefPrivacy.GENDER_PRIVACY_REF, currentUid, b);
            }
        });
    }

    private void saveSharedPref(String key, String currentUid, boolean b) {
        SharedPreferences sharedPreferencesPrivacy = getActivity().getSharedPreferences(Constant.SharePrefPrivacy.PRIVACY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesPrivacy.edit();
        editor.putBoolean(key, b);
        editor.putString(Constant.SharePrefPrivacy.CURRENT_UID, currentUid);
        editor.commit();
    }
}
