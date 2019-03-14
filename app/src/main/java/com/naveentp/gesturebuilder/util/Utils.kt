package com.naveentp.gesturebuilder.util

import android.os.Environment
import java.io.File

/**
 * @author Naveen T P
 * @since 14/03/19
 */

object Utils {
    fun getGestureFile(): File =
        File(Environment.getExternalStorageDirectory(), "/gesture/gesture.txt")
}