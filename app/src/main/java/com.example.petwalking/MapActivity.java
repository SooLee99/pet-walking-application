package com.example.petwalking;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.StrictMath.atan2;
import static java.lang.StrictMath.sin;
import static java.lang.StrictMath.sqrt;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback, SensorEventListener {

    private ImageView playBtn, pauseBtn, stopBtn, timeLapseBtn;
    private TextView timeView;
    private String API_Key = "l7xxb927c2c48f4b402192ef7872a0e0754a";
    private TMapView tMapView = null;
    private TMapGpsManager tMapGPS = null;
    private ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();
    private TextView tv_distance;
    private ImageButton down;

    // 이동 거리 계산 필드
    double[] lon = new double[100];
    double[] lat = new double[100];
    int count= 0;
    double total = 0; // 총 거리

    // 타이머 계산 필드
    // integers to store hours, minutes, seconds,  ms
    int hours, minutes, secs, ms;
    // integer to store seconds
    private int seconds = 0;
    // boolean to check if the stopwatch is running or not
    private boolean running;
    // simple count variable to count number of laps
    int lapCount=0;

    // 걸음 수 계산 필드
    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private TextView stepCountView;
    // 현재 걸음 수
    private int currentSteps = 0;
    // 걸음 수 현재 상태
    private int stepState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        stepCountView = findViewById(R.id.stepCountView);
        stepState = 0;

        // 활동 퍼미션 체크
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        // 걸음 센서 연결
        // * 옵션
        // - TYPE_STEP_DETECTOR:  리턴 값이 무조건 1, 앱이 종료되면 다시 0부터 시작
        // - TYPE_STEP_COUNTER : 앱 종료와 관계없이 계속 기존의 값을 가지고 있다가 1씩 증가한 값을 리턴
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // 디바이스에 걸음 센서의 존재 여부 체크
        if (stepCountSensor == null) {
            Toast.makeText(this, "디바이스에 걸음 센서가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
        }

        // 리셋 버튼 추가 - 리셋 기능

        // 거리
        tv_distance = findViewById(R.id.tv_distance);

        // permission 부분(접근 권한)
        verifyStoragePermission(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        down = findViewById(R.id.down);

        // 스톱워치
        playBtn = findViewById(R.id.playBtn) ;
        pauseBtn = findViewById(R.id.pauseBtn) ;
        stopBtn = findViewById(R.id.stopBtn) ;

        // initializing the text view objects
        timeView = findViewById(R.id.time_view) ;

        // play button click listener
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showing simple toast message to user
                Toast.makeText(MapActivity.this, "산책이 시작되었습니다.", Toast.LENGTH_SHORT).show();
                stepState = 1;
                // hide the play and stop button
                playBtn.setVisibility(View.GONE) ;
                stopBtn.setVisibility(View.GONE) ;

                // show the pause  and time lapse button
                pauseBtn.setVisibility(View.VISIBLE) ;

                // set running true
                running = true ;
            }
        }) ;
        // pause button click listener
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showing simple toast message to user
                Toast.makeText(MapActivity.this, "산책이 일시멈춤 되었습니다.", Toast.LENGTH_SHORT).show();

                stepState = 0;

                // show the play  and stop  button
                playBtn.setVisibility(View.VISIBLE) ;
                stopBtn.setVisibility(View.VISIBLE) ;
                pauseBtn.setVisibility(View.GONE) ;
                running = false ;
            }
        }) ;

        // stop button click listener
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showing simple toast message to user
                Toast.makeText(MapActivity.this, "산책이 중단되었습니다.", Toast.LENGTH_SHORT).show();

                stepState = 0;

                // 걸음 수 초기화
                currentSteps = 0;
                stepCountView.setText(String.valueOf(currentSteps));

                // set running to false
                running = false ;
                seconds = 0 ;
                lapCount = 0 ;

                // setting the text view to zero
                timeView.setText("00:00:00") ;

                // show the play
                playBtn.setVisibility(View.VISIBLE) ;

                // hide the pause , stop and time lapse button
                pauseBtn.setVisibility(View.GONE) ;
                stopBtn.setVisibility(View.GONE) ;
            }
        }) ;

        runTimer() ;

        ///////////////// 지도 API ////////////////
        // T Map View
        tMapView = new TMapView(this);

        // API Key
        tMapView.setSKTMapApiKey(API_Key);

        // Initial Setting
        tMapView.setZoomLevel(17);
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        // T Map View Using Linear Layout
        LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        linearLayoutTmap.addView(tMapView);

        // Request For GPS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // GPS using T Map
        tMapGPS = new TMapGpsManager(this);

        // Initial Setting
        tMapGPS.setMinTime(1);
        tMapGPS.setMinDistance(1);
        tMapGPS.setProvider(tMapGPS.NETWORK_PROVIDER);
        //tMapGPS.setProvider(tMapGPS.GPS_PROVIDER);

        tMapGPS.OpenGps();

    }

    // T MAP 실시간 위치
    @Override
    public void onLocationChange(Location location) {
        double Longitude = location.getLongitude(); //경도
        double Latitude = location.getLatitude();   //위도

        // 실시간 위치
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());

        // 실시간 이동 선그리기
        alTMapPoint.add( new TMapPoint(location.getLatitude(), location.getLongitude()));
        TMapPolyLine tMapPolyLine = new TMapPolyLine();
        tMapPolyLine.setLineColor(Color.BLUE);
        tMapPolyLine.setLineWidth(2);
        for( int i=0; i<alTMapPoint.size(); i++) {
            tMapPolyLine.addLinePoint( alTMapPoint.get(i) );
        }
        tMapView.addTMapPolyLine("Line1", tMapPolyLine);

        // 이동 거리 계산
        // 거리계산 식
        if(count == 0){
            lon[0] = Longitude;
            lat[0] = Latitude;
            lon[1] = Longitude;
            lat[1] = Latitude;
        }else{
            lon[count] = Longitude;     // count로 매번 포인트마다 위도/경도를 대입
            lat[count] = Latitude;
            double d2r = (Math.PI / 180D);
            double dlong = (lon[count] - lon[count-1]) * d2r;
            double dlat = (lat[count] - lat[count-1]) * d2r;
            double a = pow(sin(dlat/2.0), 2) + cos(lat[count-1]*d2r) * cos(lat[count]*d2r) * pow(sin(dlong/2.0), 2);
            double c = 2 * atan2(sqrt(a), sqrt(1-a));
            double d = (6367 * c) / 100;

            total += d;
            tv_distance = findViewById(R.id.tv_distance);
            tv_distance.setText(String.format("%.2f", total) +"km");    // km단위로 거리 출력
        }
        count++;
    }

    // 타이머 메소드
    private void runTimer() {
        final Handler handlertime = new Handler();
        handlertime.post(new Runnable() {@Override

        public void run() {
            hours = seconds / 3600;
            minutes = (seconds % 3600) / 60;
            secs = seconds % 60;
            // if running increment the seconds
            if (running) {
                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);
                seconds++;
            }

            handlertime.postDelayed(this, 1000);
        }
        });
    }

    // 캡처버튼 메소드
    public void ScreenshotButton(View view) {
        View rootView = getWindow().getDecorView();  //전체화면 부분

        File screenShot = ScreenShot(rootView);
        if (screenShot != null) {
            //갤러리에 추가합니다
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
        }
        down.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),"갤러리에 저장되었습니다.",Toast.LENGTH_SHORT).show();
    }

    //화면 캡쳐하기
    public File ScreenShot(View view){
        down.setVisibility(View.INVISIBLE);

        view.setDrawingCacheEnabled(true);

        Bitmap screenBitmap = view.getDrawingCache(); //비트맵으로 변환

        String filename = "petWalking_screenshot"+System.currentTimeMillis()+".png";
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures", filename);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os); //비트맵 > PNG파일
            os.close();
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);
        return file;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSION_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    PERMISSION_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public void onStart() {
        super.onStart();
        if(stepCountSensor !=null) {
            // 센서 속도 설정
            // * 옵션
            // - SENSOR_DELAY_NORMAL: 20,000 초 딜레이
            // - SENSOR_DELAY_UI: 6,000 초 딜레이
            // - SENSOR_DELAY_GAME: 20,000 초 딜레이
            // - SENSOR_DELAY_FASTEST: 딜레이 없음
            //
            sensorManager.registerListener(this,stepCountSensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 걸음 센서 이벤트 발생시
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // 타이머가 실행되는 동안에만 센서 측정!
            if(stepState == 1) {
                if (event.values[0] == 1.0f) {
                    // 센서 이벤트가 발생할때 마다 걸음수 증가
                    currentSteps++;
                    stepCountView.setText(String.valueOf(currentSteps));
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {    }
}