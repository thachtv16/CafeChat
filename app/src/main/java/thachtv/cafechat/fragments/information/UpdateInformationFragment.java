package thachtv.cafechat.fragments.information;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import thachtv.cafechat.R;
import thachtv.cafechat.base.BaseFragment;
import thachtv.cafechat.define.Constant;

/**
 * Created by Thinkpad on 10/06/2017.
 */

public class UpdateInformationFragment extends BaseFragment implements View.OnClickListener {

    private DatabaseReference userReference;
    private FirebaseUser firebaseUser;

    private ImageView ivLeftUpdate;
    private TextView tvTitleUpdate;

    private EditText etUserNameUpdate;
    private EditText etPhoneUpdate;
    private Spinner spnGenderUpdate;
    private Button btnSaveUpdate;
    private ProgressBar pbContentUpdate;

    private String userNameUpdate;
    private String phoneUpdate;
    private String genderUpdate;

    public static UpdateInformationFragment newInstance() {
        UpdateInformationFragment addPersonFragment = new UpdateInformationFragment();
        return addPersonFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_infor, container, false);

        ivLeftUpdate = (ImageView) rootView.findViewById(R.id.iv_left_extra);
        ivLeftUpdate.setOnClickListener(this);
        tvTitleUpdate = (TextView) rootView.findViewById(R.id.tv_title_extra);
        etUserNameUpdate = (EditText) rootView.findViewById(R.id.et_user_name_update);
        etPhoneUpdate = (EditText) rootView.findViewById(R.id.et_phone_update);
        spnGenderUpdate = (Spinner) rootView.findViewById(R.id.spn_gender_update);
        btnSaveUpdate = (Button) rootView.findViewById(R.id.btn_save_update);
        btnSaveUpdate.setOnClickListener(this);
        pbContentUpdate = (ProgressBar) rootView.findViewById(R.id.pb_content_update);
        tvTitleUpdate.setText("Update Information");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(Constant.FirebaseDatabase.USERS).child(firebaseUser.getUid());

        showGender();
        getDataFromInformationFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_extra:
                getActivity().onBackPressed();
                break;
            case R.id.btn_save_update:
                pbContentUpdate.setVisibility(View.VISIBLE);
                userNameUpdate = etUserNameUpdate.getText().toString();
                userReference.child(Constant.FirebaseDatabase.USER_NAME).setValue(userNameUpdate);
                phoneUpdate = etPhoneUpdate.getText().toString();
                userReference.child(Constant.FirebaseDatabase.PHONE).setValue(phoneUpdate);
                userReference.child(Constant.FirebaseDatabase.GENDER).setValue(genderUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pbContentUpdate.setVisibility(View.INVISIBLE);
                            getActivity().onBackPressed();
                        }else {
                            pbContentUpdate.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity().getApplicationContext(), "Save Fail. Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }

    private void showGender() {
        List<String> list = new ArrayList<>();
        list.add("Male");
        list.add("Female");
        list.add("Other");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_multiple_choice);
        spnGenderUpdate.setAdapter(adapter);
        spnGenderUpdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderUpdate = spnGenderUpdate.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getDataFromInformationFragment() {
        Bundle bundle = getArguments();
        userNameUpdate = bundle.getString(Constant.FirebaseDatabase.USER_NAME);
        phoneUpdate = bundle.getString(Constant.FirebaseDatabase.PHONE);
        etUserNameUpdate.setText(userNameUpdate);
        etPhoneUpdate.setText(phoneUpdate);
    }

}
