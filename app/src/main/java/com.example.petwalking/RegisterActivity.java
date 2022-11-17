package com.example.petwalking;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;             // 파이어베이스 인증처리하는 객체
    private DatabaseReference mDatabaseRef;         // 실시간 데이터베이스 연동하는 객체
    private EditText mEtEmail, mEtPwd, mEtCfPwd;    // 회원가입 입력 필드
    private Button mBtnRegister;                    // 회원가입 버튼 필드
    private ImageView dog;                          // 강아지 이미지 뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 이미지 뷰
        dog = findViewById(R.id.iv_DogWalkCycle_gif);
        Glide.with(this).load(R.mipmap.dogwalkcycle).into(dog);

        // 객체 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();     // 파이어베이스 인증 객체 초기화
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        // 필드 초기화
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtCfPwd = findViewById(R.id.et_cfpwd);
        mBtnRegister = findViewById(R.id.btn_ok);

        // PhoneAuthActivity.java에서 인텐트로 가져온 정보들
        Intent intent = getIntent();
        String strName = intent.getStringExtra("name");
        String strPhone = intent.getStringExtra("phone");


        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strCfPwd = mEtCfPwd.getText().toString();

                // 이메일 형식 확인
                if(checkID(strEmail) < 2) {
                    Toast.makeText(RegisterActivity.this, "이메일 형식을 맞춰주세요.", Toast.LENGTH_SHORT).show();
                } /*else {
                    // 비밀번호 자리 수 확인
                    if (mEtPwd.length() < 6 || mEtPwd.length() > 20) {
                        Toast.makeText(RegisterActivity.this, "비밀번호는 6자리 이상, 20자리 이하만 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 특수문자 확인
                        if (checkPWDMethod(strPwd) == 1) {
                            Toast.makeText(RegisterActivity.this, "비밀번호 특수문자는 !@#만 포함 가능 합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (checkPWD(strPwd) != 1) {
                                Toast.makeText(RegisterActivity.this, "비밀번호 특수문자 !@#을 포함해야 합니다.", Toast.LENGTH_SHORT).show();
                            } */else {
                                // 비밀번호 길이 6글자 이상인 경우
                                if(strPwd.length() >= 6) {
                                    // 비밀번호와 비밀번호 재입력이 일치하는 경우
                                    if (strPwd.equals(strCfPwd)) {
                                        // FireBase Auth 진행
                                        mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                // 회원가입이 성공했을 경우
                                                if (task.isSuccessful()) {
                                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                                    UserAccount account = new UserAccount();
                                                    account.setIdToken(firebaseUser.getUid());
                                                    account.setEmailId(firebaseUser.getEmail());    // 로그인 완료된 유저 이메일을 정확히 가져오기 위함
                                                    account.setPassword(strPwd);                    // 사용자가 입력했던 패스워드를 가져오기
                                                    account.setName(strName);                       // 이전 페이지에서 작성했던 정보
                                                    account.setPhoneNumber(strPhone);               // 이전 페이지에서 작성했던 정보

                                                    // setValue : DB에 UserAccount 정보를 insert 함.
                                                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                                                    // 회원가입 완료 출력
                                                    Toast.makeText(RegisterActivity.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(RegisterActivity.this, ActivityDogInfoADD.class);
                                                    startActivity(intent);
                                                    finish();   // 현재 액티비티 파괴
                                                } else {
                                                    // 회원가입 실패 출력
                                                    Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        // 비밀번호가 틀린 경우
                                        Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // 비밀번호가 6글자 이하인 경우
                                    Toast.makeText(RegisterActivity.this, "비밀번호의 길이가 6글자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                   /* }
                }
            }

            // 회원가입 시 비밀번호에 제한되는 특수문자 확인 메소드
            public int checkPWDMethod(String pwd) {
                int check = 0;
                char alpha;
                int code;
                for (int i = 0; i < pwd.length(); i++) {
                    alpha = pwd.charAt(i);
                    code = (int) alpha;
                    if (code >= 0 && code <= 32 || code == 34 || code >= 36 && code <= 47 || code >= 58 && code <= 63 || code >= 91 && code <= 96 || code >= 123 && code <= 127) {
                        check = 1;
                    }
                }
                return check;
            }

            // 회원가입 시 비밀번호 특수문자 확인 메소드 (비밀번호 특수문자의 경우 : !@#만 포함 가능)
            public int checkPWD(String pwd) {
                int check = 0;
                char alpha;
                int code;
                for (int i = 0; i < pwd.length(); i++) {
                    alpha = pwd.charAt(i);
                    code = (int) alpha;
                    if (code == 33 || code == 64 || code == 35) {
                        check = 1;
                    }
                }
                return check;
            }*/

            // 이메일 형식 확인 메소드 (@ 포함 확인)
            public int checkID(String pwd) {
                int stack = 0;
                char alpha;
                int code;
                for (int i = 0; i < pwd.length(); i++) {
                    alpha = pwd.charAt(i);
                    code = alpha;
                    if (code == 64 || code == 46) {
                        stack++;
                    }
                }
                return stack++;
            }
        });
    }
}