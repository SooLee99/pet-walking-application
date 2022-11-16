package com.example.petwalking;
// 2022-10-21 이수 <TimerHandler.Class>

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class TimerHandler extends Handler{
    // 메모리 누수를 막기위해 WeakReference를 사용
    // 엑티비티가 화면에서 사라졌을때 정상적으로 메모리 해제되어야하는데 다른쓰레드가 잡고있으면 -> 메모리누수원인
    private final WeakReference<PhoneAuthActivity> mActivty;

    public TimerHandler(PhoneAuthActivity activity){
        mActivty=new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        PhoneAuthActivity activity = mActivty.get();
        if(activity!=null){
            activity.handleMessage(msg);
        }
    }
}