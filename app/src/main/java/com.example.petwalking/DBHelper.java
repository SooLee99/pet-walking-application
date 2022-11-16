package com.example.petwalking;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;

// Daily DB 관리 클래스 - 앱 내부 DB
public class DBHelper extends SQLiteOpenHelper {
    private static final  int DB_VERSION = 1;
    private static final String DB_NAME = "petwalking.db";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // DB가 생성될 때 호출. (Diary 테이블 생성 / 컬럼: id, title, content, writeDate)
        db.execSQL("CREATE TABLE IF NOT EXISTS Diary " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " + // AUTOINCREMENT : 데이터 생성 시, 자동 증가
                "title TEXT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "writeDate TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    // SELECT문 (일기 목록 조회.)
    public ArrayList<DiaryItem> getDailyList() {
        ArrayList<DiaryItem> diaryItems = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        // Daily에 있는 모든 데이터들을 내림차순으로 정렬하여, 조회한다.
        Cursor cursor = db.rawQuery("SELECT * FROM Diary ORDER BY writeDate DESC", null);
        // 조회한 데이터가 있을 때, 리스트뷰에 나오게 수행
        if(cursor.getCount() != 0) {
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String writeDate = cursor.getString(cursor.getColumnIndexOrThrow("writeDate"));

                DiaryItem diaryItem = new DiaryItem();
                diaryItem.setTitle(title);
                diaryItem.setContent(content);
                diaryItem.setWriteDate(writeDate);
                diaryItems.add(diaryItem);
            }
        }
        cursor.close();
        return diaryItems;
    }

    // INSERT문 (일기를 작성함.)
    public void InsertDiary(String _title, String _content, String _writeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO Diary (title, content, writeDate) " +
                "VALUES('" + _title + "', '" + _content + "','" + _writeDate + "');");
    }

    // UPDATE문 (일기를 수정함.)
    public void UpdateDiary(String _title, String _content, String _writeDate, String _beforeDate) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE Diary " +
                "SET title='" + _title +"', content='" + _content + "' , writeDate='" + _writeDate + "' " +
                "WHERE writeDate='" + _beforeDate +"'");
    }

    // DELETE문 (일기를 삭제함.)
    public void DeleteDiary(String _beforeDate) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM Diary WHERE writeDate ='" + _beforeDate + "'");
    }
}
