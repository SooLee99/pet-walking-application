package com.example.petwalking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// 사용자 지정 어댑터 클래스
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private ArrayList<DiaryItem> mDiaryItems;
    private Context mContext;
    private DBHelper mDBHelper;

    public CustomAdapter(ArrayList<DiaryItem> mDiaryItems, Context mContext) {
        this.mDiaryItems = mDiaryItems;
        this.mContext = mContext;
        mDBHelper = new DBHelper(mContext);
    }

    public CustomAdapter() {
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
        holder.tv_title.setText(mDiaryItems.get(position).getTitle());
       //holder.tv_content.setText(mDiaryItems.get(position).getContent());
        holder.tv_writeDate.setText(mDiaryItems.get(position).getWriteDate());
    }

    @Override
    public int getItemCount() {
        return mDiaryItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        //private TextView tv_content;
        private TextView tv_writeDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            //tv_content = itemView.findViewById(R.id.tv_content);
            tv_writeDate = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curPos = getAdapterPosition(); // 현재 리스트 아이템 위치
                    DiaryItem diaryItem = mDiaryItems.get(curPos);

                    String[] strChoiceItems = {"수정하기", "삭제하기"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("원하는 작업을 선택 해주세요.");
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if(position == 0) {
                                // 수정하기 옵션 선택 시
                                // 팝업 창 띄우기
                                Dialog dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
                                dialog.setContentView(R.layout.dialog_edit);
                                EditText et_title = dialog.findViewById(R.id.et_title);
                                EditText et_content = dialog.findViewById(R.id.et_content);
                                Button btn_ok = dialog.findViewById(R.id.btn_ok);
                                btn_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Update Table
                                        String title = et_title.getText().toString();
                                        String content = et_content.getText().toString();
                                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 현재 시간 받아오기
                                        String beforeTime = diaryItem.getWriteDate();

                                        mDBHelper.UpdateDiary(et_title.getText().toString(), et_content.getText().toString(), currentTime, beforeTime);

                                        // Update UI
                                        diaryItem.setTitle(title);
                                        diaryItem.setContent(content);
                                        diaryItem.setWriteDate(currentTime);
                                        notifyItemChanged(curPos, diaryItem);
                                        dialog.dismiss();
                                        Toast.makeText(mContext, "목록 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.show();
                            }
                            else if(position == 1) {
                                // Delete Table
                                String beforeTime = diaryItem.getWriteDate();
                                mDBHelper.DeleteDiary(beforeTime);

                                // Delete UI
                                mDiaryItems.remove(curPos);
                                notifyItemRemoved(curPos);
                                Toast.makeText(mContext, "목록이 제거되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    // 프래그먼트에서 호출되는 함수이며, 현재 어뎁터에 새로운 게시글 아이템을 전달받아 추가하는 목적이다.
    public void addItem(DiaryItem _item) {
        mDiaryItems.add(0, _item);
        notifyItemChanged(0);

    }
}