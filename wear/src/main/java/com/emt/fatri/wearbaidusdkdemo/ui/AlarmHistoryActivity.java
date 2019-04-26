package com.emt.fatri.wearbaidusdkdemo.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;

import com.emt.fatri.wearbaidusdkdemo.R;
import com.emt.fatri.wearbaidusdkdemo.datamodel.NodeInfo;
import com.emt.fatri.wearbaidusdkdemo.provider.NodeManageApi;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AlarmHistoryActivity extends WearableActivity {

    private ArrayList<NodeInfo> mNodesData=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private NodeRecycleViewAdapter mNodeRecycleViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_history);
        mRecyclerView=findViewById(R.id.alarm_history);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        NodeManageApi.deleNeedlessNode();
                        mNodesData= NodeManageApi.getNodeInfoList();
                        emitter.onNext(1);
                        emitter.onComplete();

                }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                        mNodeRecycleViewAdapter =new NodeRecycleViewAdapter(AlarmHistoryActivity.this,mNodesData);
                        mRecyclerView.setAdapter(mNodeRecycleViewAdapter);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();


    }
}
