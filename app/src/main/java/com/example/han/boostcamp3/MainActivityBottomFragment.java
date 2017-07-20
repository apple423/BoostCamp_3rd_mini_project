package com.example.han.boostcamp3;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Han on 2017-07-18.
 */

public class MainActivityBottomFragment extends Fragment {

    private NextButtonClickListener nextButtonClickListener;
    private CardView nextCardView;
    private boolean oneTime = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nextButtonClickListener = (NextButtonClickListener)context;

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main_bottom_fragment,container,false);
        nextCardView = (CardView) view.findViewById(R.id.next_cardView);
        nextCardView.setOnClickListener(onClickListener);

        return view;

    }

    public static MainActivityBottomFragment newInstance() {

        Bundle args = new Bundle();

        MainActivityBottomFragment fragment = new MainActivityBottomFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public interface NextButtonClickListener{
        void onClickNextButton(int id);
    }


    View.OnClickListener onClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            nextButtonClickListener.onClickNextButton(v.getId());
        }
    };


}
