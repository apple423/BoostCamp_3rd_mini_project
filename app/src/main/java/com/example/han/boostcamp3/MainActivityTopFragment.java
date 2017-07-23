package com.example.han.boostcamp3;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.han.boostcamp3.data.ShopContract;
import com.google.android.gms.location.places.Place;

/**
 * Created by Han on 2017-07-18.
 */

public class MainActivityTopFragment extends Fragment {

    private OnClickViewInTopFragmentListener onClickViewInTopFragmentListener;
    private boolean oneTime;
    private CardView addressCardView;
    private EditText addressEditText;

    private EditText shopTitleEditText,shopAddressEditText,
            shopPhoneEditText,shopContentEditText;

    private TextView countLengthTextView;

    private Place place;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onClickViewInTopFragmentListener = (OnClickViewInTopFragmentListener)context;

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main_top_fragment,container,false);
        shopTitleEditText = (EditText) view.findViewById(R.id.shop_title_EditText);

        shopAddressEditText = (EditText) view.findViewById(R.id.shop_address_EditText);
        shopAddressEditText.setOnClickListener(onClickListener);

        shopPhoneEditText = (EditText)view.findViewById(R.id.shop_phone_EditText);

        shopContentEditText = (EditText)view.findViewById(R.id.shop_content_EditText);
        countLengthTextView = (TextView)view.findViewById(R.id.textView_count);
        shopContentEditText.addTextChangedListener(textWatcher);


        return view;
    }

    public static MainActivityTopFragment newInstance() {

        Bundle args = new Bundle();

        MainActivityTopFragment fragment = new MainActivityTopFragment();
        fragment.setArguments(args);
        return fragment;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int length = s.length();
            String text = String.format(getString(R.string.set_text_in_count_textview),length);
            countLengthTextView.setText(text);


        }
    };


    public interface OnClickViewInTopFragmentListener{

            void onClickView(int id);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            onClickViewInTopFragmentListener.onClickView(v.getId());

        }
    };

    // PalceAutoComplete에서 place정보를 가져옵니다.
    public Place setPlaceFromMainActivity(Place place){

        this.place = place;

        return place;
    }

    public void setAddressInCardView(String string){

        shopAddressEditText.setText(string);

    }

    // EditText에서 사용자가 입력한 정보를 저장하여 반환
    public ContentValues getShopValues(){

        String title = shopTitleEditText.getText().toString();
        String address = shopAddressEditText.getText().toString();
        double lat = place.getLatLng().latitude;
        double lng = place.getLatLng().longitude;

        String phone = shopPhoneEditText.getText().toString();
        String content = shopContentEditText.getText().toString();

        ContentValues cv = new ContentValues();
        cv.put(ShopContract.ShopEntry.SHOP_TITLE,title);
        cv.put(ShopContract.ShopEntry.SHOP_ADDRESS,address);
        cv.put(ShopContract.ShopEntry.SHOP_LAT,lat);
        cv.put(ShopContract.ShopEntry.SHOP_LNG,lng);
        cv.put(ShopContract.ShopEntry.SHOP_PHONE, phone);
        cv.put(ShopContract.ShopEntry.SHOP_CONTENT, content);

        return cv;
    }

    //EditText를 비워주기 위함
    public void refreshEditText(){

        shopTitleEditText.setText("");
        shopAddressEditText.setText("");
        shopPhoneEditText.setText("");
        shopContentEditText.setText("");
    }
}
