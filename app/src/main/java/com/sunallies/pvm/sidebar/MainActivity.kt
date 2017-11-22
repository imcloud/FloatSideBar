package com.sunallies.pvm.sidebar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sunallies.pvm.floatsidebar.SideBar
import com.sunallies.pvm.floatsidebar.SideBar.OnIndexChooseListener

class MainActivity : AppCompatActivity() {

    private val TAG: String = "com.sunallies.sidebar"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sideBar = findViewById<SideBar>(R.id.side_bar)

        sideBar.setOnChooseIndexListener(object: OnIndexChooseListener {
            override fun chooseIndex(position: Int, index: String) {
                Log.d(TAG, "position = $position, index = $index")
            }

        })
    }
}
