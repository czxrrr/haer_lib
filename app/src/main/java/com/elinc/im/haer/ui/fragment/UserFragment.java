package com.elinc.im.haer.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.elinc.im.haer.R;
import com.elinc.im.haer.adapter.BookListAdapter;
import com.elinc.im.haer.bean.Book;
import com.elinc.im.haer.bean.User;
import com.elinc.im.haer.ui.BookItemActivityElinc;
import com.elinc.im.haer.ui.FragmentBase;
import com.elinc.im.haer.util.CollectionUtils;
import com.elinc.im.haer.view.xlist.XListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


//这个页面显示用户拥有的书，提供的功能主要是对书设置是否已经读完
public class UserFragment extends FragmentBase implements View.OnClickListener,XListView.IXListViewListener,AdapterView.OnItemClickListener {
    private User me;
    List<Book> book = new ArrayList<Book>();
    XListView mListView;
    BookListAdapter adapter;
    private final int pageCapacity=5;
    int curPage = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        adapter = new BookListAdapter(getActivity(), book);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        String questionId = book.get(position-1).getObjectId();
        bundle.putString("questionId", questionId);
        intent.putExtras(bundle);
        intent.setClass(getActivity(), BookItemActivityElinc.class);
        startAnimActivity(intent);
    }

    @Override
    public void onClick(View arg0) {

    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public void onLoadMore() {
        BmobQuery<Book> mainQuery = new BmobQuery<Book>();

        mainQuery.setSkip((curPage + 1) * pageCapacity);
        curPage++;
        mainQuery.setLimit(pageCapacity);
        mainQuery.include("owner");
        mainQuery.order("-createdAt");

        mainQuery.addWhereEqualTo("owner", me);

        mainQuery.findObjects(getActivity(), new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (CollectionUtils.isNotNull(list)) {
                    if (list.size() < pageCapacity) {
                        mListView.setPullLoadEnable(false);
                    } else {
                        mListView.setPullLoadEnable(true);
                    }
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

    public void initData () {
        BmobQuery<Book> allQuery = new BmobQuery<Book>();
        allQuery.include("owner");
        allQuery.order("-createdAt");
        allQuery.setLimit(pageCapacity);

        allQuery.addWhereEqualTo("owner", me);

        allQuery.findObjects(getActivity(), new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (CollectionUtils.isNotNull(list)) {
                    book.clear();
                    book.addAll(list);
                } else {
                    BmobLog.i("查询成功:无返回值");
                    if (book != null) {
                        book.clear();
                    }
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
                //这样能保证每次查询都是从头开始
                curPage = 0;
            }
        });
    }


    public void initAdapter(){
        if (CollectionUtils.isNotNull(book)) {
            if (book.size() < pageCapacity) {
                mListView.setPullLoadEnable(false);
            } else {
                mListView.setPullLoadEnable(true);
            }
        } else {
            mListView.setPullLoadEnable(false);
            refreshPull();
            //ShowToast("没有您要找的问题，去提问吧");
        }
        refreshPull();
    }
    private void refreshList(){
        curPage =0;
        BmobQuery<Book> mainQuery = new BmobQuery<Book>();
        mainQuery.include("owner");
        mainQuery.order("-createdAt");
        mainQuery.setLimit(pageCapacity);

        mainQuery.addWhereEqualTo("owner", me);

        mainQuery.findObjects(getActivity(), new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (CollectionUtils.isNotNull(list)) {
                    book.clear();
                    adapter.addAll(list);
                    if (list.size() < pageCapacity) {
                        mListView.setPullLoadEnable(false);
                        //ShowToast("问题加载完成!");
                    } else {
                        mListView.setPullLoadEnable(true);
                    }
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



//    Integer emo;
//    private Goal[] goal;
//    private int goalNum = 3;
//    private CardView goal1,goal2,goal3;
//    private TextView tag1,tag2,tag3;
//    private TextView title1,title2,title3;
//    private TextView claim1,claim2,claim3;
//    private TextView date1,date2,date3;
//    private TextView fight_num1,fight_num2,fight_num3;
//    private TextView comment_num1,comment_num2,comment_num3;
//    private Button btn_add_goal;
//    private String time1;
//    private Boolean checked=false;
//    TextView card1,card2,card3;
//
//    public UserFragment() {}
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_user, container, false);
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        me = BmobUser.getCurrentUser(getActivity(), User.class);
//        initView();
//    }
//
//    private void initView() {
//        goal1 = (CardView) findViewById(R.id.goal1);
//        goal2 = (CardView) findViewById(R.id.goal2);
//        goal3 = (CardView) findViewById(R.id.goal3);
//        title1 = (TextView) findViewById(R.id.title1);
//        title2 = (TextView) findViewById(R.id.title2);
//        title3 = (TextView) findViewById(R.id.title3);
//        tag1 = (TextView) findViewById(R.id.tag1);
//        tag2 = (TextView) findViewById(R.id.tag2);
//        tag3 = (TextView) findViewById(R.id.tag3);
//        claim1 = (TextView) findViewById(R.id.claim1);
//        claim2 = (TextView) findViewById(R.id.claim2);
//        claim3 = (TextView) findViewById(R.id.claim3);
//        date1 = (TextView) findViewById(R.id.date1);
//        date2 = (TextView) findViewById(R.id.date2);
//        date3 = (TextView) findViewById(R.id.date3);
//        fight_num1 = (TextView) findViewById(R.id.fight_num1);
//        fight_num2 = (TextView) findViewById(R.id.fight_num2);
//        fight_num3 = (TextView) findViewById(R.id.fight_num3);
//        comment_num1 = (TextView) findViewById(R.id.comment_num1);
//        comment_num2 = (TextView) findViewById(R.id.comment_num2);
//        comment_num3 = (TextView) findViewById(R.id.comment_num3);
//        btn_add_goal = (Button) findViewById(R.id.btn_new_goal);
//        card1 = (TextView) findViewById(R.id.btn_card1);
//        card2 = (TextView) findViewById(R.id.btn_card2);
//        card3 = (TextView) findViewById(R.id.btn_card3);
//        btn_add_goal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startAnimActivity(new Intent(getActivity(), NewGoalActivityElinc.class));
//            }
//        });
//    }
//
//    private void initList(){
//        goal = new Goal[3];
//        btn_add_goal.setVisibility(View.GONE);
//        goal1.setVisibility(View.GONE);
//        goal2.setVisibility(View.GONE);
//        goal3.setVisibility(View.GONE);
//        BmobQuery<Goal> query = new BmobQuery<>();
//        //用此方式可以构造一个BmobPointer对象。只需要设置objectId就行
//        query.addWhereEqualTo("author", new BmobPointer(me));
//        query.addWhereNotEqualTo("out", true);
//        query.findObjects(getActivity(), new FindListener<Goal>() {
//            @Override
//            public void onSuccess(final List<Goal> list) {
//                int length = 0;
//                for (int i = 0; i < list.size() && length < 3; i++) {
//                    if (!list.get(i).getOut()) {
//                        goal[length++] = list.get(i);
//                    }
//                }
//                goalNum = length;
//                Log.i("goalNum", goalNum + "");
//                btn_add_goal.setVisibility(View.VISIBLE);
//                if (goalNum == 0) {
//                    btn_add_goal.setBackgroundResource(R.drawable.corner_bg_large_btn_card1);
//                }
//                if (goalNum > 0) {
//                    goal1.setVisibility(View.VISIBLE);
//                    btn_add_goal.setBackgroundResource(R.drawable.corner_bg_large_btn_card2);
//                    title1.setText(goal[0].getGoalContent());
//                    tag1.setText(goal[0].getType());
//                    claim1.setText(goal[0].getClaim());
//                    try {
//                        Calendar calendar = Calendar.getInstance();
//                        Calendar calendarNow = Calendar.getInstance();
//                        calendarNow.setTime(new Date());
//                        String createdAt = goal[0].getCreatedAt();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//                        Date date = sdf.parse(createdAt);
//                        calendar.setTime(date);
//                        long t1 = calendar.getTimeInMillis();
//                        long t2 = calendarNow.getTimeInMillis();
//                        long passedDays = (t2 - t1) / (24 * 60 * 60 * 1000);
//                        if (goal[0].getDay() > passedDays) {
//                            date1.setText("只剩 " + (goal[0].getDay() - passedDays) + "天了");
//                        } else {
//                            date1.setText("过期了呢");
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    comment_num1.setText("0");
//                    fight_num1.setText("0");
//                    card1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Bmob.getServerTime(getActivity(), new GetServerTimeListener() {
//                                @Override
//                                public void onSuccess(long time) {
//                                    BmobQuery<Card> query=new BmobQuery<>();
//                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
//                                    time1 = formatter.format(new Date(time * 1000L));
//                                    Log.i("bmob", "当前服务器时间为:" + time1);
//                                    query.addWhereEqualTo("goal", goal[0]);
//                                    query.order("-createdAt");
//                                    //ShowToast(time1);
//                                    query.findObjects(getActivity(), new FindListener<Card>() {
//                                        @Override
//                                        public void onSuccess(List<Card> list) {
//                                            if (list.size() == 0 || !list.get(0).getCreatedAt().contains(time1)) {
//                                                dialog(0);
//                                            } else {
//                                                ShowToast("一天只能打一次卡");
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onError(int i, String s) {
//                                            ShowToast("没有网，臣妾做不到啊");
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onFailure(int code, String msg) {
//                                    Log.i("bmob", "获取服务器时间失败:" + msg);
//                                }
//                            });
//
//                        }
//                    });
//                }
//                if (goalNum > 1) {
//                    goal2.setVisibility(View.VISIBLE);
//                    btn_add_goal.setBackgroundResource(R.drawable.corner_bg_large_btn_card3);
//                    title2.setText(goal[1].getGoalContent());
//                    tag2.setText(goal[1].getType());
//                    claim2.setText(goal[1].getClaim());
//                    try {
//                        Calendar calendar = Calendar.getInstance();
//                        Calendar calendarNow = Calendar.getInstance();
//                        calendarNow.setTime(new Date());
//                        String createdAt = goal[1].getCreatedAt();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//                        Date date = sdf.parse(createdAt);
//                        calendar.setTime(date);
//                        long t1 = calendar.getTimeInMillis();
//                        long t2 = calendarNow.getTimeInMillis();
//                        long passedDays = (t2 - t1) / (24 * 60 * 60 * 1000);
//                        if (goal[1].getDay() > passedDays) {
//                            date2.setText("只剩 " + (goal[1].getDay() - passedDays) + "天了");
//                        } else {
//                            date2.setText("过期了呢");
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    comment_num2.setText("0");
//                    fight_num2.setText("0");
//                    card2.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            Bmob.getServerTime(getActivity(), new GetServerTimeListener() {
//                                @Override
//                                public void onSuccess(long time) {
//                                    BmobQuery<Card> query = new BmobQuery<>();
//                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
//                                    time1 = formatter.format(new Date(time * 1000L));
//                                    Log.i("bmob", "当前服务器时间为:" + time1);
//                                    query.addWhereEqualTo("goal", goal[1]);
//                                    query.order("-createdAt");
//                                    //ShowToast(time1);
//                                    query.findObjects(getActivity(), new FindListener<Card>() {
//                                        @Override
//                                        public void onSuccess(List<Card> list) {
//                                            if (list.size() == 0 || !list.get(0).getCreatedAt().contains(time1)) {
//                                                dialog(1);
//                                            } else {
//                                                ShowToast("一天只能打一次卡");
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onError(int i, String s) {
//                                            ShowToast("没有网，臣妾做不到啊");
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onFailure(int code, String msg) {
//                                    Log.i("bmob", "获取服务器时间失败:" + msg);
//                                }
//                            });
//
//                        }
//                    });
//                }
//                if (goalNum > 2) {
//                    goal3.setVisibility(View.VISIBLE);
//                    title3.setText(goal[2].getGoalContent());
//                    tag3.setText(goal[2].getType());
//                    claim3.setText(goal[2].getClaim());
//                    try {
//                        Calendar calendar = Calendar.getInstance();
//                        Calendar calendarNow = Calendar.getInstance();
//                        calendarNow.setTime(new Date());
//                        String createdAt = goal[2].getCreatedAt();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//                        Date date = sdf.parse(createdAt);
//                        calendar.setTime(date);
//                        long t1 = calendar.getTimeInMillis();
//                        long t2 = calendarNow.getTimeInMillis();
//                        long passedDays = (t2 - t1) / (24 * 60 * 60 * 1000);
//                        if (goal[2].getDay() > passedDays) {
//                            date3.setText("只剩 " + (goal[2].getDay() - passedDays) + "天了");
//                        } else {
//                            date3.setText("过期了呢");
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    comment_num3.setText("0");
//                    fight_num3.setText("0");
//                    card3.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Bmob.getServerTime(getActivity(), new GetServerTimeListener() {
//                                @Override
//                                public void onSuccess(long time) {
//                                    BmobQuery<Card> query=new BmobQuery<>();
//                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
//                                    time1 = formatter.format(new Date(time * 1000L));
//                                    Log.i("bmob", "当前服务器时间为:" + time1);
//                                    query.addWhereEqualTo("goal", goal[2]);
//                                    query.order("-createdAt");
//                                    //ShowToast(time1);
//                                    query.findObjects(getActivity(), new FindListener<Card>() {
//                                        @Override
//                                        public void onSuccess(List<Card> list) {
//                                            if (list.size() == 0 || !list.get(0).getCreatedAt().contains(time1)) {
//                                                dialog(2);
//                                            } else {
//                                                ShowToast("一天只能打一次卡");
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onError(int i, String s) {
//                                            ShowToast("没有网，臣妾做不到啊");
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onFailure(int code, String msg) {
//                                    Log.i("bmob", "获取服务器时间失败:" + msg);
//                                }
//                            });
//
//                        }
//                    });
//                    btn_add_goal.setVisibility(View.GONE);
//                }
//
//            }
//
//            @Override
//            public void onError(int code, String msg) {
//                Toast.makeText(getActivity(), "无法获取目标！", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//    private void dialog(final int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_hit_card, null);
//        //设置我们自己定义的布局文件作为弹出框的Content
//        builder.setView(view);
//        builder.setTitle("今日打卡");
//        final RadioButton e1= (RadioButton) view.findViewById(R.id.radioButton);
//        final RadioButton e2= (RadioButton) view.findViewById(R.id.radioButton2);
//        final RadioButton e3= (RadioButton) view.findViewById(R.id.radioButton3);
//        final RadioButton e4= (RadioButton) view.findViewById(R.id.radioButton4);
//        final RadioGroup r= (RadioGroup) view.findViewById(R.id.emo_radio);
//        r.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                if(i==e1.getId()){
//                    emo=1;
//                }else if(i==e2.getId()){
//                    emo=2;
//                }else if(i==e3.getId()){
//                    emo=3;
//                }else {
//                    emo=4;
//                }
//            }
//        });
//        final CheckBox done= (CheckBox) view.findViewById(R.id.done);
//        done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                checked = b;
//            }
//        });
//        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (!checked) {
//                    final EditText et = (EditText) view.findViewById(R.id.dialog_message);
//
//                    if (et.getText().toString().equals("")) {
//                        ShowToast("还没有说一句话呢");
//                    } else {
//                        Card card = new Card();
//                        card.setGoal(goal[position]);
//                        card.setLikedBy(new BmobRelation());
//                        card.setLikedByNum(0);
//                        card.setEmo(emo);
//                        card.setReply(new BmobRelation());
//                        card.setCardClaim(et.getText().toString());
//                        card.save(getContext(), new SaveListener() {
//                            @Override
//                            public void onSuccess() {
//                                ShowToast("打卡成功");
//                            }
//
//                            @Override
//                            public void onFailure(int i, String s) {
//                                ShowToast("打卡失败");
//                            }
//                        });
//                    }
//                } else {
//                    final EditText et = (EditText) view.findViewById(R.id.dialog_message);
//                    if (et.getText().toString().equals("")) {
//                        ShowToast("还没有说一句话呢");
//                    } else {
//                        Card card = new Card();
//                        card.setGoal(goal[position]);
//                        card.setLikedBy(new BmobRelation());
//                        card.setLikedByNum(0);
//                        card.setReply(new BmobRelation());
//                        card.setCardClaim(et.getText().toString());
//                        card.save(getContext(), new SaveListener() {
//                            @Override
//                            public void onSuccess() {
//                                //ShowToast("打卡成功");
//                            }
//
//                            @Override
//                            public void onFailure(int i, String s) {
//                                //ShowToast("打卡失败");
//                            }
//                        });
//
//
//
//                        goal[position].setOut(true);
//                        goal[position].update(getContext(), goal[position].getObjectId(), new UpdateListener() {
//                            @Override
//                            public void onSuccess() {
//                                ShowToast("目标完成!成长树将会记录你的轨迹!");
//                                initList();
//                            }
//                            @Override
//                            public void onFailure(int i, String s) {
//                                ShowToast("操作失败请重试");
//                            }
//                        });
//                    }
//                }
//
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.create().show();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        initList();
//    }
}
