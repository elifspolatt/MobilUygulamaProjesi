package com.example.mobiluygulamaprojesi

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.mobiluygulamaprojesi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var design: ActivityMainBinding

    private val fragmentBottomNav = listOf(
        R.id.anasayfa_nav,
        R.id.favoriler_nav,
        R.id.sepetim_nav,
        R.id.hesabim_nav,
        R.id.iletisim_nav
    )
    private val noBottomOrToolbarNav = listOf(
        R.id.giris_nav,
        R.id.kayit_nav,
        R.id.sifreSifirlama_nav,
        R.id.sifreGirme_nav
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        design = ActivityMainBinding.inflate(layoutInflater)
        setContentView(design.root)

        setSupportActionBar(design.toolbar)
        supportActionBar?.title = ""

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(design.bottomNav,navHostFragment.navController)

        NavigationUI.setupActionBarWithNavController(this,navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if(destination.id in fragmentBottomNav) {
                design.bottomNav.visibility = View.VISIBLE
                design.toolbar.visibility = View.VISIBLE
            }
            else if(destination.id in noBottomOrToolbarNav){
                design.bottomNav.visibility = View.GONE
                design.toolbar.visibility = View.GONE
            }
            else {
                design.bottomNav.visibility = View.GONE
                design.toolbar.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}