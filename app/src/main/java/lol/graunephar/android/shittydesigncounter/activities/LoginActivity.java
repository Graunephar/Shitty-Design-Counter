package lol.graunephar.android.shittydesigncounter.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.function.BiConsumer;

import lol.graunephar.android.shittydesigncounter.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edtUsername;
    private EditText edtPassword;

    private int START_LIST_REQUEST = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance(); //instantiate firebase

        generateUI();
    }

    private void generateUI() {
        Button btnsignin = findViewById(R.id.btnSignIn);
        Button btncreateuser = findViewById(R.id.btnCreateUser);

        TextView txtusername = findViewById(R.id.txtUsername);
        TextView txtpassword = findViewById(R.id.txtPassword);

        edtUsername = findViewById(R.id.edtUser);
        edtPassword = findViewById(R.id.edtPass);

        txtusername.setText(R.string.username_label_text);
        txtpassword.setText(R.string.password_label_text);

        btnsignin.setText(R.string.sign_in_btn);
        btncreateuser.setText(R.string.create_user_btn);

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btncreateuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }



    private void createUser() {
        String email = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if(validateEmailAndPassword(email, password)) {
            createFirebaseUserWithEmailAndPassword(email, password);
        }
    }

    private void createFirebaseUserWithEmailAndPassword(String email, String password) {

        Log.d("CREATING USER", "USER:" + email + " " + password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOGIN", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOGIN", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), R.string.sign_in_toast_error, Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }


    private void signIn() {
        String email = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if(validateEmailAndPassword(email, password)) {
            signInFirebase(email, password);
        }
    }


    private boolean validateEmailAndPassword(String email, String password) {
        if(email == null || email.equals("")) {
            Toast.makeText(getApplicationContext(), R.string.login_toast_no_email, Toast.LENGTH_LONG).show();
            return false;
        } else if(password == null || password.equals("")) {
            Toast.makeText(getApplicationContext(), R.string.login_toast_no_password, Toast.LENGTH_LONG).show();
            return false;
        } else  {
            return true;
        }
    }

    private void signInFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOGIN", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                            showUserLoginError(task);

                            }
                            updateUI(null);
                        }

                        // ...
                    });
                }

    private void showUserLoginError(Task<AuthResult> task) {
        Exception exception = task.getException();
        int msgid;
        try {
            throw exception;
        } catch (FirebaseAuthInvalidUserException e) {
            msgid = R.string.login_toast_invalid_user;
        } catch (FirebaseAuthInvalidCredentialsException e) {
            msgid = R.string.login_toast_invalid_pass;

        } catch (Exception e) {
            msgid = R.string.login_toast_unknown_error;
        }

        String msg = getResources().getString(msgid);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

    }

    private void getUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }

    }

    private void updateUI(FirebaseUser user) {

        if(user != null) {
            Intent intent = new Intent(this, ListActivity.class);
            //intent.putExtra(IntentConstants.FIREBASE_USER, user);
            startActivityForResult(intent, START_LIST_REQUEST);
        } else {
            // Not sure if something should be done here, at the moment we send toasts everywhere :p
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            Toast.makeText(getApplicationContext(), R.string.login_toast_logged_out_message, Toast.LENGTH_LONG).show();
            Log.v("LOGIN", "USER LOGGED OUT");
    }
}
