package com.example.hanoitower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class Home extends Activity {
    // при запуске приложения запустим layout home.xml
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.home);
    }

    //При нажатии на кнопку старт передадим управление классу Levels
    public void game(View v) {
        Intent i = new Intent(this, Levels.class);
        startActivity(i);
    }

    //При нажатии на кнопку инструкции передадим управление классу Instruct
    public void instruct(View v) {
        Intent i = new Intent(this, Instruct.class);
        startActivity(i);
    }

    //При нажатии на кнопку выход завершим работу приложения
    public void exit(View v) {
        finish();
        System.exit(0);
    }
}
