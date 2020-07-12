package com.testtube.gstreporter.views.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.testtube.gstreporter.R
import com.testtube.gstreporter.utils.Common
import kotlinx.android.synthetic.main.fragment_image_viewer.view.*

class ImageViewerFrag : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: ImageViewerFragArgs by navArgs()
        val filePath: String = args.filePath as String
        val bitmap = Common.grabBitMapfromFileAsync(context, filePath)
        view.imageView.setImageBitmap(bitmap)
    }

}