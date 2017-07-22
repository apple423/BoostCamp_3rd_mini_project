package com.example.han.boostcamp3;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.TextView;

import com.example.han.boostcamp3.data.ShopContract;
import com.example.han.boostcamp3.data.ShopDBHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Han on 2017-07-20.
 */

public class MapTopFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMyLocationButtonClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MAX_RESULT_OF_ADDRESS = 1;
    private static final int DEFAULT_ZOOM = 15;
    private boolean mLocationPermissionGranted;

    SupportMapFragment mapFragment;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    private GoogleMap mMap;
    TextView mapAddressTextView;
    Geocoder geocoder;
    Address address;
    List<Address> List;
    LatLng latLng;
    GoogleApiClient mGoogleApiClient;
    Location mLastKnownLocation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_top_fragment, container, false);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), null)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();


        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        ShopDBHelper shopDBHelper = new ShopDBHelper(getContext());
        sqLiteDatabase = shopDBHelper.getReadableDatabase();
        //cursor = getAllShops();

        if (mapFragment != null) {

            mapFragment.getMapAsync(this);
        }
        mapAddressTextView = (TextView) view.findViewById(R.id.map_address_textView);
        geocoder = new Geocoder(getContext(), Locale.KOREA);
        return view;
    }

    public static MapTopFragment newInstance() {

        Bundle args = new Bundle();

        MapTopFragment fragment = new MapTopFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        cursor = getAllShops();
        String address;
        double lat;
        double lng;


        if (cursor.moveToFirst()) {

            do {

                String title = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_TITLE));
                address = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_ADDRESS));
                lat = cursor.getDouble(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_LAT));
                lng = cursor.getDouble(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_LNG));
                Log.i("datas : ", title + " &" + lat + " & " + lng);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(title)
                        .draggable(true));

            } while (cursor.moveToNext());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lat, lng), 15));

            mapAddressTextView.setText(address);
        }

        mMap.setOnMarkerDragListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        checkMyLocation();

    }

    public Cursor getAllShops() {
        return sqLiteDatabase.query(
                ShopContract.ShopEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        latLng = marker.getPosition();

        String addressString = getCurrentAddress(latLng.latitude, latLng.longitude);

        mapAddressTextView.setText(addressString);

    }

    public String getCurrentAddress(double lat, double lng) {

        try {
            List = geocoder.getFromLocation(lat, lng, MAX_RESULT_OF_ADDRESS);
            address = List.get(0);
            List.clear();

            return address.getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    public void checkMyLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        if (mLocationPermissionGranted) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
        } else {

            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }


    }

    @Override
    public boolean onMyLocationButtonClick() {

        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        if (mLocationPermissionGranted) {

            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        String string = getCurrentAddress(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        mapAddressTextView.setText(string);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLatitude()))
                .title("current Location")
                .draggable(true));

        return true;
    }


    public void searchNextLocation() {

        double lat;
        double lng;

        if (cursor.isAfterLast()||cursor.isLast()) {

            cursor.moveToFirst();
            Log.d("MoveToFirst","yyy");

        } else {

            cursor.moveToNext();
            Log.d("MoveToNext","nextnextnext");
        }

        lat = cursor.getDouble(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_LAT));
        lng = cursor.getDouble(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_LNG));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lng), 15));

        String addressString = getCurrentAddress(lat,lng);
        mapAddressTextView.setText(addressString);

    }
}
