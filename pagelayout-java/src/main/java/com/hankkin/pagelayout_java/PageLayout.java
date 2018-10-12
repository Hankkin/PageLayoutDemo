package com.hankkin.pagelayout_java;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


/**
 * Created by ${Hankkin} on 2018/10/12.
 */

public class PageLayout extends FrameLayout {

    enum State{
        EMPTY_TYPE,
        LOADING_TYPE,
        ERROR_TYPE,
        CONTENT_TYPE,
        CUSTOM_TYPE
    }

    private View mLoading;
    private View mEmpty;
    private View mError;
    private View mContent;
    private View mCustom;
    private Context mContext;
    private BlinkLayout mBlinkLayout;
    private State mCurrentState;

    public PageLayout(@NonNull Context context) {
        super(context);
    }

    public PageLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void showView(final State state){
        if (Looper.myLooper() == Looper.getMainLooper()){
            changeView(state);
        }
        else {
            post(new Runnable() {
                @Override
                public void run() {
                    changeView(state);
                }
            });
        }
    }

    private void changeView(State state){
        mCurrentState = state;
        switch (state){
            case LOADING_TYPE:
                mLoading.setVisibility(VISIBLE);
                break;
            case EMPTY_TYPE:
                mEmpty.setVisibility(VISIBLE);
                break;
            case ERROR_TYPE:
                mError.setVisibility(VISIBLE);
                break;
            case CUSTOM_TYPE:
                mCustom.setVisibility(VISIBLE);
                break;
            case CONTENT_TYPE:
                mContent.setVisibility(VISIBLE);
                break;
        }
    }

    public void showLoading(){
        showView(State.LOADING_TYPE);
        if (mBlinkLayout != null){
            mBlinkLayout.startShimmerAnimation();
        }
    }

    public void showError(){
        showView(State.ERROR_TYPE);
    }

    public void showEmpty(){
        showView(State.EMPTY_TYPE);
    }

    public void hide(){
        showView(State.CONTENT_TYPE);
        if (mBlinkLayout!= null){
            mBlinkLayout.stopShimmerAnimation();
        }
    }

    public void showCustom(){
        showView(State.CUSTOM_TYPE);
    }

    class Builder{
        private PageLayout mPageLayout;
        private LayoutInflater mInflater;
        private Context mContext;
        private TextView mTvEmpty;
        private TextView mTvError;
        private TextView mTvErrorRetry;
        private TextView mTvLoading;
        private TextView mTvLoadingBlink;
        private BlinkLayout mBlinkLayout;
        private OnRetryClickListener mOnRetryClickListener;

        public Builder(Context context) {
            mContext = context;
            this.mPageLayout = new PageLayout(mContext);
            this.mInflater = LayoutInflater.from(mContext);
        }

        private void initDefault(){
            if (mPageLayout.mEmpty == null){
                setDefaultEmpty();
            }

        }

        private void setDefaultEmpty(){
            mPageLayout.mEmpty = mInflater.inflate(R.layout.layout_empty, mPageLayout, false);
            mTvEmpty = mPageLayout.mEmpty.findViewById(R.id.tv_page_empty);
            mPageLayout.mEmpty.setVisibility(GONE);
            mPageLayout.addView(mPageLayout.mEmpty);
        }

        private void setDefaultError()
    }

    interface OnRetryClickListener{
        void onRetry();
    }
}
