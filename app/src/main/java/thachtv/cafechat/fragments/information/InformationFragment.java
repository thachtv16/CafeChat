package thachtv.cafechat.fragments.information;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import thachtv.cafechat.R;
import thachtv.cafechat.application.CafeChat;
import thachtv.cafechat.base.BaseFragment;
import thachtv.cafechat.define.Constant;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Thinkpad on 09/30/2017.
 */

public class InformationFragment extends BaseFragment {

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private ImageView ivAvatar;
    private TextView tvUserName;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvGender;
    private Button btnUpdate;
    private ProgressBar pbContentInformation;

    private String email;
    private String userName;
    private String linkAvatar;
    private String phone;
    private String gender;

    private CafeChat cafeChat = CafeChat.getInstance();

    public static InformationFragment newInstance() {
        InformationFragment informationFragment = new InformationFragment();
        return informationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);
        ivAvatar = (ImageView) rootView.findViewById(R.id.civ_image_avatar);
        registerForContextMenu(ivAvatar);
        tvUserName = (TextView) rootView.findViewById(R.id.tv_user_name);
        tvEmail = (TextView) rootView.findViewById(R.id.tv_email_information);
        tvPhone = (TextView) rootView.findViewById(R.id.tv_phone_information);
        tvGender = (TextView) rootView.findViewById(R.id.tv_gender_information);
        btnUpdate = (Button) rootView.findViewById(R.id.btn_update_information);
        pbContentInformation = (ProgressBar) rootView.findViewById(R.id.pb_content_information);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constant.FirebaseDatabase.USERS).child(firebaseUser.getUid());

        nextUpdateInformation();
        addDataFromFirebase();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.change_image_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_image:

                /*CropImage.activity()
                        .start(getContext(), this);*/

                Intent imageIntent = new Intent(Intent.ACTION_PICK);
                imageIntent.setType("image/*");
                startActivityForResult(imageIntent, Constant.InformationImage.FOLDER_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.menu_camera:
                if (isStoragePermissionGranted()) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, Constant.InformationImage.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.InformationImage.FOLDER_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            CropImage.activity(uri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(300, 300)
                    .start(getContext(), this);

        }/*else if (requestCode == Constant.InformationImage.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.d("Image=====", "test");
            StorageReference path = storageReference.child("photos_avatar").child(firebaseUser.getUid() + ".jpg");
            path.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        linkAvatar = task.getResult().getDownloadUrl().toString();
                        databaseReference.child("link_avatar").setValue(linkAvatar).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    pbContentInformation.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getActivity(), "Update Image Successful", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else {
                        pbContentInformation.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }*/

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pbContentInformation.setVisibility(View.VISIBLE);
                Uri resultUri = result.getUri();
                StorageReference path = storageReference.child("photos_avatar").child(firebaseUser.getUid() + ".jpg");
                path.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            linkAvatar = task.getResult().getDownloadUrl().toString();
                            Log.d("InformationFragment", linkAvatar);
                            databaseReference.child("link_avatar").setValue(linkAvatar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Update Image Successful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(getActivity(), "Update Image Error", Toast.LENGTH_SHORT).show();
                        }
                        pbContentInformation.setVisibility(View.INVISIBLE);
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                pbContentInformation.setVisibility(View.INVISIBLE);
                Exception error = result.getError();
            }
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, Constant.InformationImage.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void nextUpdateInformation() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateInformationFragment updateInformationFragment = UpdateInformationFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("user_name", userName);
                bundle.putString("phone", phone);
                updateInformationFragment.setArguments(bundle);
                replaceFragmentFromFragment(updateInformationFragment, getActivity().getSupportFragmentManager());
            }
        });
    }

    private void addDataFromFirebase() {
        Log.d("InformationFragment", "abcdef");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("email").getValue().toString();
                userName = dataSnapshot.child("user_name").getValue().toString();
                linkAvatar = dataSnapshot.child("link_avatar").getValue().toString();
                phone = dataSnapshot.child("phone").getValue().toString();
                gender = dataSnapshot.child("gender").getValue().toString();

                tvUserName.setText(userName);
                tvEmail.setText(email);
                tvPhone.setText(phone);
                tvGender.setText(gender);

                if (isAdded() && getActivity() !=  null) {
                    cafeChat.loadImages(getActivity(), linkAvatar, ivAvatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
