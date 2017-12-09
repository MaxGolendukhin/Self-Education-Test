package com.golendukhin.selfeducationtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GreetingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.greeting_activity);
    }

    public void startTest(View view) {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}