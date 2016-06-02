package com.tangramfactory.smartweight.activity.workoutresult;

/**
 * Created by shlim on 2016-05-28.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tangramfactory.smartweight.R;

public class ImageFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int mPosition;

    public static ImageFragment newInstance(int param1) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView view = new ImageView(getActivity());
        view.setImageResource(R.drawable.badge_large);
        return view;
    }
}