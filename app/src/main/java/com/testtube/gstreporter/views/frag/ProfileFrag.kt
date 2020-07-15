package com.testtube.gstreporter.views.frag

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.work.Data.Builder
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.testtube.gstreporter.R
import com.testtube.gstreporter.firestoreController.ProfileAdapter
import com.testtube.gstreporter.model.Profile
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.views.adapters.StateAutoCompleteAdapter
import com.testtube.gstreporter.workers.FirebaseStorageFileUpload
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFrag : Fragment() {

    var selectedState: String? = null
    var avatarImageTempPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initviews(view)
        context?.let {
            ProfileAdapter(it).getProfile().addOnSuccessListener {
                val profile = it?.toObject(Profile::class.java)
                setProfile(profile!!)
            }
        }
    }

    private fun setProfile(profile: Profile) {
        input_name.setText(profile.name)
        input_firm_name.setText(profile.firmName)
        input_gst_number.setText(profile.gstNumber)
        input_state.setText(profile.state)
    }

    private fun initviews(view: View) {
        val states = resources.getStringArray(R.array.states)
        context?.let {
            val stateAutoCompleteAdapter = StateAutoCompleteAdapter(it, states.toList())
            val autoCompleteState = view.input_state
            autoCompleteState.setAdapter(stateAutoCompleteAdapter)
            autoCompleteState.setOnClickListener { _ -> autoCompleteState.showDropDown() }
            autoCompleteState.setOnItemClickListener { adapterView, view, i, l ->
                selectedState = adapterView.getItemAtPosition(i) as String
            }
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

        if (name.isEmpty()) {
            input_name.error = getString(R.string.required)
            return
        } else if (firmName.isEmpty()) {
            input_firm_name.error = getString(R.string.required)
            return
        } else if (gstNumber.isEmpty()) {
            input_gst_number.error = getString(R.string.required)
            return
        }

        context?.let { it ->
            val profile =
                Profile(name, firmName, gstNumber, selectedState.toString(), "").saveProfile(it)
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (context != null && avatarImageTempPath != null) {
                avatar_image.setImageBitmap(
                    Common.getCircularCroppedImage(
                        Common.grabBitMapfromFileAsync(context, avatarImageTempPath)!!
                    )
                )
            }
        } else Common.showToast(context, R.string.image_capture_error)
    }
}