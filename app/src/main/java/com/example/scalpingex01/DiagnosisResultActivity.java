package com.example.scalpingex01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagnosisResultActivity extends AppCompatActivity {

    LinearLayout mainLayout,diagLayout,histLayout,infoLayout;
    private ImageView resultImageView;
    private TextView resultTextView, diagDetailTextView, diagSolutionTextView, dateTextView, userName,userName2, scalpType, TypeDesc;

    private LineChart lineChart;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_result);

        //resultImageView = findViewById(R.id.user_image);
        userName = findViewById(R.id.userNameText);     //사용자 이름,,, 나중에 설정할 수 있게ㅜ
        userName2 =findViewById(R.id.userNameText2);
        scalpType = findViewById(R.id.scTypeText);      //결과 유형 부분에 적을 것
        TypeDesc = findViewById(R.id.typeDesc);         //두피 유형 설명

        resultTextView = findViewById(R.id.TVdiagResult);   //두피 관리 방법
        diagDetailTextView = findViewById(R.id.TVdiagDetail); // 진단 세부사항 표시를 위한 텍스트뷰
        //diagSolutionTextView = findViewById(R.id.TVdiagSolution); // 추천 솔루션 텍스트뷰
        dateTextView= findViewById(R.id.dateText);

        mainLayout = findViewById(R.id.MainLayout);
        diagLayout = findViewById(R.id.DiagLayout);
        histLayout = findViewById(R.id.HistLayout);
        infoLayout = findViewById(R.id.InfoLayout);

        mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(DiagnosisResultActivity.this,MainActivity.class);
            startActivity(intent);
        });
        histLayout.setOnClickListener(view -> {
            Intent intent = new Intent(DiagnosisResultActivity.this,HistoryActivity.class);
            startActivity(intent);
        });
        infoLayout.setOnClickListener(view -> {
            Intent intent = new Intent(DiagnosisResultActivity.this,InfoActivity.class);
            startActivity(intent);
        });


        // LoadingActivity에서 이미지 받기
        //Intent intent = getIntent();
        //String result = intent.getStringExtra("result");    //LoadingActivity에서 받아올 결과. result종류에 따라 뒤에 if문으로 정해진 text나오도록

        imagePath = getIntent().getStringExtra("imagePath");
        Log.d("DiagnosisResultActivity", "imagePath: " + imagePath);

        // Intent로부터 결과 배열 받기
        int[] intArrayResult = getIntent().getIntArrayExtra("intArrayResult");

        // 결과 출력 (예: 로그 출력)
        for (int i = 0; i < intArrayResult.length; i++) {
            Log.d("DiagnosisResult", "Model " + (i + 1) + ": " + intArrayResult[i]);
        }


        //진단 결과로 나온 6개 값에 대한 배열 각각0~3사이의 값을 가짐
        //미세각질, 지루성두피염, 모낭사이홍반, 모낭홍반농포, 비듬, 탈모
        // 배열 -> 리스트 변환
        List<Integer> details = new ArrayList<>();
        for (int value : intArrayResult) {
            details.add(value);
        }


        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = sdf.format(calendar.getTime());

        // TextView에 현재 날짜 설정
        dateTextView.setText(currentDate);


        /* 차트를 그려보자... */
        // LineChart 객체 찾기
        lineChart = findViewById(R.id.lineChart);

        // 데이터를 생성합니다.
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, intArrayResult[0]));  // 미세각질
        entries.add(new Entry(1, intArrayResult[1]));  // 지루성두피염
        entries.add(new Entry(2, intArrayResult[2]));  // 모낭사이홍반
        entries.add(new Entry(3, intArrayResult[3]));  // 모낭홍반농포
        entries.add(new Entry(4, intArrayResult[4]));  // 비듬
        entries.add(new Entry(5, intArrayResult[5]));  // 탈모

        // 데이터셋 생성
        LineDataSet lineDataSet = new LineDataSet(entries, "Scalp Condition");
        lineDataSet.setColor(getResources().getColor(R.color.keypurple));  // 선 색상 설정
        lineDataSet.setCircleColor(getResources().getColor(R.color.keypurple)); // 점 색상 설정
        lineDataSet.setLineWidth(2f);  // 선 두께 설정
        lineDataSet.setCircleRadius(5f);  // 점 크기 설정
        lineDataSet.setDrawValues(false);  // 각 점에 값 표시 숨김

        // 라인 데이터 설정
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();  // 차트 업데이트

        // Y축 설정 (왼쪽 Y축 숨기기)
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);  // y축 최소값
        yAxisLeft.setAxisMaximum(3f);  // y축 최대값
        yAxisLeft.setGranularity(1f);   // y축 간격을 1로 설정
        yAxisLeft.setDrawGridLines(false);  // 그리드 선 숨기기
        yAxisLeft.setDrawLabels(false);  // Y축 레이블 숨기기
        yAxisLeft.setDrawAxisLine(false);  // Y축 라인 숨기기

        // 오른쪽 Y축 숨기기
        lineChart.getAxisRight().setEnabled(false);

        // 차트 여백 설정 - 왼쪽에 여백 추가
        lineChart.setExtraOffsets(20, 10, 10, 20);  // 왼쪽 여백을 늘려 레이블 공간 확보

        // X축 설정
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // x축을 하단에 배치
        xAxis.setGranularity(1f);  // x축 간격을 1로 설정
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // 각 항목에 맞는 레이블을 설정
                switch ((int) value) {
                    case 0:
                        return "미세각질";
                    case 1:
                        return "지루성두피염";
                    case 2:
                        return "모낭사이홍반";
                    case 3:
                        return "모낭홍반농포";
                    case 4:
                        return "비듬";
                    case 5:
                        return "탈모";
                    default:
                        return "";
                }
            }
        });
        xAxis.setDrawAxisLine(false);  // X축 라인 숨기기
        //xAxis.setTextColor(getResources().getColor(R.color.transparent)); // X축 텍스트를 투명 처리하여 숨기기
        xAxis.setDrawGridLines(true);  // X축 그리드 선(세로선) 표시
        xAxis.setGridColor(getResources().getColor(R.color.white));  // X축 세로선 색상을 흰색으로 설정
        xAxis.setGridLineWidth(1.5f);  // X축 세로선 두께 설정 (2f로 예시, 원하는 굵기로 조정 가능)

        // 범례 숨기기
        lineChart.getLegend().setEnabled(false);

        // 설명 숨기기
        lineChart.getDescription().setEnabled(false);

        /*    차트는 여기까지...      */




        String pid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 현재 로그인한 사용자 ID

        //String solutions = "추천 솔루션,,,";         //result종류에 따라 뒤에 if문으로 정해진 text나오도록 && chatGPT API 이용할 때 사용.

        //9가지 진단 유형 설정 로직
        //switch case로 각 9가지 유형에 따른 관리 방법을 strings에서 가져옴, 두피 관리 방법 설명
        String type = diagnos_nine(intArrayResult);

        //String type = "Oily";

        userName.setText("나린");   //firbase에서 userName을 받아옴
        userName2.setText("나린");
        scalpType.setText(type);    //TypeDesc에 두피 유형 설명


        // 결과와 이미지를 UI에 표시
  //      if (imagePath != null) {
  //          Bitmap bitmap = BitmapFactory.decodeFile(imagePath); // 파일 경로로부터 이미지 디코딩
            //resultImageView.setImageBitmap(bitmap); // 이미지뷰에 설정
  //      }

        //resultTextView.setText(result);

        // 진단 날짜 가져오기 (현재 날짜)
        String currentDate1 = getCurrentDate();

        // 진단 데이터 Firebase에 저장
        saveDiagnosisData(pid, imagePath, type, details, currentDate1);
    }




    //1.정상  2.건성  3.지성  4.민감성  5.지루성  6.염증성  7.비듬성  8.탈모  9.복합성
    private String diagnos_nine(int[] rst) {
        if(rst[0] ==0 && rst[1] ==0 && rst[2] ==0 && rst[3] ==0 && rst[4] ==0 && rst[5]==0){
            TypeDesc.setText(getString(R.string.NORMAL));
            resultTextView.setText(getString(R.string.scalp_care_tips1));
            return "정상";
        } else if (rst[0] != 0 && rst[1] ==0 && rst[2] ==0 && rst[3] ==0 && rst[4] ==0 && rst[5]==0) {
            TypeDesc.setText(getString(R.string.DRY));
            resultTextView.setText(getString(R.string.scalp_care_tips2));
            return "건성";
        } else if (rst[0] ==0 && rst[1] !=0 && rst[2] ==0 && rst[3] ==0 && rst[4] ==0 && rst[5]==0) {
            TypeDesc.setText(getString(R.string.OILY));
            resultTextView.setText(getString(R.string.scalp_care_tips3));
            return "지성";
        } else if (rst[1] ==0 && rst[2] !=0 && rst[3] ==0 && rst[4] ==0 && rst[5]==0) {
            TypeDesc.setText(getString(R.string.SENSITIVE));
            resultTextView.setText(getString(R.string.scalp_care_tips4));
            return "민감성";
        } else if (rst[1] !=0 && rst[2] !=0 && rst[3] !=0 && rst[5]==0) {
            TypeDesc.setText(getString(R.string.SEBORRHEIC));
            resultTextView.setText(getString(R.string.scalp_care_tips5));
            return "지루성";
        } else if (rst[2] ==0 && rst[3] !=0 && rst[5]==0) {
            TypeDesc.setText(getString(R.string.INFLAMMATORY));
            resultTextView.setText(getString(R.string.scalp_care_tips6));
            return "염증성";
        } else if (rst[2] ==0 && rst[3] ==0 && rst[4] !=0 && rst[5]==0) {
            TypeDesc.setText(getString(R.string.DANDRUFF));
            resultTextView.setText(getString(R.string.scalp_care_tips7));
            return "비듬성";
        } else if (rst[0] ==0 && rst[1] !=0 && rst[2] ==0 && rst[3] ==0 && rst[4] ==0 && rst[5]!=0) {
            TypeDesc.setText(getString(R.string.HAIR_LOSS));
            resultTextView.setText(getString(R.string.scalp_care_tips8));
            return "탈모";
        }else{
            TypeDesc.setText(getString(R.string.COMBINATION));
            resultTextView.setText(getString(R.string.scalp_care_tips9));
            return "복합성";
        }
    }



    // 현재 날짜를 "yyyy-MM-dd" 형식으로 반환하는 메서드
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    // Firebase에 진단 데이터 저장
    private void saveDiagnosisData(String pid, String imagePath, String type, List<Integer> details, String currentDate) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(pid).child("diagnoses");

        // 데이터를 저장할 Map 생성
        Map<String, Object> diagnosisData = new HashMap<>();
        diagnosisData.put("type", type); // 진단 유형
        diagnosisData.put("imagePath", imagePath); // 이미지 경로
        diagnosisData.put("details", details); // 배열 형태의 6가지 데이터
        diagnosisData.put("note", ""); // 초기값은 빈 문자열

        // 현재 날짜를 키로 사용하여 데이터를 Firebase에 저장
        myRef.child(currentDate).setValue(diagnosisData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "진단 데이터 저장 성공!", Toast.LENGTH_SHORT).show();

                    Log.d("ResultActivity", "imagePath: " + imagePath);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "진단 데이터 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}