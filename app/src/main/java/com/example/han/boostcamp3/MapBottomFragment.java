package com.example.han.boostcamp3;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Han on 2017-07-18.
 */

public class MapBottomFragment extends Fragment implements View.OnClickListener{

    private NextButtonClickListener nextButtonClickListener;
    CardView map_next_CardView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        nextButtonClickListener = (NextButtonClickListener)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.map_bottom_fragment,container,false);

        map_next_CardView = (CardView) view.findViewById(R.id.map_next_cardView);
        map_next_CardView.setOnClickListener(onClickListener);
        return view;
    }

    public static MapBottomFragment newInstance() {

        Bundle args = new Bundle();

        MapBottomFragment fragment = new MapBottomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {

    }

    public interface NextButtonClickListener{
        void onClickMapNextButton(int id);
    }


    View.OnClickListener onClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            nextButtonClickListener.onClickMapNextButton(v.getId());
        }
    };
}
