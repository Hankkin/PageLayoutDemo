package com.hankkin.pagelayoutdemo

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.hankkin.pagelayout.PageLayout
import kotlinx.android.synthetic.main.activity_default.*

class DefaultActivity : AppCompatActivity() {

    private val mPageLayout by lazy {
        PageLayout.Builder(this)
                .initPage(ll_default)
                .setOnRetryListener(object : PageLayout.OnRetryClickListener{
                    override fun onRetry() {
                        loadData()
                    }

                })
                .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)

        loadData()

        empty.setOnClickListener {
            mPageLayout.showEmpty()
        }

        error.setOnClickListener {
            mPageLayout.showError()
        }

        loading.setOnClickListener {
            mPageLayout.showLoading()
        }

        content.setOnClickListener {
            mPageLayout.hide()
        }
    }


    private fun loadData(){
        mPageLayout.showLoading()
        Handler().postDelayed({
            mPageLayout.hide()
        },3000)
    }

}
