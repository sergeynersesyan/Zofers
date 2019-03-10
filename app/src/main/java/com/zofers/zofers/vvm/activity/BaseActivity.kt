package com.zofers.zofers.vvm.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zofers.zofers.App

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var x = 1.rangeTo(5);
        for ( i in x ) {

        }
    }

    protected fun getApp (): App {
        return App.getInstance();
    }
}
