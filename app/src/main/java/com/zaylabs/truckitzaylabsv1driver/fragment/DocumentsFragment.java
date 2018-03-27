package com.zaylabs.truckitzaylabsv1driver.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;
import com.zaylabs.truckitzaylabsv1driver.R;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentsFragment extends Fragment implements View.OnClickListener {


    public DocumentsFragment() {
        // Required empty public constructor
    }

    private ImageView mCNICFRONT;
    private ImageView mCNICBACK;
    private ImageView mDLFRONT;
    private ImageView mDLBACK;
    private ImageView mREG;

    private Uri mCNICFRONTuri;
    private Uri mCNICBACKuri;
    private Uri mDLFRONTuri;
    private Uri mDLBACKuri;
    private Uri mREGuri;

    private TextView mCNICFRONTtextview;
    private TextView mCNICBACKtextview;
    private TextView mDLFRONTtextview;
    private TextView mDLBACKtextview;
    private TextView mREGtextview;

    private TextView mCNICFRONTtextviewStatus;
    private TextView mCNICBACKtextviewStatus;
    private TextView mDLFRONTtextviewStatus;
    private TextView mDLBACKtextviewStatus;
    private TextView mREGtextviewStatus;


    private StorageReference mImageRef;
    private DatabaseReference mDBRef;
    private FirebaseAuth mAuth;
    private String userID;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    private static final int CNICFRONTSAVE = 111;
    private static final int CNICBACKSAVE = 222;
    private static final int DLFRONTSAVE = 333;
    private static final int DLBACKSAVE = 444;
    private static final int REGSAVE = 555;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_documents, container, false);


        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDBRef = mDatabase.child("users").child("driver").child(userID);
        mImageRef = mStorageRef.child(userID);


        mCNICFRONT = view.findViewById(R.id.IMG_CNIC_Front);
        mCNICBACK = view.findViewById(R.id.IMG_CNIC_Back);
        mDLFRONT = view.findViewById(R.id.IMG_DL_FRONT);
        mDLBACK = view.findViewById(R.id.IMG_DL_BACK);
        mREG = view.findViewById(R.id.IMG_REG);

        mCNICFRONTtextview = view.findViewById(R.id.TextViewCNICFRONT);
        mCNICBACKtextview = view.findViewById(R.id.TextViewCNICBACK);
        mDLFRONTtextview = view.findViewById(R.id.TextViewDLFRONT);
        mDLBACKtextview = view.findViewById(R.id.TextViewDLBACK);
        mREGtextview = view.findViewById(R.id.TextViewREG);

        mCNICFRONTtextviewStatus = view.findViewById(R.id.TEXVIEWCNICFrontStatus);
        mCNICBACKtextviewStatus = view.findViewById(R.id.TEXVIEWCNICBackStatus);
        mDLFRONTtextviewStatus = view.findViewById(R.id.TEXVIEWDLFrontStatus);
        mDLBACKtextviewStatus = view.findViewById(R.id.TEXVIEWDLBackStatus);
        mREGtextviewStatus = view.findViewById(R.id.TEXVIEWREGStatus);


        mCNICFRONT.setOnClickListener(this);
        mCNICBACK.setOnClickListener(this);
        mDLFRONT.setOnClickListener(this);
        mDLBACK.setOnClickListener(this);
        mREG.setOnClickListener(this);

        updateStatus();

        return view;
    }


    @Override
    public void onClick(View v) {

        int i = v.getId();

        if (i == R.id.IMG_CNIC_Front) {
            choosecnicFRONTSAVE();
        } else if (i == R.id.IMG_CNIC_Back) {
            choosecnicBACKSAVE();
        } else if (i == R.id.IMG_DL_FRONT) {
            choosedlFRONTSAVE();
        } else if (i == R.id.IMG_DL_BACK) {
            choosedlBACKSAVE();
        } else if (i == R.id.IMG_REG) {
            chooseREGSAVE();
        }

    }

    // Start CNIC FRONT SAVE
    private void choosecnicFRONTSAVE() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CNICFRONTSAVE);
        }
    }


    private void saveCNICFRONT() {

        mCNICFRONT.setDrawingCacheEnabled(true);
        mCNICFRONT.buildDrawingCache();
        Bitmap bitmap = mCNICFRONT.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mImageRef.child("CNICFront").putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                mCNICFRONTuri = taskSnapshot.getDownloadUrl();
                mCNICFRONTtextview.setText(mCNICFRONTuri.toString());
                saveCNICFRONTMap();
            }
        });
    }

    public void saveCNICFRONTMap() {
        Map<String, Object> cnicFRONTUpdate = new HashMap<>();
        final String cnicFront = mCNICFRONTtextview.getText().toString();
        cnicFRONTUpdate.put("cnicFront", cnicFront);
        mDBRef.updateChildren(cnicFRONTUpdate);

    }

////End CNIC FRONT SAVE

    // Start CNIC BACK SAVE
    private void choosecnicBACKSAVE() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CNICBACKSAVE);
        }
    }


    private void saveCNICBACK() {

        mCNICBACK.setDrawingCacheEnabled(true);
        mCNICBACK.buildDrawingCache();
        Bitmap bitmap = mCNICBACK.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mImageRef.child("CNICBACK").putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                mCNICBACKuri = taskSnapshot.getDownloadUrl();
                mCNICBACKtextview.setText(mCNICBACKuri.toString());
                saveCNICBACKMap();
            }
        });
    }

    public void saveCNICBACKMap() {
        Map<String, Object> cnicBACKUpdate = new HashMap<>();
        final String cnicBack = mCNICBACKtextview.getText().toString();
        cnicBACKUpdate.put("cnicBack", cnicBack);
        mDBRef.updateChildren(cnicBACKUpdate);
    }

////End CNIC BACK SAVE

    // Start DL FRONT SAVE

    private void choosedlFRONTSAVE() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, DLFRONTSAVE);
        }
    }


    private void saveDLFRONT() {

        mDLFRONT.setDrawingCacheEnabled(true);
        mDLFRONT.buildDrawingCache();
        Bitmap bitmap = mDLFRONT.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mImageRef.child("DLFRONT").putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                mDLFRONTuri = taskSnapshot.getDownloadUrl();
                mDLFRONTtextview.setText(mDLFRONTuri.toString());
                saveDLFRONTMap();
            }
        });
    }

    public void saveDLFRONTMap() {
        Map<String, Object> dlFrontUpdate = new HashMap<>();
        final String dlFront = mDLFRONTtextview.getText().toString();
        dlFrontUpdate.put("dlFront", dlFront);
        mDBRef.updateChildren(dlFrontUpdate);
    }

////End DL FRONT SAVE


    // Start DL BACK SAVE
    private void choosedlBACKSAVE() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, DLBACKSAVE);
        }
    }


    private void saveDLBACK() {

        mDLBACK.setDrawingCacheEnabled(true);
        mDLBACK.buildDrawingCache();
        Bitmap bitmap = mDLBACK.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mImageRef.child("DLBACK").putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                mDLBACKuri = taskSnapshot.getDownloadUrl();
                mDLBACKtextview.setText(mDLBACKuri.toString());
                saveDLBACKMap();
            }
        });
    }

    public void saveDLBACKMap() {
        Map<String, Object> dlBackUpdate = new HashMap<>();
        final String dlBack = mDLBACKtextview.getText().toString();
        dlBackUpdate.put("dlBack", dlBack);
        mDBRef.updateChildren(dlBackUpdate);
    }

////End DL BACK SAVE

    // Start REG SAVE

    private void chooseREGSAVE() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REGSAVE);
        }
    }


    private void saveREG() {

        mREG.setDrawingCacheEnabled(true);
        mREG.buildDrawingCache();
        Bitmap bitmap = mREG.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mImageRef.child("REG").putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                mREGuri = taskSnapshot.getDownloadUrl();
                mREGtextview.setText(mREGuri.toString());
                saveREGMap();
            }
        });
    }

    public void saveREGMap() {
        Map<String, Object> REGUpdate = new HashMap<>();
        final String reg = mREGtextview.getText().toString();
        REGUpdate.put("reg", reg);
        mDBRef.updateChildren(REGUpdate);

    }


////End REG SAVE


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CNICFRONTSAVE:
                if (resultCode == getActivity().RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mCNICFRONT.setImageBitmap(imageBitmap);
                    saveCNICFRONT();
                    //encodeBitmapAndSaveToFirebase(imageBitmap);
                }
                break;

            case CNICBACKSAVE:
                if (resultCode == getActivity().RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mCNICBACK.setImageBitmap(imageBitmap);
                    saveCNICBACK();
                    //encodeBitmapAndSaveToFirebase(imageBitmap);
                }
                break;

            case DLFRONTSAVE:
                if (resultCode == getActivity().RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mDLFRONT.setImageBitmap(imageBitmap);
                    saveDLFRONT();
                    //encodeBitmapAndSaveToFirebase(imageBitmap);
                }
                break;

            case DLBACKSAVE:
                if (resultCode == getActivity().RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mDLBACK.setImageBitmap(imageBitmap);
                    saveDLBACK();
                    //encodeBitmapAndSaveToFirebase(imageBitmap);
                }
                break;

            case REGSAVE:
                if (resultCode == getActivity().RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mREG.setImageBitmap(imageBitmap);
                    saveREG();
                    //encodeBitmapAndSaveToFirebase(imageBitmap);
                }
                break;
        }
    }

    public void updateStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("cnicFront").getValue() != null)
                        mCNICFRONTtextviewStatus.setText("Uploaded");
                    if (dataSnapshot.child("cnicBack").getValue() != null)
                    mCNICBACKtextviewStatus.setText("Uploaded");
                    if (dataSnapshot.child("reg").getValue() != null)
                    mDLFRONTtextviewStatus.setText("Uploaded");
                    if (dataSnapshot.child("reg").getValue() != null)
                    mDLBACKtextviewStatus.setText("Uplaoded");
                    if (dataSnapshot.child("reg").getValue() != null)
                    mREGtextviewStatus.setText("Uploaded");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}












