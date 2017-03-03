package com.ownchan.slideswitchbutton;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ownchan.slideswitchbutton.view.SlideButton;
import com.ownchan.slideswitchbutton.view.SlideSwitchButton;

public class MainActivity extends Activity implements SlideSwitchButton.SlideListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SlideSwitchButton slideSwitchButton1 = (SlideSwitchButton) findViewById(R.id.slide1);
        slideSwitchButton1.setSlideListener(this);
        SlideSwitchButton slideSwitchButton2 = (SlideSwitchButton) findViewById(R.id.slide2);
        slideSwitchButton2.setSlideListener(this);
        SlideSwitchButton slideSwitchButton3 = (SlideSwitchButton) findViewById(R.id.slide3);
        slideSwitchButton3.setSlideListener(this);
        SlideSwitchButton slideSwitchButton4 = (SlideSwitchButton) findViewById(R.id.slide4);
        slideSwitchButton4.setSlideListener(this);
    }

    @Override
    public void openState(boolean isOpen, View view) {
        switch (view.getId()) {
            case R.id.slide1:
                Toast.makeText(MainActivity.this, "通知: " + isOpen, Toast.LENGTH_SHORT).show();
                break;
            case R.id.slide2:
                Toast.makeText(MainActivity.this, "夜间模式: " + isOpen, Toast.LENGTH_SHORT).show();
                break;
            case R.id.slide3:
                Toast.makeText(MainActivity.this, "显示比分: " + isOpen, Toast.LENGTH_SHORT).show();
                break;
            case R.id.slide4:
                Toast.makeText(MainActivity.this, "清空缓存: " + isOpen, Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }
}
