package com.testtube.gstreporter.views.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.testtube.gstreporter.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener(NavController.OnDestinationChangedListener
        { controller, destination, arguments ->
            when (destination.id) {
                R.id.SecondFragment -> supportActionBar?.setTitle(
                    getString(R.string.new_sale)
                )
                else -> supportActionBar?.setTitle(getString(R.string.app_name))
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        if (FirebaseAuth.getInstance().currentUser != null)
            finish()
        else onBackPressed()
        return true;
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                if (navController.currentDestination?.id == R.id.FirstFragment) {
                    FirebaseAuth.getInstance().signOut()
                    var intent = Intent(applicationContext, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
