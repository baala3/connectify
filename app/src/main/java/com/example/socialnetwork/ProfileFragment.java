package com.example.socialnetwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
ImageButton user_saves, user_posts;
Button edit_profile,log_out;
    ////
    RecyclerView my_post_list,my_save_list;
    DatabaseReference likeRef;
    List<String> savedList=new ArrayList<>();
    List<Post> postList=new ArrayList<>();
    DatabaseReference userRef;
    Boolean likeChecker=false,editChecker=false;

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
        edit_profile=v.findViewById(R.id.edit_profile);
        log_out=v.findViewById(R.id.log_out);


        myFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setfragment("My Friends",new FriendsFragment(),MainActivity2.FRIENDS);
            }
        });
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setfragment("Settings",new SettingsFragment(),MainActivity2.SETTINGS);
            }
        });
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.confirmDialog.show();
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
                 //   user_posts.setText(fullName);
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
                    if(countFrds==1)
                    myFriends.setText(countFrds+" Friend");
                    else
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


        //////
        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        likeRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        my_post_list=v.findViewById(R.id.my_post_list);
        my_save_list=v.findViewById(R.id.my_save_list);
        user_posts=v.findViewById(R.id.user_posts);
        user_saves=v.findViewById(R.id.user_saves);
        my_post_list.setHasFixedSize(false);
        my_post_list.setNestedScrollingEnabled(false);
        my_save_list.setHasFixedSize(false);
        my_save_list.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getActivity(),2);


        LinearLayoutManager layoutManager=new GridLayoutManager(getActivity(),2);

        savesMyPosts();
        my_post_list.setLayoutManager(linearLayoutManager);
        my_save_list.setLayoutManager(layoutManager);
        displayMyPosts();


        user_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_post_list.setVisibility(View.VISIBLE);
                my_save_list.setVisibility(View.GONE);
                user_posts.setImageResource(R.drawable.ic_baseline_apps_24);
                user_saves.setImageResource(R.drawable.ic_baseline_bookmark_border_24);

            }
        });
        user_saves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_posts.setImageResource(R.drawable.ic_baseline_grid_on_24);
                user_saves.setImageResource(R.drawable.ic_baseline_bookmark_24);
                my_post_list.setVisibility(View.GONE);
                my_save_list.setVisibility(View.VISIBLE);
            }
        });
        return  v;
    }

    private void displayMyPosts() {
        Query sortMyPost=  postRef.orderByChild("uid").startAt(currentUserId).endAt(currentUserId+"\uf8ff");

        FirebaseRecyclerOptions<Post> options=new FirebaseRecyclerOptions.Builder<Post>().setQuery(sortMyPost,Post.class).build();
        FirebaseRecyclerAdapter<Post, MyPostViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Post, MyPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MyPostViewHolder holder, final int position, @NonNull final Post model) {
              //  holder.setPostImage(holder.itemView.getContext(),model.getPostImage());
                Picasso.with(holder.itemView.getContext()).load(model.getPostImage()).into(holder.grid_image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(holder.itemView.getContext(),ImageViewActivity.class);
                        i.putExtra("postKey",getRef(position).getKey());
                        i.putExtra("comment","false");
                        i.putExtra("uid",model.getUid());
                        holder.itemView.getContext().startActivity(i);
                    }
                });



            }

            @NonNull
            @Override
            public MyPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(getActivity()).inflate(R.layout.all_grid_layout,parent,false);
                return  new MyPostViewHolder(v);

            }
        };
        my_post_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
    public static class MyPostViewHolder extends RecyclerView.ViewHolder{

        ImageView grid_image;
        public MyPostViewHolder(@NonNull View itemView) {
            super(itemView);
            grid_image=itemView.findViewById(R.id.grid_image);
        }



    }
    MainActivity2 activity;
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity= (MainActivity2) activity;
    }
    private void savesMyPosts() {
        Query sortMyPost=  FirebaseDatabase.getInstance().getReference().child("Saved").child(currentUserId);

        FirebaseRecyclerOptions<Post> options=new FirebaseRecyclerOptions.Builder<Post>().setQuery(sortMyPost,Post.class).build();
        FirebaseRecyclerAdapter<Post, MySavedViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Post, MySavedViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MySavedViewHolder holder, final int position, @NonNull final Post model) {
                /*holder.setPostImage(holder.itemView.getContext(),model.getPostImage());*/
                Picasso.with(holder.itemView.getContext()).load(model.getPostImage()).into(holder.grid_image);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(holder.itemView.getContext(),ImageViewActivity.class);
                        i.putExtra("postKey",getRef(position).getKey());
                        i.putExtra("uid",model.getUid());
                        i.putExtra("comment","false");
                        holder.itemView.getContext().startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public MySavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(getActivity()).inflate(R.layout.all_grid_layout,parent,false);
                return  new MySavedViewHolder(v);

            }
        };
        my_save_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
    public static class MySavedViewHolder extends RecyclerView.ViewHolder{

        ImageView grid_image;
        public MySavedViewHolder(@NonNull View itemView) {
            super(itemView);
            grid_image=itemView.findViewById(R.id.grid_image);
        }




    }



}