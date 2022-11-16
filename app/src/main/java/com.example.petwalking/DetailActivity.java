package com.example.petwalking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailActivity extends AppCompatActivity {
    private TextView tv_title, tv_date, tv_time, tv_name, tv_content;
    private Button btn_update, btn_delete;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tv_title = findViewById(R.id.tv_title);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        tv_name = findViewById(R.id.tv_name);
        tv_content = findViewById(R.id.tv_content);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);

        Intent intent = getIntent(); /*데이터 수신*/
        String name = intent.getExtras().getString("name");
        String idToken = intent.getExtras().getString("idToken");
        String content = intent.getExtras().getString("content");
        String title = intent.getExtras().getString("title");
        String date = intent.getExtras().getString("date");
        String time = intent.getExtras().getString("time");

        tv_name.setText(name);
        tv_content.setText(content);
        tv_time.setText(time);
        tv_title.setText(title);
        tv_date.setText(date);

        if(idToken.equals(firebaseUser.getUid())){
            btn_update.setVisibility(View.VISIBLE);
            btn_update.setEnabled(true);
            btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailActivity.this, BoardUpdateActivity.class);
                    intent.putExtra("idToken", idToken);
                    intent.putExtra("name", name);
                    intent.putExtra("content", content);
                    intent.putExtra("time", time);
                    intent.putExtra("title", title);
                    intent.putExtra("date", date);
                    startActivity(intent);
                    finish();
                }
            });

            btn_delete.setVisibility(View.VISIBLE);
            btn_delete.setEnabled(true);
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabaseRef.child("Board").child(date+time).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void Void) {
                            Toast toast = Toast.makeText(DetailActivity.this, "다이어리가 삭제되었습니다.", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("에러발생~",e.getMessage());
                            Toast toast = Toast.makeText(DetailActivity.this, "다이어리 삭제가 실패하였습니다.", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                        }
                    });
                }
            });
        } else {
            // 작성자를 제외한 이용자는 게시글 수정과 삭제가 불가능!
            btn_update.setVisibility(View.GONE);
            btn_update.setEnabled(false);
            btn_delete.setVisibility(View.GONE);
            btn_delete.setEnabled(false);
        }
    }
}