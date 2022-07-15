package com.journalmetro.app.common.data.mapping

interface Mapper<I, O> {
    fun map(input: I): O

    fun mapList(listOfInputs: List<I>?): List<O> {
        return listOfInputs?.map { map(it) } ?: listOf()
    }
}