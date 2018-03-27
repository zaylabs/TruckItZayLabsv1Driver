package com.zaylabs.truckitzaylabsv1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ForgetPasswordActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mEmail;
    private Button mForgetPassword;
    private TextView mBackToLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.etForget);
        mForgetPassword = findViewById(R.id.btnForgetPassword);
        mBackToLogin = findViewById(R.id.return_to_login);

        mForgetPassword.setOnClickListener(this);
        mBackToLogin.setOnClickListener(this);
        }



    private void signIn() {

        String email = mEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(ForgetPasswordActivity.this, "Enter your email!", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetPasswordActivity.this, "Re-set Email Sent", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ForgetPasswordActivity.this, "Email Sending Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void backToLogin() {

        Intent intent = new Intent(ForgetPasswordActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }


    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnForgetPassword) {
            signIn();
        } else if (i == R.id.return_to_login) {
            backToLogin();
        }
    }


}

