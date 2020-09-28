package com.example.socialnetwork;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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


    FrameLayout parentframelayout;
    String log="SignUpFrag";
    TextView alreadyhaveAccount;
    ProgressDialog loadingBar;
    EditText email,password,cnfpassword;
    Button signupbutton;
    ImageButton closebutton;
    FirebaseAuth firebaseAuth;
    String EMAIL_PATTERN="[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    FirebaseFirestore firebaseFirestore;
    CircleImageView signimageView;
    LinearLayout signupconatiner;
    Uri resultUri;

//------
LinearLayout setupconatiner;
    CircleImageView setUPImage,add_image_bth;
    EditText setUpName,setUpFullName,setUpCountry;
    Button setUpAccount;
    TextView setUpBack;
    final static  int GALLERY_PICK=1;
    StorageReference userProfileImage;
    DatabaseReference reference;
    public RegisterActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (RegisterActivity) activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_sign_up, container, false);
        loadingBar=new ProgressDialog(activity);
        signupconatiner=v.findViewById(R.id.signupconatiner);
        alreadyhaveAccount=v.findViewById(R.id.signuphaveaccount);
        email=v.findViewById(R.id.signupemail);
        password=v.findViewById(R.id.signuppassword);
        cnfpassword=v.findViewById(R.id.signupconfirmpassword);
        signupbutton=v.findViewById(R.id.signupbutton);
        closebutton=v.findViewById(R.id.signupclose);
        parentframelayout=activity.findViewById(R.id.registerframelayout);
        signimageView=v.findViewById(R.id.imageView);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        textWatchers();

        alreadyhaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment(),RegisterActivity.SIGN_UP,RegisterActivity.SIGN_IN);
            }
        });
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });

        /////////------------------------
        resultUri=Uri.parse("android.resource://com.example.socialnetwork/drawable/profile");
        setupconatiner=v.findViewById(R.id.setupconatiner);
        userProfileImage= FirebaseStorage.getInstance().getReference().child("Profile Images");
        setUPImage=v.findViewById(R.id.setUPImage);
        setUPImage.setImageURI(resultUri);
        add_image_bth=v.findViewById(R.id.add_image_bth);
        setUpName=v.findViewById(R.id.setUpName);
        setUpFullName=v.findViewById(R.id.setUpFullName);
        setUpCountry=v.findViewById(R.id.setUpCountry);
        setUpAccount=v.findViewById(R.id.setUpAccount);
        setUpBack=v.findViewById(R.id.setUpBack);
        setUptextWatches();
        setUpAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
        }
        });
        setUpBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupconatiner.setAlpha(1f);
                setupconatiner.setVisibility(View.GONE);
                setupconatiner.animate()

                        .alpha(0f)
                        .setDuration(1000)
                        .setListener(null);



                signupconatiner.setAlpha(0f);
                signupconatiner.setVisibility(View.VISIBLE);
                signupconatiner.animate()
                        .alpha(1f)
                        .setDuration(1000)
                        .setListener(null);

            }
        });
        add_image_bth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_PICK);

            }
        });

        return  v;
    }
    private void setUptextWatches() {
        setUpFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setUpcheckinputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setUpName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setUpcheckinputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setUpCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setUpcheckinputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setUpcheckinputs() {

            if(!TextUtils.isEmpty(setUpFullName.getText()))
            {
                if (!TextUtils.isEmpty(setUpName.getText()) ) {
                    if (!TextUtils.isEmpty(setUpCountry.getText()) ) {
                        setUpAccount.setEnabled(true);
                        setUpAccount.setTextColor(Color.rgb(255, 255, 255));

                    }
                    else {
                        setUpAccount.setEnabled(false);
                        setUpAccount.setTextColor(Color.argb(55, 255, 255, 255));
                    }
                }
                else
                {
                    setUpAccount.setEnabled(false);
                    setUpAccount.setTextColor(Color.argb(55,255,255,255));
                }
            }

            else
            {
                setUpAccount.setEnabled(false);
                setUpAccount.setTextColor(Color.argb(55,255,255,255));
            }
        }


    private void textWatchers() {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkinputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkinputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cnfpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkinputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void checkinputs(){
        if(!TextUtils.isEmpty(email.getText()))
        {

            if (!TextUtils.isEmpty(password.getText()) && password.length() >= 8) {
                if (!TextUtils.isEmpty(cnfpassword.getText()) && cnfpassword.length() >= 8) {
                    signupbutton.setEnabled(true);
                    signupbutton.setTextColor(Color.rgb(255, 255, 255));

                }
                else {
                    signupbutton.setEnabled(false);
                    signupbutton.setTextColor(Color.argb(55, 255, 255, 255));
                }
            }
            else
            {
                signupbutton.setEnabled(false);
                signupbutton.setTextColor(Color.argb(55,255,255,255));
            }
        }

        else
        {
            signupbutton.setEnabled(false);
            signupbutton.setTextColor(Color.argb(55,255,255,255));
        }
    }
    public void  checkEmailAndPassword(){
        Drawable erroricon=getResources().getDrawable(R.drawable.erroricon);
        erroricon.setBounds(0,0,erroricon.getIntrinsicWidth(),erroricon.getIntrinsicHeight());
        if(email.getText().toString().matches(EMAIL_PATTERN))
        {
            if(password.getText().toString().equals(cnfpassword.getText().toString()) && password.getText().toString().length()>=8)
            {
                signupconatiner.setAlpha(1f);
                signupconatiner.setVisibility(View.GONE);
                signupconatiner.animate()
                        .alpha(0f)
                        .setDuration(1000)
                        .setListener(null);



                setupconatiner.setAlpha(0f);
                setupconatiner.setVisibility(View.VISIBLE);
                setupconatiner.animate()
                        .alpha(1f)
                        .setDuration(1000)
                        .setListener(null);
            }
            else {
                cnfpassword.setError("password does not matched",erroricon);
            }

        }
        else
        {
            email.setError("Invalid Email Address",erroricon);

        }


    }
    private void createAccount() {
        loadingBar.setTitle("Creating New Account");
        loadingBar.setMessage("Please Wait....Account is creating");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        signupbutton.setEnabled(false);
        signupbutton.setTextColor(Color.argb(55,255,255,255));
        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            loadingBar.dismiss();

                            Map<String,Object> user=new HashMap<>();
                            user.put("fullName",setUpFullName.getText().toString());
                            user.put("name",setUpName.getText().toString());
                            user.put("country",setUpCountry.getText().toString());
                            user.put("status","hey there ");
                            user.put("gender","none");
                            user.put("dob","none");
                            user.put("profileImage","null");
                            user.put("relationShipStatus","none");
                            loadingBar.setMessage("Please Wait....Uploading Image");
                            reference= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
                            final StorageReference filepath = userProfileImage.child(FirebaseAuth.getInstance().getUid() + ".jpg");
                            filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                       FirebaseStorage.getInstance().getReference().child("Profile Images").child(FirebaseAuth.getInstance().getUid()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final String downloadUrl = uri.toString();
                                                reference.child("profileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(activity, "Profile Success retreived", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                        loadingBar.dismiss();
                                                    }
                                                });
                                            }
                                        });

                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            loadingBar.setMessage("Please Wait....saving Data");
                            loadingBar.show();
                            reference.updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {

                                        loadingBar.dismiss();
                                        Toast.makeText(activity,"successfully saved your data",Toast.LENGTH_LONG).show();
                                        main();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(activity,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }


                                }
                            });
           }
                        else
                        {
                            signupbutton.setEnabled(true);
                            signupbutton.setTextColor(Color.rgb(255,255,255));
                            Toast.makeText(activity,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }
    private void main() {
        Intent i=new Intent(activity,MainActivity.class);
        startActivity(i);
        activity.finish();
    }
    private void setFragment(Fragment fragment,int PrevFrag,int CurrentFrag) {
        RegisterActivity.currentFrag=CurrentFrag;
        RegisterActivity.prevFrag=PrevFrag;
        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_left,R.anim.slide_out_from_right).replace(parentframelayout.getId(),fragment).commit();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri  ImageUri = data.getData();
            CropImage.activity(ImageUri)
                    .start(getContext(), this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.getUri();
               setUPImage.setImageURI(resultUri);
          }
        }
    }
}