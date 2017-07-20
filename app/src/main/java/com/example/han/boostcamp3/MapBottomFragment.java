package com.example.han.boostcamp3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Han on 2017-07-18.
 */

public class MapBottomFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_bottom_fragment,container,false);

        return view;
    }

    public static MapBottomFragment newInstance() {

        Bundle args = new Bundle();

        MapBottomFragment fragment = new MapBottomFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
