package com.example.wxq.wxqutilslibrary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
* 懒加载fragment 用到显示的时候才加载
* 王晓清10.25 防止覆盖
*
* */
public abstract class SuperFragment extends Fragment {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"; //防止fragment重叠

    public String fragmentTitle;

    private boolean isVisible;

    private boolean isPrepared;

    private boolean isFirstLoad = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 若 viewpager 不设置 setOffscreenPageLimit 或设置数量不够
        // 销毁的Fragment onCreateView 每次都会执行(但实体类没有从内存销毁)
        // 导致initData反复执行,所以这里注释掉
        // isFirstLoad = true;
        // 2016/04/29
        // 取消 isFirstLoad = true的注释 , 因为上述的initData本身就是应该执行的
        // onCreateView执行 证明被移出过FragmentManager initData确实要执行.
        // 如果这里有数据累加的Bug 请在initViews方法里初始化您的数据 比如 list.clear();







        //  必须设置好缓存界面否则的话对象销毁总是执行 lazyload（）mViewPager.setOffscreenPageLimit(4);
        isFirstLoad = true;
        View view = initViews(inflater, container, savedInstanceState);
        isPrepared = true;
        lazyLoad();
        return view;
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     * @param isVisibleToUser 是否显示出来了
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden hidden True if the fragment is now hidden, false if it is not
     *               visible.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void onInvisible() {
    }

    /**
     * 要实现延迟加载Fragment内容,需要在 onCreateView
     * isPrepared = true;
     */
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirstLoad) {
            //if (!isAdded() || !isVisible || !isFirstLoad) {
            return;
        }
        isFirstLoad = false;
        initData();
    }

    protected abstract View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void initData();

    public String getTitle() {
        if (null == fragmentTitle) {
            setDefaultFragmentTitle(null);
        }
        return TextUtils.isEmpty(fragmentTitle) ? "" : fragmentTitle;
    }

    public void setTitle(String title) {
        fragmentTitle = title;
    }

    /**
     * 设置fragment的Title直接调用 {@link BaseFragment#setTitle(String)},若不显示该title 可以不做处理
     *
     * @param title 一般用于显示在TabLayout的标题
     */
    protected abstract void setDefaultFragmentTitle(String title);


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
//        只要上面的9行代码！ FragmentState没帮我们保存Hidden状态，那就我们自己来保存，在页面重启后，我们自己来决定Fragment是否显示！
//        解决思路转变了，由Activity/父Fragment来管理子Fragment的Hidden状态转变为 由Fragment自己来管理自己的Hidden状态！
    }


}