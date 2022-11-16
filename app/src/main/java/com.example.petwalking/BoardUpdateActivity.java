package com.example.petwalking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class BoardUpdateActivity extends AppCompatActivity {
    private TextView tv_date, tv_time, tv_name;
    private EditText et_content, et_title;
    private Button mBtnOk, mUploadBtn;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();                              // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();                           // 방금 로그인 성공한 유저의 정보를 가져오는 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_update);
        et_title = findViewById(R.id.et_title);
        tv_date = findViewById(R.id.tv_date);
        tv_name = findViewById(R.id.tv_name);
        tv_time = findViewById(R.id.tv_time);
        et_content = findViewById(R.id.et_content);
        mBtnOk = findViewById(R.id.btn_insert);

        Intent intent = getIntent(); /*데이터 수신*/
        String content = intent.getExtras().getString("content");
        String title = intent.getExtras().getString("title");
        String date = intent.getExtras().getString("date");
        String time = intent.getExtras().getString("time");
        String name = intent.getExtras().getString("name");

        et_title.setText(title);
        tv_date.setText(date);
        tv_time.setText(time);
        tv_name.setText(name);
        et_content.setText(content);

        mBtnOk = findViewById(R.id.btn_insert);
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = et_title.getText().toString();
                String content = et_content.getText().toString();
                // 사진업로드
                if (!title.equals("") || !content.equals("")) {
                    Map<String, Object> taskMap1 = new HashMap<String, Object>();
                    taskMap1.put("title", title);
                    mDatabaseRef.child("Board").child(date + time).updateChildren(taskMap1);
                    Map<String, Object> taskMap2 = new HashMap<String, Object>();
                    taskMap2.put("content", content);
                    mDatabaseRef.child("Board").child(date + time).updateChildren(taskMap2);
                    Toast toast = Toast.makeText(BoardUpdateActivity.this, "게시물이 수정되었습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                } else {
                    Toast toast = Toast.makeText(BoardUpdateActivity.this, "입력하신 정보를 다시 확인해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}