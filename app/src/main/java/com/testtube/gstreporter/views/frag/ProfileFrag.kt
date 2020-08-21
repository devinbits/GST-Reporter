package com.testtube.gstreporter.views.frag

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.Data.Builder
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.testtube.gstreporter.R
import com.testtube.gstreporter.firestoreController.ProfileAdapter
import com.testtube.gstreporter.model.Profile
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.utils.Constants
import com.testtube.gstreporter.workers.FirebaseStorageFileUpload
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFrag : Fragment() {

    private var avatarImageTempPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        context?.let {
            ProfileAdapter(it).getProfile().addOnSuccessListener {
                val profile = it?.toObject(Profile::class.java) ?: null
                if (profile != null) {
                    setProfile(profile)
                }else
                    progress_circular.visibility = View.GONE
            }
        }
    }

    private fun setProfile(profile: Profile) {
        input_name.setText(profile.name)
        input_firm_name.setText(profile.firmName)
        input_gst_number.setText(profile.gstNumber)
        input_state.setText(profile.state)

        Firebase.storage.reference.child("${Common.getUser()}/avatar.jpg").downloadUrl.addOnSuccessListener {
            Glide.with(this /* context */)
                .load(it)
                .centerCrop()
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(avatar_image)
        }
    }

    private fun initViews(view: View) {
//        val states = resources.getStringArray(R.array.states)
//        context?.let {
//            val stateAutoCompleteAdapter = StateAutoCompleteAdapter(it, states.toList())
//            val autoCompleteState = view.input_state
//            autoCompleteState.setAdapter(stateAutoCompleteAdapter)
//            autoCompleteState.setOnClickListener { _ -> autoCompleteState.showDropDown() }
//            autoCompleteState.setOnItemClickListener { adapterView, _, i, _ ->
//                selectedState = adapterView.getItemAtPosition(i) as String
//            }
//        }

        (input_gst_number as TextInputEditText).addTextChangedListener { text: Editable? ->
            val len = text?.length ?: 0
            val states = Constants.States.values()
            if (len in 1..2) {
                when (val code = text.toString().toIntOrNull()) {
                    null -> input_state.setText(states[0].name)
                    else -> {
                        if (code in 1..states.size)
                            input_state.setText(states[code].name.replace("_", " "))
                        else
                            input_state.setText(states[0].name)
                    }
                }
            } else
                input_state.setText(states[0].name)
        }

        view.avatar_image.setOnClickListener {
            avatarImageTempPath = Common.startPictureCaptureIntentFragment(this, 0)
        }

        view.save.setOnClickListener { saveProfile() }

    }

    private fun saveProfile() {
        val name = view?.input_name?.text.toString()
        val firmName = view?.input_firm_name?.text.toString()
        val gstNumber = view?.input_gst_number?.text.toString()
        val state = view?.input_state?.text.toString()

        if (name.isEmpty()) {
            input_name.error = getString(R.string.required)
            return
        } else if (firmName.isEmpty()) {
            input_firm_name.error = getString(R.string.required)
            return
        } else if (gstNumber.isEmpty()) {
            input_gst_number.error = getString(R.string.required)
            return
        } else if (state.isEmpty()) {
            input_gst_number.error = getString(R.string.required)
            return
        }

        context?.let { it ->
            val profile =
                Profile(name, firmName, gstNumber, state, "").saveProfile(it)
            if (avatarImageTempPath != null) {
                val data = Builder()
                    .putString("path", avatarImageTempPath)
                    .putBoolean("is-avatar", true)
                    .build();
                val req = OneTimeWorkRequestBuilder<FirebaseStorageFileUpload>()
                    .setInputData(data)
                    .build()
                WorkManager.getInstance(it).enqueue(req)
            }
        }
        findNavController().navigateUp();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (context != null && avatarImageTempPath != null) {
                Glide.with(this)
                    .load(avatarImageTempPath)
                    .centerCrop()
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(avatar_image)
            }
        } else Common.showToast(context, R.string.image_capture_error)
    }
}