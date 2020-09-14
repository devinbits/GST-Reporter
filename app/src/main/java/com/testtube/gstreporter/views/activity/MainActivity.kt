package com.testtube.gstreporter.views.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.testtube.gstreporter.R
import com.testtube.gstreporter.model.Profile
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener(NavController.OnDestinationChangedListener
        { _, destination, _ ->
            supportActionBar?.show()
            when (destination.id) {
                R.id.FirstFragment -> {
                    profile_button?.visibility = View.VISIBLE
                    toolbar_title?.text = "Sale Records"
                }
                R.id.SecondFragment -> {
                    profile_button?.visibility = View.INVISIBLE
                    toolbar_title?.text = getString(R.string.new_sale)

                }
                R.id.AuthFrag -> {
                    supportActionBar?.hide()
                }
                else -> {
                    profile_button?.visibility = View.INVISIBLE
                    supportActionBar?.setTitle(getString(R.string.app_name))
                }
            }
        })

        profile_button.setOnClickListener {
            navController.navigate(R.id.action_FirstFragment_to_profile)
        }
        if (savedInstanceState == null)
            FirebaseAuth.getInstance().addAuthStateListener { it1 ->
                when (it1.currentUser) {
                    null -> {
                        if (navController.currentDestination?.id == R.id.profile) {
                            val popUpTo =
                                NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
                            navController.navigate(R.id.action_profile_to_AuthFrag, null, popUpTo)
                        } else navController.navigate(R.id.action_FirstFragment_to_AuthFrag)
                    }
                    else -> {
                        Profile().getProfile(applicationContext).addOnCompleteListener { task ->
                            when {
                                task.isSuccessful -> {
                                    when (task.result?.toObject(Profile::class.java)) {
                                        null -> when (navController.currentDestination?.id) {
                                            R.id.FirstFragment ->
                                                navController.navigate(
                                                    R.id.action_FirstFragment_to_profile,
                                                    null,
                                                    NavOptions.Builder()
                                                        .setPopUpTo(R.id.FirstFragment, true)
                                                        .build()
                                                )
                                        }
//                                        else -> when {
//                                            navController.currentDestination?.id != R.id.FirstFragment -> navController.navigate(
//                                                R.id.action_authFrag_to_FirstFragment
//                                            )
//                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
