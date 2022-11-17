package com.example.storyins.ui.main


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.storyins.R
import com.example.storyins.databinding.ActivityMainBinding
import com.example.storyins.ui.main.auth.AuthActivity
import com.example.storyins.ui.main.story.AddStoryActivity
import com.example.storyins.ui.main.story.maps.MapsActivity
import com.example.storyins.ui.factory.StoryViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var navView : BottomNavigationView

    private lateinit var mainViewModel: MainViewModel
    private var progressBar : Dialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        mainViewModel = ViewModelProvider(this , StoryViewModelFactory(this))[MainViewModel::class.java]


        initView()
        viewLoading()


    }



    override fun onCreateOptionsMenu(menu: Menu?) : Boolean{
        menuInflater.inflate(R.menu.setting_menu , menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_setting -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }



    private fun initView(){
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView = findViewById(R.id.nav_bottom_view)
        navView.background=null
        navView.menu.getItem(2).isEnabled=false
        navView.setOnItemSelectedListener(this)

        activityMainBinding.fabStory.setOnClickListener {
            val intent = Intent(this , AddStoryActivity::class.java)
            startActivity(intent)
        }


    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navigation_home -> {
                item.isEnabled=true
            }
            R.id.navigation_story_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                item.isEnabled=true
            }
            R.id.navigation_logout -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.exit))
                    .setMessage(getString(R.string.ask_logout))
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        showLoading()
                        mainViewModel.logout()
                        toAuthActivity()
                    }
                    .create()
                    .show()
                item.isEnabled = true
            }



        }

        return true
    }


    private fun toAuthActivity(){
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    @SuppressLint("InflateParams")
    private fun viewLoading(){
        progressBar = Dialog(this)
        val progressbarLayout = layoutInflater.inflate(R.layout.dialog_progressbar , null)

        progressBar?.let {
            it.setContentView(progressbarLayout)
            it.setCancelable(false)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }


    private fun showLoading() {
        progressBar?.show()
    }


    private fun dismissLoading() {
        progressBar?.dismiss()
    }


    override fun onDestroy() {
        super.onDestroy()
        dismissLoading()
    }


}