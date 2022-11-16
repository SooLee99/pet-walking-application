package com.example.petwalking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class UserInfoActivity extends AppCompatActivity {

    private TextView tv_user;       // 닉네임 TextView
    private ImageView iv_profile;   // 회원 사진 ImageView
    private Button btn_logout;      // 로그아웃 Button
    private TextView tv_email;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
/*
        Intent intent = getIntent();
        String strName = intent.getStringExtra("name");                 // LoginActivity 에서 닉네임 전달 받음.
        String strProfileImg = intent.getStringExtra("profileImg");     // LoginActivity 에서 프로필 사진 URL 전달 받음.

        //tv_user = findViewById(R.id.tv_user);
        tv_user.setText(strName);                                           // 닉네임 text를 텍스트 뷰에 세팅.

        //iv_profile = findViewById(R.id.iv_profile);
        Glide.with(this).load(strProfileImg).into(iv_profile);       // 프로필 URL을 이미지 뷰에 세팅.
*/
        //btn_logout = findViewById(R.id.btn_logout);
        // 로그아웃
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        // 로그아웃 성공 시 수행하는 지점
                        finish();   // 현재 액티비티 종료
                    }
                });
            }
        });

        /*bottomNavigationView = findViewById(R.id.bottomNavi);

        //처음화면
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new FragHome()).commit(); //FrameLayout에 fragment.xml 띄우기

        //바텀 네비게이션뷰 안의 아이템 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml띄우기
                    case R.id.item_diary:
                        //getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new FragHome()).commit();
                        break;
                    case R.id.item_main:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new FragHome()).commit();
                        break;
                    case R.id.item_setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new CalenderFrag()).commit();
                        break;
                }
                return true;
            }
        });*/
    }
}