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
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.zip.Inflater;

import static com.example.han.boostcamp3.R.id.map;

public class MainActivity extends AppCompatActivity implements
        MainActivityBottomFragment.NextButtonClickListener,
        MainActivityTopFragment.OnClickViewInTopFragmentListener,
        MapBottomFragment.NextButtonClickListener,
        MapTopFragment.OnClickFabClickListener{

    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1; // 주변에 1개의 장소만 가져오겠다는 상수
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private MainActivityTopFragment mainActivityTopFragment;
    private MainActivityBottomFragment mainActivityBottomFragment;
    private MapBottomFragment mapBottomFragment;
    private MapTopFragment mapTopFragment;


    private Place placeForAddress; // 사용자가 선택한 장소를 얻어오기 위함



    private SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 툴바와 네비게이션 아이콘 사용

        // 프레그먼트들 생성
        mainActivityTopFragment = MainActivityTopFragment.newInstance();
        mainActivityBottomFragment = MainActivityBottomFragment.newInstance();
        mapBottomFragment = MapBottomFragment.newInstance();
        mapTopFragment = MapTopFragment.newInstance();



        // 프레그먼트 트렌젝션을 통한 초기 화면 구성
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_top_fragment, mapTopFragment);
        fragmentTransaction.add(R.id.main_bottom_fragment, mapBottomFragment);
        fragmentTransaction.commit();

        // DB 생성
        ShopDBHelper shopDBHelper = new ShopDBHelper(this);
        sqLiteDatabase = shopDBHelper.getWritableDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;

    }

    // 맛집 추가에서 다음 버튼을 눌렀을때
    @Override
    public void onClickNextButton(int id) {

        switch(id){
            case R.id.next_cardView :

                addShopInfo(); // DB에 추가
                mainActivityTopFragment.refreshEditText();

                FragmentTransaction fragmentTransactionNext = getSupportFragmentManager().beginTransaction();
                fragmentTransactionNext.replace(R.id.main_top_fragment,mapTopFragment);
                fragmentTransactionNext.replace(R.id.main_bottom_fragment, mapBottomFragment);
                fragmentTransactionNext.commit();


                break;

            case R.id.prev_cardView:

                FragmentTransaction fragmentTransactionPrev = getSupportFragmentManager().beginTransaction();
                fragmentTransactionPrev.replace(R.id.main_top_fragment,mapTopFragment);
                fragmentTransactionPrev.replace(R.id.main_bottom_fragment, mapBottomFragment);
                fragmentTransactionPrev.commit();


        }

    }

    // 추가 화면에서 주소를 눌렀을 시에 자동 완성 플레이스를 띄우는 과정
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
                mainActivityTopFragment.setAddressInCardView(placeForAddress.getAddress().toString());// 주소 입력 EditText를 가져온 주소로 채웁니다.
                mainActivityTopFragment.setPlaceFromMainActivity(placeForAddress);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("ㄸㄲㄲㄲㄲㄲㄲ꺢", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled operation.
            }
        }
    }

    // Floating action Button을 누르면 사용자 입력 화면이 뜨도록 하기 위한 함수
    public void onClickFab(){

        FragmentTransaction fragmentTransactionNext = getSupportFragmentManager().beginTransaction();
        fragmentTransactionNext.replace(R.id.main_top_fragment,mainActivityTopFragment);
        fragmentTransactionNext.replace(R.id.main_bottom_fragment, mainActivityBottomFragment);
        fragmentTransactionNext.commit();

    }

    // 가게 정보 입력 화면에서 얻어 온 데이터를 DB에 저장
    public void addShopInfo(){

        ContentValues cv = mainActivityTopFragment.getShopValues();

        sqLiteDatabase.insert(ShopContract.ShopEntry.TABLE_NAME,null,cv);
    }


    // 저장 된 데이터를 통해 다음 장소를 보여주는 함수
    @Override
    public void onClickMapNextButton(int id) {

        if(id == R.id.map_next_cardView){


            mapTopFragment.searchNextLocation();
        }

    }
}
