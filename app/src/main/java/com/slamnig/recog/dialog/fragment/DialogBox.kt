package com.slamnig.recog.dialog.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.slamnig.recog.dialog.data.DialogData
import com.slamnig.recog.dialog.listener.DialogListener
import com.slamnig.recog.dialog.viewmodel.DialogViewModel

open class DialogBox(
    val viewModel: DialogViewModel,
) : DialogFragment()
{
    private val LOGTAG = this.javaClass.simpleName

    companion object {
        const val TAG = "DialogBox"
    }

    private var dialogView: ComposeView? = null

    val showObserver: Observer<Boolean> = Observer<Boolean> { show ->
        if(show == false) {
            dismiss()
            Log.d(LOGTAG, "showObserver - dismiss")
        }
    }

    fun show(activity: FragmentActivity, data: DialogData, listener: DialogListener? = null)
    {
        viewModel.showDialog(data, listener)
        super.show(activity.getSupportFragmentManager(), TAG)
    }

    open fun createComposeView(viewModel: DialogViewModel): ComposeView?
    {
        return null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog
    {
        Log.d(LOGTAG, "onCreateDialog()")

        // store dialog ref to get lifecycle owner in onStart(),
        // because onCreateView is never called:
        dialogView = createComposeView(viewModel)

        val dlg = Dialog(requireContext()).apply {
            dialogView?.let { setContentView(it) }
        }

        // make default dialog background transparent:
        dlg.getWindow()?.apply {
            try {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                Log.d(LOGTAG, "onCreateDialog()", e)
            }
        }

        return dlg
    }

    override fun onStart()
    {
        super.onStart()
        //Log.d(LOGTAG, "onStart()")

        // get lifecycle owner here, from dialog, and start observing
        // show state - because onCreateView() is never called:
        dialogView?.apply{
            findViewTreeLifecycleOwner()?.let { owner ->
                Log.d(LOGTAG, "onStart() - LcOwner found")
                viewModel.show.observe(owner, showObserver)
            }
        }
    }

    override fun onCancel(dialog: DialogInterface)
    {
        //Log.d(LOGTAG, "onCancel()")
        viewModel.onDialogCancel()
        super.onCancel(dialog)
    }
}