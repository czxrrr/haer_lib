package com.elinc.im.haer.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.elinc.im.haer.R;
import com.elinc.im.haer.bean.Book;
import com.elinc.im.haer.bean.Tool;
import com.elinc.im.haer.bean.User;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class RentBook extends ActivityBase {

    private TextView apply= (TextView) findViewById(R.id.apply);
    private User u=new User();
    private Book book=new Book();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_book);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                u=BmobUser.getCurrentUser(RentBook.this, User.class);
                book.setObjectId(getIntent().getStringExtra("book"));
                Tool.alert(RentBook.this, book.getObjectId());
                book.setOwner(u);
                book.save(RentBook.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Tool.alert(RentBook.this, "done");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Tool.alert(RentBook.this, "fail");
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rent_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
