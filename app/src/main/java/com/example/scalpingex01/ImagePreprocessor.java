package com.example.scalpingex01;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.opencv.core.Mat;
import org.opencv.android.Utils;
import android.graphics.Bitmap;


public class ImagePreprocessor {
    private Context context;

    public ImagePreprocessor(Context context) {
        this.context = context;
    }

    // 이미지 파일 경로를 받아 ByteBuffer로 변환하는 메서드
    public ByteBuffer preprocessImage(String imagePath) {
        // 이미지 파일을 Bitmap으로 로드
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap == null) {
            throw new IllegalArgumentException("Invalid image path: " + imagePath);
        }

        // OpenCV에서 사용할 수 있는 Mat 형식으로 변환
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        // 퓨리에 변환 적용
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

        Mat dft = new Mat();
        Core.dft(gray, dft, Core.DFT_COMPLEX_OUTPUT);

        // 결과를 시간 도메인으로 변환 (역 퓨리에 변환)
        Mat idft = new Mat();
        Core.idft(dft, idft, Core.DFT_SCALE | Core.DFT_REAL_OUTPUT, 0);

        // 결과를 정규화하여 [0, 255] 범위로 매핑
        Core.normalize(idft, idft, 0, 255, Core.NORM_MINMAX);

        // Mat를 다시 Bitmap으로 변환
        Bitmap processedBitmap = Bitmap.createBitmap(idft.cols(), idft.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(idft, processedBitmap);

        // 크기 조정 (600x600)
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(processedBitmap, 600, 600, true);

        // ByteBuffer 할당 (1, 600, 600, 3)
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 600 * 600 * 3); // float형 4바이트
        byteBuffer.order(ByteOrder.nativeOrder()); // 바이트 순서 설정

        // 이미지를 ByteBuffer로 변환 (RGB 채널)
        int[] pixels = new int[600 * 600];
        resizedBitmap.getPixels(pixels, 0, 600, 0, 0, 600, 600);

        // 각 픽셀을 정규화하여 ByteBuffer에 넣음
        for (int pixel : pixels) {
            float r = ((pixel >> 16) & 0xFF) / 255.0f;
            float g = ((pixel >> 8) & 0xFF) / 255.0f;
            float b = (pixel & 0xFF) / 255.0f;

            // 모델 학습에 필요한 정규화된 값 넣기
            byteBuffer.putFloat((r - 0.485f) / 0.229f);  // Red 채널 정규화
            byteBuffer.putFloat((g - 0.456f) / 0.224f);  // Green 채널 정규화
            byteBuffer.putFloat((b - 0.406f) / 0.225f);  // Blue 채널 정규화
        }

        return byteBuffer;
    }
}
