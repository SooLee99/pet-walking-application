package com.example.petwalking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FindPWActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextUserEmail;
    private Button btn_sendEmail;//, btn_moveLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private ImageView dog;                          // 강아지 이미지 뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwactivity);

        editTextUserEmail = (EditText) findViewById(R.id.et_email);
        btn_sendEmail = (Button) findViewById(R.id.btn_sendEmail);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        btn_sendEmail.setOnClickListener(this);

        // 이미지 뷰
        dog = findViewById(R.id.iv_DogWalkCycle_gif);
        Glide.with(this).load(R.mipmap.dogwalkcycle).into(dog);

    }

    @Override
    public void onClick(View view) {
        String strEmail = editTextUserEmail.getText().toString();
        if(strEmail.equals("")){
            Toast.makeText(FindPWActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            if (view == btn_sendEmail) {
                // 이메일 형식 확인
                if(checkID(strEmail) < 2) {
                    Toast.makeText(FindPWActivity.this, "이메일 형식을 맞춰주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("처리중입니다. 잠시 기다려 주세요...");
                    progressDialog.show();

                    //비밀번호 재설정 이메일 보내기
                    String emailAddress = editTextUserEmail.getText().toString().trim();
                    firebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(FindPWActivity.this, "이메일 전송이 되었습니다.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(FindPWActivity.this, "이메일 전송이 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }
    }
    // 회원가입 시 이메일 형식 확인 메소드 (@ 포함 확인)
    public int checkID(String pwd) {
        int stack = 0;
        char alpha;
        int code;
        for (int i = 0; i < pwd.length(); i++) {
            alpha = pwd.charAt(i);
            code = (int) alpha;
            if (code == 64 || code == 46) {
                stack++;
            }
        }
        return stack++;
    }
}