package com.ygzbrsnm.autoatwechat

import android.app.Application
import com.lzf.easyfloat.EasyFloat

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        EasyFloat.init(this, true)
    }

}
 