package com.mrkazofficial.mdiskdownloader.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mrkazofficial.mdiskdownloader.R
import com.mrkazofficial.mdiskdownloader.dialogs.DialogLoading
import com.mrkazofficial.mdiskdownloader.utils.Utils
import com.mrkazofficial.mdiskdownloader.utils.Utils.videoId
import com.mrkazofficial.mdiskdownloader.viewmodels.VideoDataViewModel
import kotlinx.coroutines.launch


class DashboardFragment : Fragment() {

    private lateinit var mActivity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val etUrl = view.findViewById<EditText>(R.id.etUrl)
        val imgRocket = view.findViewById<ImageView>(R.id.imgRocket)

        lifecycleScope.launch {
            Glide.with(mActivity)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .load(R.drawable.rocket)
                .into(imgRocket)
        }

        view.findViewById<ImageView>(R.id.imagePaste).setOnClickListener {
            Utils.pasteFromClipboard(context = mActivity).let {
                if (it != null) {
                    if (it.contains("mdisk") || it.contains("mdisk.me") || it.contains("mdisk.me/convertor"))
                        etUrl.text = it.toEditable()
                    else Toast.makeText(
                        mActivity,
                        getString(R.string.please_add_valid_url),
                        Toast.LENGTH_SHORT
                    ).show()
                } else Toast.makeText(
                    mActivity,
                    getString(R.string.clipboard_empty_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val loadingDialog = DialogLoading(mContext = requireContext())
        loadingDialog.setMessage(getString(R.string.dialog_txt_loading))
        loadingDialog.setCancelable(false)

        val viewModel = ViewModelProvider(this)[VideoDataViewModel::class.java]

        view.findViewById<ImageView>(R.id.btnDownload).setOnClickListener {
            if (etUrl.text != null) {
                if (etUrl.text.isNotEmpty()) {
                    val url = etUrl.text.toString()
                    viewModel.viewModelScope.launch {
                        loadingDialog.show()
                        viewModel.videoData.observe(viewLifecycleOwner) {
                            if (it != null) {
                                val bottomDetails = BottomSheetDetails()
                                bottomDetails.videoDataModel = it
                                bottomDetails.show(
                                    (mActivity as AppCompatActivity).supportFragmentManager,
                                    null
                                )
                                etUrl.text.clear()
                            }
                            loadingDialog.dismiss()
                        }
                    }
                    viewModel.getVideoData(videoId = url.videoId)
                }
            }

            if (etUrl.text == null) {
                Toast.makeText(mActivity, getString(R.string.please_add_url), Toast.LENGTH_SHORT)
                    .show()
            } else if (etUrl.text.isEmpty()) {
                Toast.makeText(mActivity, getString(R.string.please_add_url), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onAttach(context: Context) {
        if (context is Activity)
            this.mActivity = context
        super.onAttach(context)
    }
}