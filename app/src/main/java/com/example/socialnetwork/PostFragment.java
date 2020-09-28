package com.example.socialnetwork;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
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

    ImageButton select_post_image;
    EditText post_description;
    Button update_post_btn;
    static  final  int GALLERY_PICK=1;
    Uri resultUri;
    String description;
    String downloadUrl;
    StorageReference postImageRef;
    DatabaseReference userRef,postRef;
    FirebaseAuth mAuth;
    String currentUserId;
    String saveCurrentDate,saveCurrentTime, postRandomName;
    FrameLayout parentframelayout;

    long countPost=0;
    ProgressDialog loadingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_post, container, false);
        loadingBar=new ProgressDialog(activity);
      mAuth=FirebaseAuth.getInstance();
      currentUserId=mAuth.getCurrentUser().getUid();
        postImageRef= FirebaseStorage.getInstance().getReference();
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        select_post_image=v.findViewById(R.id.select_post_image);
        post_description=v.findViewById(R.id.click_post_description);
        update_post_btn=v.findViewById(R.id.update_post_btn);
        parentframelayout=getActivity().findViewById(R.id.main_container);
        select_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        update_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              validatePostInfo();
            }
        });


  return  v;
    }

    private void validatePostInfo() {
         description=post_description.getText().toString();
        if(resultUri==null)
        {
            Toast.makeText(getActivity(),"please select image",Toast.LENGTH_LONG).show();
        }
      else  if(description.trim().length()==0)
        {
            Toast.makeText(getActivity(),"please say about image",Toast.LENGTH_LONG).show();
        }
      else
        {
            loadingBar.setTitle("Add new Post");
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            storeImageToStorage();
        }
    }

    private void storeImageToStorage() {
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd MMM yyyy z");
      saveCurrentDate=currentDate.format(calForDate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime=currentTime.format(calForTime.getTime());

        postRandomName =saveCurrentDate+saveCurrentTime;

        StorageReference filePath=postImageRef.child("Post Images").child(resultUri.getLastPathSegment()+postRandomName+".jpg");

        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    FirebaseStorage.getInstance().getReference().child("Post Images").child(resultUri.getLastPathSegment()+postRandomName+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl=uri.toString();
                            savingPostInfoToDataBase();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                    Toast.makeText(getActivity(),"Uploaded success",Toast.LENGTH_LONG).show();

                }
                else
                {
                    Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void savingPostInfoToDataBase() {

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    countPost=snapshot.getChildrenCount();
                }
                else
                {
                    countPost=0;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName=snapshot.child("fullName").getValue().toString();
                String profileImage=snapshot.child("profileImage").getValue().toString();
                HashMap postMap=new HashMap();
                postMap.put("uid",currentUserId);
                postMap.put("date",saveCurrentDate);
                postMap.put("time",saveCurrentTime);
                postMap.put("description",description);
                postMap.put("postImage",downloadUrl);
                postMap.put("fullName",userName);
                postMap.put("profileImage",profileImage);
                postMap.put("counter",countPost);

                postRef.child(currentUserId+postRandomName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {

                            setfragment("Home",new HomeFragment(), MainActivity.HOME);
                        loadingBar.dismiss();
                            Toast.makeText(getActivity(),"Grand Success",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
loadingBar.dismiss();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_PICK);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK && resultCode== Activity.RESULT_OK && data!=null) {
            Uri ImageUri = data.getData();
            CropImage.activity(ImageUri)
                    .start(getContext(), this);
        }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == Activity.RESULT_OK) {
                     resultUri = result.getUri();
                    select_post_image.setImageURI(resultUri);
                }
        }
    }

    public  void setfragment(String title,Fragment fragment,int prevFrag)
    {

            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle(title);
            if(activity.currentfrag!=-1)
                activity.navigationView.getMenu().getItem(activity.currentfrag).setChecked(false);
            activity.navigationView.getMenu().getItem(prevFrag).setChecked(true);
            activity.currentfrag=prevFrag;
            activity.getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.fade_n,R.anim.fade_out).replace(activity.frameLayout.getId(),fragment).commit();


    }
    public MainActivity activity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }
}