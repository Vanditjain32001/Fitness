package com.example.android.fitness;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int RC_SIGN_IN = 123;
    public static final String ANONYMOUS = "anonymous";

    private String mUsername;
    private String mUserId;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private TextView mBmiTextView;
    private LottieAnimationView lottieAnimationView;
    private TextView mWeightTextView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("users");
    private double bmi;
    private static DecimalFormat df = new DecimalFormat("0.0");
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        mBmiTextView = (TextView) findViewById(R.id.bmi);
        lottieAnimationView = (LottieAnimationView)findViewById(R.id.exercise);
        mWeightTextView = (TextView)findViewById(R.id.weight);
        mButton = (Button)findViewById(R.id.image_button);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
             FirebaseUser user = firebaseAuth.getCurrentUser();
             if (user != null)
             {
                 mUserId = user.getUid();
                 onSignedInInitialise(user.getDisplayName());
             }
             else
             {
                 onSignOutCleanUp();
                 createSignInIntent();
             }
            }
        };
       mButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this,ImageActivity.class));
           }
       });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                Toast.makeText(MainActivity.this,"Signing out",Toast.LENGTH_SHORT).show();
                lottieAnimationView.setAnimation("before.json");
                lottieAnimationView.playAnimation();
                signOut();
                return true;
            case R.id.edit_menu:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if( user != null)
                {
                    String id = user.getUid();
                    Intent intent = new Intent(MainActivity.this,EditUserInfo.class);
                    intent.putExtra("uid",id);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    mUserId = user.getUid();

                    DocumentReference docRef = usersRef.document(mUserId);
                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(e == null) {
                                if (documentSnapshot.exists()) {
                                    if (documentSnapshot.getDouble("gender") == 1) {
                                        lottieAnimationView.setAnimation("man.json");
                                        lottieAnimationView.playAnimation();
                                    } else {
                                        lottieAnimationView.setAnimation("girl.json");
                                        lottieAnimationView.playAnimation();
                                    }
                                    double val = documentSnapshot.getDouble("weight") / (documentSnapshot.getDouble("height") * documentSnapshot.getDouble("height"));
                                    bmi = val * 10000.0;
                                    bmi = Math.round(bmi * 10D) / 10D;
                                    mBmiTextView.setText("Here's your BMI: " + bmi);
                                    checkBmi(bmi);
                                    // mBmiTextView.setText(""+documentSnapshot.getDouble("weight")+documentSnapshot.getDouble("height"));
                                }
                                else
                                {
                                    Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                                    intent.putExtra("uid", mUserId);
                                    startActivity(intent);
                                }
                            }

                        }
                    });
                }
            } else if (response == null) {
                finish();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
            else
            {

            }
        }
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.ic_launcher_background)
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_signout]
    }

    public void delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_delete]
    }

    public void privacyAndTerms() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();
        // [START auth_fui_pp_tos]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_pp_tos]
    }

    private void onSignedInInitialise(String username){
        mUsername = username;
        final DocumentReference docRef = usersRef.document(mUserId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e == null)
                {   if (documentSnapshot.exists()) {
                    if (documentSnapshot.getDouble("gender") == 1) {
                        lottieAnimationView.setAnimation("man.json");
                        lottieAnimationView.playAnimation();
                    } else {
                        lottieAnimationView.setAnimation("girl.json");
                        lottieAnimationView.playAnimation();
                    }
                    double val = documentSnapshot.getDouble("weight") / (documentSnapshot.getDouble("height") * documentSnapshot.getDouble("height"));
                    bmi = val * 10000.0;
                    bmi = Math.round(bmi * 10D) / 10D;
                    mBmiTextView.setText("Here's your BMI: " + bmi);
                    checkBmi(bmi);
                    //mBmiTextView.setText(""+documentSnapshot.getDouble("weight")+documentSnapshot.getDouble("height"));
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this,UserInfoActivity.class);
                    intent.putExtra("uid",mUserId);
                    startActivity(intent);
                }
            }

            }
        });

    }

    private void onSignOutCleanUp(){
        mUsername = ANONYMOUS;
    }
    private void checkBmi(double val)
    {
        if(val < 18.5d)
        {
            mWeightTextView.setText("RANGE : UNDERWEIGHT");
        }
        else if(val>=18.5d || val<25.0d)
        {
            mWeightTextView.setText("RANGE : NORMAL");
        }
        else if(val>=25.0d || val<=30.0d)
        {
            mWeightTextView.setText("RANGE : OVERWEIGHT");
        }
        else
        {
            mWeightTextView.setText("RANGE : OBESE");
        }
    }
}
