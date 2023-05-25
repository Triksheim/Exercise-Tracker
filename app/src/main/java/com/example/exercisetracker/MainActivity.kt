package com.example.exercisetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.exercisetracker.databinding.ActivityMainBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val sharedViewModel: SharedViewModel by viewModels {
        SharedViewModelFactory(
            (application as TrainingApplication).trainingRepository
        )
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        sharedViewModel.activeUser.observe(this, Observer {
            if (sharedViewModel.activeUser.value != null) {
                lifecycleScope.launch {
                    sharedViewModel.getUserStats()
                }
            }
        })

        sharedViewModel.currentProgram.observe(this, Observer {
            sharedViewModel.flowExercisesForCurrentProgram()
            sharedViewModel.getSessionsForCurrentProgram()
        })

        sharedViewModel.currentSession.observe(this, Observer {
            sharedViewModel.getDataForCurrentSession()
            sharedViewModel.getPhotosForCurrentSession()
        })

        sharedViewModel.sessionData.observe(this, Observer {
            sharedViewModel.calcSessionDistanceAndHeight()
        })

        sharedViewModel.toolbarTitle.observe(this, Observer {title ->
            binding.toolbar.title = title
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        return when (item.itemId) {
            R.id.nav_home_statistics -> {
                // Check if the current destination is the mySessionsFragment
                if (navController.currentDestination?.id == R.id.mySessionsFragment) {
                    // Pop up to the MyExercisesFragment
                    navController.popBackStack(R.id.mySessionsFragment, true)
                } else {
                    // Navigate to myExercisesFragment
                    navController.navigate(R.id.mySessionsFragment)
                    true
                }
            }
            /**
            R.id.nav_all_sessions -> {

                // Check if the current destination is the allSessionsFragment
                if(navController.currentDestination?.id == R.id.allSessionsFragment) {
                    // Pop up to the allSessionsFragment
                    navController.popBackStack(R.id.allSessionsFragment, true)
                } else {
                    // Navigate to allSessionsFragment
                    navController.navigate(R.id.allSessionsFragment)
                    true
                }

            }
             */
            R.id.nav_my_exercises -> {
                // Check if the current destination is the myExercisesFragment
                if(navController.currentDestination?.id == R.id.myExercisesFragment) {
                    // Pop up to the MyExercisesFragment
                    navController.popBackStack(R.id.myExercisesFragment, false)
                } else {
                    // Navigate to myExercisesFragment
                    navController.navigate(R.id.myExercisesFragment)
                    true
                }
            }
            R.id.action_my_programs -> {
                // Check if the current destination is the myProgramsFragment
                if(navController.currentDestination?.id == R.id.myProgramsFragment) {
                    // Pop up to the myProgramsFragment
                    navController.popBackStack(R.id.myProgramsFragment, false)
                } else {
                    // Navigate to myProgramsFragment
                    navController.navigate(R.id.myProgramsFragment)
                    true
                }
            }
            R.id.new_program -> {
                // Check if the current destination is the SecondFragment
                if(navController.currentDestination?.id == R.id.SecondFragment) {
                    // Pop up to the SecondFragment
                    navController.popBackStack(R.id.SecondFragment, false)
                } else {
                    // Navigate to SecondFragment
                    navController.navigate(R.id.SecondFragment)
                    true
                }
            }
            R.id.new_exercise -> {
                // Check if the current destination is the newExerciseFragment
                if(navController.currentDestination?.id == R.id.newExerciseFragment) {
                    // Pop up to the newExerciseFragment
                    navController.popBackStack(R.id.newExerciseFragment, false)
                } else {
                    // Navigate to newExerciseFragment
                    navController.navigate(R.id.newExerciseFragment)
                    true
                }
            }

            R.id.nav_logout -> {
                // Handle logout action
                performLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    private fun performLogout() {
        sharedViewModel.logout()
        // Navigate to the login screen
        navController.navigate(R.id.LoginFragment)
    }

}