package com.example.mobiluygulamaprojesi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mobiluygulamaprojesi.databinding.FragmentIletisimBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class IletisimFragment : Fragment() {

    private lateinit var design: FragmentIletisimBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design = FragmentIletisimBinding.inflate(layoutInflater,container,false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
            val noctuaKonum = LatLng(40.7662, 29.9196)
            googleMap.addMarker(MarkerOptions().position(noctuaKonum)
                .title("Noctua Kitabevi"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(noctuaKonum, 15f))
            googleMap.mapType = com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
        }

        return design.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

}