package com.elinc.im.haer.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elinc.im.haer.R;
import com.elinc.im.haer.adapter.base.BaseListAdapter;
import com.elinc.im.haer.adapter.base.ViewHolder;
import com.elinc.im.haer.bean.Book;
import com.elinc.im.haer.bean.Tool;
import com.elinc.im.haer.bean.User;
import com.elinc.im.haer.ui.SetMyInfoActivity;
import com.elinc.im.haer.util.ImageLoadOptions;
import com.elinc.im.haer.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import cn.bmob.v3.BmobUser;

public class BookListAdapter extends BaseListAdapter<Book> {
    Context context;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    public BookListAdapter(Context context, List<Book> list) {
        super(context, list);
        this.context=context;
        // TODO Auto-generated constructor stub
    }

    @Override
    public View bindView(int arg0, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_book_in_list_elinc, null);
        }
        final Book contract = getList().get(arg0);
        TextView book_title = ViewHolder.get(convertView, R.id.book_title);
        TextView book_content = ViewHolder.get(convertView, R.id.book_content);
        final TextView book_author = ViewHolder.get(convertView,R.id.book_author);
        TextView book_available = ViewHolder.get(convertView,R.id.book_available);
        ImageView user_avatar= ViewHolder.get(convertView,R.id.avatar_for_book);
        //final TextView book_number_of_answer = ViewHolder.get(convertView,R.id.book_number_of_answer);
        //Button btn_add = ViewHolder.get(convertView, R.id.book_content);

        //String avatar = contract.getAvatar();

        /*if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.drawable.default_head);
        }*/
        if(contract.getTitle()!=null){book_title.setText(contract.getTitle());}
        if(contract.getIntro()!=null){book_content.setText(contract.getIntro());}
        if(contract.getOwner()!=null){book_author.setText(contract.getOwner().getUsername());}
        if(contract.getAvailable()==false){
            book_available.setText("不可借阅");
        }else{
            book_available.setText("可借阅");
        }
        String avatar=contract.getBookAvatar();
        if(avatar!=null && !avatar.equals("")){//加载头像-为了不每次都加载头像
            ImageLoader.getInstance().displayImage(avatar, user_avatar, ImageLoadOptions.getOptions(),animateFirstListener);
        }else {
            user_avatar.setImageResource(R.drawable.no_cover);
        }
        //book_number_of_answer.setText("" + contract.getNumberOfAnswer());


        book_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                User u= BmobUser.getCurrentUser(context, User.class);
                bundle.putString("username", book_author.getText().toString());
                if(book_author.getText().toString().equals(u.getUsername().toString())){
                    bundle.putString("from", "me");
                }else{
                    bundle.putString("from", "add");
                }
                intent.putExtras(bundle);
                intent.setClass(context, SetMyInfoActivity.class);
                context.startActivity(intent);
            }
        });

        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                User u = BmobUser.getCurrentUser(context, User.class);
                bundle.putString("username", book_author.getText().toString());
                if (book_author.getText().toString().equals(u.getUsername().toString())) {
                    bundle.putString("from", "me");
                } else {
                    bundle.putString("from", "add");
                }
                intent.putExtras(bundle);
                intent.setClass(context, SetMyInfoActivity.class);
                context.startActivity(intent);
            }
        });




        return convertView;
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
