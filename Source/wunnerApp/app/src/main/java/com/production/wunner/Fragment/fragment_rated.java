package com.production.wunner.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.production.wunner.R;

public class fragment_rated extends Fragment {
    Context context;

    public fragment_rated(Context context) {
        this.context = context;
    }

    public static fragment_rated newInstance(Context context)
    {
        return new fragment_rated(context);
    }


    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.layout_rated,container,false);
       return view;
    }
}
