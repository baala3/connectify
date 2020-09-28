package com.example.socialnetwork;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    CircleImageView settings_profile_image;
    EditText settings_profile_status,settings_user_name,settings_user_fullname,settings_user_country,
            settings_dob,settings_gender,settings_relation_status;
    Button settings_update_btn;
    DatabaseReference settingUserRef;
    FirebaseAuth mAuth;
    Uri resultUri;
    String currentUserId;
    final static  int GALLERY_PICK=1;
    DatabaseReference reference;
    ProgressDialog loadingBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_settings, container, false);
        mAuth=FirebaseAuth.getInstance();
        resultUri=Uri.parse("android.resource://com.example.socialnetwork/drawable/profile");
        currentUserId=mAuth.getCurrentUser().getUid();
        settingUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        reference= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
        loadingBar=new ProgressDialog(getActivity());
        settings_profile_status=v.findViewById(R.id.settings_profile_status);
        settings_user_name=v.findViewById(R.id.settings_user_name);
        settings_user_fullname=v.findViewById(R.id.settings_user_fullname);
        settings_user_country=v.findViewById(R.id.settings_user_country);
        settings_dob=v.findViewById(R.id.settings_dob);
        settings_gender=v.findViewById(R.id.settings_gender);
        settings_relation_status=v.findViewById(R.id.settings_relation_status);
        settings_profile_image=v.findViewById(R.id.settings_profile_image);
        settings_update_btn=v.findViewById(R.id.settings_update_btn);
        settingUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String name=snapshot.child("name").getValue().toString();
                    String fullName=snapshot.child("fullName").getValue().toString();
                    String dob=snapshot.child("dob").getValue().toString();
                    String country=snapshot.child("country").getValue().toString();
                    String gender=snapshot.child("gender").getValue().toString();
                    String profileImage=snapshot.child("profileImage").getValue().toString();
                    String relationShipStatus=snapshot.child("relationShipStatus").getValue().toString();
                    String status=snapshot.child("status").getValue().toString();
                    Picasso.with(getActivity()).load(profileImage).placeholder(R.drawable.profile).into(settings_profile_image);
                    settings_profile_status.setText(status);
                    settings_user_name.setText(name);
                    settings_user_fullname.setText(fullName);
                    settings_dob.setText(dob);
                    settings_user_country.setText(country);
                    settings_gender.setText(gender);
                    settings_relation_status.setText(relationShipStatus);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        settings_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAccountInfo();
            }
        });
        settings_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_PICK);
            }
        });
        return v;
    }

    private void validateAccountInfo() {

        String username=settings_user_name.getText().toString();
        String fullname=settings_user_fullname.getText().toString();
        String gender=settings_gender.getText().toString();
        String dob=settings_dob.getText().toString();
        String country=settings_user_country.getText().toString();
        String status=settings_profile_status.getText().toString();
        String retaltionstaus=settings_relation_status.getText().toString();
        if(Ch(username)&&Ch(fullname)&&Ch(gender)&&Ch(dob)&&Ch(country)&&Ch(status)&&Ch(retaltionstaus))
        {
            updateAccountInfo(username,fullname,gender,dob,country,status,retaltionstaus);


        }
        else
        {
            Toast.makeText(getActivity(),"Please Fill all fields",Toast.LENGTH_LONG).show();
        }
    }

    private void updateAccountInfo(String username, String fullname, String gender, String dob, String country, String status, String retaltionstaus) {
        loadingBar.setTitle("Updating Account");
        loadingBar.setMessage("Please Wait....Image is uploading");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
        StorageReference filePath= FirebaseStorage.getInstance().getReference().child("Profile Images").child(currentUserId+".jpg");

        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {

                    FirebaseStorage.getInstance().getReference().child("Profile Images").child(FirebaseAuth.getInstance().getUid()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String downloadUrl = uri.toString();
                            reference.child("profileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Profile Success retreived", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    loadingBar.dismiss();
                                }
                            });
                        }
                    });

                }
                else
                {
                    loadingBar.dismiss();
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });





        HashMap updateAccount=new HashMap();
        updateAccount.put("country",country);
        updateAccount.put("dob",dob);
        updateAccount.put("fullName",fullname);
        updateAccount.put("name",username);
        updateAccount.put("gender",gender);
        updateAccount.put("status",status);
        updateAccount.put("relationShipStatus",retaltionstaus);
        loadingBar.setMessage("Please Wait....Info is uploading");
        loadingBar.show();

        settingUserRef.updateChildren(updateAccount).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    loadingBar.dismiss();
                    Toast.makeText(getActivity(),"Success",Toast.LENGTH_LONG).show();
                }

                else
                {

                    loadingBar.dismiss();
                    Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    boolean Ch(String s)
    {
        return  (s.length()>0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            CropImage.activity(ImageUri)
                    .start(getContext(), this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.getUri();
                settings_profile_image.setImageURI(resultUri);
            }
        }
    }
}