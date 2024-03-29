package org.yczbj.ycvideoplayer.base.mvp1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.pedaily.yc.ycdialoglib.customToast.ToastUtil;

import butterknife.ButterKnife;

/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/5/18
 * 描    述：所有Activity的父类
 * 修订历史：
 * ================================================
 */
public abstract class BaseActivity<T extends BasePresenter> extends  AppCompatActivity {
    /*
    將代理类通用行为抽象出来
     */
    protected  T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        ButterKnife.bind(this);

        //避免切换横竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(mPresenter != null) {
            mPresenter.subscribe();
        }
        initView();
        initListener();
        initData();
        if(!NetworkUtils.isConnected()) {
            ToastUtils.showShort("请检查网络连接");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null) {
            mPresenter.unSubscribe();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /*
    返回一个用于显示界面的布局id
     */
    public  abstract int getContentView();

    /*
    初始化View的代码写在这个方法中
     */
    public abstract void initView();

    /*
    初始化监听器的代码写在这个方法中
     */
    public abstract void initListener();

    /*
    初始数据的代码写在这个方法中，用于从服务器获取数据
     */
    public abstract void initData();

    /*
    通过Class跳转界面
     */
    public void startActivity(Class<?> cls) {
        startActivity(cls,null);
    }
    /*
    通过Class跳转界面
     */
    public void startActivityForResult(Class<?> cls,int requestCode) {
        startActivityForResult(cls,null,requestCode);
    }

    /*
    含义Bundle通过Class跳转界面
     */
    public void startActivityForResult(Class<?> cls,Bundle bundle,int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this,cls);
        if(bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent,requestCode);
    }

    /*
    含有Bundle通过Class跳转页面
     */
    public void startActivity(Class<?> cls ,Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this,cls);
        if(bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /*
    用来检测所有Activity的内存泄漏
     */
    private void initLeakCanary() {
        /*RefWatcher refWatcher = BaseApplication.getRefWatcher(this);
        refWatcher.watch(this);*/
    }
}
