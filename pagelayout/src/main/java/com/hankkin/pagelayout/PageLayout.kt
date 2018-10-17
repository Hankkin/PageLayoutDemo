package com.hankkin.pagelayout

import android.app.Activity
import android.content.Context
import android.os.Looper
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

/**
 * Created by huanghaijie on 2018/9/5.
 */
class PageLayout : FrameLayout {

    enum class State{
        EMPTY_TYPE,
        LOADING_TYPE,
        ERROR_TYPE,
        CONTENT_TYPE,
        CUSTOM_TYPE
    }
    private var mLoading: View? = null
    private var mEmpty: View? = null
    private var mError: View? = null
    private var mContent: View? = null
    private var mCustom: View? = null
    private var mContext: Context? = null
    private var mBlinkLayout: BlinkLayout? = null
    private var mCurrentState = State.CONTENT_TYPE

    private var mOnRetryClickListener: OnRetryClickListener? = null


    fun getOnRetryListener(): OnRetryClickListener?{
        return mOnRetryClickListener
    }

    fun setOnRetryListener(onRetryClickListener: OnRetryClickListener) {
        this.mOnRetryClickListener = onRetryClickListener
    }


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private fun showView(type: State) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            changeView(type)
        } else {
            post { changeView(type) }
        }
    }

    private fun changeView(type: State) {
        mCurrentState = type
        mLoading?.visibility = if (type == State.LOADING_TYPE) View.VISIBLE else View.GONE
        mContent?.visibility = if (type == State.CONTENT_TYPE) View.VISIBLE else View.GONE
        mError?.visibility = if (type == State.ERROR_TYPE) View.VISIBLE else View.GONE
        mEmpty?.visibility = if (type == State.EMPTY_TYPE) View.VISIBLE else View.GONE
        mCustom?.visibility = if (type == State.CUSTOM_TYPE) View.VISIBLE else View.GONE
    }



    fun showLoading() {
        showView(State.LOADING_TYPE)
        mBlinkLayout?.apply {
            mBlinkLayout!!.startShimmerAnimation()
        }
    }


    fun showError() {
        showView(State.ERROR_TYPE)
    }

    fun showEmpty() {
        showView(State.EMPTY_TYPE)
    }

    fun hide() {
        showView(State.CONTENT_TYPE)
        mBlinkLayout?.apply {
            mBlinkLayout!!.stopShimmerAnimation()
        }
    }

    fun showCustom(){
        showView(State.CUSTOM_TYPE)
    }


    class Builder {
        private var mPageLayout: PageLayout
        private var mInflater: LayoutInflater
        private var mContext: Context
        private lateinit var mTvEmpty: TextView
        private lateinit var mTvError: TextView
        private lateinit var mTvErrorRetry: TextView
        private lateinit var mTvLoading: TextView
        private lateinit var mTvLoadingBlink: TextView
        private lateinit var mBlinkLayout: BlinkLayout


        constructor(context: Context) {
            this.mContext = context
            this.mPageLayout = PageLayout(context)
            mInflater = LayoutInflater.from(context)
        }

        private fun initDefault() {
            if (mPageLayout.mEmpty == null) {
                setDefaultEmpty()
            }
            if (mPageLayout.mError == null) {
                setDefaultError()
            }
            if (mPageLayout.mLoading == null) {
                setDefaultLoading()
            }
        }

        private fun setDefaultEmpty() {
            mPageLayout.mEmpty = mInflater.inflate(R.layout.layout_empty, mPageLayout, false)
                    .apply {
                        mTvEmpty = findViewById<TextView>(R.id.tv_page_empty)!!
                    }
            mPageLayout.mEmpty?.visibility = View.GONE
            mPageLayout.addView(mPageLayout.mEmpty)
        }

        private fun setDefaultError() {
            mPageLayout.mError = mInflater.inflate(R.layout.layout_error, mPageLayout, false)
                    .apply {
                        mTvError = findViewById(R.id.tv_page_error)
                        mTvErrorRetry = findViewById(R.id.tv_page_error_retry)
                        mTvErrorRetry.setOnClickListener { mPageLayout.getOnRetryListener()?.onRetry() }
                    }
            mPageLayout.mError?.visibility = View.GONE
            mPageLayout.addView(mPageLayout.mError)
        }

        private fun setDefaultLoading() {
            mPageLayout.mLoading = mInflater.inflate(R.layout.layout_loading, mPageLayout, false)
                    .apply {
                        mBlinkLayout = findViewById(R.id.blinklayout)
                        mPageLayout.mBlinkLayout = mBlinkLayout
                        mTvLoadingBlink = findViewById(R.id.tv_page_loading_blink)
                    }
            mPageLayout.mLoading?.visibility = View.GONE
            mPageLayout.addView(mPageLayout.mLoading)
        }

        /**
         * 设置loading布局
         */
        fun setLoading(loading: Int,loadingTvId: Int): Builder {
            mInflater.inflate(loading, mPageLayout, false).apply {
                mTvLoading = findViewById(loadingTvId)
                mPageLayout.mLoading = this
                mPageLayout.addView(this)
            }
            return this
        }

        /**
         * 自定义错误布局
         * 默认样式，传入错误文案ID，及点击回调
         */
        fun setError(errorView: Int, errorClickId: Int, onRetryClickListener: OnRetryClickListener): Builder {
            mInflater.inflate(errorView, mPageLayout, false).apply {
                mPageLayout.mError = this
                mPageLayout.addView(this)
                mTvError = findViewById(errorClickId)
                mTvError.setOnClickListener { onRetryClickListener.onRetry() }
            }
            return this
        }

        /**
         * 自定义错误布局
         * 设置前需手动初始化好View中各个事件
         */
        fun setError(errorView: View): Builder {
            mPageLayout.apply {
                mError = errorView
                addView(errorView)
            }
            return this
        }


        /**
         * 自定义空布局
         */
        fun setEmpty(empty: Int,emptyTvId: Int): Builder {
            mInflater.inflate(empty, null, false).apply {
                mTvEmpty = findViewById(emptyTvId)
                mPageLayout.mEmpty = this
                mPageLayout.addView(this)
            }
            return this
        }

        /**
         * 自定义布局
         */
        fun setCustomView(view: View): Builder{
            mPageLayout.apply {
                mCustom = view
                addView(view)
            }
            return this
        }

        /**
         * 设置加载文案
         */
        fun setLoadingText(text: String): Builder {
            mTvLoading.text = text
            return this
        }

        /**
         * 设置默认闪烁加载文案
         */
        fun setDefaultLoadingBlinkText(text: String): Builder {
            mTvLoadingBlink.text = text
            return this
        }

        /**
         * 设置加载文字颜色
         */
        fun setLoadingTextColor(color: Int): Builder {
            mTvLoading.setTextColor(mContext.resources.getColor(color))
            return this
        }

        /**
         * 设置默认加载闪烁颜色
         */
        fun setDefaultLoadingBlinkColor(color: Int): Builder {
            mBlinkLayout.setShimmerColor(mContext.resources.getColor(color))
            return this
        }

        /**
         * 设置默认空布局文案
         */
        fun setDefaultEmptyText(text: String): Builder {
            mTvEmpty.text = text
            return this
        }

        /**
         * 设置默认空布局文案颜色
         */
        fun setDefaultEmptyTextColor(color: Int): Builder {
            mTvEmpty.setTextColor(mContext.resources.getColor(color))
            return this
        }

        /**
         * 设置默认错误布局文案
         */
        fun setDefaultErrorText(text: String): Builder {
            mTvError.text = text
            return this
        }


        /**
         * 设置默认错误布局文案颜色
         */
        fun setDefaultErrorTextColor(color: Int): Builder {
            mTvError.setTextColor(mContext.resources.getColor(color))
            return this
        }

        /**
         * 设置默认错误布局重试文案
         */
        fun setDefaultErrorRetryText(text: String): Builder {
            mTvErrorRetry.text = text
            return this
        }

        /**
         * 设置默认错误布局重试文案颜色
         */
        fun setDefaultErrorRetryTextColor(color: Int): Builder {
            mTvErrorRetry.setTextColor(mContext.resources.getColor(color))
            return this
        }

        /**
         * 设置空布局提醒图片
         */
        fun setEmptyDrawable(resId: Int): Builder{
            setTopDrawables(mTvEmpty,resId)
            return this
        }

        /**
         * 设置错误布局提醒图片
         */
        fun setErrorDrawable(resId: Int): Builder{
            setTopDrawables(mTvError,resId)
            return this
        }


        /**
         * 设置布局top drawable
         */
        private fun setTopDrawables(textView: TextView, resId: Int) {
            if (resId == 0){
                textView.setCompoundDrawables(null, null, null, null)
            }
            val drawable = mContext.resources.getDrawable(resId)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)//必须设置图片大小，否则不显示
            textView.setCompoundDrawables(null, drawable, null, null)
            textView.compoundDrawablePadding = 20
        }

        /**
         * set target view for root
         */
        fun initPage(targetView: Any): Builder {
            var content: ViewGroup? = null
            when (targetView) {
                is Activity -> {    //如果是Activity，获取到android.R.content
                    mContext = targetView
                    content = (mContext as Activity).findViewById(android.R.id.content)
                }
                is Fragment -> {    //如果是Fragment获取到parent
                    mContext = targetView.activity!!
                    content = (targetView.view)?.parent as ViewGroup
                }
                is View -> {        //如果是View，也取到parent
                    mContext = targetView.context
                    try {
                        content = (targetView.parent) as ViewGroup
                    } catch (e: TypeCastException) {
                    }
                }
            }
            val childCount = content?.childCount
            var index = 0
            val oldContent: View
            if (targetView is View) {   //如果是某个线性布局或者相对布局时，遍历它的孩子，找到对应的索引，记录下来
                oldContent = targetView
                childCount?.let {
                    for (i in 0 until childCount) {
                        if (content!!.getChildAt(i) === oldContent) {
                            index = i
                            break
                        }
                    }
                }

            } else {    //如果是Activity或者Fragment时，取到索引为第一个的View
                oldContent = content!!.getChildAt(0)
            }
            mPageLayout.mContent = oldContent   //给PageLayout设置contentView
            mPageLayout.removeAllViews()
            content?.removeView(oldContent)     //将本身content移除，并且把PageLayout添加到DecorView中去
            val lp = oldContent.layoutParams
            content?.addView(mPageLayout, index, lp)
            mPageLayout.addView(oldContent)
            initDefault()   //设置默认状态布局
            return this
        }


        fun create() = mPageLayout
    }

    interface OnRetryClickListener {
        fun onRetry()
    }

}