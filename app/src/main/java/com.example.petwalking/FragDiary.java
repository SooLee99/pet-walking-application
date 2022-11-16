package com.example.petwalking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragDiary extends Fragment {
    // 파이어베이스 객체
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private DatabaseReference mDatabaseRef1;
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    
    // 리사이클러뷰 객체
    private RecyclerView recyclerView;
    private RecyclerView.Adapter diaryAdater;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DiaryItem> diaryArrayList;
    private FloatingActionButton btn_write;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_diary, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        diaryArrayList = new ArrayList<>(); // 객체에 정보담을 배열
        mDatabaseRef1 = mFirebaseDB.getInstance().getReference().child("Diary").child(firebaseUser.getUid()).getRef();
        mDatabaseRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 파이어베이스 데이터베이스 데이터를 받아오는 곳
                diaryArrayList.clear();
                for(DataSnapshot ss : snapshot.getChildren()){
                    DiaryItem diaryItem = ss.getValue(DiaryItem.class);
                    diaryArrayList.add(diaryItem);
                }
                diaryAdater.notifyDataSetChanged(); // 리스트에 저장 및 새로고침
            }
            // DB 에러처리
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB에러", "DB에러");
            }
        });
        diaryAdater = new DiaryAdapter(diaryArrayList, getContext());
        recyclerView.setAdapter(diaryAdater);

        // 일기 추가하기 버튼
        btn_write = view.findViewById(R.id.btn_write);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiaryAdd1Activity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
}