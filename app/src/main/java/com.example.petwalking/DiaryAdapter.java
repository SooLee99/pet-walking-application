package com.example.petwalking;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private ArrayList<DiaryItem> arrayList;
    private Context context;

    // viewType 형태의 아이템 뷰를 위한 뷰홀더 객체 생성.
    public DiaryAdapter(ArrayList<DiaryItem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        DiaryViewHolder holder = new DiaryViewHolder(view);
        return holder;
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.DiaryViewHolder holder, int position) {
        holder.tv_title.setText(arrayList.get(position).getTitle());

        // 문장의 길이가 길면 (...더보기) 표시
        if((arrayList.get(position).getContent()).length() >= 15){
            holder.tv_more.setVisibility(View.VISIBLE);
            String content = arrayList.get(position).getContent().substring(0, 14);
            holder.tv_content.setText(content);
        } else {
            holder.tv_content.setText(arrayList.get(position).getContent());
            holder.tv_more.setVisibility(View.GONE);
        }
        holder.tv_date.setText(arrayList.get(position).getWriteDate());
        Glide.with(holder.itemView).load(arrayList.get(position).getPhoto()).into(holder.iv_photo);
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_date, tv_content, tv_more;

        ImageView iv_photo;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_date = itemView.findViewById(R.id.tv_date);
            this.tv_content = itemView.findViewById(R.id.tv_content);
            this.iv_photo = itemView.findViewById(R.id.iv_photo);
            this.tv_more = itemView.findViewById(R.id.tv_more);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int curPos = getAdapterPosition();      //현재 선택한 리스트 아이템 위치
                    DiaryItem itemList = arrayList.get(curPos);
                    Intent intent =  new Intent(context, Diary1Activity.class);   //화면 넘겨주기
                    Log.d("일기 인덱스", String.valueOf(arrayList.get(curPos)));
                    intent.putExtra("index", curPos);
                    intent.putExtra("content", itemList.getContent());
                    intent.putExtra("title", itemList.getTitle());
                    intent.putExtra("date", itemList.getWriteDate());
                    intent.putExtra("time", itemList.getWriteTime());
                    intent.putExtra("photo", itemList.getPhoto());
                    context.startActivity(intent);
                }
            });
        }
    }
}
