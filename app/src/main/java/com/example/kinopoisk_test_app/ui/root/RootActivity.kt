package com.example.kinopoisk_test_app.ui.root

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.kinopoisk_test_app.R
import com.example.kinopoisk_test_app.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {
    private var _binding: ActivityRootBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fc_root_container) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bnBottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailFragment -> binding.bnBottomNavigation.isVisible = false
                else -> binding.bnBottomNavigation.isVisible = true
            }
        }
        setupLandscapeOrientation(navController)
    }

    private fun setupLandscapeOrientation(navController: NavController) {
        if (resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            navController.popBackStack(R.id.popularFragment, false)
        }
    }
}