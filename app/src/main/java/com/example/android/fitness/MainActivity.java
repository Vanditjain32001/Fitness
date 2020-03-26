package com.example.android.fitness;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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


import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int RC_SIGN_IN = 123;
    public static final String ANONYMOUS = "anonymous";


    private FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener listener;
    GoogleSignInClient mGoogleSignInClient;

    private TextView mBmiTextView;
    private LottieAnimationView lottieAnimationView;
    private TextView mWeightTextView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("users");
    private double bmi;
    private static DecimalFormat df = new DecimalFormat("0.0");
    private Button mButton;
    private Button mButton2;
    private Button mButton3;

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mBmiTextView = (TextView) findViewById(R.id.bmi);
        lottieAnimationView = (LottieAnimationView)findViewById(R.id.exercise);
        mWeightTextView = (TextView)findViewById(R.id.weight);
        mButton = (Button)findViewById(R.id.image_button);
        mButton2 = (Button)findViewById(R.id.image_button2);
        mButton3 = (Button)findViewById(R.id.image_button3);

        mFirebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null)
                {
                    startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                }
            }
        };
       FirebaseUser user = mFirebaseAuth.getCurrentUser();
       String id = user.getUid();
       DocumentReference reference = usersRef.document(id);
       reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               if (e == null) {
                   if(documentSnapshot.exists())
                   {
                       if (documentSnapshot.getDouble("gender") == 1) {
                           lottieAnimationView.setAnimation("man.json");
                           lottieAnimationView.playAnimation();
                       } else {
                           lottieAnimationView.setAnimation("girl.json");
                           lottieAnimationView.playAnimation();
                       }
                       double val = (documentSnapshot.getDouble("weight") )/ ((documentSnapshot.getDouble("height"))*(documentSnapshot.getDouble("height")));
                       bmi = val * 10000.0;
                       bmi = Math.round(bmi * 10D) / 10D;
                       mBmiTextView.setText("Here's your BMI: " + bmi);
                       checkBmi(bmi);
                   }
                   else
                   {
                       Toast.makeText(MainActivity.this,"some error in retrieving data",Toast.LENGTH_SHORT).show();
                   }
               }
           }
       });


        mButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              // startActivity(new Intent(MainActivity.this,ImageActivity.class));
               Intent intent = new Intent(MainActivity.this,ExercisesListActivity.class);
               intent.putExtra("exercise","exercises");
               startActivity(intent);

           }
       });
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(MainActivity.this,ImageActivity.class));
                Intent intent = new Intent(MainActivity.this,ExercisesListActivity.class);
                intent.putExtra("exercise","Body Building");
                startActivity(intent);
            }
        });
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(MainActivity.this,ImageActivity.class));
                Intent intent = new Intent(MainActivity.this,ExercisesListActivity.class);
                intent.putExtra("exercise","Body Toning");
                startActivity(intent);
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

    public void signOut() {
        mFirebaseAuth.signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this,"signed out",Toast.LENGTH_SHORT);
                    }
                });
    }

    private void checkBmi(double val)
    {
        if(Double.compare(val,18.5d) < 0)
        {
            mWeightTextView.setText("RANGE : UNDERWEIGHT");
        }
        else if(Double.compare(val,18.5d)>=0 && Double.compare(val,25.0d)<0)
        {
            mWeightTextView.setText("RANGE : NORMAL");
        }
        else if( Double.compare(val,25.0d)>=0 && Double.compare(val,30.0d)<0)
        {
            mWeightTextView.setText("RANGE : OVERWEIGHT");
        }
        else
        {
            mWeightTextView.setText("RANGE : OBESE");
        }
    }
}
