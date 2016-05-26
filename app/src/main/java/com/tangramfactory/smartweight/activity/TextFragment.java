package com.tangramfactory.smartweight.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;

/**
 * Created by B on 2016-05-24.
 */
public class TextFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int mPosition;

    public static TextFragment newInstance(int param1) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public TextFragment() {

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
        View view = inflater.inflate(R.layout.text_fragment_layout, container, false);
        TextView textView = (TextView)view.findViewById(R.id.text);
        String str = getString(getResources().getIdentifier("level" + (mPosition + 1), "string", getActivity(). getPackageName()));
        textView.setText(str);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}