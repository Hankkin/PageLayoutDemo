package com.hankkin.pagelayoutdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_default.setOnClickListener {
            startActivity(Intent(this,DefaultActivity::class.java))
        }

        btn_demo.setOnClickListener {
            startActivity(Intent(this,DemoActivity::class.java))
        }
    }

}
