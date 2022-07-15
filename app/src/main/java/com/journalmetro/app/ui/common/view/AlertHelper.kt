package com.journalmetro.app.ui.common.view

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class AlertHelper {

    companion object {
        fun showAlert(context: Context, title: String?, message: String,listener: DialogCallBackListener) {
            val builder = AlertDialog.Builder(context)
            if (title != null)
                builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
                listener.onPositionButtonPress()
                dialog.dismiss()
            }

            builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                listener.onNegativeButtonPress()
                dialog.dismiss()
            }

            builder.show()
        }

        fun showToastShort(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun showToastLong(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}