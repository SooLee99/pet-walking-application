package com.example.petwalking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.math.BigInteger;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mFirebaseAuth;             // 파이어베이스 인증처리하는 객체
    private DatabaseReference mDatabaseRef;         // 실시간 데이터베이스 연동하는 객체
    private EditText mEtEmail, mEtPwd;              // 로그인 입력 필드
    private Button btn_login;
    private TextView btn_register, btn_findIdPwd;
    private ImageView dog;

    private SignInButton btn_google;                // 구글 로그인 버튼 필드
    private GoogleApiClient googleApiClient;        // 구글 API 클라이언트 객체
    private static final int REQ_SIGN_GOOGLE = 100; // 구글 로그인 결과 코드

    private ISessionCallback mSessionCallback;      // 카카오 로그인

    // 엡이 실행될 때 처음 수행되는 곳
    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 이미지 뷰
        dog = findViewById(R.id.iv_DogWalkCycle_gif);
        Glide.with(this).load(R.mipmap.dogwalkcycle).into(dog);

        // 객체 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("PetWalking");

        // 필드 초기화
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);

        // 로그인 버튼 수행 동작
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                if(strEmail.equals("") ||strPwd.equals("")){
                    Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    // 로그인 요청
                    mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // 로그인 성공 시
                                Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();   // 현재 액티비티 파괴
                            } else {
                                Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        // 회원가입 버튼 수행 동작
        btn_register = findViewById(R.id.btn_ok);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, PhoneAuthActivity.class);
                startActivity(intent);
            }
        });

        // 비밀번호 찾기 버튼 수행 동작
        btn_findIdPwd = findViewById(R.id.btn_findIdPwd);
        btn_findIdPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, FindPWActivity.class);
                startActivity(intent);
            }
        });

        // 구글 로그인 시 설정 세팅
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        // 구글 로그인 버튼을 클릭했을 때 수행되는 문장
        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REQ_SIGN_GOOGLE);
            }
        });

        // 카카오 로그인 시 (세션 콜백 구현)
        mSessionCallback = new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                // 카카오 로그인 요청
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        // 카카오 로그인이 실패
                        Log.i("KAKAO_SESSION", "로그인 실패");
                        Toast.makeText(LoginActivity.this, "로그인 도중에 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        // 카카오 로그인 세션이 닫힌 경우
                        Toast.makeText(LoginActivity.this, "세션이 닫혔습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        // 카카오로그인이 성공한 경우
                        Intent userIntent = new Intent(LoginActivity.this, UserInfoActivity.class);

                        // 파이어베이스에 회원정보를 입력하기 위한 임시 코드 (회원정보를 확인하기 위해 이메일, 비밀번호를 임시로 저장시킴.)
                        userIntent.putExtra("email", result.getKakaoAccount().getEmail());
                        userIntent.putExtra("name", result.getKakaoAccount().getProfile().getNickname());
                        //userIntent.putExtra("profileImg", result.getKakaoAccount().getProfile().getProfileImageUrl());

                        String strName = userIntent.getStringExtra("name");
                        String strEmail = userIntent.getStringExtra("email");
                        String strPwd = toHex(strName).substring(30, 40);

                        //    클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
                        if (strEmail != null) {
                            Log.d("이메일 확인 : ", result.getKakaoAccount().getEmail()); //이메일
                        }
                        else {
                            strEmail = "kakao"+strPwd+"@gmail.com";
                        }
                        // FireBase Auth 진행
                        mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // 회원가입이 성공했을 경우
                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 현재 로그인 된 유저의 정보를 가져온다.
                                UserAccount accountKakao = new UserAccount();

                                accountKakao.setIdToken(firebaseUser.getUid());
                                accountKakao.setEmailId(firebaseUser.getEmail());
                                accountKakao.setPassword(strPwd);
                                accountKakao.setName(strName);

                                // setValue() : DB에 UserAccount 정보를 insert 함.
                                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(accountKakao);
                            }
                        });

                        Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();   // 현재 액티비티 파괴
                    }
                });

            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                Log.e("KAKAO_SESSION", "로그인 실패", exception);
            }
        };
        Session.getCurrentSession().addCallback(mSessionCallback); // 세션 콜백 등록
        Session.getCurrentSession().checkAndImplicitOpen();

    }

    // 로그인 인증을 요청했을 때 결과 값을 되돌려 받는 곳
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 카카오톡 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        // 구글 로그인 확인
        if(requestCode == REQ_SIGN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            // 인증 결과가 성공이면 이 문장을 수행
            if(result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount(); // account라는 구글 로그인 정보를 담고 있는 객체(닉네임, 프로필사진, 이메일 주소 등)
                resultLogin(account);   // 로그인 결과 값 출력 수행 메소드
            }
            else{
                Toast.makeText(LoginActivity.this, "구글 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 구글 로그인 결과 값 출력 수행 메소드
    private void resultLogin(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                // 로그인 된 구글 정보를 통해 어플 로그인의 성공 여부
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 로그인 성공 시 실행문장    // task : 인증 결과
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            // 회원가입이 성공했을 경우
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 현재 로그인 된 유저의 정보를 가져온다.

                            // 구글 로그인 정보 옮기는 문장
                            UserAccount userAccountGoogle = new UserAccount();
                            userAccountGoogle.setIdToken(firebaseUser.getUid());
                            userAccountGoogle.setEmailId(firebaseUser.getEmail());
                            userAccountGoogle.setName(firebaseUser.getDisplayName());

                            // setValue() : DB에 UserAccount 정보를 insert 함.
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(userAccountGoogle);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();   // 현재 액티비티 파괴
                        }
                        // 로그인 실패 시 실행 문장
                        else {
                            Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * 카카오 로그인 시 필요한 해시키를 얻는 메소드 이다.

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for(Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 죽었을 때, 객체 해제하는 역활 // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(mSessionCallback);
    }

    // 문자열을 16진수로 변환해주는 코드 (카카오로그인 패스워드 작성 시 사용)
    public String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }
}