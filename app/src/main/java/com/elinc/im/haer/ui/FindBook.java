
/*
public class FindBook extends AppCompatActivity {


}*/


package com.elinc.im.haer.ui;

        import java.util.ArrayList;
        import java.util.List;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.EditText;

        import cn.bmob.im.util.BmobLog;
        import cn.bmob.v3.BmobQuery;
        import cn.bmob.v3.listener.FindListener;

        import com.elinc.im.haer.R;
        import com.elinc.im.haer.adapter.QuestionListAdapter;
        import com.elinc.im.haer.bean.Book;
        import com.elinc.im.haer.util.CollectionUtils;
        import com.elinc.im.haer.view.xlist.XListView;

/** 添加好友
 * @ClassName: SearchQuestion
 * @Description: TODO
 * @author smile
 * @date 2014-6-5 下午5:26:41
 */
public class FindBook extends ActivityBase implements View.OnClickListener,XListView.IXListViewListener,AdapterView.OnItemClickListener {
    private EditText et_search_question;
    private List<Book> book = new ArrayList<Book>();
    private XListView mListView;
    private QuestionListAdapter adapter;
    private final int pageCapacity=5;
    int curPage = 0;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_book);
        initView();
    }

    private void initView(){
        initTopBarForLeft("查找问题");
        et_search_question = (EditText)findViewById(R.id.et_search_question);
        Button btn_search_question = (Button) findViewById(R.id.btn_search_question);
        btn_search_question.setOnClickListener(this);
        initXListView();
        initAdapter();
    }

    private void initXListView() {
        mListView = (XListView) findViewById(R.id.list_question_e);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        mListView.setDividerHeight(2);
        adapter = new QuestionListAdapter(FindBook.this, book);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }


    private void initSearchList(final boolean isUpdate){
        if(!isUpdate){
            progress = new ProgressDialog(FindBook.this);
            progress.setMessage("正在搜索...");
            progress.setCanceledOnTouchOutside(true);
            progress.show();
        }
        BmobQuery<Book> eq1 = new BmobQuery<>();
        eq1.addWhereContains("title", et_search_question.getText().toString());
        BmobQuery<Book> eq2 = new BmobQuery<>();
        eq2.addWhereContains("question_content", et_search_question.getText().toString());
        BmobQuery<Book> eq3=new BmobQuery<>();
        eq3.addWhereContains("tags", et_search_question.getText().toString());
        List<BmobQuery<Book>> queries = new ArrayList<>();
        queries.add(eq1);
        queries.add(eq2);
        queries.add(eq3);
        BmobQuery<Book> mainQuery = new BmobQuery<>();
        mainQuery.setLimit(pageCapacity);
        mainQuery.order("-createdAt");
        mainQuery.include("author");
        mainQuery.or(queries);
        mainQuery.findObjects(FindBook.this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (list.size() < pageCapacity) {
                    mListView.setPullLoadEnable(false);
                    ShowToast("问题搜索完成!");
                } else {
                    mListView.setPullLoadEnable(true);
                }
                if (CollectionUtils.isNotNull(list)) {
                    if (isUpdate) {
                        book.clear();
                    }
                    adapter.addAll(list);
                } else {
                    BmobLog.i("查询成功:无返回值");
                    if (book != null) {
                        book.clear();
                    }
                    ShowToast("没有您需要的问题，去提问吧");
                }
                if (!isUpdate) {
                    progress.dismiss();
                } else {
                    refreshPull();
                }
                //这样能保证每次查询都是从头开始
                curPage = 0;
            }
            @Override
            public void onError(int code, String msg) {
                BmobLog.i("查询错误:" + msg);
                if (book != null) {
                    book.clear();
                }
                ShowToast("奇怪，怎么没网了呢？");
                mListView.setPullLoadEnable(false);
                refreshPull();
                //这样能保证每次查询都是从头开始
                curPage = 0;
            }
        });

    }

    /** 查询更多
     * @Title: queryMoreNearList
     * @Description:
     * @param page
     * @return void
     */
    private void onLoadMore(int page){
        BmobQuery<Book> eq1 = new BmobQuery<>();
        eq1.addWhereContains("title", et_search_question.getText().toString());
        BmobQuery<Book> eq2 = new BmobQuery<>();
        eq2.addWhereContains("question_content", et_search_question.getText().toString());
        BmobQuery<Book> eq3=new BmobQuery<>();
        eq3.addWhereContains("tags", et_search_question.getText().toString());
        List<BmobQuery<Book>> queries = new ArrayList<>();
        queries.add(eq1);
        queries.add(eq2);
        queries.add(eq3);
        BmobQuery<Book> mainQuery = new BmobQuery<>();
        mainQuery.include("author");
        mainQuery.setSkip((curPage + 1) * pageCapacity);
        mainQuery.setLimit(pageCapacity);
        mainQuery.order("-createdAt");
        mainQuery.or(queries);
        mainQuery.findObjects(FindBook.this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (list.size() < pageCapacity) {
                    mListView.setPullLoadEnable(false);
                    ShowToast("问题搜索完成!");
                } else {
                    mListView.setPullLoadEnable(true);
                }
                if (CollectionUtils.isNotNull(list)) {
                    adapter.addAll(list);

                }
                refreshLoad();
            }

            @Override
            public void onError(int i, String s) {
                ShowLog("打开WIFI会死啊！根本就没网，臣妾做不到！");
                mListView.setPullLoadEnable(false);
                refreshLoad();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        String questionId = book.get(position-1).getObjectId();
        bundle.putString("questionId", questionId);
        intent.putExtras(bundle);
        intent.setClass(FindBook.this, BookItemActivityElinc.class);
        startAnimActivity(intent);
    }


    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_search_question://搜索
                book.clear();
                String searchName = et_search_question.getText().toString();
                if(!searchName.equals("")){
                    initSearchList(false);
                }else{
                    ShowToast("请输入搜索内容");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    @Override
    public void onLoadMore() {
        BmobQuery<Book> eq1 = new BmobQuery<>();
        eq1.addWhereContains("title", et_search_question.getText().toString());
        BmobQuery<Book> eq2 = new BmobQuery<>();
        eq2.addWhereContains("question_content", et_search_question.getText().toString());
        BmobQuery<Book> eq3=new BmobQuery<>();
        eq3.addWhereContains("tags", et_search_question.getText().toString());
        List<BmobQuery<Book>> queries = new ArrayList<>();
        queries.add(eq1);
        queries.add(eq2);
        queries.add(eq3);
        BmobQuery<Book> mainQuery = new BmobQuery<>();
        mainQuery.include("author");
        mainQuery.setSkip((curPage + 1) * pageCapacity);
        curPage++;
        mainQuery.setLimit(pageCapacity);
        mainQuery.order("-createdAt");
        mainQuery.or(queries);
        mainQuery.findObjects(FindBook.this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (list.size() < pageCapacity) {
                    mListView.setPullLoadEnable(false);
                } else {
                    mListView.setPullLoadEnable(true);
                }
                if (CollectionUtils.isNotNull(list)) {
                    adapter.addAll(list);
                }
                refreshLoad();
            }

            @Override
            public void onError(int i, String s) {
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
        BmobQuery<Book> allQuery = new BmobQuery<>();
        allQuery.include("author");
        allQuery.setLimit(pageCapacity);
        allQuery.order("-createdAt");
        allQuery.findObjects(FindBook.this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (CollectionUtils.isNotNull(list)) {
                    book.clear();
                    adapter.addAll(list);
                    if (list.size() < pageCapacity) {
                        mListView.setPullLoadEnable(false);
                    } else {
                        mListView.setPullLoadEnable(true);
                    }
                } else {
                    BmobLog.i("查询成功:无返回值");
                    if (book != null) {
                        book.clear();
                    }
                    ShowToast("没有您要找的问题，去提问吧");
                    mListView.setPullLoadEnable(false);
                }
                refreshPull();
                curPage = 0;
            }
            @Override
            public void onError(int code, String msg) {
                BmobLog.i("查询错误:" + msg);
                if (book != null) {
                    book.clear();
                }
                ShowToast("问题不存在");
                mListView.setPullLoadEnable(false);
                refreshPull();
                curPage = 0;
            }
        });
    }
    private void refreshList(){
        curPage = 0;
        BmobQuery<Book> eq1 = new BmobQuery<>();
        eq1.addWhereContains("title", et_search_question.getText().toString());
        BmobQuery<Book> eq2 = new BmobQuery<>();
        eq2.addWhereContains("question_content", et_search_question.getText().toString());
        BmobQuery<Book> eq3=new BmobQuery<>();
        eq3.addWhereContains("tags", et_search_question.getText().toString());
        List<BmobQuery<Book>> queries = new ArrayList<>();
        queries.add(eq1);
        queries.add(eq2);
        queries.add(eq3);
        BmobQuery<Book> mainQuery = new BmobQuery<>();
        mainQuery.setLimit(pageCapacity);
        mainQuery.order("-createdAt");
        mainQuery.include("author");
        mainQuery.or(queries);
        mainQuery.findObjects(FindBook.this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (list.size() < pageCapacity) {
                    mListView.setPullLoadEnable(false);
                } else {
                    mListView.setPullLoadEnable(true);
                }
                if (CollectionUtils.isNotNull(list)) {
                    book.clear();
                    adapter.addAll(list);

                    mListView.stopRefresh();
                }
                refreshLoad();
            }

            @Override
            public void onError(int i, String s) {
                ShowLog("搜索更多问题出错:"+s);
                mListView.setPullLoadEnable(false);
                refreshLoad();
                mListView.stopRefresh();
            }
        });
    }
}
