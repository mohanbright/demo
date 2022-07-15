package com.journalmetro.app.common.operation

interface ExecutableOperation {

    val operationName: String

    suspend fun execute() : Boolean

}