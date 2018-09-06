package com.hankkin.pagelayoutdemo

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.hankkin.pagelayout.PageLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var page: PageLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        PageLayout.Builder(this)
                .initPage(this)
                .setEmpty(R.layout.layout_empty)
                .setError(R.layout.layout_error)
                .setLoading(R.layout.layout_loading)
                .setOnRetryListener(object : PageLayout.OnRetryClickListener{
                    override fun onRetry() {
                        loadData()
                    }
                })
                .create()

         page = PageLayout.Builder(this)
                .initPage(ll)
                .setOnRetryListener(object : PageLayout.OnRetryClickListener{
                    override fun onRetry() {
                        loadData()
                    }
                })
                .create()

        loadData()
        loading.setOnClickListener {
            page.showLoading()
        }

        empty.setOnClickListener {
            page.showEmpty()
        }

        error.setOnClickListener { page.showError() }

        content.setOnClickListener { page.showContent() }

        loading_demo.setOnClickListener {
            page = PageLayout.Builder(this)
                    .initPage(ll)
                    .setLoading(R.layout.layout_loading_demo)
                    .setOnRetryListener(object : PageLayout.OnRetryClickListener{
                        override fun onRetry() {
                            loadData()
                        }
                    })
                    .create()
            loadData()
        }


    }

    private fun loadData(){
        page.showLoading()

        Handler().postDelayed( { page.showContent() },3000)

    }
}
