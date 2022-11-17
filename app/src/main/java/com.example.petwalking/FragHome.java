package com.example.petwalking;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FragHome extends Fragment {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();        // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseStorage mDatabase = FirebaseStorage.getInstance();      // 파이어베이스 이미지 가져오기
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();     // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private Button btn_calender, btn_walk;
    private ImageButton btn_reset;
    private TextView tvLocation, tvWeather, tvTemperatures, tvDogName, tv_DogBreed, tv_DogBirth, tv_DogGender, tv_DogWeight;
    private String baseDate, baseTime, weather,tmperature, weatherResult, photo;                                             // 기온 결과
    private View view;
    private ImageView ivWeather;
    private RoundedImageView ivDog;
    public static String weatherSave = "현재 날씨는 맑은 상태입니다.";

    // 위치 정보
    private double longitude = 37.4481;    // 인하공전 경도
    private double latitude = 126.6585;    // 인하공전 위도

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_home, container, false);
        tvDogName = view.findViewById(R.id.tv_DogName);
        tv_DogBreed = view.findViewById(R.id.tv_DogBreed);
        tv_DogBirth = view.findViewById(R.id.tv_DogBirth);
        tv_DogGender = view.findViewById(R.id.tv_DogGender);
        tv_DogWeight = view.findViewById(R.id.tv_DogWeight);
        ivDog = view.findViewById(R.id.iv_Dog);

        //데이터 읽기
        final DogInfo[] dogInfo = {new DogInfo()};
        mDatabaseRef.child("DogInfo").child("DogInfo").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            // 참조에 액세스 할 수 없을 때 호출
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvDogName.setText("이름을 입력해주세요.");
                tv_DogBreed.setText("견종을 입력해주세요.");
                tv_DogBirth.setText("생일을 입력해주세요.");
                tv_DogGender.setText("성별을 입력해주세요.");
                tv_DogWeight.setText("몸무게를 입력해주세요.");
                Toast.makeText(getActivity(), "액세스가 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dogInfo[0] = snapshot.getValue(DogInfo.class);
                if(dogInfo[0] == null) {
                    Log.d("여기", "1");
                    tvDogName.setText("이름을 입력해주세요.");
                    tv_DogBreed.setText("견종을 입력해주세요.");
                    tv_DogBirth.setText("생일을 입력해주세요.");
                    tv_DogGender.setText("성별을 입력해주세요.");
                    tv_DogWeight.setText("몸무게를 입력해주세요.");
                    ivDog.setImageResource(R.drawable.dog);
                } else {
                    Log.d("여기", "2");
                    if(dogInfo[0].getDogName().equals("")) {
                        tvDogName.setText("이름을 입력해주세요.");
                    } else {
                        tvDogName.setText(dogInfo[0].getDogName());
                    }

                    if(dogInfo[0].getDogBreed().equals("")) {
                        tv_DogBreed.setText("견종을 입력해주세요.");
                    } else {
                        tv_DogBreed.setText(dogInfo[0].getDogBreed());
                    }

                    if(dogInfo[0].getDogBirth().equals("")) {
                        tv_DogBirth.setText("생일을 입력해주세요.");
                    } else {
                        tv_DogBirth.setText(dogInfo[0].getDogBirth());
                    }
                    if(dogInfo[0].getDogGender().equals("")) {
                        tv_DogGender.setText("성별을 입력해주세요.");
                    } else {
                        tv_DogGender.setText(dogInfo[0].getDogGender());
                    }
                    if(dogInfo[0].getDogWeight().equals("")) {
                        tv_DogWeight.setText("몸무게를 입력해주세요.");
                    } else {
                        tv_DogWeight.setText(dogInfo[0].getDogWeight());
                    }
                    if ((dogInfo[0].getDogImg()).equals("null"))
                        ivDog.setImageResource(R.drawable.dog);
                    else {
                        photo = dogInfo[0].getDogImg();
                        ivDog.setImageURI(Uri.parse(photo));
                    }
                }
            }
        });
        btn_walk = view.findViewById(R.id.btn_walk);    // 산책 버튼
        //btn_calender = view.findViewById(R.id.btn_calender);
        btn_reset = view.findViewById(R.id.btn_reset);  // 날씨, 위치 리셋버튼
        tvWeather = view.findViewById(R.id.tvWeather);  // 날씨 텍스트
        tvTemperatures = view.findViewById(R.id.tvTemperatures);    // 날씨 온도
        tvLocation = view.findViewById(R.id.tvLocation);            // 현재 위치

        // 날씨 이미지 뷰
        ivWeather = view.findViewById(R.id.ivWeather);
        Glide.with(this).load(R.mipmap.sun).into(ivWeather);

        /* 달력 버튼
        btn_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalenderActivity.class);
                startActivity(intent);
            }
        });*/

        // 산책 버튼
        btn_walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 화면으로 이동
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });

        // 위치정보 확인하는 버튼
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            0);
                } else {
                    // 위치 관리자 객체 참조하기
                    LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                    // 가장최근 위치정보 가져오기
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.d("현재 위치", String.valueOf(location));

                    if (location != null) {
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                        Geocoder g = new Geocoder(getActivity());
                        List<Address> address = null;

                        // 주소 객체의 메서드 이용하여 정보 추출
                        try {
                            address = g.getFromLocation(latitude, longitude, 10);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        tvLocation.setText("현재위치 : " + address.get(0).getAddressLine(0));
                    } else {
                        longitude = 37.4481;    // 인하공전 경도
                        latitude = 126.6585;    // 인하공전 위도
                        tvLocation.setText("위치를 확인할 수 없습니다.\n(초기위치 : 인하공업전문대학)");
                    }
                    if (weatherResult.equals("날씨를 확인할 수가 없어요.")) {
                        tvWeather.setText(weatherResult);
                    } else {
                        int beginIndex = weatherResult.lastIndexOf(",") + 1;
                        int endIndex = weatherResult.length();
                        // 혹시 모를 에러 처리하기!!
                        if (beginIndex != 0) {
                            Log.d("정보", String.valueOf(beginIndex));
                            String temperatures = weatherResult.substring(beginIndex, endIndex);    // 기온
                            String weather = weatherResult.substring(0, (beginIndex - 1));    // 날씨
                            tvTemperatures.setText(temperatures);
                            if(!weatherSave.equals(weather)) {
                                // 날씨에 따라 이미지 변경
                                if (weather.equals("현재 날씨는 맑은 상태입니다.")) {
                                    Glide.with(ivWeather).load(R.mipmap.sun).into(ivWeather);
                                    tvWeather.setText(weather);
                                    weatherSave = "현재 날씨는 맑은 상태입니다.";
                                    ivWeather.setImageResource(R.mipmap.sun);
                                } else if (weather.equals("현재 날씨는 비가 오는 상태입니다.")) {
                                    Glide.with(ivWeather).load(R.mipmap.rain).into(ivWeather);
                                    tvWeather.setText(weather);
                                    weatherSave = "현재 날씨는 비가 오는 상태입니다.";
                                    ivWeather.setImageResource(R.mipmap.rain);
                                } else if (weather.equals("현재 날씨는 구름이 많은 상태입니다.")) {
                                    Glide.with(ivWeather).load(R.mipmap.cloudy).into(ivWeather);
                                    tvWeather.setText(weather);
                                    weatherSave = "현재 날씨는 구름이 많은 상태입니다.";
                                    ivWeather.setImageResource(R.mipmap.cloudy);
                                } else if (weather.equals("현재 날씨는 흐린 상태입니다.")) {
                                    Glide.with(ivWeather).load(R.mipmap.clouds).into(ivWeather);
                                    tvWeather.setText(weather);
                                    weatherSave = "현재 날씨는 흐린 상태입니다.";
                                    ivWeather.setImageResource(R.mipmap.clouds);
                                }
                            }
                        }
                    }
                }
            }
        });

        new Thread(() -> {
            try {
                weatherResult = lookUpWeather(longitude, latitude);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();

        return view;
    }

    // 날씨 구하는 메서드
    public String lookUpWeather(double dx, double dy) throws IOException, JSONException {
        // 현재 위치 필드 저장
        int ix = (int) dx;
        int iy = (int) dy;
        String nx = String.valueOf(ix);
        String ny = String.valueOf(iy);
        Log.i("날씨: 위도!!", nx);
        Log.i("날씨: 경도!!", ny);

        // 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        baseDate = String.valueOf(date).replaceAll("-", "");
        int correctionDate = Integer.parseInt(baseDate) - 1;     // 날씨 API : 매 시각 45분 이후 호출 // 오전 12시인 경우 사용

        // 시간(30분 단위로 맞추기)
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HHmm");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH");

        int itime1 = Integer.parseInt(time.format(formatter1)); // 실제 시간
        int itime2 = Integer.parseInt(time.format(formatter2)) - 1;

        //  /*06시30분 발표(30분 단위)*/
        if (itime2 <= 7) {
            itime2 = 23;
            baseDate = String.valueOf(correctionDate);
            baseTime = "2100";
        } else {
            // api가 30분 단위로 업데이트
            if (itime1 % 100 >= 30) baseTime = itime2 + "30";
            else baseTime = itime2 + "00";
        }
        // 오전에는 시간이 3자리로 나옴...
        if (baseTime.length() == 3) {
            baseTime = "0" + baseTime;
        }

        String weatherResult = "현재 날씨를 확인할 수가 없어요.";

        Log.i("날씨: 입력일자!!", baseDate);
        Log.i("날씨: 입력시간!!", baseTime);

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=eWD3WU%2B78w6UiyRQFINsKmuNGrDvg3JnKDnefyrBx1jEAGOxNI%2FuFwXB5W7LgsBunL2cQz6OqBLIuJQWDES1SQ%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /*‘21년 6월 28일 발표*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /*06시30분 발표(30분 단위)*/
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); /*예보지점 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); /*예보지점 Y 좌표값*/

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String result = sb.toString();

        Log.d("정보", result);

        // response 키를 가지고 데이터를 파싱
        JSONObject jsonObj_1 = new JSONObject(result);
        String response = jsonObj_1.getString("response");

        // response 로 부터 body 찾기
        JSONObject jsonObj_2 = new JSONObject(response);
        String body = jsonObj_2.getString("body");

        // body 로 부터 items 찾기
        JSONObject jsonObj_3 = new JSONObject(body);
        String items = jsonObj_3.getString("items");
        Log.i("ITEMS", items);

        // items 로 부터 itemlist 를 받기
        JSONObject jsonObj_4 = new JSONObject(items);
        JSONArray jsonArray = jsonObj_4.getJSONArray("item");

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObj_4 = jsonArray.getJSONObject(i);
            String fcstValue = jsonObj_4.getString("fcstValue");
            String category = jsonObj_4.getString("category");

            if (category.equals("SKY")) {
                weather = "현재 날씨는 ";
                if (fcstValue.equals("1")) {
                    weather += "맑은 상태입니다.";
                } else if (fcstValue.equals("2")) {
                    weather += "비가 오는 상태입니다.";
                } else if (fcstValue.equals("3")) {
                    weather += "구름이 많은 상태입니다.";
                } else if (fcstValue.equals("4")) {
                    weather += "흐린 상태입니다.";
                }
            }
            if (category.equals("T3H") || category.equals("T1H")) {
                tmperature = fcstValue + " ℃";
            }
            weatherResult = weather + "," + tmperature;
        }
        return weatherResult;
    }
}