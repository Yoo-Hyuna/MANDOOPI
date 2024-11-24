package com.example.scalpingex01;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiagnosisActivity extends AppCompatActivity {


    LinearLayout mainLayout,diagLayout,histLayout,infoLayout;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ImageView scalpImg;
    private Button buttonSelectImage, buttonDiagnose;

    private Bitmap selectedImage;
    private String imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);

        mainLayout = findViewById(R.id.MainLayout);
        diagLayout = findViewById(R.id.DiagLayout);
        histLayout = findViewById(R.id.HistLayout);
        infoLayout = findViewById(R.id.InfoLayout);

        scalpImg = findViewById(R.id.ImgLayout);
        buttonSelectImage = findViewById(R.id.SelectPicture);
        buttonDiagnose = findViewById(R.id.diagResultBtn);

        buttonSelectImage.setOnClickListener(view -> {
            // 권한 체크 후 갤러리 열기
            if (checkPermission()) {
                openGallery();
            } else {
                requestPermission();
            }
        });

        mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(DiagnosisActivity.this,MainActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });
        histLayout.setOnClickListener(view -> {
            Intent intent = new Intent(DiagnosisActivity.this,HistoryActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });
        infoLayout.setOnClickListener(view -> {
            Intent intent = new Intent(DiagnosisActivity.this,InfoActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });


        // 진단 시작 버튼 클릭 시 로딩 화면으로 전환
        buttonDiagnose.setOnClickListener(view -> {
            // 이미지가 선택된 경우에만 로딩 액티비티로 전달
            if (selectedImage != null) {
                String imagePath = saveImageToInternalStorage(selectedImage);
                if (imagePath != null) {
                    Intent intent = new Intent(DiagnosisActivity.this, LoadingActivity.class);
                    intent.putExtra("imagePath", imagePath); // 파일 경로 전달
                    Log.d("DiagnosisActivity", "imagePath: " + imagePath);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // 갤러리 열기 함수
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 갤러리에서 이미지 선택 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                displayImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지를 불러오는 데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // 선택한 이미지를 ImageView에 표시하는 함수
    private void displayImage(Bitmap selectedImage) {
        try {
            // Bitmap을 scalpImg ImageView에 설정
            scalpImg.setImageBitmap(selectedImage);
            scalpImg.setScaleType(ImageView.ScaleType.CENTER_CROP); // 이미지 크기 조정


            // 활성화된 상태에 맞는 배경과 텍스트 색상 설정
            buttonSelectImage.setBackgroundResource(R.drawable.button_round); // 활성화 배경
            buttonSelectImage.setTextColor(getResources().getColor(R.color.black)); // 흰색 텍스트

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "이미지를 불러오는 데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
/*
    // 선택한 이미지를 내부 저장소에 저장하고 경로를 반환하는 함수
    private String saveImageToInternalStorage(Bitmap bitmap) {
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(directory, "selected_image.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    */

    // 이미지를 내부 저장소에 고유한 이름으로 저장하고 그 경로를 반환하는 함수
    private String saveImageToInternalStorage(Bitmap bitmap) {
        // 현재 시간 기반으로 고유한 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";

        // 파일 경로 설정
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ScalpImages");
        if (!directory.exists()) {
            directory.mkdirs();  // 디렉토리가 없다면 생성
        }

        File file = new File(directory, fileName);
        Log.d("DiagnosisActivity", "fileName: " + fileName);

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  // 비트맵을 JPEG로 압축하여 저장
            out.flush();
            return file.getAbsolutePath();  // 저장된 파일의 경로 반환
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 권한 체크 함수
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // 권한 요청 함수
    private void requestPermission() {
        // Android 11 이상에서는 MANAGE_EXTERNAL_STORAGE 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            } else {
                openGallery(); // 권한이 허용되었으므로 갤러리 열기
            }
        } else {
            // Android 10 이하에서는 READ_EXTERNAL_STORAGE 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery(); // 권한이 허용되면 갤러리 열기
            } else {
                Toast.makeText(this, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}