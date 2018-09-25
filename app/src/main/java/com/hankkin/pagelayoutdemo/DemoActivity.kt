package com.hankkin.pagelayoutdemo

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.hankkin.pagelayout.PageLayout
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity() {

    private val mPageLayout by lazy {
        PageLayout.Builder(this)
                .initPage(ll_demo)
                .setLoading(R.layout.layout_loading_demo,R.id.tv_page_loading_demo)
                .setEmpty(R.layout.layout_empty_demo,R.id.tv_page_empty_demo)
                .setCustomView(layoutInflater.inflate(R.layout.layout_custom, null)
                        .apply {
                            //todo
                            findViewById<ImageView>(R.id.iv_custom).setImageResource(R.mipmap.icon_smile)
                            findViewById<TextView>(R.id.tv_custom_content).setText("This is PageLayout")
                        })
                .setError(R.layout.layout_error_demo,R.id.tv_page_error_demo,object : PageLayout.OnRetryClickListener{
                    override fun onRetry() {
                        loadData()
                    }
                })
                .setEmptyDrawable(R.drawable.pic_empty)
                .setErrorDrawable(R.drawable.pic_error)
                .setLoadingText("Loading")
                .create()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        loadData()

    }

    private fun loadData(){
        mPageLayout.showLoading()
        Handler().postDelayed({
            mPageLayout.hide()
        },3000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menus, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_loading -> loadData()
            R.id.menu_empty -> mPageLayout.showEmpty()
            R.id.menu_error -> mPageLayout.showError()
            R.id.menu_content -> mPageLayout.hide()
            R.id.menu_customer -> mPageLayout.showCustom()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
