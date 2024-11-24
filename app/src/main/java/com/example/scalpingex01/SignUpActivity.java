package com.example.scalpingex01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextName, editTextPhone, editTextBirthdate;
    private RadioGroup radioGroupGender;
    private Button signUpButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 초기화
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextBirthdate = findViewById(R.id.editTextBirthdate);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        signUpButton = findViewById(R.id.signUpButton);

        // FirebaseAuth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();
        // FirebaseDatabase 인스턴스 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference();

        signUpButton.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();
            String name = editTextName.getText().toString();
            String phone = editTextPhone.getText().toString();
            String birthdate = editTextBirthdate.getText().toString();
            String gender = ((RadioButton)findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();

            // 빈 입력값 확인
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() ||
                    phone.isEmpty() || birthdate.isEmpty() || gender.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "모든 필드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 비밀번호 일치 확인
            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase로 회원가입 처리
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                UserProfile userProfile = new UserProfile(name, phone, birthdate, gender);
                                mDatabase.child("users").child(userId).setValue(userProfile)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Intent intent = new Intent(SignUpActivity.this, SignUpCompleteActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "데이터 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}