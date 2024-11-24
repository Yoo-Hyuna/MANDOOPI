package com.example.scalpingex01;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.opencv.android.OpenCVLoader;

import java.io.IOException;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    LinearLayout mainLayout,diagLayout,histLayout,infoLayout;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int MANAGE_STORAGE_PERMISSION_REQUEST_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.MainLayout);
        diagLayout = findViewById(R.id.DiagLayout);
        histLayout = findViewById(R.id.HistLayout);
        infoLayout = findViewById(R.id.InfoLayout);

        // 권한 체크 및 요청
        requestGalleryPermission();

        diagLayout.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,DiagnosisActivity.class);
            startActivity(intent);
        });
        histLayout.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
            startActivity(intent);
        });
        infoLayout.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,InfoActivity.class);
            startActivity(intent);
        });

        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV 초기화 실패!");
        } else {
            Log.d(TAG, "OpenCV 초기화 성공!!!!!");
        }
    }


    private void requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 이상에서는 MANAGE_EXTERNAL_STORAGE 권한이 필요합니다.
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, MANAGE_STORAGE_PERMISSION_REQUEST_CODE);
            } else {
                //onGalleryPermissionGranted();
            }
        } else {
            // Android 11 미만에서는 READ_EXTERNAL_STORAGE 권한을 요청합니다.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                //onGalleryPermissionGranted();
            }
        }
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //onGalleryPermissionGranted();
            } else {
                Toast.makeText(this, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MANAGE_STORAGE_PERMISSION_REQUEST_CODE) {
            // 권한이 허용되었는지 확인
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    //onGalleryPermissionGranted();
                } else {
                    Toast.makeText(this, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void onGalleryPermissionGranted() {
        // 갤러리 접근 권한이 허용되었을 때 수행할 동작
        Toast.makeText(this, "갤러리 접근 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
        // 갤러리 기능을 사용하도록 코드를 추가하세요.
    }
}