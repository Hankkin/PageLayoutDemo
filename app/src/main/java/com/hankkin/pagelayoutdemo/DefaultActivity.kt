package com.hankkin.pagelayoutdemo

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.hankkin.pagelayout.PageLayout
import kotlinx.android.synthetic.main.activity_default.*

class DefaultActivity : AppCompatActivity() {

    private val mPageLayout by lazy {
        PageLayout.Builder(this)
                .initPage(ll_default)
                .setCustomView(layoutInflater.inflate(R.layout.layout_custom, null)
                        .apply {
                            //todo
                            findViewById<ImageView>(R.id.iv_custom).setImageResource(R.mipmap.icon_smile)
                        })
                .setOnRetryListener(object : PageLayout.OnRetryClickListener {
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

    }


    private fun loadData() {
        mPageLayout.showLoading()
        Handler().postDelayed({
            mPageLayout.hide()
        }, 3000)
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
