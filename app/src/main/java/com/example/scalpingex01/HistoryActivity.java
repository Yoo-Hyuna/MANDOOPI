package com.example.scalpingex01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout mainLayout,diagLayout,histLayout,infoLayout;
    private RecyclerView calendarRecyclerView;
    private TextView selectedDateTextView, selectMonth, dateTV, tvSCtype, tvSCdesc, toggleTextView;
    private ImageView imageView;
    private EditText noteEditText;
    private Button saveButton;
    private Uri imageUri;

    private String selectedDate; // 선택된 날짜
    private DatabaseReference reference;
    private String userId, imagePath;
    private LineChart lineChart;
    private Bitmap selectedImage;

    private boolean isImageVisible = false;
    private List<String> recordedDates; // 기록이 있는 날짜 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        toggleTextView = findViewById(R.id.toggleTextView);
        imageView = findViewById(R.id.imageView);

        // UI 연결
        calendarRecyclerView = findViewById(R.id.rv_calendar);       //달력 안짝
        //selectedDateTextView = findViewById(R.id.tv_selected_date);  //선택된 날짜를 달력 아래에서 보여줌
        dateTV = findViewById(R.id.dateText);       //보라색 박스 날짜

        tvSCtype = findViewById(R.id.scTypeText);
        tvSCdesc = findViewById(R.id.typeDesc);

        noteEditText = findViewById(R.id.et_note);                      //두피 일기 작성칸

        saveButton = findViewById(R.id.btn_save_note);
        selectMonth = findViewById(R.id.textOverlay);           //달력 위 mm월


        mainLayout = findViewById(R.id.MainLayout);
        diagLayout = findViewById(R.id.DiagLayout);
        histLayout = findViewById(R.id.HistLayout);
        infoLayout = findViewById(R.id.InfoLayout);

        mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(HistoryActivity.this,MainActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });
        diagLayout.setOnClickListener(view -> {
            Intent intent = new Intent(HistoryActivity.this,DiagnosisActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });
        infoLayout.setOnClickListener(view -> {
            Intent intent = new Intent(HistoryActivity.this,InfoActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });


        // 사용자 ID 가져오기
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("diagnoses");

        recordedDates = new ArrayList<>(); // 기록이 있는 날짜 리스트

        // 현재 날짜를 가져와서 연도와 월을 추출
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH는 0부터 시작하므로 +1

        // 현재 월에 해당하는 데이터 가져와서 달력에 표시
        loadRecordedDates(currentYear, currentMonth);

        // 현재 월을 표시
        setCurrentMonth();

        selectMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        // TextView 클릭 리스너 설정
        toggleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageVisible) {
                    // 이미지 숨기기
                    imageView.setVisibility(View.GONE);
                    toggleTextView.setText("이미지 보기");
                } else {
                    // 이미지 보이기 - Uri를 Bitmap으로 변환하여 ImageView에 설정
                    if (imageUri != null) {
                        // Uri를 실제 파일 경로로 변환
                        String imagePath = getRealPathFromURI(imageUri);  // 실제 경로를 반환
                        if (imagePath != null) {
                            // Bitmap을 실제 파일 경로에서 디코딩
                            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                            if (bitmap != null) {
                                // Bitmap을 ImageView에 설정
                                imageView.setImageBitmap(bitmap);
                                imageView.setVisibility(View.VISIBLE);
                                toggleTextView.setText("접기");
                            } else {
                                // Bitmap을 디코딩할 수 없는 경우
                                toggleTextView.setText("이미지 로딩 실패");
                            }
                        } else {
                            // imagePath가 null인 경우
                            toggleTextView.setText("이미지 경로 없음");
                        }
                    } else {
                        // imageUri가 null인 경우
                        toggleTextView.setText("이미지 URI 없음");
                    }
                }
                // 상태 업데이트
                isImageVisible = !isImageVisible;
            }
        });


        // 저장 버튼 클릭 이벤트
        saveButton.setOnClickListener(v -> saveNote());

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        } else {
            return contentUri.getPath();
        }
    }



    // 현재 월을 가져와서 textOverlay에 설정하는 함수
    private void setCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;  // Calendar.MONTH는 0부터 시작, 그래서 1을 더함
        String currentMonthText = month + "월";
        selectMonth.setText(currentMonthText);
    }

    // 날짜 선택 다이얼로그 표시
    private void showDatePickerDialog() {
        // 팝업 다이얼로그에 사용할 NumberPicker 설정
        View spinnerView = getLayoutInflater().inflate(R.layout.date_picker_dialog, null);
        NumberPicker yearPicker = spinnerView.findViewById(R.id.yearPicker);
        NumberPicker monthPicker = spinnerView.findViewById(R.id.monthPicker);

        // 연도 NumberPicker 설정
        yearPicker.setMinValue(2019);
        yearPicker.setMaxValue(2024);

        // 월 NumberPicker 설정
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);

        // 다이얼로그 빌더 설정
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("날짜 선택")
                .setView(spinnerView)
                .setPositiveButton("완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 선택된 연도와 월을 가져와서 버튼에 표시
                        String selectedYear = String.valueOf(yearPicker.getValue());
                        String selectedMonth = String.format("%02d", monthPicker.getValue());
                        String selectedDate = selectedYear + "년 " + selectedMonth + "월";
                        selectMonth.setText(selectedDate);
                        Toast.makeText(HistoryActivity.this, "선택된 날짜: " + selectedDate, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // 기록이 있는 날짜 가져오기
    private void loadRecordedDates(int year, int month) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recordedDates.clear();
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey(); // 형식: "2024-12-05"
                    if (date != null && date.startsWith(year + "-" + String.format("%02d", month))) {
                        recordedDates.add(date.split("-")[2]); // 일(day)만 저장
                    }
                }

                // 달력 데이터 생성 및 RecyclerView 설정
                List<String> days = CalendarUtils.generateCalendarDays(year, month);
                CalendarAdapter adapter = new CalendarAdapter(days, recordedDates, HistoryActivity.this::onDateSelected);
                calendarRecyclerView.setLayoutManager(new GridLayoutManager(HistoryActivity.this, 7));
                calendarRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, "데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 날짜 선택 시 호출
    private void onDateSelected(String day) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH는 0부터 시작하므로 +1

        selectedDate = day;
        //selectedDateTextView.setText("선택된 날짜: " + day);
        loadDiagnosisData(year, month, day); // 선택한 날짜 데이터 불러오기
    }

    // 선택된 날짜 데이터 불러오기
    private void loadDiagnosisData(int year, int month, String day) {
        String selectedDate = String.format("%d-%02d-%s", year, month, day);

        // 이미 diagnoses 노드를 참조하고 있으므로 child("diagnoses")는 불필요
        reference.child(selectedDate) // 이제 diagnoses/{selectedDate} 노드를 바로 참조
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // 데이터를 가져오기
                            String note = snapshot.child("note").getValue(String.class);
                            imagePath = snapshot.child("imagePath").getValue(String.class);
                            String type = snapshot.child("type").getValue(String.class);

                            // details 배열 가져오기
                            List<Integer> details = new ArrayList<>();
                            for (DataSnapshot item : snapshot.child("details").getChildren()) {
                                Integer value = item.getValue(Integer.class);
                                if (value != null) {
                                    details.add(value);
                                }
                            }

                            // 리스트를 배열로 변환
                            Integer[] detailsArray = details.toArray(new Integer[0]);


                            // UI 업데이트
                            dateTV.setText(selectedDate);   //날짜
                            drawPlot(detailsArray);         //그래프
                            tvSCtype.setText(type);         //두피 유형
                            diagnos_nine(type);             //두피 설명

                            imageUri = Uri.fromFile(new File(imagePath));


                            // 이미지가 있으면 "이미지 보기" 버튼 표시
                            if (imagePath != null) {
                                toggleTextView.setVisibility(View.VISIBLE);
                                toggleTextView.setText("이미지 보기");
                                isImageVisible = false;
                                imageView.setVisibility(View.GONE);
                            } else {
                                // 이미지가 없으면 버튼과 이미지 숨기기
                                toggleTextView.setVisibility(View.GONE);
                                imageView.setVisibility(View.GONE);
                            }

                            noteEditText.setText(note != null ? note : "");

                            // Log 값 출력
                            Log.d("DiagnosisData", "Note: " + note);
                            Log.d("DiagnosisData", "Image Path: " + imagePath);
                            Log.d("DiagnosisData", "Type: " + type);
                            Log.d("DiagnosisData", "Details: " + details.toString());

                        } else {
                            // 데이터가 없을 때
                            noteEditText.setText("");
                            dateTV.setText("해당 날짜의 데이터가 없습니다.");
                            Toast.makeText(HistoryActivity.this, "데이터가 없습니다.", Toast.LENGTH_SHORT).show();

                            // Log 값 출력
                            Log.d("DiagnosisData", "No data found for date: " + selectedDate);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // 데이터 가져오기 실패
                        Toast.makeText(HistoryActivity.this, "데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("DiagnosisData", "Database error: " + error.getMessage());
                    }
                });
    }

    //유형에 따른 strings 간단한거 불러오기
    private void diagnos_nine(String type) {
        switch (type) {
            case "정상":
                tvSCdesc.setText(getString(R.string.NORMAL));
                break;
            case "건성":
                tvSCdesc.setText(getString(R.string.DRY));
                break;
            case "지성":
                tvSCdesc.setText(getString(R.string.OILY));
                break;
            case "민감성":
                tvSCdesc.setText(getString(R.string.SENSITIVE));
                break;
            case "지루성":
                tvSCdesc.setText(getString(R.string.SEBORRHEIC));
                break;
            case "염증성":
                tvSCdesc.setText(getString(R.string.INFLAMMATORY));
                break;
            case "비듬성":
                tvSCdesc.setText(getString(R.string.DANDRUFF));
                break;
            case "탈모":
                tvSCdesc.setText(getString(R.string.HAIR_LOSS));
                break;
            case "복합성":
                tvSCdesc.setText(getString(R.string.COMBINATION));
                break;
            default:
                break;
        }
    }

    // 경로에서 이미지를 로드하는 메서드
    private Bitmap loadImageFromPath(String path) {
        File imgFile = new File(path);
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return null;
    }

    private void drawPlot(Integer[] intArrayResult) {
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

    }


    // 두피 일기 저장하기
    private void saveNote() {
        if (selectedDate == null) {
            Toast.makeText(this, "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = noteEditText.getText().toString();
        reference.child("diagnoses").child(selectedDate).child("note").setValue(note)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "저장 성공!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
