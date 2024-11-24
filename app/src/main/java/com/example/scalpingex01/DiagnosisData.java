package com.example.scalpingex01;

public class DiagnosisData {
    private String resultImagePath;
    private String details;
    private String solutions;

    // 기본 생성자 (Firebase에서 데이터를 자동으로 매핑할 때 필요)
    public DiagnosisData() {}

    // 생성자
    public DiagnosisData(String resultImagePath, String details, String solutions) {
        this.resultImagePath = resultImagePath;
        this.details = details;
        this.solutions = solutions;
    }

    // Getter, Setter
    public String getResultImagePath() {
        return resultImagePath;
    }

    public void setResultImagePath(String resultImagePath) {
        this.resultImagePath = resultImagePath;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getSolutions() {
        return solutions;
    }

    public void setSolutions(String solutions) {
        this.solutions = solutions;
    }
}
