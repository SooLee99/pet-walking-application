package com.example.petwalking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class FragSetting extends Fragment {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();                              // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();                           // 방금 로그인 성공한 유저의 정보를 가져오는 객체

    private Button btn_profile, btn_dogInfo, btn_logout;                        // 로그아웃 Button
    private View view;
    private String result = "일반";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_setting, container, false);
        btn_profile = view.findViewById(R.id.btn_profile);
        btn_dogInfo = view.findViewById(R.id.btn_dogInfo);
        btn_logout = view.findViewById(R.id.btn_logout);

        // 강아지 정보수정 버튼
        btn_dogInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ActivityDogInfo.class));
            }
        });

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ActivityUserInfo1.class));
            }
        });

        // 로그아웃 버튼
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final UserAccount[] userInfo = {new UserAccount()};
                //데이터 읽기
                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                        // 로그아웃 성공 시 수행하는 지점
                        Toast.makeText(getActivity(), "로그아웃이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();   // 현재 액티비티 종료
                        Log.d("로그아웃", "참조실패");
                    }

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userInfo[0] = snapshot.getValue(UserAccount.class);
                        if (userInfo[0] == null || userInfo[0].equals(null)) {
                            // 로그아웃 성공 시 수행하는 지점
                            Toast.makeText(getActivity(), "로그아웃이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();   // 현재 액티비티 종료
                            Log.d("로그아웃", "회원정보는 null값으로 가져옴.");
                        } else {
                            // 구글회원 또는 일반회원 로그아웃 기능수행.
                            result = userInfo[0].getResult();
                            if(result.equals("구글") || result.equals("일반")) {
                                Toast.makeText(getActivity(), "로그아웃이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();   // 현재 액티비티 종료
                                Log.d("로그아웃", "구글회원 또는 일반회원 로그아웃");
                            }
                            if(result.equals("카카오")){
                                // 카카오회원 로그아웃 기능수행.
                                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                    @Override
                                    public void onCompleteLogout() {
                                        // 로그아웃 성공 시 수행하는 지점
                                        Log.d("로그아웃", "카카오회원 로그아웃");
                                        getActivity().finish();   // 현재 액티비티 종료
                                    }
                                });
                            }

                        }
                    }
                });
            }
        });
        return view;
    }
}