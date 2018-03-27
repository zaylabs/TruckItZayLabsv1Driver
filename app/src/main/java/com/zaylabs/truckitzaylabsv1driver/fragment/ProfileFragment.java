package com.zaylabs.truckitzaylabsv1driver.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.zaylabs.truckitzaylabsv1driver.R;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProfileFragment";
    private DatabaseReference mDatabase;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private EditText mName;
    private EditText mPhone;
    private Button mSave;
    private TextView mImageLink;
    private ImageView mUserDP;
    private String mPostKey;

    private StorageReference mImageRef;
    private DatabaseReference mDBRef;
    private FirebaseAuth mAuth;
    private String userID;
    private StorageReference mStorageRef;
    private Uri mDP;
    private EditText mCNIC;
    private EditText mRegNumber;




    public ProfileFragment() {



        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        // Get post key from intent

        mStorageRef = FirebaseStorage.getInstance().getReference();


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userID = mAuth.getCurrentUser().getUid();
        mDBRef = mDatabase.child("users").child("driver").child(userID);
        mName = view.findViewById(R.id.field_name);
        mPhone = view.findViewById(R.id.field_phone);
        mImageLink = view.findViewById(R.id.imagelink);
        mUserDP = view.findViewById(R.id.userDP);
        mSave = view.findViewById(R.id.button_update);
        mCNIC = view.findViewById(R.id.field_CNIC);
        mRegNumber = view.findViewById(R.id.field_field_CarReg);




        mUserDP.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mImageRef= mStorageRef.child(userID);
        getInfo();
        // Inflate the layout for this fragment
        return view;
    }

    private void chooseImage() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mUserDP.setImageBitmap(imageBitmap);
            saveimage();
            //encodeBitmapAndSaveToFirebase(imageBitmap);
        }

    }

    /*public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        mUserDP.setImageBitmap(imageEncoded);

    }
*/


    private void getInfo() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            boolean emailVerified = user.isEmailVerified();
            String uid = user.getUid();
            mName.setText(name);
            if (user.getPhotoUrl()!=null){
                String photodp = user.getPhotoUrl().toString();
                Picasso.with(getActivity()).load(photodp).resize(150,150).centerCrop().into(mUserDP);
            }}
            mDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    mPhone.setText(phone);
                    String cnic = dataSnapshot.child("cnic").getValue().toString();
                    mCNIC.setText(cnic);
                    String reg_num = dataSnapshot.child("reg_number").getValue().toString();
                    mRegNumber.setText(reg_num);

                    if (dataSnapshot.child("imageURI").getValue()!=null) {
                    String imageuri = dataSnapshot.child("imageURI").getValue().toString();
                        mImageLink.setText(imageuri);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    public void saveimage(){
        // Get the data from an ImageView as bytes

        mUserDP.setDrawingCacheEnabled(true);
        mUserDP.buildDrawingCache();
        Bitmap bitmap = mUserDP.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mImageRef.child("DP").putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                mDP = taskSnapshot.getDownloadUrl();
                mImageLink.setText(mDP.toString());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(mImageLink.getText().toString()))
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                }
                            }
                        });

                saveMap();
            }
        });

    }
    public void saveMap(){
        Map<String, Object> userUpdates = new HashMap<>();
        final String imageURI = mImageLink.getText().toString();
        userUpdates.put("imageURI", imageURI);

        final String phone = mPhone.getText().toString();
        userUpdates.put("phone", phone);
        final String cnic = mCNIC.getText().toString();
        userUpdates.put("cnic", cnic);
        final String reg_number = mRegNumber.getText().toString();
        userUpdates.put("reg_number", reg_number);

        mDBRef.updateChildren(userUpdates);
    }
    private void saveInfo() {
        saveMap();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(mName.getText().toString())
                .setPhotoUri(Uri.parse(mImageLink.getText().toString()))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            Toast.makeText(getActivity(), "User profile updated.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.userDP) {
            chooseImage();
        } else if (i == R.id.button_update) {
            saveInfo();
        }
    }


}
