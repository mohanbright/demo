package com.journalmetro.app.ui.common.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapViewModel @Inject constructor(
) :
    ViewModel() {

    var centerPosition : CameraPosition? = null
}