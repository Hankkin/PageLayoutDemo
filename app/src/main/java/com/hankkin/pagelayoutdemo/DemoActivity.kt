package com.hankkin.pagelayoutdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.hankkin.pagelayout.PageLayout
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity() {

    private val mPageLayout by lazy {
        PageLayout.Builder(this)
                .initPage(ll_demo)
                .setLoading(R.layout.layout_loading_demo)
                .setEmpty(R.layout.layout_empty_demo)
                .setError(R.layout.layout_error_demo,R.id.tv_page_error_demo,object : PageLayout.OnRetryClickListener{
                    override fun onRetry() {
                        loadData()
                    }
                })
                .setEmptyDrawable(R.drawable.pic_empty)
                .setErrorDrawable(R.drawable.pic_error)
                .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        loadData()

        loading_demo.setOnClickListener {
            loadData()
        }

        error_demo.setOnClickListener {
            mPageLayout.showError()
        }

        empty_demo.setOnClickListener {
            mPageLayout.showEmpty()
        }
    }

    private fun loadData(){
        mPageLayout.showLoading()
        Handler().postDelayed({
            mPageLayout.hide()
        },3000)
    }
}
