package com.elinc.im.haer.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.elinc.im.haer.R;
import com.elinc.im.haer.adapter.BookListAdapter;
import com.elinc.im.haer.bean.Book;
import com.elinc.im.haer.bean.Tool;
import com.elinc.im.haer.bean.User;
import com.elinc.im.haer.util.CollectionUtils;
import com.elinc.im.haer.view.xlist.XListView;
import com.elinc.im.haer.view.xlist.XListView.IXListViewListener;

/** 添加好友
 * @ClassName: SearchQuestion
 * @Description: TODO
 * @owner smile
 * @date 2014-6-5 下午5:26:41
 */
public class MyFavoriteActivityElinc extends ActivityBase implements OnClickListener,IXListViewListener,OnItemClickListener{
    EditText et_search_question;
    Button btn_search_question;
    List<Book> book = new ArrayList<Book>();
    XListView mListView;
    BookListAdapter adapter;
    private View view;
    private final int pageCapacity=5;
    int curPage = 0;
    ProgressDialog progress ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView(){

        setContentView(R.layout.activity_my_favorite_activity_elinc);
        initTopBarForLeft("我的收藏");
        initXListView();
        initAdapter();

    }

    private void initXListView() {
        mListView = (XListView) findViewById(R.id.list_question_e);
        // 首先不允许加载更多
        mListView.setPullLoadEnable(true);
        // 不允许下拉
        mListView.setPullRefreshEnable(false);
        // 设置监听器
        mListView.setXListViewListener(this);
        //
        mListView.pullRefreshing();

        adapter = new BookListAdapter(this, book);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        String bookId = book.get(position-1).getObjectId();
        //ShowToast("point"+position);
        bundle.putString("bookId", bookId);
        intent.putExtras(bundle);
        intent.setClass(this, BookItemActivityElinc.class);
        startAnimActivity(intent);
    }


    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLoadMore() {
        BmobQuery<Book> query = new BmobQuery<Book>();
        User u = BmobUser.getCurrentUser(this, User.class);
        u.setObjectId(u.getObjectId());
        query.include("owner");
        query.setSkip((curPage + 1) * pageCapacity);
        curPage++;
        query.setLimit(pageCapacity);
        query.addWhereRelatedTo("follow", new BmobPointer(u));
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                // TODO Auto-generated method stub
                if (list.size() < pageCapacity) {
                    mListView.setPullLoadEnable(false);
                    //ShowToast("问题搜索完成!");
                } else {
                    mListView.setPullLoadEnable(true);
                }if (CollectionUtils.isNotNull(list)) {
                    adapter.addAll(list);
                }
                refreshLoad();
            }

            @Override
            public void onError(int i, String s) {
                // TODO Auto-generated method stub
                ShowLog("搜索更多问题出错:" + s);
                mListView.setPullLoadEnable(false);
                refreshLoad();
            }
        });
    }

    private void refreshLoad(){
        if (mListView.getPullLoading()) {
            mListView.stopLoadMore();
        }
    }

    private void refreshPull(){
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
        }
    }

    public void initAdapter(){
        BmobQuery<Book> query = new BmobQuery<Book>();
        User u = BmobUser.getCurrentUser(this, User.class);
        u.setObjectId(u.getObjectId());
        query.include("owner");
        query.setLimit(pageCapacity);
        query.addWhereRelatedTo("follow", new BmobPointer(u));
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                // TODO Auto-generated method stub
                if (list.size() < pageCapacity) {
                    mListView.setPullLoadEnable(false);
                    //ShowToast("问题搜索完成!");
                } else {
                    mListView.setPullLoadEnable(true);
                }
                if (CollectionUtils.isNotNull(list)) {
                    book.clear();
                    adapter.addAll(list);
                    if (list.size() < pageCapacity) {
                        mListView.setPullLoadEnable(false);
                        //ShowToast("问题搜索完成!");
                    } else {
                        mListView.setPullLoadEnable(true);
                    }
                } else {
                    BmobLog.i("查询成功:无返回值");
                    if (book != null) {
                        book.clear();
                    }
                    ShowToast("没有您收藏问题，去收藏吧");
                }
                if (!true) {
                    progress.dismiss();
                } else {
                    refreshPull();
                }
                //这样能保证每次查询都是从头开始
                curPage = 0;
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                BmobLog.i("查询错误:" + msg);
                if (book != null) {
                    book.clear();
                }
                ShowToast("问题不存在");
                mListView.setPullLoadEnable(false);
                refreshPull();
                //这样能保证每次查询都是从头开始
                curPage = 0;
            }
        });
    }
    private void initItemListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MyFavoriteActivityElinc.this, "" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                String bookId = book.get(position-1).getObjectId();
                bundle.putString("bookId", bookId);
                intent.putExtras(bundle);
                intent.setClass(MyFavoriteActivityElinc.this, BookItemActivityElinc.class);
                startAnimActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ShowToast(""+position);
                dialog(book.get(position-1));
                return false;
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        //initListView();
        initItemListener();
    }
    protected void dialog(final Book book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyFavoriteActivityElinc.this);
        builder.setMessage("确认取消关注吗？");
        builder.setTitle("取消关注");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                User user = BmobUser.getCurrentUser(MyFavoriteActivityElinc.this, User.class);
                User u=new User();
                u.setObjectId(user.getObjectId());
                BmobRelation relation = new BmobRelation();
                relation.remove(book);
                u.setFollow(relation);
                u.update(MyFavoriteActivityElinc.this, user.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        Log.i("life", "关联关系删除成功");
                        Tool.alert(MyFavoriteActivityElinc.this, "取消关注成功");
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        Log.i("life", "关联关系删除失败：" + arg0 + "-" + arg1);
                        //Tool.alert(MyFavoriteActivityElinc.this, "取消关注失败，请检查网络");
                        ShowToast("" + arg1);
                    }
                });
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}