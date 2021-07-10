package com.masterwarchief.diary;


import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    private final static int RC_SIGN_IN=5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        email=findViewById(R.id.login_email_field);
        password=findViewById(R.id.login_password_field);
        loginButton=findViewById(R.id.login_button);
        mProgress= new ProgressDialog(this);
        mProgress.setTitle("Logging In...");
        mProgress.setCanceledOnTouchOutside(false);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDetails();
            }
        });

    }


    private void verifyDetails() {
        String emailId=email.getText().toString().trim();
        String pass=password.getText().toString().trim();

        if (emailId.isEmpty()){
            email.setError("Email id required!");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailId).matches()){
            email.setError("Enter a valid email address!");
            email.requestFocus();
            return;
        }

        if (pass.isEmpty()){
            password.setError("Password should not be empty");
            password.requestFocus();
            return;
        }

        if (pass.length()<6){
            password.setError("Password length should be 6");
            password.requestFocus();
            return;
        }

        loginUser(emailId,pass);
    }

    private void loginUser(String emailId, String pass) {
        mProgress.show();

        mAuth.signInWithEmailAndPassword(emailId,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mProgress.dismiss();
                    Toast.makeText(LoginActivity.this, "Authentication Successful!", Toast.LENGTH_SHORT).show();

                    Intent intent =new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    mProgress.dismiss();

                    if (task.getException() instanceof FirebaseAuthInvalidUserException){
                        Toast.makeText(getApplicationContext(),"User with email address does not exist!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void toRegistratinActivity(View view) {
        Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    public void resetPassword(View view) {
        Intent intent=new Intent(getApplicationContext(),ResetPasswordActivity.class);
        startActivity(intent);
    }
}