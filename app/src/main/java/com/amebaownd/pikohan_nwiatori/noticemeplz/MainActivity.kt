package com.amebaownd.pikohan_nwiatori.noticemeplz

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.MyContext

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        MyContext.onCreateAppcalition(this)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
