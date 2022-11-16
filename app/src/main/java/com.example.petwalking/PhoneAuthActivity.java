package com.example.petwalking;
// 2022-10-21 이수 <휴대폰 인증 액티비티>

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

//휴대전화 인증 엑티비티
public class PhoneAuthActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "tak";
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks authCallback;
    private OnCompleteListener onCompleteListener;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance() .getReference(); //데이터베이스

    private String mVerifyId;
    private EditText mPhone_number, verify_code, mName;
    private Button request_button, resend_button, verify_Button;
    private Timer timer = null;
    private int resendTime = 20; //타이머시간
    private ViewGroup outhLayout;
    SharedPreferences sharedpref;
    private final TimerHandler mHandler= new TimerHandler(this);
    /**
     * 파이어베이스 전화번호 입력 후, sms를 받아 인증하는방식
     * 호출순서 : 1. verifyPhoneNumber(): 전화번호가 적절한지 확인 및 옵션 설정
     *          2.onCodesent(): 인증번호가 보내지면 호출되며, 인증id를 파라미터로 전달받는다
     *          (3. 휴대폰 인증코드를 자동인식 한다면 onVerificationCompleted() 호출)
     *          4. verifyAuthCode: 인증id와 입력한인증번호로 PhoneAuthCredential 객체를 만들고 회원가입신청한다.
     *          5.  signinWithPhoneAuthCredential(): FirebaseAuth가 그 Credential을 보고 맞는 Credential인지 유효한지 확인하고 로그인콜백 여부를 결정한다.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        Log.d("tak","gate");

        init();
        initSet();
        callbackInit();
    }

    public void init(){
        sharedpref=getSharedPreferences("user", Activity.MODE_PRIVATE);
        request_button = findViewById(R.id.btn_request); //인증번호요청 버튼
        verify_Button = findViewById(R.id.btn_verify); //인증번호확인 버튼
        mPhone_number = findViewById(R.id.et_phoneNumber); //전화번호입력 에디트텍스트
        verify_code = findViewById(R.id.verify_code); //인증번호입력 에디트텍스트
        mName = findViewById(R.id.et_name);
        resend_button = findViewById(R.id.bts_AgainRequest); //재전송버튼
        outhLayout = findViewById(R.id.outh_layout);

    }
    public void initSet(){
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("ko-KR");

        /*
        //초기(라디오버튼을 누르지않는상태) 에디트텍스트와 버튼을 비활성화시킴
        request_button.setEnabled(false);
        verify_Button.setEnabled(false);
        mName.setEnabled(false);
        mPhone_number.setEnabled(false);
        verify_code.setEnabled(false);*/

        verify_Button.setEnabled(false);
        request_button.setOnClickListener(this);
        resend_button.setOnClickListener(this);
        verify_Button.setOnClickListener(this);
    }

    public void callbackInit(){

        /**사용자가 인증하기 버튼을 누르면 콜백 호출*/
        authCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //즉시인증(자동으로 수신되는 SMS를 감지하여 사용자의 개입없이 인증을 수행)
            //이 메소드가 호출되면 PhoneAuthCredential 객체가 즉시 만들어져서, 파라미터로 전달된다.
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInwithPhoneAuthCrdential(credential);
                pauseTimer(); //성공 시 타이머중지

                Toast.makeText(getApplicationContext(),"휴대폰의 SMS를 자동으로 감지하여 인증완료!",Toast.LENGTH_LONG).show();
            }

            //잘못된 인증 요청에 대한 응답호출
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "실패");
                pauseTimer();  //실패 시 타이머중지
                if (e instanceof FirebaseAuthInvalidCredentialsException) {Log.d(TAG, "Invalid phone number."); }
                else if (e instanceof FirebaseTooManyRequestsException) { Log.d(TAG, "SMS Quota exceeded."); }

                Toast.makeText(PhoneAuthActivity.this, "잘못된 인증 요청입니다.", Toast.LENGTH_SHORT).show();
                mPhone_number.setEnabled(true);
                request_button.setEnabled(true);
                verify_Button.setEnabled(false);
                verify_code.setEnabled(false);
            }

            /**
             * 이 메소드는 제공된 전화번호로 인증 코드가 전송된 후에 호출
             * 후에, 사용자가 인증 코드를 입력하면, 인증 코드 + 인증 ID를 사용하여 PhoneAuthCredential 객체를 만든다.
             * PhoneAuthCredential 객체로 사용자를 로그인 처리 할수있다.
             */
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(TAG, "onCodeSent: " + verificationId);

                //후에 재전송을 위해 인증id와 토큰을 저장해놓는다.
                mVerifyId = verificationId;
                mResendToken = forceResendingToken;

                verify_Button.setEnabled(true);
                verify_code.setEnabled(true);
                request_button.setVisibility(View.GONE);
                resend_button.setVisibility(View.VISIBLE);

                startTimer(); //타이머호출
                Log.d("TAKMIN","시간: "+ resendTime);
            }
        };

        /**
         * 유저가 인증코드를 입력하면, PhoneAuthCredential 객체를 만든다.
         * (onCodeSent에 전달된 인증ID와 인증코드를 통해)
         */
        onCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) { //로그인이 성공하면
                    //Toast.makeText(PhoneAuthActivity.this, "로그인성공", Toast.LENGTH_SHORT).show();
                    //데이터베이스에 저장
                    //SharedPreferences.Editor sharedprefEdit=sharedpref.edit();
                    String strName = mName.getText().toString();
                    String strPhoneNumber = mPhone_number.getText().toString();

                    Toast.makeText(PhoneAuthActivity.this, "사용자 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PhoneAuthActivity.this, RegisterActivity.class);
                    intent.putExtra("name", strName);
                    intent.putExtra("phone", strPhoneNumber);
                    startActivity(intent);
                    finish();

                    /*//사용자 라디오버튼이 눌려있다면
                    if(user_RadioBt.isChecked()){
                        final Intent intent = new Intent(PhoneAuthActivity.this, ConnectBlueToothActivity.class);

                        //User클래스의 해쉬맵을이용하지않고 아래줄처럼만해도 똑같이 동작함
                        //mDatabase.child("유저").child("사용자").push().setValue(mPhone_number.getText().toString());
                        sharedprefEdit.putBoolean("isGuardian",false);
                        sharedprefEdit.commit();

                        //가입한 유저가 DB에 있는지 검사
                        checkUserDB(user,"사용자");
                        startActivity(intent);
                        finish();

                    }
                    //보호자 라디오버튼이 눌려있다면
                    else if(guardian_RadioBt.isChecked()){
                        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        //User클래스의 해쉬맵을이용하지않고 아래줄처럼만해도 똑같이 동작함
                        //mDatabase.child("유저").child("보호자").push().setValue(mPhone_number.getText().toString());
                        sharedprefEdit.putBoolean("isGuardian",true);
                        sharedprefEdit.commit();

                        //가입한 유저가 DB에 있는지 검사
                        checkUserDB(user,"보호자");
                        startActivity(intent);
                        finish();
                    }*/
                }
                else { Toast.makeText(PhoneAuthActivity.this, "인증번호가 틀립니다.", Toast.LENGTH_SHORT).show(); }
            }
        };
    }

    //Firebase인증 서버에서 전달받은 Credential이 올바른지 확인하고 로그인 여부를 결정
    private void signInwithPhoneAuthCrdential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(onCompleteListener);
    }

    //파이어베이스에 가입된 유저인지 체크
    void checkUserDB(final UserAccount user, final String userType){
        //UID조회
        mDatabase.child("유저").child(userType).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag=false;
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    String uid=data.getKey();
                    Log.d("tak","uid: "+uid);
                    //현재 로그인된 UID가 파이어베이스계정에 가입되어있다면(로그인)
                    if(uid.equals(mAuth.getCurrentUser().getUid()))
                        flag=true;

                }
                //가입이 안되있다면 데이터추가(회원가입)
                if(!flag)
                    mDatabase.child("유저").child(userType).child(user.getIdToken()).setValue(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    //입력한 인증번호와 메시지인증번호를 확인하는코드
    private void verifyAuthCode(String verificationId, String code) {
        //인증ID+ 입력한 인증코드를 통해 Credential을 만든다.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        //얻은 Credential로 그 Credential로 로그인 신청을한다.
        signInwithPhoneAuthCrdential(credential);
    }

    /**
     * phoneNumber를 국제 번호양식에 맞게 수정합니다.
     * 사용자가 +를 써서 입력했을 경우 그대로 받고 0으로 시작하여 입력했으면
     * 처음 0을 +82로 치환합니다.
     */
    String modifyPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return phoneNumber.replaceFirst("0", "+82");
        } else {
            Toast.makeText(this, "잘못된 번호입니다!", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    /** 인증번호 전송 요청 **/
    // 매개변수로받은 사용자의 전화번호를 확인하도록 요청->oncodesent()
    private void sendAuthCode(String phoneNumber) {
        if (mName.length()==0 || mPhone_number.length()==0) {
            Toast.makeText(PhoneAuthActivity.this, "이름 또는 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            String modiPhone = modifyPhoneNumber(phoneNumber);

            if (!modiPhone.equals("")) {
                Log.d("tak", modiPhone);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(modiPhone, 20, TimeUnit.SECONDS, this, authCallback);
            }
        }
    }

    /** 인증번호 재전송 요청**/
    private void resendAuthCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        verify_Button.setEnabled(true);
        String modiPhone = modifyPhoneNumber(phoneNumber);
        if (!modiPhone.equals(""))
            PhoneAuthProvider.getInstance().verifyPhoneNumber(modiPhone, 20, TimeUnit.SECONDS, this, authCallback, token);
    }

    //핸들러에게 메세지전송-> 핸들러에서 HandleMessage()가호출됨
    private void startTimer() {
        //초기상태거나 타이머해제가되면 Null이므로 타이머 객체를 생성
        if(timer==null) timer=new Timer();

        if (resendTime < 20) return;

        TimerTask TT = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        };
        timer.schedule(TT,0,1000); //Timer을 1초를 주기로 실행
    }

    /** 타이머 중지 **/
    //for 메모리누수 방지를 위해 인증완료나 인증실패시 이함수를 호출시킴
    private void pauseTimer(){
        resendTime=20;
        try{
            timer.cancel();
            timer=null;
        } catch (Exception e){
            //타이밍에따라 오류발생할수있음
        }
        resend_button.setText("재전송");
    }

    @Override
    public void onClick(View v) {
        if (mName.length()==0 || mPhone_number.length()==0) {
            Toast.makeText(PhoneAuthActivity.this, "이름 또는 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            if (v == request_button) { //인증시작
                sendAuthCode(mPhone_number.getText().toString());
                outhLayout.setVisibility(View.VISIBLE);

                //재전송버튼
                //과거 인증번호요청했을때의 onCodeSent()에서 저장했던 토큰을 매개변수로전달
            } else if (v == resend_button) {
                resendAuthCode(mPhone_number.getText().toString(), mResendToken);

                //확인 버튼
            } else if (v == verify_Button) {
                //입력한 인증번호와 인증ID를 이용하여 PhoneAuthCredential객체를만들기위해 다음메소드호출->기존에있던 PhoneAuthCredential객체와 비교
                verifyAuthCode(mVerifyId, verify_code.getText().toString());
            }
        }
    }

    //메인엑티비티 메소드
    //핸들러에서 1초주기로 이함수를호출함
    //1초씩 감소시킴으로써 시간을 표시하고 0초가되면 타이머 중지시킴
    public void handleMessage(Message msg){
        resendTime-=1;
        if(resendTime==0){
            resend_button.setText("재전송");
            resend_button.setEnabled(true);
            verify_Button.setEnabled(false);
            try {
                timer.cancel();
                timer=null;
            }catch (Exception e){
                //타이밍에따라 오류가발생할수있음
            }
            resendTime=20;
        } else{
            resend_button.setText("재전송까지 : " +resendTime);
        }
        if(resendTime<0)
            resendTime=0;
    }
}