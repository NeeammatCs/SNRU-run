package snru.chokchai.snrurun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
     // Explicit

    private MyManage myManage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myManage = new MyManage(MainActivity.this);



    }// Main method

    public void clickSignUPMain(View view) {
      startActivity(new Intent(MainActivity.this,SignUp.class));
    }

} // Main class นี่คือคลาสหลัก
