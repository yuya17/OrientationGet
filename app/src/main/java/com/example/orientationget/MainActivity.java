package com.example.orientationget;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor mMagField;
    private Sensor mAccelerometer;

    private static final int MATRIX_SIZE =16;
    //センサーの値
    private float[] mvalues = new float[3];
    private float[] acValues = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,mMagField,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this,mAccelerometer);
        sensorManager.unregisterListener(this,mMagField);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        TextView txt01 = (TextView) findViewById(R.id.txt01);

        float[] inR = new float[MATRIX_SIZE];
        float[] outR = new float[MATRIX_SIZE];
        float[] I = new float[MATRIX_SIZE];
        float[] orValues = new float[3];

        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                acValues = event.values.clone();//配列にイベントで起こった時の値を複製
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mvalues = event.values.clone();
                break;
        }

        if(mvalues !=null && acValues!=null){
            SensorManager.getRotationMatrix(inR,I,acValues,mvalues);
            //携帯を水平にもち、アクティビティはポートレイト
            SensorManager.remapCoordinateSystem(inR,SensorManager.AXIS_X,SensorManager.AXIS_Y,outR);
            SensorManager.getOrientation(outR,orValues);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("方位角(Azimath)");
            stringBuilder.append(rad2Deg(orValues[0]));
            stringBuilder.append("\n");
            stringBuilder.append("傾斜角(ピッチ)");
            stringBuilder.append(rad2Deg(orValues[1]));
            stringBuilder.append("\n");
            stringBuilder.append("回転角(ロール)");
            stringBuilder.append(rad2Deg(orValues[2]));
            stringBuilder.append("\n");
            txt01.setText(stringBuilder.toString());

        }
    }

    private int rad2Deg(float rad){
        return (int) Math.floor(Math.toDegrees(rad));//ラジアンを角度に変換し、小数点以下切り上げ

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
