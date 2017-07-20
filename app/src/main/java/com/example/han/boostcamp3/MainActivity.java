package com.example.han.boostcamp3;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

import com.example.han.boostcamp3.data.ShopContract;
import com.example.han.boostcamp3.data.ShopDBHelper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.han.boostcamp3.R.id.map;

public class MainActivity extends AppCompatActivity implements
        MainActivityBottomFragment.NextButtonClickListener,
        MainActivityTopFragment.OnClickViewInTopFragmentListener{
    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;

    private MainActivityTopFragment mainActivityTopFragment;
    private MainActivityBottomFragment mainActivityBottomFragment;
    private MapBottomFragment mapBottomFragment;
    private MapTopFragment mapTopFragment;

    private GoogleApiClient mGoogleApiClient;
    private Place placeForAddress;

    private SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActivityTopFragment = MainActivityTopFragment.newInstance();
        mainActivityBottomFragment = MainActivityBottomFragment.newInstance();
        mapBottomFragment = MapBottomFragment.newInstance();
        //mapFragment = SupportMapFragment.newInstance();
        mapTopFragment = MapTopFragment.newInstance();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.main_top_fragment, mapTopFragment);
        fragmentTransaction.add(R.id.main_bottom_fragment, mapBottomFragment);
        /*fragmentTransaction.add(R.id.main_top_fragment, mainActivityTopFragment);
        fragmentTransaction.add(R.id.main_bottom_fragment, mainActivityBottomFragment);*/
        fragmentTransaction.commit();




        //mapFragment.getMapAsync(this);*/
        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, null)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();*/




        ShopDBHelper shopDBHelper = new ShopDBHelper(this);
        sqLiteDatabase = shopDBHelper.getWritableDatabase();


    }

    @Override
    public void onClickNextButton(int id) {

        switch(id){
            case R.id.next_cardView :
                /*mapFragment = SupportMapFragment.newInstance();
                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);*/

                addShopInfo();
                FragmentTransaction fragmentTransactionNext = getSupportFragmentManager().beginTransaction();
                fragmentTransactionNext.replace(R.id.main_top_fragment,mapTopFragment);
                fragmentTransactionNext.replace(R.id.main_bottom_fragment, mapBottomFragment);
                fragmentTransactionNext.commit();
                mGoogleApiClient.connect();

               /* supportMapFragment = mapTopFragment.getMapFragment();
                supportMapFragment.getMapAsync(this);*/

                break;

        }

    }


    @Override
    public void onClickView(int id) {

        switch (id){

            case R.id.shop_address_EditText :

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

                break;

        }

    }

    // 사용자가 장소를 선택했을 때 장소를 얻어 오기 위함
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                placeForAddress = PlaceAutocomplete.getPlace(this, data);
                mainActivityTopFragment.setAddressInCardView(placeForAddress.getAddress().toString());
                mainActivityTopFragment.setPlaceFromMainActivity(placeForAddress);
                //Log.i("ㄴㅇㅎㅎㄴㅇㄶㅇㅁ", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("ㄸㄲㄲㄲㄲㄲㄲ꺢", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled operation.
            }
        }
    }

    public void addShopInfo(){

        ContentValues cv = mainActivityTopFragment.getShopValues();

        /*ContentValues cv = new ContentValues();
        cv.put(ShopContract.ShopEntry.SHOP_TITLE,title);
        cv.put(ShopContract.ShopEntry.SHOP_ADDRESS,address);
        cv.put(ShopContract.ShopEntry.SHOP_LAT,lat);
        cv.put(ShopContract.ShopEntry.SHOP_LNG,lng);
        cv.put(ShopContract.ShopEntry.SHOP_PHONE, phone);
        cv.put(ShopContract.ShopEntry.SHOP_CONTENT, content);*/

        sqLiteDatabase.insert(ShopContract.ShopEntry.TABLE_NAME,null,cv);
    }

 /*   public Cursor getAllShops(){
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Cursor cursor = getAllShops();

        if(cursor.moveToFirst()){

            do{

                String title = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_TITLE));
                double lat = cursor.getDouble(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_LAT));
                double lng = cursor.getDouble(cursor.getColumnIndex(ShopContract.ShopEntry.SHOP_LNG));
                Log.i("datas : ",title + " &" + lat + " & " + lng);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(title));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lat, lng), 15));

            }while(cursor.moveToNext());
        }
    }*/

}