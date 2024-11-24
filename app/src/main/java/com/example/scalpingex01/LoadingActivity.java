package com.example.scalpingex01;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class LoadingActivity extends AppCompatActivity {

    //private Bitmap selectedImage;
    private ProgressBar progressBar;
    //private Interpreter tflite2, tflite3;
    private Interpreter tflite1, tflite2, tflite3,tflite4, tflite5, tflite6;
    private int[] intArray = {0, 0, 0, 0, 0, 0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        // 프레그레스 바와 텍스트뷰 초기화
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE); // 프레그레스 바 보이게 설정

        // 두피 이미지 경로를 Intent에서 가져옴
        String imagePath = getIntent().getStringExtra("imagePath");
        Log.d("loadingActivity", "imagePath: " + imagePath);

        // 백그라운드에서 모델 실행
        new DiagnoseTask(imagePath).execute(); // 모델 실행을 위한 비동기 작업 시작

    }


    // AsyncTask로 모델을 비동기적으로 실행
    private class DiagnoseTask extends AsyncTask<Void, Void, String> {

        private String imagePath;

        public DiagnoseTask(String imagePath) {
            this.imagePath = imagePath;
        }


        //모델 돌리기 시작
        @Override
        protected String doInBackground(Void... voids) {
            try {
                // 여기에서 텐서플로우 모델을 호출하여 결과를 얻어오는 로직을 추가
                //String result = runModel(imagePath); // 실제 모델 실행 코드
                //String result1 = runModel2(imagePath); // 첫 번째 모델 실행
                //String result2 = runModel3(imagePath); // 두 번째 모델 실행
                /*
                intArray[0] = runModel1(imagePath); // 첫 번째 모델 실행하고 결과를 배열 0번째에
                intArray[1] = runModel2(imagePath); // 두 번째 모델 실행하고 결과를 배열 1번째에
                intArray[2] = runModel3(imagePath); // 세 번째 모델 실행하고 결과를 배열 2번째에
                intArray[3] = runModel4(imagePath); // 네 번째 모델 실행하고 결과를 배열 3번째에
                intArray[4] = runModel5(imagePath); // 다섯 번째 모델 실행하고 결과를 배열 4번째에
                intArray[5] = runModel6(imagePath); // 여섯 번째 모델 실행하고 결과를 배열 5번째에
                */


                //빠른 테스트를 위해 임의의 값을 배열에 담았다..................
                intArray[0] = 0; // 첫 번째 모델 실행하고 결과를 배열 0번째에
                intArray[1] = 0;; // 두 번째 모델 실행하고 결과를 배열 1번째에
                //intArray[1] = runModel2(imagePath);; // 두 번째 모델 실행하고 결과를 배열 1번째에
                intArray[2] = 1; // 세 번째 모델 실행하고 결과를 배열 2번째에
                intArray[3] = 1; // 네 번째 모델 실행하고 결과를 배열 3번째에
                intArray[4] = 2; // 다섯 번째 모델 실행하고 결과를 배열 4번째에
                intArray[5] = 3; // 여섯 번째 모델 실행하고 결과를 배열 5번째에


                return "Models executed successfully";


            } catch (Exception e) {
                e.printStackTrace();
                return "Error occurred during model execution";
            }
        }


        //모델 다 돌아가면
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // 4초 후 다음 액티비티로 배열 전달
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        // 프레그레스 바 숨기기
                        progressBar.setVisibility(ProgressBar.INVISIBLE);

                        // 바로 진단 결과 액티비티로 전환
                        Intent intent = new Intent(LoadingActivity.this, DiagnosisResultActivity.class);
                        intent.putExtra("intArrayResult", intArray);
                        intent.putExtra("imagePath", imagePath); // 이미지 경로
                        startActivity(intent);
                        finish(); // 현재 액티비티 종료
                }
            }, 4000); // 4초 지연



        }


        /*
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // 프레그레스 바 숨기기
            progressBar.setVisibility(ProgressBar.INVISIBLE);

            // 모델 실행 후 바로 진단 결과 액티비티로 전환
            Intent intent = new Intent(LoadingActivity.this, DiagnosisResultActivity.class);
            intent.putExtra("result", result); // 모델 결과
            intent.putExtra("imagePath", imagePath); // 이미지 경로
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        }
        */

    }
    /*
    // 첫 번째 모델을 실행하는 메서드 (tflite_model2.tflite)
    private int runModel2(String imagePath) {
        try {
            // 첫 번째 모델 로드
            tflite2 = new Interpreter(loadModelFile("tflite_model2.tflite"));

            // 이미지를 ByteBuffer로 변환
            ByteBuffer inputBuffer = convertImageToByteBuffer(imagePath);
            ImagePreprocessor preprocessor = new ImagePreprocessor(context);
            ByteBuffer inputBuffer = preprocessor.preprocessImage("path_to_image.jpg");

            // 모델 출력 배열 (예시로 4개의 클래스)
            float[][] output = new float[1][4];

            // 첫 번째 모델 실행
            tflite2.run(inputBuffer, output);

            // output 배열의 값을 로그로 출력하여 확인
            for (int i = 0; i < output[0].length; i++) {
                Log.d("ModelOutput2", "Class " + i + ": " + output[0][i]);
            }

            // 출력값을 분석하여 진단 결과 반환
            return processOutput(output);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

     */

    // 첫 번째 모델을 실행하는 메서드 (tflite_model1.tflite)
    private int runModel1(String imagePath) {
        try {
            // 두 번째 모델 로드
            tflite1 = new Interpreter(loadModelFile("tflite_model1.tflite"));

            // ImagePreprocessor를 사용하여 이미지를 전처리
            //ImagePreprocessor preprocessor = new ImagePreprocessor(LoadingActivity.this);
            //ByteBuffer inputBuffer = preprocessor.preprocessImage(imagePath); // 이미지 전처리

            Log.d("loadingActivity", "image 전처리함");

            // 이미지를 ByteBuffer로 변환
            //ByteBuffer inputBuffer = convertImageToByteBuffer(imagePath);
            ByteBuffer inputBuffer = preprocessImage(imagePath);

            // 모델 출력 배열 (예시로 4개의 클래스)
            float[][] output = new float[1][4];

            // 두 번째 모델 실행
            tflite1.run(inputBuffer, output);

            // output 배열의 값을 로그로 출력하여 확인
            for (int i = 0; i < output[0].length; i++) {
                Log.d("ModelOutput1", "Class " + i + ": " + output[0][i]);
            }

            // 출력값을 분석하여 진단 결과 반환
            return processOutput(output);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
    // 두 번째 모델을 실행하는 메서드 (tflite_model2.tflite)
    private int runModel2(String imagePath) {
        try {
            // 두 번째 모델 로드
            tflite2 = new Interpreter(loadModelFile("tflite_model2.tflite"));

            // ImagePreprocessor를 사용하여 이미지를 전처리
            //ImagePreprocessor preprocessor = new ImagePreprocessor(LoadingActivity.this);
            //ByteBuffer inputBuffer = preprocessor.preprocessImage(imagePath); // 이미지 전처리

            Log.d("loadingActivity", "image 전처리함");

            // 이미지를 ByteBuffer로 변환
            //ByteBuffer inputBuffer = convertImageToByteBuffer(imagePath);
            ByteBuffer inputBuffer = preprocessImage(imagePath);

            // 모델 출력 배열 (예시로 4개의 클래스)
            float[][] output = new float[1][4];

            // 두 번째 모델 실행
            tflite2.run(inputBuffer, output);

            // output 배열의 값을 로그로 출력하여 확인
            for (int i = 0; i < output[0].length; i++) {
                Log.d("ModelOutput2", "Class " + i + ": " + output[0][i]);
            }

            // 출력값을 분석하여 진단 결과 반환
            return processOutput(output);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 세 번째 모델을 실행하는 메서드 (tflite_model3.tflite)
    private int runModel3(String imagePath) {
        try {
            // 세 번째 모델 로드
            tflite3 = new Interpreter(loadModelFile("tflite_model3.tflite"));

            // ImagePreprocessor를 사용하여 이미지를 전처리
            //ImagePreprocessor preprocessor = new ImagePreprocessor(LoadingActivity.this);
            //ByteBuffer inputBuffer = preprocessor.preprocessImage(imagePath); // 이미지 전처리

            Log.d("loadingActivity", "image 전처리함");

            // 이미지를 ByteBuffer로 변환
            //ByteBuffer inputBuffer = convertImageToByteBuffer(imagePath);
            ByteBuffer inputBuffer = preprocessImage(imagePath);

            // 모델 출력 배열 (예시로 4개의 클래스)
            float[][] output = new float[1][4];

            // 두 번째 모델 실행
            tflite3.run(inputBuffer, output);

            // output 배열의 값을 로그로 출력하여 확인
            for (int i = 0; i < output[0].length; i++) {
                Log.d("ModelOutput2", "Class " + i + ": " + output[0][i]);
            }

            // 출력값을 분석하여 진단 결과 반환
            return processOutput(output);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 네 번째 모델을 실행하는 메서드 (tflite_model4.tflite)
    private int runModel4(String imagePath) {
        try {
            // 네 번째 모델 로드
            tflite4 = new Interpreter(loadModelFile("tflite_model4.tflite"));

            // ImagePreprocessor를 사용하여 이미지를 전처리
            //ImagePreprocessor preprocessor = new ImagePreprocessor(LoadingActivity.this);
            //ByteBuffer inputBuffer = preprocessor.preprocessImage(imagePath); // 이미지 전처리

            Log.d("loadingActivity", "image 전처리함");

            // 이미지를 ByteBuffer로 변환
            //ByteBuffer inputBuffer = convertImageToByteBuffer(imagePath);
            ByteBuffer inputBuffer = preprocessImage(imagePath);

            // 모델 출력 배열 (예시로 4개의 클래스)
            float[][] output = new float[1][4];

            // 네 번째 모델 실행
            tflite4.run(inputBuffer, output);

            // output 배열의 값을 로그로 출력하여 확인
            for (int i = 0; i < output[0].length; i++) {
                Log.d("ModelOutput4", "Class " + i + ": " + output[0][i]);
            }

            // 출력값을 분석하여 진단 결과 반환
            return processOutput(output);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
    // 다섯 번째 모델을 실행하는 메서드 (tflite_model5.tflite)
    private int runModel5(String imagePath) {
        try {
            // 다섯 번째 모델 로드
            tflite5 = new Interpreter(loadModelFile("tflite_model5.tflite"));

            // ImagePreprocessor를 사용하여 이미지를 전처리
            //ImagePreprocessor preprocessor = new ImagePreprocessor(LoadingActivity.this);
            //ByteBuffer inputBuffer = preprocessor.preprocessImage(imagePath); // 이미지 전처리

            Log.d("loadingActivity", "image 전처리함");

            // 이미지를 ByteBuffer로 변환
            //ByteBuffer inputBuffer = convertImageToByteBuffer(imagePath);
            ByteBuffer inputBuffer = preprocessImage(imagePath);

            // 모델 출력 배열 (예시로 4개의 클래스)
            float[][] output = new float[1][4];

            // 다섯 번째 모델 실행
            tflite5.run(inputBuffer, output);

            // output 배열의 값을 로그로 출력하여 확인
            for (int i = 0; i < output[0].length; i++) {
                Log.d("ModelOutput5", "Class " + i + ": " + output[0][i]);
            }

            // 출력값을 분석하여 진단 결과 반환
            return processOutput(output);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 여섯 번째 모델을 실행하는 메서드 (tflite_model6.tflite)
    private int runModel6(String imagePath) {
        try {
            // 여섯 번째 모델 로드
            tflite6 = new Interpreter(loadModelFile("tflite_model6.tflite"));

            // ImagePreprocessor를 사용하여 이미지를 전처리
            //ImagePreprocessor preprocessor = new ImagePreprocessor(LoadingActivity.this);
            //ByteBuffer inputBuffer = preprocessor.preprocessImage(imagePath); // 이미지 전처리

            Log.d("loadingActivity", "image 전처리함");

            // 이미지를 ByteBuffer로 변환
            //ByteBuffer inputBuffer = convertImageToByteBuffer(imagePath);
            ByteBuffer inputBuffer = preprocessImage(imagePath);

            // 모델 출력 배열 (예시로 4개의 클래스)
            float[][] output = new float[1][4];

            // 여섯 번째 모델 실행
            tflite6.run(inputBuffer, output);

            // output 배열의 값을 로그로 출력하여 확인
            for (int i = 0; i < output[0].length; i++) {
                Log.d("ModelOutput6", "Class " + i + ": " + output[0][i]);
            }

            // 출력값을 분석하여 진단 결과 반환
            return processOutput(output);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }



    /*
    // 모델을 실행하는 메서드 (여기에 실제 모델 호출 코드 작성)
    private String runModel(String imagePath) {
        try {
            // TensorFlow Lite 모델 로드
            tflite2 = new Interpreter(loadModelFile());

            // 이미지 데이터를 ByteBuffer로 변환
            ByteBuffer inputBuffer = convertImageToByteBuffer(imagePath);

            // 모델 출력 배열 (예시로 4개의 클래스)
            float[][] output = new float[1][4];

            // 모델 실행
            tflite2.run(inputBuffer, output);

            // output 배열의 값을 로그로 출력하여 확인
            for (int i = 0; i < output[0].length; i++) {
                Log.d("ModelOutput", "Class " + i + ": " + output[0][i]);
            }

            // 출력값을 분석하여 진단 결과 반환
            return processOutput(output);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Model loading failed";
        }
    }

    // 모델 파일을 로드하는 메서드
    private MappedByteBuffer loadModelFile() throws IOException {
        // "tflite_model2.tflite"는 assets 폴더에 저장된 모델 파일 이름
        AssetFileDescriptor fileDescriptor = getAssets().openFd("tflite_model2.tflite");
        FileInputStream inputStream = fileDescriptor.createInputStream(); // AssetFileDescriptor에서 InputStream 생성
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getLength();

        // MappedByteBuffer로 파일을 매핑
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    */
    // 모델 파일을 로드하는 메서드 (두 모델 공통)
    private MappedByteBuffer loadModelFile(String modelName) throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd(modelName);
        FileInputStream inputStream = fileDescriptor.createInputStream();
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    public ByteBuffer preprocessImage(String imagePath) {
        // 이미지 파일을 Bitmap으로 로드
        Bitmap inputBitmap = BitmapFactory.decodeFile(imagePath);
        if (inputBitmap == null) {
            throw new IllegalArgumentException("이미지 불러오기 실패: " + imagePath);
        }

        // 1. 이미지 크기 조정 (600x600)
        Bitmap resizedBitmap = resizeBitmap(inputBitmap, 600, 600);

        // 2. 랜덤 수평 반전 (50% 확률)
        if (Math.random() > 0.5) {
            resizedBitmap = flipHorizontal(resizedBitmap);
        }

        // 3. 랜덤 수직 반전 (50% 확률)
        if (Math.random() > 0.5) {
            resizedBitmap = flipVertical(resizedBitmap);
        }

        // 4. 랜덤 회전 (최대 10도)
        resizedBitmap = rotateBitmap(resizedBitmap);

        // 5. 랜덤 크기 및 기울기 조정 (Affine 변환)
        resizedBitmap = applyAffineTransformation(resizedBitmap);

        // 6. 텐서로 변환 (정규화 포함)
        ByteBuffer byteBuffer = BitmapToByteBuffer(resizedBitmap);

        // Bitmap 메모리 해제 (성능 향상)
        if (!resizedBitmap.isRecycled()) {
            resizedBitmap.recycle();
        }


        return byteBuffer;  // 전처리된 이미지 반환
    }


    // 크기 조정 (비율 유지)
    private Bitmap resizeBitmap(Bitmap bitmap, int targetWidth, int targetHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float aspectRatio = (float) width / height;

        if (width > height) {
            targetHeight = (int) (targetWidth / aspectRatio);
        } else {
            targetWidth = (int) (targetHeight * aspectRatio);
        }
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }

    // 수평 반전
    private Bitmap flipHorizontal(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);  // 수평 반전
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    // 수직 반전
    private Bitmap flipVertical(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);  // 수직 반전
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    // 회전
    private Bitmap rotateBitmap(Bitmap bitmap) {
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.postRotate((float) (Math.random() * 20 - 10));  // -10도에서 10도 사이
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotationMatrix, true);
    }

    // Affine 변환 (크기 및 기울기 조정)
    private Bitmap applyAffineTransformation(Bitmap bitmap) {
        Matrix affineMatrix = new Matrix();
        float scale = (float) (0.8 + Math.random() * 0.4);  // 0.8에서 1.2 사이
        affineMatrix.setScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), affineMatrix, true);
    }


    private ByteBuffer BitmapToByteBuffer(Bitmap bitmap) {
        // 모델의 입력 크기 (예: 600x600)
        int tensorWidth = 600;
        int tensorHeight = 600;

        // ByteBuffer 할당
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * tensorWidth * tensorHeight * 3); // float형 4바이트
        byteBuffer.order(ByteOrder.nativeOrder());  // 바이트 순서 설정

        // 이미지 크기 조정 (필요에 따라 이미지를 리사이징)
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, tensorWidth, tensorHeight, false);

        // 리사이징된 이미지의 픽셀 데이터 얻기
        int[] pixels = new int[tensorWidth * tensorHeight];
        resizedBitmap.getPixels(pixels, 0, tensorWidth, 0, 0, tensorWidth, tensorHeight);

        // 각 픽셀을 정규화하여 ByteBuffer에 넣음
        for (int i = 0; i < tensorHeight; i++) {
            for (int j = 0; j < tensorWidth; j++) {
                int pixel = pixels[i * tensorWidth + j];
                float r = ((pixel >> 16) & 0xFF) / 255.0f;
                float g = ((pixel >> 8) & 0xFF) / 255.0f;
                float b = (pixel & 0xFF) / 255.0f;

                // 이미지 정규화 (평균과 표준편차를 기반으로)
                byteBuffer.putFloat((r - 0.485f) / 0.229f);  // Red 채널 정규화
                byteBuffer.putFloat((g - 0.456f) / 0.224f);  // Green 채널 정규화
                byteBuffer.putFloat((b - 0.406f) / 0.225f);  // Blue 채널 정규화
            }
        }
        return byteBuffer;
    }

    // runModel 안에서 출력값을 처리하는 메서드
    private int processOutput(float[][] output) {
        // 예측된 결과에서 가장 큰 확률을 가진 클래스를 찾음
        float maxProb = -1;
        int predictedClass = -1;
        for (int i = 0; i < output[0].length; i++) {
            if (output[0][i] > maxProb) {
                maxProb = output[0][i];
                predictedClass = i;
            }
        }
        return predictedClass;
    }

}