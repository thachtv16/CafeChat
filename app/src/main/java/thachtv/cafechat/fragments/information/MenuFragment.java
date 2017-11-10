package thachtv.cafechat.fragments.information;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import thachtv.cafechat.R;
import thachtv.cafechat.application.CafeChat;
import thachtv.cafechat.base.BaseFragment;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.fragments.contact.SearchUserFragment;

/**
 * Created by Thinkpad on 10/30/2017.
 */

public class MenuFragment extends BaseFragment implements View.OnClickListener {

    private DatabaseReference userReference;
    private FirebaseUser firebaseUser;

    private CircleImageView civImageMenu;
    private TextView tvUserNameMenu;
    private RelativeLayout rlUserMenu;
    private RelativeLayout rlAddFriendMenu;
    private RelativeLayout rlPrivacyMenu;
    private RelativeLayout rlSecurityMenu;
    private RelativeLayout rlAboutCafeChatMenu;

    private CafeChat cafeChat = CafeChat.getInstance();


    public static MenuFragment newInstance() {
        MenuFragment menuFragment = new MenuFragment();
        return menuFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        civImageMenu = (CircleImageView) rootView.findViewById(R.id.civ_image_menu);
        tvUserNameMenu = (TextView) rootView.findViewById(R.id.tv_user_name_menu);
        rlUserMenu = (RelativeLayout) rootView.findViewById(R.id.rl_information);
        rlUserMenu.setOnClickListener(this);
        rlAddFriendMenu = (RelativeLayout) rootView.findViewById(R.id.rl_add_friend);
        rlAddFriendMenu.setOnClickListener(this);
        rlPrivacyMenu = (RelativeLayout) rootView.findViewById(R.id.rl_privacy);
        rlPrivacyMenu.setOnClickListener(this);
        rlSecurityMenu = (RelativeLayout) rootView.findViewById(R.id.rl_security);
        rlSecurityMenu.setOnClickListener(this);
        rlAboutCafeChatMenu = (RelativeLayout) rootView.findViewById(R.id.rl_information_cafe_chat);
        rlAboutCafeChatMenu.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(Constant.FirebaseDatabase.USERS).child(firebaseUser.getUid());

        addDataFromFirebase();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_information:
                replaceFragmentFromFragment(InformationFragment.newInstance(), getActivity().getSupportFragmentManager());
                break;
            case R.id.rl_add_friend:
                replaceFragmentFromFragment(SearchUserFragment.newInstance(), getActivity().getSupportFragmentManager());
                break;
            case R.id.rl_privacy:
                replaceFragmentFromFragment(PrivacyFragment.newInstance(), getActivity().getSupportFragmentManager());
                break;
            case R.id.rl_security:
                break;
            case R.id.rl_information_cafe_chat:
                break;
        }
    }

    private void addDataFromFirebase() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child(Constant.FirebaseDatabase.USER_NAME).getValue().toString();
                String linkAvatar = dataSnapshot.child(Constant.FirebaseDatabase.LINK_AVATAR).getValue().toString();

                tvUserNameMenu.setText(userName);

                if (isAdded() && getActivity() !=  null) {
                    cafeChat.loadImages(getActivity(), linkAvatar, civImageMenu);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
