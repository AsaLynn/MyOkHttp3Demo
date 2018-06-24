package com.think.okhttp3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class EnterActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button button;
    protected Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_enter);
        initView();
    }

    private void initView() {
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(EnterActivity.this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(EnterActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            startActivity(new Intent(this, OkHttp3Activity.class));
        } else if (view.getId() == R.id.button2) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
