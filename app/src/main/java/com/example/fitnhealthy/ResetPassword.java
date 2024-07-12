package com.example.fitnhealthy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ResetPassword extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser currentUser;
    Button resetBtn;
    TextInputEditText email;
    TextView returnToSignIn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_reset_pwd);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        returnToSignIn=findViewById(R.id.returnToLogin);
        email=findViewById(R.id.emailInput);
        resetBtn=findViewById(R.id.resetPwdBtn);

        returnToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPassword.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(Objects.requireNonNull(email.getText()).toString())){
                    Toast.makeText(ResetPassword.this,"Enter your email",Toast.LENGTH_SHORT).show();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    Toast.makeText(ResetPassword.this,"Incorrect email",Toast.LENGTH_SHORT).show();
                }else {
                    auth.sendPasswordResetEmail(Objects.requireNonNull(email.getText()).toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ResetPassword.this, "Reset password link has been sent to "+email.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ResetPassword.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ResetPassword.this, Login.class);
                startActivity(intent);
                // Finish the current activity
                finish();
            }
        });

    }

}
