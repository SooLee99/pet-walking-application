package com.example.petwalking;

import android.net.Uri;

public class DiaryItem {
    private String title;       // 일기 제목
    private String content;     // 일기 내용
    private String writeDate;   // 작성 날짜
    private String writeTime;   // 작성 날짜
    private String photo;       // 일기 사진(URL 주소로 저장)
    private int index;          // 일기 인덱스

    public DiaryItem() { }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public String getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(String writeTime) {
        this.writeTime = writeTime;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
