package com.example.mobiluygulamaprojesi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.mobiluygulamaprojesi.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var design: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        design = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(design.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.adminFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(design.adminBottomNav,navController)
    }
}