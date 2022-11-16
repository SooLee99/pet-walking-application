package com.example.petwalking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DetailActivity extends AppCompatActivity {
    private TextView tv_title, tv_date, tv_name, tv_content;
    private Button btn_update;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tv_title = findViewById(R.id.tv_title);
        tv_date = findViewById(R.id.tv_date);
        tv_name = findViewById(R.id.tv_name);
        tv_content = findViewById(R.id.tv_content);
        btn_update = findViewById(R.id.btn_insert);

        Intent intent = getIntent(); /*데이터 수신*/
        String name = intent.getExtras().getString("name");
        String content = intent.getExtras().getString("content");
        String title = intent.getExtras().getString("title");
        String date = intent.getExtras().getString("date");
        int itemList = intent.getExtras().getInt("itemList");

        tv_name.setText(name);
        tv_content.setText(content);
        tv_title.setText(title);
        tv_date.setText(date);

        /*(if(idToken.equals(firebaseUser.getUid())) {
            btn_update.setVisibility(View.VISIBLE);
            btn_update.setEnabled(true);
            btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailActivity.this, BoardUpdateActivity.class);
                    intent.putExtra("idToken", idToken);
                    intent.putExtra("name", name);
                    intent.putExtra("content", content);
                    intent.putExtra("title", title);
                    intent.putExtra("date", date);
                    intent.putExtra("field", field);
                    intent.putExtra("itemList", itemList);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            btn_update.setVisibility(View.GONE);
            btn_update.setEnabled(false);
        }*/
    }
}