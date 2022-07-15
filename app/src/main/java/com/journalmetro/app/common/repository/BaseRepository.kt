package com.journalmetro.app.common.repository

import com.journalmetro.app.common.data.DataSource
import com.journalmetro.app.common.data.model.Model

abstract class BaseRepository<M : Model> {
    protected abstract val dataSource: DataSource
}

