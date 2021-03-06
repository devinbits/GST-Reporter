package com.testtube.gstreporter.views.frag

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.testtube.gstreporter.R
import com.testtube.gstreporter.utils.Common

/**
 * A simple [Fragment] subclass.
 * Use the [AuthFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class AuthFrag : Fragment() {

    private val sResult: Int = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AuthFrag().apply {

            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.button_sign_in).setOnClickListener(View.OnClickListener {
            startAuth()
        })
    }

    private fun startAuth() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher_round)
                .build(),
            sResult
        )
        Common.showToast(context,"Create account. Please Wait...")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == sResult) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Common.showToast(context, " Welcome ${user?.displayName}")
                findNavController().navigate(
                    R.id.action_AuthFrag_to_profile
                )
            } else {
                Common.showToast(context, "Failed- ${response?.error?.errorCode}")
            }
        }
    }

}
