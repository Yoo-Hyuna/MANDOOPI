package com.example.scalpingex01;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarUtils {

    public static List<String> generateCalendarDays(int year, int month) {
        List<String> days = new ArrayList<>();

        // 달력 객체 생성
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // month가 1부터 시작하므로 1을 빼서 설정
        //calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // 시작 요일 (일요일: 1, 월요일: 2...)
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 월의 마지막 날짜
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 빈 칸 추가 (첫 주 시작 요일에 맞게)
        for (int i = 1; i < firstDayOfWeek; i++) {
            days.add(""); // 공백
        }

        // 날짜 추가
        for (int day = 1; day <= lastDay; day++) {
            days.add(String.valueOf(day));
        }

        return days;
    }

}
