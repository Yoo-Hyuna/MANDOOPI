package com.example.scalpingex01;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private final List<String> days;
    private final List<String> recordedDates; // 기록된 날짜 리스트
    private final OnDayClickListener onDayClickListener;
    private String selectedDate = "";  // 선택된 날짜 변수 추가

    public CalendarAdapter(List<String> days, List<String> recordedDates, OnDayClickListener onDayClickListener) {
        this.days = days;
        this.recordedDates = recordedDates;
        this.onDayClickListener = onDayClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_day_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String day = days.get(position);

        // 빈 칸 처리: 빈 칸은 아무것도 하지 않음
        if (day.isEmpty()) {
            holder.dayTextView.setText("");
            holder.dayTextView.setBackgroundResource(0);  // 배경을 없애기
            return;
        }


        holder.dayTextView.setText(day);

        // 기록된 날짜 스타일 설정
        if (recordedDates.contains(day)) {
            holder.dayTextView.setBackgroundResource(R.drawable.select_cal_g); // 회색 동그라미 배경
        } else {
            holder.dayTextView.setBackgroundResource(0); // 배경 없음
        }

        // 선택된 날짜에 대해 보라색 원 스타일 적용
        if (day.equals(selectedDate)) {
            holder.dayTextView.setBackgroundResource(R.drawable.select_cal_p); // 보라색 동그라미 배경
        }

        holder.itemView.setOnClickListener(v -> {
            if (!day.isEmpty()) { // 날짜가 비어있지 않은 경우만 처리
                selectedDate = day;  // 선택된 날짜를 저장
                notifyDataSetChanged();  // 어댑터 갱신하여 보라색 원 표시
                onDayClickListener.onDayClick(day);
            }
        });
    }


    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dayTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);  //calendar_day_item 내 textView
        }
    }

    public interface OnDayClickListener {
        void onDayClick(String day);
    }
}
