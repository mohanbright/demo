package com.journalmetro.app.common.util

/**
 * Created by App Developer on August/2021.
 */

fun Boolean?.isSafelyTrue(): Boolean {
    return this != null && this == true
}