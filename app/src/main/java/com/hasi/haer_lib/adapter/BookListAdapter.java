package com.hasi.haer_lib.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hasi.haer_lib.R;
import com.hasi.haer_lib.adapter.base.BaseListAdapter;
import com.hasi.haer_lib.adapter.base.ViewHolder;
import com.hasi.haer_lib.bean.Book;
import com.hasi.haer_lib.bean.Question;
import com.hasi.haer_lib.bean.Tool;
import com.hasi.haer_lib.bean.User;
import com.hasi.haer_lib.ui.SetMyInfoActivity;
import com.hasi.haer_lib.util.ImageLoadOptions;
import com.hasi.haer_lib.view.CircleImageView;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by HUBIN on 2015/11/25.
 */
public class BookListAdapter extends BaseListAdapter<Book> {
    Context context;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public BookListAdapter(Context context, List<Book> list) {
        super(context, list);
        this.context = context;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_book_in_list_elinc, null);
        }

        final Book contract = getList().get(position);
        TextView book_title = ViewHolder.get(convertView, R.id.book_title);
        TextView book_label = ViewHolder.get(convertView, R.id.book_label);
        TextView uploader = ViewHolder.get(convertView, R.id.uploader);
        ImageView book_pic = ViewHolder.get(convertView, R.id.avatar_for_book);

        if (contract.getTitle() != null) {
            book_title.setText(contract.getTitle());
        }
        if (contract.getLabel() != null) {
            book_label.setText(contract.getLabel());
        }
        if (contract.getUploader() != null) {
            uploader.setText(contract.getUploader().getUsername());
        }
        String avatar = contract.getAvatar();
        if (avatar != null && !avatar.equals("")) {     //加载图片-为了不必每次都加载
            ImageLoader.getInstance().displayImage(avatar, book_pic, ImageLoadOptions.getOptions(), animateFirstListener);
        } else {
            book_pic.setImageResource(R.drawable.default_book_pic);
        }

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
