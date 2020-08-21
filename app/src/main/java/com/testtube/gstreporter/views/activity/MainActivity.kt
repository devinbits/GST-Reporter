package com.testtube.gstreporter.views.activity

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.testtube.gstreporter.R
import com.testtube.gstreporter.model.Profile

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
                R.id.SecondFragment -> supportActionBar?.setTitle(
                    getString(R.string.new_sale)
                )
                R.id.AuthFrag -> {
                    supportActionBar?.hide()
                }
                else -> {
                    supportActionBar?.setTitle(getString(R.string.app_name))
                }
            }
        })

        FirebaseAuth.getInstance().addAuthStateListener {
            when (it.currentUser) {
                null -> {
                    navController.navigate(R.id.action_FirstFragment_to_AuthFrag)
                }
                else -> {
                    Profile().getProfile(applicationContext).addOnCompleteListener {
                        when {
                            it.isSuccessful -> {
                                val profile = it.result?.toObject(Profile::class.java)
                                when (profile) {
                                    null -> when (navController.currentDestination?.id) {
                                        R.id.FirstFragment ->
                                            navController.navigate(
                                            R.id.action_FirstFragment_to_profile,null,NavOptions.Builder().setPopUpTo(R.id.FirstFragment,true).build()
                                        )
                                        R.id.AuthFrag -> Handler().postDelayed({
                                            navController.navigate(
                                                R.id.action_AuthFrag_to_profile
                                            )
                                        }, 2000)

                                    }
                                    else -> when {
                                        navController.currentDestination?.id != R.id.FirstFragment -> navController.navigate(
                                            R.id.action_authFrag_to_FirstFragment
                                        )
                                    }
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
        return true;
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
//            R.id.action_settings -> {
//                Prefs.clearPrefs(applicationContext)
//                FirebaseAuth.getInstance().signOut()
//                return true
//            }
            R.id.action_profile -> {
                navController.navigate(R.id.action_FirstFragment_to_profile)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
