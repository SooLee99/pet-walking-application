package com.example.petwalking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Diary1Activity extends AppCompatActivity {
    private TextView tv_title, tv_date, tv_content, tv_time;
    private ImageView iv_photo;
    private Button btn_update, btn_delete;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();                           // 방금 로그인 성공한 유저의 정보를 가져오는 객체

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        tv_title = findViewById(R.id.tv_title);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        tv_content = findViewById(R.id.tv_content);
        iv_photo = findViewById(R.id.iv_photo);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);

        Intent intent = getIntent(); /*데이터 수신*/
        String content = intent.getExtras().getString("content");
        String title = intent.getExtras().getString("title");
        String date = intent.getExtras().getString("date");
        String time = intent.getExtras().getString("time");
        String photo = intent.getExtras().getString("photo");

        if(photo != null) {
            iv_photo.setImageURI(Uri.parse(photo));
        }
        tv_content.setText(content);
        tv_title.setText(title);
        tv_date.setText(date);
        tv_time.setText(time);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Diary1Activity.this, DiaryUpdate1Activity.class);
                intent.putExtra("content", content);
                intent.putExtra("title", title);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("photo", photo);
                startActivity(intent);
                finish();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("Diary").child(firebaseUser.getUid()).child(date+time).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void Void) {
                        Toast toast = Toast.makeText(Diary1Activity.this, "다이어리가 삭제되었습니다.", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("에러발생~",e.getMessage());
                        Toast toast = Toast.makeText(Diary1Activity.this, "다이어리 삭제가 실패하였습니다.", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }
                });
            }
        });

    }
}
