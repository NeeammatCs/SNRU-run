package snru.chokchai.snrurun;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
        //Test add user
       // myManage.addUser("เบลล์", "fdg", "1233", "3");

        //delete all SQL
        deleteAllSQLite();

    }// Main method

    private void deleteAllSQLite() {
        SQLiteDatabase sqLiteDatabase=openOrCreateDatabase(MyOpenHelper.database_name,
                MODE_PRIVATE,null);
        sqLiteDatabase.delete(MyManage.user_table, null, null);

    }

    public void clickSignUPMain(View view) {
      startActivity(new Intent(MainActivity.this,SignUp.class));
    }

} // Main class นี่คือคลาสหลัก
