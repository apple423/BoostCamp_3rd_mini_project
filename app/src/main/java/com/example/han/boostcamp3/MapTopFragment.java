package com.example.han.boostcamp3;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.Button;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1; //
    private static final int MAX_RESULT_OF_ADDRESS = 1; // 주변에서 얻어올 주소의 수
    private static final int DEFAULT_ZOOM = 15; // 카메라 줌
    private boolean mLocationPermissionGranted = false; // Loation permission이 부여 되었나를 확인하기 위한 flag
    private boolean isGoogleClientCreated = false; // 중복 생성되면 에러가 발생하기에 선언한 flag입니다.

    SupportMapFragment mapFragment;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    private GoogleMap mMap;
    TextView mapAddressTextView;
    Geocoder geocoder;
    OnClickFabClickListener onClickFabClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onClickFabClickListener = (OnClickFabClickListener)context;
    }

    Address address;
    List<Address> List;
    LatLng latLng;
    GoogleApiClient mGoogleApiClient;
    Location mLastKnownLocation;
    FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_top_fragment, container, false);

        if(!isGoogleClientCreated) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .enableAutoManage(getActivity(), null)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();

            isGoogleClientCreated = true;

        }
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        ShopDBHelper shopDBHelper = new ShopDBHelper(getContext());
        sqLiteDatabase = shopDBHelper.getReadableDatabase();
        //cursor = getAllShops();

        if (mapFragment != null) {

            mapFragment.getMapAsync(this);
        }

        mapAddressTextView = (TextView) view.findViewById(R.id.map_address_textView);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(onClickFabListener);
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

        // DB에 정보가 있을시 커서를 움직이면서 마커를 추가합니다.
        if(cursor !=null) {
            if (cursor.moveToFirst()) {

                do {

                    String title = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_TITLE));
                    address = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_ADDRESS));
                    lat = cursor.getDouble(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_LAT));
                    lng = cursor.getDouble(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_LNG));
                    String content = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_CONTENT));
                    Log.i("datas : ", title + " &" + lat + " & " + lng);

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(title)
                            .snippet(content)
                            .draggable(true));

                } while (cursor.moveToNext());

                // 마커 추가후 마지막 추가 마커에 카메라를 위치시킵니다.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lat, lng), 15));

                mapAddressTextView.setText(address);
            }
        }

        // 리스너 및 오른쪽 상단의 자기위치 버튼과 오른쪽 하단에 + - 버튼을 추가합니다.
        mMap.setOnMarkerDragListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        checkMyLocation();

    }

    // 조건없이 shop 테이블의 모든 정보를 가져옵니다.
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

        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        latLng = marker.getPosition();

        String addressString = getCurrentAddress(latLng.latitude, latLng.longitude);

        mapAddressTextView.setText(addressString);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latLng.latitude,
                        latLng.longitude) ,DEFAULT_ZOOM));
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

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
        }

        else {
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
        String address = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_ADDRESS));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lng), 15));

        //String addressString = getCurrentAddress(lat,lng);
        mapAddressTextView.setText(address);

    }

    public interface OnClickFabClickListener{

        void onClickFab();
    }

    Button.OnClickListener onClickFabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            onClickFabClickListener.onClickFab();

        }
    };




}
