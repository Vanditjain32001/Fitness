package com.example.android.fitness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {
    private static final String LOG_TAG = UserInfoActivity.class.getName();
    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 2;
    private static final int FIRST_TIME_SIGNIN = 1;

    private static final String KEY_GENDER = "gender";
    private static final String KEY_AGE = "age";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_CHEST_SIZE ="chest size";
    private static final String KEY_WAIST_SIZE = "waist size";
    private static final String KEY_BELLY_SIZE = "Belly size";
    private static final String KEY_THIGH_SIZE = "Thigh girth";
    private static final String KEY_FIRST_TIME_SIGNIN  = "first_time_sign_in";

    private int mGender = 1;
    private int mAge;
    private int mHeight;
    private int mWeight;
    private int mChestSize;
    private int mWaistSize;
    private int mBellyCirumference;
    private int mThighGirth;

    private Spinner mGenderSpinner;
    private FloatingActionButton mFab;
    private EditText mAgeEditText;
    private EditText mHeightEditText;
    private EditText mWeightEditText;
    private EditText mChestEditText;
    private EditText mWaistEditText;
    private EditText mBellyEditText;
    private EditText mThighEditText;

    private boolean mIsAgeValid = false;
    private boolean mIsHeightValid = false;
    private boolean mIsWeightValid = false;
    private boolean mIsChestValid = true;
    private boolean mIsWaistValid = true;
    private boolean mIsBellyValid = true;
    private boolean mIsThighValid = true;

    private String mUserId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mUsersData = db.collection("users");
    private DocumentReference mUserDb;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener listener;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Intent intent = getIntent();
        mUserId = intent.getStringExtra("uid");
        mUserDb = mUsersData.document(mUserId);

        mAuth = FirebaseAuth.getInstance();

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
                    startActivity(new Intent(UserInfoActivity.this,RegisterActivity.class));
                }
            }
        };

        mGenderSpinner = (Spinner)findViewById(R.id.spinner_gender);
        mFab = (FloatingActionButton) findViewById(R.id.fab_next);
        mAgeEditText = (EditText)findViewById(R.id.age_field);
        mHeightEditText = (EditText)findViewById(R.id.height_field);
        mWeightEditText = (EditText)findViewById(R.id.weight_field);
        mChestEditText = (EditText)findViewById(R.id.chest_field);
        mWaistEditText = (EditText)findViewById(R.id.waist_field);
        mBellyEditText = (EditText)findViewById(R.id.belly_field);
        mThighEditText = (EditText)findViewById(R.id.thigh_field);

        Toast.makeText(UserInfoActivity.this,"First fill mandatory information", Toast.LENGTH_SHORT).show();

        mFab.setEnabled(false);
        mAgeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkFields();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();

            }

            @Override
            public void afterTextChanged(Editable s) {
                String ageInputString = mAgeEditText.getText().toString();
                if(ageInputString.isEmpty()){
                    mAgeEditText.setError("this is mandatory field");
                    mIsAgeValid = false;
                }
                else
                {
                    int ageInputInteger = Integer.parseInt(mAgeEditText.getText().toString());
                    if(ageInputInteger<10 || ageInputInteger>150)
                    {
                        mAgeEditText.setError("Please enter a valid age");
                        mIsAgeValid = false;
                    }
                    else
                    {
                        mIsAgeValid = true;
                    }

                }
                checkFields();
            }
        });
        mHeightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkFields();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mHeightEditText.getText().toString().isEmpty()){
                    mHeightEditText.setError("this is mandatory field");
                    mIsHeightValid = false;
                }
                else{
                    int HeightInputInteger = Integer.parseInt(mHeightEditText.getText().toString());
                    if(HeightInputInteger < 25 || HeightInputInteger > 300) {
                        mHeightEditText.setError("Please enter a valid Height");
                        mIsHeightValid = false;
                    }
                    else
                    {
                        mIsHeightValid = true;
                    }
                }
               checkFields();
            }
        });
        mWeightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkFields();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mWeightEditText.getText().toString().isEmpty()){
                    mWeightEditText.setError("this is mandatory field");
                    mIsWeightValid = false;
                }
                else {
                    int weightInputInteger = Integer.parseInt(mWeightEditText.getText().toString());
                    if(weightInputInteger < 1 || weightInputInteger > 700) {
                        mWeightEditText.setError("Please enter a valid Height");
                        mIsWeightValid = false;
                    }
                    else
                    {
                        mIsWeightValid = true;
                    }
                }
                checkFields();
            }
        });
        mChestEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkFields();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mChestEditText.getText().toString().isEmpty())
                {
                    mIsChestValid = true;
                }
                else
                {
                    int chestInputInteger = Integer.parseInt(mChestEditText.getText().toString());
                    if(chestInputInteger < 10 || chestInputInteger > 300) {
                        mChestEditText.setError("Please enter a valid chest size");
                        mIsChestValid = false;
                    }
                    else
                    {
                        mIsChestValid = true;
                    }
                }
                checkFields();
            }
        });
        mWaistEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                checkFields();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mWaistEditText.getText().toString().isEmpty())
                {
                    mIsWaistValid = true;
                }
                else {
                    int waistInputInteger = Integer.parseInt(mWaistEditText.getText().toString());
                    if(waistInputInteger < 10 || waistInputInteger > 150) {
                        mWaistEditText.setError("Please enter a valid waist");
                        mIsWaistValid = false;
                    }
                    else
                    {
                        mIsWaistValid = true;
                    }
                }
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

                checkFields();
            }
        });
        mBellyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkFields();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mBellyEditText.getText().toString().isEmpty())
                {
                   mIsBellyValid = true;
                }
                else {
                    int bellyInputInteger = Integer.parseInt(mBellyEditText.getText().toString());
                    if(bellyInputInteger < 10 || bellyInputInteger > 150) {
                        mBellyEditText.setError("Please enter a valid belly circumference");
                        mIsBellyValid = false;
                    }
                    else
                    {
                        mIsBellyValid = true;
                    }
                }
               checkFields();
            }
        });
        mThighEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkFields();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mThighEditText.getText().toString().isEmpty()) {
                  mIsThighValid = true;
                }
                else
                {
                    int thighInputInteger = Integer.parseInt(mThighEditText.getText().toString());
                    if(thighInputInteger < 10 || thighInputInteger > 150) {
                        mThighEditText.setError("Please enter a valid age");
                        mIsThighValid = false;
                    }
                    else
                    {
                        mIsThighValid = true;
                    }
                }
                checkFields();
            }
        });

        setupSpinner();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachTodatabase();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                Toast.makeText(UserInfoActivity.this,"Signing out",Toast.LENGTH_SHORT);
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("Male")) {
                        mGender = 1;
                    }
                    else {
                        mGender = 2;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 1;
            }
        });
    }

    private void checkFields()
    {
        if(mIsAgeValid&&mIsBellyValid&&mIsChestValid&&mIsHeightValid&&mIsThighValid&&mIsWaistValid&&mIsWeightValid)
        {
            mFab.setEnabled(true);
        }
        else
        {
            mFab.setEnabled(false);
        }
    }

    private void attachTodatabase(){
        mAge = Integer.parseInt(mAgeEditText.getText().toString());
        mHeight =Integer.parseInt(mHeightEditText.getText().toString());
        mWeight = Integer.parseInt(mWeightEditText.getText().toString());
        if(mChestEditText.getText().toString().isEmpty())
        {
            mChestSize = 0;
        }
        else
        {
            mChestSize = Integer.parseInt(mChestEditText.getText().toString());
        }
        if(mWaistEditText.getText().toString().isEmpty())
        {
            mWaistSize = 0;
        }
        else
        {
            mWaistSize = Integer.parseInt(mWaistEditText.getText().toString());
        }
        if(mBellyEditText.getText().toString().isEmpty())
        {
            mBellyCirumference = 0;
        }
        else
        {
            mBellyCirumference = Integer.parseInt(mBellyEditText.getText().toString());
        }
        if(mThighEditText.getText().toString().isEmpty())
        {
            mThighGirth = 0;
        }
        else
        {
            mThighGirth = Integer.parseInt(mThighEditText.getText().toString());
        }
        Map<String,Integer> userInfo = new HashMap<>();
        userInfo.put(KEY_GENDER,mGender);
        userInfo.put(KEY_AGE,mAge);
        userInfo.put(KEY_HEIGHT,mHeight);
        userInfo.put(KEY_WEIGHT,mWeight);
        userInfo.put(KEY_CHEST_SIZE,mChestSize);
        userInfo.put(KEY_WAIST_SIZE,mWaistSize);
        userInfo.put(KEY_BELLY_SIZE,mBellyCirumference);
        userInfo.put(KEY_THIGH_SIZE,mThighGirth);
        userInfo.put(KEY_FIRST_TIME_SIGNIN,FIRST_TIME_SIGNIN);

        mUserDb.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(UserInfoActivity.this,MainActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserInfoActivity.this,"error in storing data",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signOut() {
       mAuth.signOut();
       mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserInfoActivity.this,"signing out",Toast.LENGTH_SHORT);
                    }
                });
    }
}
