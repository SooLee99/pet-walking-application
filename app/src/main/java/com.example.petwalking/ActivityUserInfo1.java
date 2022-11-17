package com.example.petwalking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ActivityUserInfo1 extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private EditText et_name;
    private Button btn_ok;
    private String changeName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        et_name = findViewById(R.id.et_name);
        btn_ok = findViewById(R.id.btn_ok);

        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                changeName = "";
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if (userInfo[0] == null || userInfo[0].equals(null)) {
                    changeName = "";
                } else {
                    changeName = userInfo[0].getName();
                    et_name.setText(changeName);
                }
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName = et_name.getText().toString();
                if(changeName.equals("") || changeName.equals(null) || changeName == null){
                    Toast.makeText(ActivityUserInfo1.this, "변경할 이름을 입력해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    Map<String, Object> taskMap1 = new HashMap<String, Object>();
                    taskMap1.put("name", changeName);
                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).updateChildren(taskMap1);
                    Toast toast = Toast.makeText(ActivityUserInfo1.this, "정보가 수정되었습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
