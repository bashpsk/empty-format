package io.bashpsk.emptyformat

import android.util.Log

internal object SetLog {

    private const val LOG_TAG = "EMPTY-FORMAT"

    fun setDebug(message: String) {

        Log.d(LOG_TAG, message)
    }

    fun setDebug(message: String?, throwable: Throwable?) {

        Log.d(LOG_TAG, message, throwable)
    }

    fun setError(message: String) {

        Log.e(LOG_TAG, message)
    }

    fun setError(message: String?, throwable: Throwable?) {

        Log.e(LOG_TAG, message, throwable)
    }

    fun setInfo(message: String) {

        Log.i(LOG_TAG, message)
    }

    fun setInfo(message: String?, throwable: Throwable?) {

        Log.i(LOG_TAG, message, throwable)
    }

    fun setVerbose(message: String) {

        Log.v(LOG_TAG, message)
    }

    fun setVerbose(message: String?, throwable: Throwable?) {

        Log.v(LOG_TAG, message, throwable)
    }

    fun setWarning(message: String) {

        Log.w(LOG_TAG, message)
    }

    fun setWarning(throwable: Throwable?) {

        Log.w(LOG_TAG, throwable)
    }

    fun setWarning(message: String?, throwable: Throwable?) {

        Log.w(LOG_TAG, message, throwable)
    }
}