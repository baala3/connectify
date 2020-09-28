package com.example.socialnetwork;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
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

    EditText memail,password;
    FrameLayout parentframelayout;
ImageView google;
    Button signInbutton;
    TextView dontHaveAccount;
    FirebaseAuth firebaseAuth;
    TextView forgotpassword;
    String EMAIL_PATTERN="[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    Dialog forgot_password_dialog;
    EditText email;
    Button resetbutton;
    LinearLayout viewGroup;
    ImageView imageicon;
    TextView texticon;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    ProgressDialog loadingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_sign_in, container, false);
        loadingBar=new ProgressDialog(getActivity());
        mAuth=FirebaseAuth.getInstance();
        dontHaveAccount=v.findViewById(R.id.signindonthaveaccount);
        memail =v.findViewById(R.id.signinemail);
        google =v.findViewById(R.id.google_sign_in_btn);
        password=v.findViewById(R.id.signinpassword);
        signInbutton =v.findViewById(R.id.signinbutton);
        parentframelayout=getActivity().findViewById(R.id.registerframelayout);
        firebaseAuth= FirebaseAuth.getInstance();
        forgotpassword=v.findViewById(R.id.signinforgotpassword);
        forgot_password_dialog=new Dialog(getActivity());
        forgot_password_dialog.setContentView(R.layout.forgot_password_dialog);
        forgot_password_dialog.setCancelable(true);
        email=forgot_password_dialog.findViewById(R.id.forgotemail);
        resetbutton=forgot_password_dialog.findViewById(R.id.forgotbutton);
        viewGroup=forgot_password_dialog.findViewById(R.id.forgotcontainer);
        imageicon=forgot_password_dialog.findViewById(R.id.forgotemailredicon);
        texticon=forgot_password_dialog.findViewById(R.id.forgottextmail);
        progressBar=forgot_password_dialog.findViewById(R.id.forgotredprogressbar);
        textWatchers();
        signInbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetbutton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                resetbutton.setText("RESET PASSWORD");
                progressBar.setVisibility(View.GONE);
                texticon.setVisibility(View.GONE);
                email.setText("");

                imageicon.setVisibility(View.GONE);
                forgot_password_dialog.show();

            }
        });
        resetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code
                String memail=email.getText().toString();
                if(memail.length()>0 && memail.matches(EMAIL_PATTERN))
                {


                    TransitionManager.beginDelayedTransition(viewGroup);
                    imageicon.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    resetbutton.setEnabled(false);
                    // resetbutton.setBackgroundColor(getResources().getColor(R.color.rbuttonRed));
                    firebaseAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0,imageicon.getWidth()/2,imageicon.getHeight()/2);
                                        scaleAnimation.setDuration(100);
                                        scaleAnimation.setInterpolator(new AccelerateInterpolator());
                                        scaleAnimation.setRepeatMode(Animation.REVERSE);
                                        scaleAnimation.setRepeatCount(1);

                                        scaleAnimation.setAnimationListener(new Animation.AnimationListener(){
                                            @Override
                                            public void onAnimationStart(Animation animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                texticon.setText("Recovery email sent successfully ! check your inbox");
                                                texticon.setTextColor(getResources().getColor(R.color.success));
                                                resetbutton.setText("Close");
                                                resetbutton.setBackgroundColor(getResources().getColor(R.color.success));
                                                progressBar.setVisibility(View.GONE);
                                                TransitionManager.beginDelayedTransition(viewGroup);
                                                texticon.setVisibility(View.VISIBLE);
                                                resetbutton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        forgot_password_dialog.dismiss();
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                                imageicon.setImageResource(R.drawable.greenmail);

                                            }
                                        });

                                        imageicon.startAnimation(scaleAnimation);
                                        resetbutton.setBackgroundColor(getResources().getColor(R.color.buttonRed));
                                        resetbutton.setEnabled(true);

                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        TransitionManager.beginDelayedTransition(viewGroup);
                                        texticon.setText(task.getException().getMessage());
                                        texticon.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        texticon.setVisibility(View.VISIBLE);
                                        resetbutton.setEnabled(true);
                                        resetbutton.setBackgroundColor(getResources().getColor(R.color.buttonRed));
                                        Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }


                                }
                            });

                }
                else
                {
                    Toast.makeText(getActivity(),"Invalid email",Toast.LENGTH_LONG).show();
                }

            }
        });
        dontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment(),RegisterActivity.SIGN_IN,RegisterActivity.SIGN_UP);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        return  v;
    }


   private void textWatchers() {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailcheckinput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        memail.addTextChangedListener(new TextWatcher() {
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
    }
    public void  checkEmailAndPassword()
    {
        if(memail.getText().toString().matches(EMAIL_PATTERN))
        {
            if(password.length()>0)
            {
                loadingBar.setTitle("Logging into your Account");
                loadingBar.setMessage("Please Wait....while we are allowing");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                signInbutton.setEnabled(false);
                signInbutton.setTextColor(Color.argb(55,255,255,255));
                firebaseAuth.signInWithEmailAndPassword(memail.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    sendToMain();
                                    Toast.makeText(getActivity(),"Login success",Toast.LENGTH_LONG).show();
                                }
                                else
                                {

                                    signInbutton.setEnabled(true);
                                    signInbutton.setTextColor(Color.rgb(255,255,255));
                                    Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                                loadingBar.dismiss();
                            }
                        });

            }
            else
            {
                Toast.makeText(getActivity(), "Password is too short", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(getActivity(),"Invalid Email", Toast.LENGTH_SHORT).show();
        }

    }
    public void checkinputs()
    {
        if(!TextUtils.isEmpty(memail.getText()))
        {
            if(!TextUtils.isEmpty(password.getText()))
            {
                signInbutton.setEnabled(true);
                signInbutton.setTextColor(Color.rgb(255,255,255));
            }
            else
            {
                signInbutton.setEnabled(false);
                signInbutton.setTextColor(Color.argb(55,255,255,255));
            }

        }
        else
        {
            signInbutton.setEnabled(false);
            signInbutton.setTextColor(Color.argb(55,255,255,255));
        }
    }
    public void emailcheckinput()
    {
        if(!TextUtils.isEmpty(email.getText()))
        {
            resetbutton.setEnabled(true);
            resetbutton.setBackgroundColor(getResources().getColor(R.color.buttonRed));
        }
        else
        {
            resetbutton.setBackgroundColor(getResources().getColor(R.color.rbuttonRed));
            resetbutton.setEnabled(false);
        }
    }
    private void sendToMain() {
        Intent main=new Intent(getActivity(),MainActivity.class);
        startActivity(main);
        getActivity().finish();
    }
    private void setFragment(Fragment fragment,int PrevFrag,int CurrentFrag) {
        RegisterActivity.currentFrag=CurrentFrag;
        RegisterActivity.prevFrag=PrevFrag;
        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_from_right,R.anim.slide_out_left).replace(parentframelayout.getId(),fragment).commit();
    }
}