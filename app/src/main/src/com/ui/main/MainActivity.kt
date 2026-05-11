package com.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.R
import com.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLastCrashIfAny()
        setupNavigation()
        observeUserSettings()
    }

    private fun showLastCrashIfAny() {
        val crashText = com.util.CrashLogger.readAndClear(this) ?: return
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.app_name))
            .setMessage("Previous crash detected:\n\n$crashText")
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun observeUserSettings() {
        val app = application as com.NewsApp
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.userSettingsRepository.userSettingsFlow.collect { settings ->
                    AppCompatDelegate.setDefaultNightMode(
                        if (settings.darkModeEnabled) {
                            AppCompatDelegate.MODE_NIGHT_YES
                        } else {
                            AppCompatDelegate.MODE_NIGHT_NO
                        }
                    )
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(settings.languageCode)
                    )
                }
            }
        }
    }
}
