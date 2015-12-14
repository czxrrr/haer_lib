package com.elinc.im.haer.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.elinc.im.haer.R;
import com.elinc.im.haer.bean.Book;
import com.elinc.im.haer.bean.Tool;
import com.elinc.im.haer.bean.User;
import com.elinc.im.haer.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RentBook extends ActivityBase {

    private TextView apply;
    private TextView done;
    private User u=new User();
    private Book book=new Book();
    private String available;
    private ImageView img;
    private ImageView book_cover;
    private String author_name;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public void openUserDetail(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        User u= BmobUser.getCurrentUser(RentBook.this, User.class);
        if(author_name!="" && author_name!=null){
            bundle.putString("username", author_name);
            if(author_name.equals(u.getUsername().toString())){
                bundle.putString("from", "me");
            }else{
                bundle.putString("from", "add");
            }
        }
        intent.putExtras(bundle);
        intent.setClass(RentBook.this, SetMyInfoActivity.class);
        RentBook.this.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_book);
        initTopBarForLeft("借书");
        book_cover=(ImageView)findViewById(R.id.book_cover);
        apply= (TextView) findViewById(R.id.btn_confirm_apply);
        done= (TextView) findViewById(R.id.btn_done);
        u=BmobUser.getCurrentUser(RentBook.this, User.class);
        book.setObjectId(getIntent().getStringExtra("book"));
        available=getIntent().getStringExtra("available");
       // Tool.alert(RentBook.this, book.getObjectId());
        img= (ImageView) findViewById(R.id.img_friend_avatar);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserDetail();
            }
        });
        BmobQuery<Book> q=new BmobQuery<Book>();
        q.include("owner");
        q.getObject(RentBook.this, getIntent().getStringExtra("book"), new GetListener<Book>() {
            @Override
            public void onSuccess(Book bb) {
                String avatar=bb.getBookAvatar();
                if (avatar != null && !avatar.equals("")) {//加载头像-为了不每次都加载头像
                    ImageLoader.getInstance().displayImage(avatar, book_cover, ImageLoadOptions.getOptions(), animateFirstListener);
                } else {
                    book_cover.setImageResource(R.drawable.no_cover);
                }

                if(bb.getOwner().getUsername().equals(u.getUsername())){
                    apply.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);
                    author_name= bb.getOwner().getUsername();

                }
            }

            @Override
            public void onFailure(int i, String s) {
                Tool.alert(RentBook.this,"没有网络");
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                u=BmobUser.getCurrentUser(RentBook.this, User.class);
                book.setAvailable(true);
                book.update(RentBook.this, getIntent().getStringExtra("book"), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Tool.alert(RentBook.this, "OK，可以等待下一个借书的人了");
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (available.equals("true")) {
                //book.setObjectId(getIntent().getStringExtra("book"));
                book.setOwner(u);
                book.setAvailable(false);
                book.update(RentBook.this, getIntent().getStringExtra("book"), new UpdateListener() {
                    @Override
                    public void onSuccess() {

                        Tool.alert(RentBook.this, "done");
                    }

                    @Override
                    public void onFailure(int i, String s) {

                        Tool.alert(RentBook.this, s);
                    }
                });
            } else {
                Tool.alert(RentBook.this, "不好意思，这本书的拥有者现在还没有读完，先等等吧！如果急的话可以去书店买本新的");
            }
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

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

}
