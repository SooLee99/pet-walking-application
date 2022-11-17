package com.example.petwalking;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FragHome fragHome;
    private FragDiary fragDiary;
    private FragSetting fragSetting;
    private FragBoard fragBoard;
    private long backBtnTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.item_main:
                        setFrag(0);
                        break;
                    case R.id.item_daily:
                        setFrag(1);
                        break;
                    case R.id.item_board:
                        setFrag(2);
                        break;
                    case R.id.item_setting:
                        setFrag(3);
                        break;
                }
                return true;
            }
        });

        fragHome = new FragHome();
        fragDiary = new FragDiary();
        fragSetting = new FragSetting();
        fragBoard = new FragBoard();
        setFrag(0); // 첫 프래그먼트 화면 지정 선택
    }

    // 프래그먼트 교체가 일어나는 실행문
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, fragHome);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, fragDiary);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, fragBoard);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, fragSetting);
                ft.commit();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        long curTime = System.currentTimeMillis();
        long gapTime = curTime- backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        } else {
            backBtnTime = curTime;
            Toast.makeText(this,"한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}