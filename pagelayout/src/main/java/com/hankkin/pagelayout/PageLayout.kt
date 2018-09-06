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

    companion object {
        const val EMPTY_TYPE = 0x1
        const val LOADING_TYPE = 0x2
        const val ERROR_TYPE = 0x3
        const val CONTENT_TYPE = 0x4
    }

    private var mLoading: View? = null
    private var mEmpty: View? = null
    private var mError: View? = null
    private var mContent: View? = null
    private var mContext: Context? = null
    private var mBlinkLayout: BlinkLayout? = null


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private fun showView(type: Int) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            changeView(type)
        } else {
            post { changeView(type) }
        }
    }

    private fun changeView(type: Int) {
        when (type) {
            EMPTY_TYPE -> {
                setEmpty()
            }
            LOADING_TYPE -> {
                setLoading()
            }
            ERROR_TYPE -> {
                setError()
            }
            CONTENT_TYPE -> {
                setContent()
            }
        }
    }


    private fun setLoading() {
        removeAllViews()
        addView(mLoading)
    }

    private fun setError() {
        removeAllViews()
        addView(mError)
    }

    private fun setEmpty() {
        removeAllViews()
        this.addView(mEmpty)
    }

    private fun setContent() {
        removeAllViews()
        addView(mContent)
    }


    fun showLoading() {
        showView(LOADING_TYPE)
        mBlinkLayout?.apply {
            mBlinkLayout!!.startShimmerAnimation()
        }
    }

    fun showContent() {
        showView(CONTENT_TYPE)
        hide()
    }

    fun showError() {
        showView(ERROR_TYPE)
    }

    fun showEmpty() {
        showView(EMPTY_TYPE)
    }

    fun hide() {
        showView(CONTENT_TYPE)
        mBlinkLayout?.apply {
            mBlinkLayout!!.stopShimmerAnimation()
        }
    }


    class Builder {
        private var mPageLayout: PageLayout
        private var mInflater: LayoutInflater
        private var mContext: Context
        private lateinit var mTvEmpty: TextView
        private lateinit var mTvError: TextView
        private lateinit var mTvLoading: TextView
        private lateinit var mBlinkLayout: BlinkLayout
        private lateinit var mOnRetryClickListener: OnRetryClickListener


        constructor(context: Context) {
            this.mContext = context
            this.mPageLayout = PageLayout(context)
            mInflater = LayoutInflater.from(context)
            initViews()
        }

        private fun initViews(){
            mPageLayout.mEmpty = mInflater.inflate(R.layout.layout_empty,mPageLayout,false)
                    .apply {
                        mTvEmpty = findViewById<TextView>(R.id.tv_page_empty)!!
                    }
            mPageLayout.mError = mInflater.inflate(R.layout.layout_error,mPageLayout,false)
                    .apply {
                        mTvError = findViewById(R.id.tv_page_error)
                        mTvError.setOnClickListener { mOnRetryClickListener.onRetry() }
                    }
            mPageLayout.mLoading = mInflater.inflate(R.layout.layout_loading,mPageLayout,false)
                    .apply {
                        mBlinkLayout = findViewById(R.id.blinklayout)
                        mPageLayout.mBlinkLayout = mBlinkLayout
                        mTvLoading = findViewById(R.id.tv_page_loading)
                    }
        }

        fun setLoading(loading: Int): Builder {
            mPageLayout.mLoading = mInflater.inflate(loading, mPageLayout, false)
            return this
        }

        fun setError(error: Int): Builder {
            mPageLayout.mError = mInflater.inflate(error, mPageLayout, false)
            return this
        }

        fun setEmpty(empty: Int): Builder {
            mPageLayout.mEmpty = mInflater.inflate(empty, null, false)
            return this
        }

        fun setLoadingText(text: String){
            mTvLoading.text = text
        }

        fun setLoadingTextColor(color: Int){
            mTvLoading.setTextColor(mContext.resources.getColor(color))
        }

        fun setLoadingBlinkColor(color: Int){
            mBlinkLayout.setShimmerColor(mContext.resources.getColor(color))
        }

        fun setEmptyText(text: String){
            mTvEmpty.text = text
        }

        fun setEmptyTextColor(color: Int){
            mTvEmpty.setTextColor(mContext.resources.getColor(color))
        }

        fun setErrorText(text: String){
            mTvError.text = text
        }

        fun setErrorTextColor(color: Int){
            mTvError.setTextColor(mContext.resources.getColor(color))
        }

        fun initPage(targetView: Any): Builder {
            var content: ViewGroup? = null
            when (targetView) {
                is Activity -> {
                    mContext = targetView as Activity
                    content = (mContext as Activity).findViewById(android.R.id.content)
                }
                is Fragment -> {
                    mContext = (targetView as Fragment).activity!!
                    content = ((targetView as Fragment).view)?.parent as ViewGroup
                }
                is View -> {
                    mContext = (targetView as View).context
                    content = ((targetView as View).parent) as ViewGroup
                }
            }
            val childCount = content?.childCount
            var index = 0
            val oldContent: View
            if (targetView is View) {
                oldContent = targetView as View
                for (i in 0 until childCount!!) {
                    if (content!!.getChildAt(i) === oldContent) {
                        index = i
                        break
                    }
                }
            } else {
                oldContent = content!!.getChildAt(0)
            }
            mPageLayout.mContent = oldContent
            content?.removeView(oldContent)
            val lp = oldContent.layoutParams
            content?.addView(mPageLayout, index, lp)
            return this
        }

        fun setOnRetryListener(onRetryClickListener: OnRetryClickListener): Builder {
            this.mOnRetryClickListener = onRetryClickListener
            return this
        }


        fun create() = mPageLayout
    }

    interface OnRetryClickListener {
        fun onRetry()
    }
}