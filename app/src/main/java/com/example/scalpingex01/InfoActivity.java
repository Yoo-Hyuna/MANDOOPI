package com.example.scalpingex01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

public class InfoActivity extends AppCompatActivity {
    LinearLayout mainLayout,diagLayout,histLayout,infoLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mainLayout = findViewById(R.id.MainLayout);
        diagLayout = findViewById(R.id.DiagLayout);
        histLayout = findViewById(R.id.HistLayout);
        infoLayout = findViewById(R.id.InfoLayout);

        mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(InfoActivity.this,MainActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });
        diagLayout.setOnClickListener(view -> {
            Intent intent = new Intent(InfoActivity.this,DiagnosisActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });
        histLayout.setOnClickListener(view -> {
            Intent intent = new Intent(InfoActivity.this,HistoryActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });


    }
}