package thachtv.cafechat.base;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Thinkpad on 10/04/2017.
 */

public abstract class BaseFireBase {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    protected FirebaseAuth getFirebaseAuth() {
        if (mAuth == null)
            return  FirebaseAuth.getInstance();
        else return mAuth;
    }

    protected DatabaseReference getDatabaseReference() {
        if (mDatabase == null)
            return  FirebaseDatabase.getInstance().getReference();
        else return mDatabase;
    }

    protected StorageReference getStorageReference() {
        if (mStorageRef == null)
            return  FirebaseStorage.getInstance().getReference();
        else return mStorageRef;
    }
}
