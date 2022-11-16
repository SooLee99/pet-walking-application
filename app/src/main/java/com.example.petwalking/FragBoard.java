package com.example.petwalking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class FragBoard extends Fragment {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private DatabaseReference mDatabaseRef1;
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private RecyclerView recyclerView;
    private RecyclerView.Adapter board_adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BoardInfo> board_arrayList;
    private FloatingActionButton btn_write;
    private ImageButton searchBtn;
    private TextView searchText;
    private String search, title;
    private long backBtnTime = 0;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_board, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        board_arrayList = new ArrayList<>(); // 객체에 정보담을 배열
        mDatabaseRef1 = mFirebaseDB.getInstance().getReference().child("Board").getRef();
        mDatabaseRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 파이어베이스 데이터베이스 데이터를 받아오는 곳
                board_arrayList.clear();
                for(DataSnapshot ss : snapshot.getChildren()){
                    BoardInfo boardInfo1 = ss.getValue(BoardInfo.class);
                    board_arrayList.add(boardInfo1);
                }
                board_adapter.notifyDataSetChanged(); // 리스트에 저장 및 새로고침
            }
            // DB 에러처리
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB에러입니당!~", "bbbbbb");
            }
        });

        board_adapter = new BoardAdapter(board_arrayList, getContext());
        recyclerView.setAdapter(board_adapter);

        // 게시판 추가하기 버튼
        btn_write = view.findViewById(R.id.btn_write);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BoardAddActivity.class);
                startActivity(intent);
            }
        });

        searchBtn = view.findViewById(R.id.searchBtn);
        searchText = view.findViewById(R.id.searchText);

        //검색버튼 클릭 시
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search = searchText.getText().toString();
                Log.d("정보", search);
                if(!search.equals("")) {
                    board_arrayList.clear();

                    mDatabaseRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // 파이어베이스 데이터베이스 데이터를 받아오는 곳
                            board_arrayList.clear();
                            for(DataSnapshot ss : snapshot.getChildren()){
                                BoardInfo boardInfo1 = ss.getValue(BoardInfo.class);
                                title = boardInfo1.getTitle();
                                if(title.contains(search)) {
                                    board_arrayList.add(boardInfo1);
                                }
                            }
                            board_adapter.notifyDataSetChanged(); // 리스트에 저장 및 새로고침
                        }
                        // DB 에러처리
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            board_arrayList.clear();
                            Toast toast = Toast.makeText(getActivity(), "게시글을 불러올 수가 없습니다.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            }
        });

        return view;
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    /*public void onBackPressed(){
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        } else {
            backBtnTime = curTime;
            Toast.makeText(getActivity(),"한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }*/
}
