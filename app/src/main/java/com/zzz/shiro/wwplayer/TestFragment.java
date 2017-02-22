package com.zzz.shiro.wwplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by wc on 2017/2/22.
 */

public class TestFragment extends Fragment {
    private View view;
    private String msg; private onTranslateClick transListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.testfragment, null);
        msg = getArguments().getString("msg");
        ((TextView) view.findViewById(R.id.id_tv)).setText(msg);

        if ("2".equals(msg) || "6".equals(msg))
            (view.findViewById(R.id.id_btn)).setVisibility(View.VISIBLE);


        (view.findViewById(R.id.id_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transListener.onClick(msg);
            }
        });
        return view;

    }


    public void setTransListener(onTranslateClick listener){
        this.transListener = listener;
    }

    public interface onTranslateClick{
        void onClick(String msg);
    }

    public static TestFragment getFragment(String msg){
        TestFragment testFragment= new TestFragment();
        Bundle bundle = new Bundle();
        bundle.putString("msg",msg);
        testFragment.setArguments(bundle);

        return testFragment;

    }

}
