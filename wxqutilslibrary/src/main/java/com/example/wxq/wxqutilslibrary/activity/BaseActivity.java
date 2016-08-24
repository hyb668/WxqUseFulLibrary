package com.example.wxq.wxqutilslibrary.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;

import com.example.wxq.wxqutilslibrary.myutils.log.LogUtils;

/**
 * Created by Administrator on 2016/8/17 0017.
 * 主要功能为：
 * 分功能为：
 * 分功能为：
 */
public abstract class BaseActivity extends FragmentActivity {
public String className="";


    @Override
        public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
            super.onCreate(savedInstanceState, persistentState);
           className=this.getClass().getName();
           beforeSetView();
           setContentView(setActivityView());
       
        }

    private void beforeSetView() {
    }

    //设置activity的view
    public  abstract int  setActivityView();



    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //ctrl alt k
public void showLog(String content){
    LogUtils.i(className,content);
}




















}
