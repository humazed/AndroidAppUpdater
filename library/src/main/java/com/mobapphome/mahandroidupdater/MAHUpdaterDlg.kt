package com.mobapphome.mahandroidupdater

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.mobapphome.mahandroidupdater.commons.setControllerFont
import com.mobapphome.mahandroidupdater.tools.Constants
import com.mobapphome.mahandroidupdater.tools.DlgModeEnum
import com.mobapphome.mahandroidupdater.tools.ProgramInfo
import kotlinx.android.synthetic.main.mah_updater_dlg.*

class MAHUpdaterDlg : DialogFragment() {

    private var programInfo: ProgramInfo? = null
    private var type: DlgModeEnum? = null
    private var btnInfoVisibility: Boolean = false
    private var btnInfoMenuItemTitle: String? = null
    private var btnInfoActionURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MAHUpdaterDlg)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "MAH Dld exit Created ")
        val arg = arguments
        val gson = Gson()
        programInfo = gson.fromJson(arg?.getString("programInfo"), ProgramInfo::class.java)
        type = arg!!.getSerializable("type") as DlgModeEnum
        btnInfoVisibility = arg.getBoolean("btnInfoVisibility")
        btnInfoMenuItemTitle = arg.getString("btnInfoMenuItemTitle")
        btnInfoActionURL = arg.getString("btnInfoActionURL")

        Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "Updateinfo from bundle " + programInfo?.updateInfo)

        dialog.window!!.attributes.windowAnimations = R.style.MAHUpdaterDialogAnimation
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnKeyListener { dialog, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                onNo()
            }
            false
        }

        return inflater.inflate(R.layout.mah_updater_dlg, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUpdateInfo.text = programInfo?.updateInfo
        btnDontUpdate.text = resources.getText(R.string.mah_android_upd_dlg_btn_no_later_txt)

        tvUpdateInfo.visibility = if (programInfo?.updateInfo != null) View.VISIBLE else View.GONE

        btnUpdate.setOnClickListener { onYes() }
        btnDontUpdate.setOnClickListener { onNo() }
        imgBtnCancel.setOnClickListener { onNo() }

        when (type) {
            DlgModeEnum.UPDATE -> {
                btnUpdate.text = resources.getText(R.string.cmnd_verb_mah_android_upd_dlg_btn_yes_update_txt)
                tvInfoTxt.text = resources.getText(R.string.mah_android_upd_updater_info_update)
            }
            DlgModeEnum.INSTALL -> {
                btnUpdate.text = resources.getText(R.string.cmnd_verb_mah_android_upd_dlg_btn_yes_install_txt)
                tvInfoTxt.text = resources.getText(R.string.mah_android_upd_updater_info_install)
            }
            DlgModeEnum.TEST -> {
                btnUpdate.text = resources.getText(R.string.cmnd_verb_mah_android_upd_dlg_btn_yes_update_txt)
                tvInfoTxt.text = resources.getText(R.string.mah_android_upd_updater_info_update)
            }
            else -> {
            }
        }

        tvTitle.setControllerFont()
        tvInfoTxt.setControllerFont()
        btnUpdate.setControllerFont()
        btnDontUpdate.setControllerFont()
    }

    fun onYes() = when (type) {
        DlgModeEnum.TEST -> {
        }
        else -> if (!programInfo?.uriCurrent!!.isEmpty()) {
            val marketIntent = Intent(Intent.ACTION_VIEW)
            marketIntent.data = Uri.parse("market://details?id=" + programInfo?.uriCurrent)
            try {
                requireActivity().startActivity(marketIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, getString(R.string.mah_android_upd_play_service_not_found), Toast.LENGTH_LONG).show()
                Log.e(Constants.MAH_ANDROID_UPDATER_LOG_TAG, getString(R.string.mah_android_upd_play_service_not_found) + e.message)
            }
        } else {

        }
    }


    fun onNo() = dismissAllowingStateLoss()


    private fun showMAHlib() =
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(btnInfoActionURL))
                requireContext().startActivity(browserIntent)
            } catch (nfe: ActivityNotFoundException) {
                val str = "You haven't set correct url to btnInfoActionURL, your url = " + btnInfoActionURL
                Toast.makeText(context, str, Toast.LENGTH_LONG).show()
                Log.d(Constants.MAH_ANDROID_UPDATER_LOG_TAG, str, nfe)
            }


    companion object {

        fun newInstance(programInfo: ProgramInfo,
                        type: DlgModeEnum,
                        btnInfoVisibility: Boolean,
                        btnInfoMenuItemTitle: String?,
                        btnInfoActionURL: String?): MAHUpdaterDlg {
            val dialog = MAHUpdaterDlg()

            val args = Bundle()
            val gson = Gson()
            args.putString("programInfo", gson.toJson(programInfo))
            args.putSerializable("type", type)
            args.putBoolean("btnInfoVisibility", btnInfoVisibility)
            args.putString("btnInfoMenuItemTitle", btnInfoMenuItemTitle)
            args.putString("btnInfoActionURL", btnInfoActionURL)
            dialog.arguments = args
            return dialog
        }
    }
}