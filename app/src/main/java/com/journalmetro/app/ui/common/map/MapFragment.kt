package com.journalmetro.app.ui.common.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import com.journalmetro.app.R
import com.journalmetro.app.databinding.FragmentMapBinding
import com.journalmetro.app.ui.common.fragment.BaseFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.MapStyleOptions


class MapFragment : BaseFragment<FragmentMapBinding, MapViewModel>() {

    override var viewModelBindingVariable: Int = BR.viewModel
    override var layoutResId: Int = R.layout.fragment_map

    var googleMapFragment: SupportMapFragment? = null

    private var overlays = arrayListOf<GroundOverlay>()

    var markerClickListener : GoogleMap.OnMarkerClickListener? = null
    var cameraMoveListener: GoogleMap.OnCameraMoveListener? = null

    override fun provideViewModel(): MapViewModel {
        return ViewModelProvider(
            this
        ).get(MapViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        googleMapFragment = SupportMapFragment()
        val fm = childFragmentManager
        fm.beginTransaction()
            .replace(R.id.google_map, googleMapFragment!!).commit()
        googleMapFragment?.getMapAsync { googleMap ->
            googleMap.uiSettings.isTiltGesturesEnabled = false
            googleMap.uiSettings.isCompassEnabled = true
            googleMap.isIndoorEnabled = false
            googleMap.uiSettings.isMapToolbarEnabled = false
            applyMarkerClickListener(googleMap)
            applyCameraMoveListener(googleMap)
            setNoMarkersForAllPOIs(googleMap)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutBinding.btnRecenter.setOnClickListener {
            recenterMap()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        markerClickListener = null
        cameraMoveListener = null
    }

    fun setOnMarkerClickListener(listener: GoogleMap.OnMarkerClickListener) {
        markerClickListener = listener
    }

    fun setOnCameraMoveListener(listener: GoogleMap.OnCameraMoveListener) {
        cameraMoveListener = listener
    }

    fun recenterMapWithNewPosition(cameraPosition: CameraPosition) {
        viewModel.centerPosition = cameraPosition
        recenterMap()
    }

    private fun recenterMap() {
        if (viewModel.centerPosition != null) {
            googleMapFragment?.getMapAsync { googleMap ->
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(viewModel.centerPosition))
            }
        }
    }

    fun recenterMapWithNewPositionWithoutAnimation(cameraPosition: CameraPosition) {
        viewModel.centerPosition = cameraPosition
        recenterMapWithoutAnimation()
    }

    private fun recenterMapWithoutAnimation() {
        if (viewModel.centerPosition != null) {
            googleMapFragment?.getMapAsync { googleMap ->
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(viewModel.centerPosition))
            }
        }
    }

    private fun applyMarkerClickListener(googleMap: GoogleMap) {
        if (markerClickListener != null) {
            googleMap.setOnMarkerClickListener(markerClickListener)
        }
    }

    private fun applyCameraMoveListener(googleMap: GoogleMap) {
        if (cameraMoveListener != null) {
            googleMap.setOnCameraMoveListener(cameraMoveListener)
        }
    }

    private fun setNoMarkersForAllPOIs(googleMap: GoogleMap) {
        val mapStyleNoMarkersForAllPOIs = "[" +
                "  {" +
                "    \"featureType\": \"poi\"," +
                "    \"elementType\": \"all\"," +
                "    \"stylers\": [" +
                "      {" +
                "        \"visibility\": \"off\"" +
                "      }" +
                "    ]" +
                "  }," +
                "  {" +
                "    \"featureType\": \"transit\"," +
                "    \"elementType\": \"labels.icon\"," +
                "    \"stylers\": [" +
                "      {" +
                "        \"visibility\": \"off\"" +
                "      }" +
                "    ]" +
                "  }" +
                "]"

        googleMap.setMapStyle(MapStyleOptions(mapStyleNoMarkersForAllPOIs))
    }

}