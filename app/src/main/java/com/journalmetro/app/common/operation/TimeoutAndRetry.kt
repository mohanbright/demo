package com.journalmetro.app.common.operation

interface TimeoutAndRetry {
    val numberOfRetries : Int
    val timeoutDuration : Int
}