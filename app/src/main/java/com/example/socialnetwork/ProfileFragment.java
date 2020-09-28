package com.example.socialnetwork;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    TextView settings_profile_status,settings_user_name,settings_user_fullname,settings_user_country,
            settings_dob,settings_gender,settings_relation_status;
    DatabaseReference profileUserRef;
    FirebaseAuth mAuth;
    String currentUserId;
    Button myPosts,myFriends;
    DatabaseReference frdRef,postRef;
    long countFrds=0,countPosts=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        frdRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        profileUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        settings_profile_status=v.findViewById(R.id.settings_profile_status);
        settings_user_name=v.findViewById(R.id.settings_user_name);
        settings_user_fullname=v.findViewById(R.id.settings_user_fullname);
        settings_user_country=v.findViewById(R.id.settings_user_country);
        settings_dob=v.findViewById(R.id.settings_dob);
        settings_gender=v.findViewById(R.id.settings_gender);
        settings_relation_status=v.findViewById(R.id.settings_relation_status);
        settings_profile_image=v.findViewById(R.id.settings_profile_image);
        myPosts=v.findViewById(R.id.person_posts);
        myFriends=v.findViewById(R.id.person_friends);
        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setfragment("My Posts",new MyPostsFragment(),MainActivity.MY_POST);
            }
        });
        myFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setfragment("My Friends",new FriendsFragment(),MainActivity.FRIENDS);
            }
        });

        profileUserRef.addValueEventListener(new ValueEventListener() {
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
                    settings_user_name.setText("@"+name);
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
        frdRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    countFrds=snapshot.getChildrenCount();
                    myFriends.setText(countFrds+" Friends");

                }
                else
                {
                    myFriends.setText("No Friends");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        postRef.orderByChild("uid").startAt(currentUserId).endAt(currentUserId+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    countPosts=snapshot.getChildrenCount();
                    myPosts.setText(countPosts+" Posts");

                }
                else
                {
                    myPosts.setText("No Posts");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return  v;
    }
    MainActivity activity;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity= (MainActivity) activity;
    }
    public  void setfragment(String title,Fragment fragment,int prevFrag)
    {
        if(MainActivity.currentfrag!=prevFrag)
        {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle(title);
            if(MainActivity.currentfrag!=-1)
                activity.navigationView.getMenu().getItem(MainActivity.currentfrag).setChecked(false);
            activity.navigationView.getMenu().getItem(prevFrag).setChecked(true);
            MainActivity.currentfrag=prevFrag;
            activity.getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.fade_n,R.anim.fade_out).replace( activity.frameLayout.getId(),fragment).commit();

        }
    }

}