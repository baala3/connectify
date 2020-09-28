package com.example.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFriendsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FindFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindFriendsFragment newInstance(String param1, String param2) {
        FindFriendsFragment fragment = new FindFriendsFragment();
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

    EditText searchBox;
    ImageButton searchIcon;
    RecyclerView searchRecycler;
    DatabaseReference allUsersRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_find_friends, container, false);

        allUsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        searchBox=v.findViewById(R.id.searchBox);
        searchIcon=v.findViewById(R.id.searchIcon);
        searchRecycler=v.findViewById(R.id.searchRecycler);
        searchRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        searchRecycler.setLayoutManager(linearLayoutManager);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String givenInput=searchBox.getText().toString();
                searchBoxInputs(givenInput);
            }
        });


        return  v;
    }

    private void searchBoxInputs(String givenInput) {

        Toast.makeText(getActivity(),"searching",Toast.LENGTH_LONG).show();
        Query searchUsersQuery=allUsersRef.orderByChild("fullName").startAt(givenInput).endAt(givenInput+"\uf8ff");

        FirebaseRecyclerOptions<FindFriends> options=new FirebaseRecyclerOptions.Builder<FindFriends>().setQuery(searchUsersQuery,FindFriends.class).build();

        FirebaseRecyclerAdapter<FindFriends,findFriendsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FindFriends, findFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull findFriendsViewHolder holder, final int position, @NonNull final FindFriends model) {

                holder.setFullName(model.getFullName());
                holder.setProfileImage(getActivity(), model.getProfileImage());
                holder.setStatus(model.getStatus());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id=getRef(position).getKey();
                        Intent PersonI=new Intent(getActivity(),PersonActivity.class);
                        PersonI.putExtra("visit_user_id",visit_user_id);
                        startActivity(PersonI);
                    }
                });
            }

            @NonNull
            @Override
            public findFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(getActivity()).inflate(R.layout.all_users_display_layout,parent,false);
                return  new findFriendsViewHolder(v);
            }
        };
        searchRecycler.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public  static class findFriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public findFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setProfileImage(Context ctx,String all_users_profile_image) {
            CircleImageView all_users_profile_imag=mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(all_users_profile_image).placeholder(R.drawable.profile).into(all_users_profile_imag);
        }
        public void setFullName(String fullName) {
            TextView myName=mView.findViewById(R.id.all_users_name);
            myName.setText(fullName);
        }
        public void setStatus(String status) {
            TextView mystatus=mView.findViewById(R.id.all_users_status);
            mystatus.setText(status);
        }
    }
}