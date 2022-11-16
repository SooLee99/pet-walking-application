package com.example.petwalking;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ActivityDogInfo extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;             // 파이어베이스 인증
    private DatabaseReference mDatabaseRef;         // 실시간 데이터베이스
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private EditText mEtdogName, mEtdogBreed, mEtweight;
    private String strBirth;
    private Spinner mSpGender;
    private Button mBtnOk, mUploadBtn;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_info);

        // 파이어베이스 데이터베이스 연동
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DogInfo");      // 파이어베이스 DB에 저장시킬 상위 주소위치

        // 반려동물 입력필드 초기화
        mEtdogName = findViewById(R.id.et_dogName);
        mEtdogBreed = findViewById(R.id.et_dogBreed);
        mEtweight = findViewById(R.id.et_weight);

        // 반려동물 성별
        mSpGender = findViewById(R.id.sp_gender);
        ArrayAdapter adapterGender = ArrayAdapter.createFromResource(this,
                R.array.arr_gender, android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpGender.setAdapter(adapterGender);

        // 반려동물 생년월일
        DatePicker datePicker = findViewById(R.id.dataPicker);
        datePicker.init(2022, 01, 01, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                strBirth = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
            }
        });

        // 반려견 사진 업로드 버튼
        mUploadBtn = findViewById(R.id.btn_upload);
        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });

        // 반려견 정보 입력 버튼
        mBtnOk = findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 반려견 정보 입력 처리
                String strDogName = mEtdogName.getText().toString();
                String strDogBread = mEtdogBreed.getText().toString();
                String strDogGender = mSpGender.getSelectedItem().toString();
                String strDogWeight = mEtweight.getText().toString();

                // 사진업로드
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                }

                // 방금 로그인 성공한 유저의 정보를 가져오는 객체
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                // 반려견 정보 수정
                DogInfo dogInfo = new DogInfo();
                dogInfo.setIdToken(firebaseUser.getUid());
                dogInfo.setDogName(strDogName);
                dogInfo.setDogBreed(strDogBread);
                dogInfo.setDogGender(strDogGender);
                dogInfo.setDogBirth(strBirth);
                dogInfo.setDogWeight(strDogWeight);

                // setValue : DB 하위주소(UserAccount)에 정보를 삽입함. (2022-10-21 이수)
                mDatabaseRef.child("DogInfo").child(firebaseUser.getUid()).setValue(dogInfo);

                Toast.makeText(ActivityDogInfo.this, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // 사진 가져오기
    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                    }
                }
            }
    );

    // 파이어베이스 이미지 업로드
    private void uploadToFirebase(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String photomime = mime.getExtensionFromMimeType(cr.getType(uri));
        StorageReference fileRef = reference.child(System.currentTimeMillis()+"."+photomime);
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // 이미지 모델에 담기
                        Model model = new Model(uri.toString());

                        // 키로 아이디 생성
                        String modelid = root.push().getKey();

                        // 데이터 넣기
                        root.child(modelid).setValue(model);
                        Toast.makeText(ActivityDogInfo.this, "사진업로드에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivityDogInfo.this, "사진업로드에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}