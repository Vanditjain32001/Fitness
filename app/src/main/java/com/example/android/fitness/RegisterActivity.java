package com.example.android.fitness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class RegisterActivity extends AppCompatActivity {

    private final static String LOG = RegisterActivity.class.getName();

    private TextView mLoginTitle;
    private TextView mCreateAccount;
    private EditText mLoginEmail;
    private EditText mLoginPassword;
    private Button mSignIn;

    private LinearLayout layout;

    private SignInButton mSignInGoogle;
    GoogleSignInClient mGoogleSignInClient;

    private Button mSignUp;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener listener;
    private final static int RC_SIGN_IN = 123;

    private boolean  isEmailValid = false;
    private boolean  isPasswordValid = false;
    private String email;
    private String password;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("users");
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(listener);
        //updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mLoginTitle = (TextView)findViewById(R.id.login_title);
        mCreateAccount = (TextView) findViewById(R.id.create_account);
        mLoginEmail = (EditText) findViewById(R.id.login_email);
        mLoginPassword = (EditText) findViewById(R.id.login_password);
        mSignIn = (Button) findViewById(R.id.sign_in);

        layout = (LinearLayout)findViewById(R.id.layout_login);

        mSignInGoogle = (SignInButton)findViewById(R.id.google_sign);

        mSignUp = (Button) findViewById(R.id.sign_up);

        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In

        mSignUp.setEnabled(false);
        mSignIn.setEnabled(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
          listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               if(firebaseAuth.getCurrentUser() != null)
               {
                   final String uid = firebaseAuth.getCurrentUser().getUid();
                   final DocumentReference documentReference = usersRef.document(uid);
                   documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                       @Override
                       public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                         if(e == null)
                         {
                             if(documentSnapshot.exists())
                             {
                                 Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                 startActivity(intent);
                             }
                             else
                             {
                                 Intent intent = new Intent(RegisterActivity.this , UserInfoActivity.class);
                                 intent.putExtra("uid",uid);
                                 startActivity(intent);
                             }
                         }
                       }
                   });

               }
            }
        };
        mSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCreateAccount.getText().toString().equals("Create new account")) {
                    mCreateAccount.setText(" Log in instead ");
                }
                else {
                    mCreateAccount.setText("Create new account");
                }
                if(mLoginTitle.getText().toString().equals("Login to your acount")) {
                    mLoginTitle.setText("Create new account");
                }
                else{
                    mLoginTitle.setText("Login to your acount");
                }
                if(mSignIn.getVisibility() == View.VISIBLE) {
                    mSignIn.setVisibility(View.GONE);
                    mSignUp.setVisibility(View.VISIBLE);
                }
                else{
                    mSignIn.setVisibility(View.VISIBLE);
                    mSignUp.setVisibility(View.GONE);
                }
            }
        });

        mLoginPassword.addTextChangedListener(new TextWatcher() {
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
                String inputPassword = mLoginPassword.getText().toString();
                if(inputPassword.isEmpty())
                {
                    mLoginPassword.setError("Password field cannot be empty");
                    isPasswordValid = false;
                }
                else
                {
                    if(inputPassword.length()<6)
                    {
                        mLoginPassword.setError("password should have atleast 6 digits");
                        isPasswordValid = false;
                    }
                    else
                    {
                        isPasswordValid = true;
                    }
                }
                checkFields();
            }
        });

        mLoginEmail.addTextChangedListener(new TextWatcher() {
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
                String inputEmail = mLoginEmail.getText().toString();
                if (inputEmail.isEmpty()) {
                    mLoginEmail.setError("Email field cannot be empty");
                    isEmailValid = false;
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
                        isEmailValid = false;
                        mLoginEmail.setError("Invalid email pattern");
                    } else {
                        isEmailValid = true;
                   }
                }
                checkFields();
            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mLoginEmail.getText().toString();
                password = mLoginPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if (task.isSuccessful())
                      {
                          final String uid = mAuth.getCurrentUser().getUid();
                          final DocumentReference documentReference = usersRef.document(uid);
                          documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                              @Override
                              public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                  if(e == null)
                                  {
                                      if(documentSnapshot.exists())
                                      {
                                          Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                          startActivity(intent);
                                      }
                                      else
                                      {
                                          Intent intent = new Intent(RegisterActivity.this , UserInfoActivity.class);
                                          intent.putExtra("uid",uid);
                                          startActivity(intent);
                                      }
                                  }
                              }
                          });
                      }
                      else
                      {
                          Log.w(LOG, "signInWithEmail:failure", task.getException());
                          Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                  Toast.LENGTH_SHORT).show();
                      }
                    }
                });
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mLoginEmail.getText().toString();
                password = mLoginPassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = mAuth.getCurrentUser();
                            final String uid = user.getUid();
                            final DocumentReference documentReference = usersRef.document(uid);
                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if(e == null)
                                    {
                                        if(documentSnapshot.exists())
                                        {
                                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            Intent intent = new Intent(RegisterActivity.this , UserInfoActivity.class);
                                            intent.putExtra("uid",uid);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                mLoginPassword.setError("weak password");
                                mLoginPassword.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                mLoginEmail.setError("invalid credentials");
                                mLoginEmail.requestFocus();
                            } catch(FirebaseAuthUserCollisionException e) {
                                mLoginEmail.setError("account already exists");
                                mLoginEmail.requestFocus();
                            } catch(Exception e) {
                                Log.e(LOG, e.getMessage());
                            }
                        }
                    }
                });
            }
        });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(LOG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this,"Authentication failed",Toast.LENGTH_SHORT);

                        }

                        // ...
                    }
                });
    }
    private void checkFields()
    {
        if(isEmailValid&&isPasswordValid)
        {
            mSignIn.setEnabled(true);
            mSignUp.setEnabled(true);
        }
        else
        {
            mSignIn.setEnabled(false);
            mSignUp.setEnabled(false);
        }
    }

}
