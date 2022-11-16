package com.example.petwalking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class FragSetting extends Fragment {
    private Button btn_profile, btn_dogInfo, btn_logout;                        // 로그아웃 Button
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_setting, container, false);
        //btn_profile = view.findViewById(R.id.btn_profile);
        btn_dogInfo = view.findViewById(R.id.btn_dogInfo);
        btn_logout = view.findViewById(R.id.btn_logout);

        // 강아지 정보수정 버튼
        btn_dogInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ActivityDogInfo.class));
            }
        });

        // 로그아웃 버튼
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        // 로그아웃 성공 시 수행하는 지점
                        getActivity().finish();   // 현재 액티비티 종료
                    }
                });
            }
        });
        return view;
    }
}