package com.zzz.shiro.wwplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zzz.shiro.wwplayer.R;

/**
 * Created by wc on 2017/2/22.
 */

public class MainFragment extends Fragment {
    private View view;
    private String msg;
    private onTranslateClick transListener;
    private onChangeFgmtClick changeFgmtListener;

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


        if (changeFgmtListener != null) {

            view.findViewById(R.id.ib_local).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeFgmtListener.onClick(view);
                }
            });

            view.findViewById(R.id.ib_list).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    changeFgmtListener.onClick(view);
                }
            });
        }



        return view;

    }


    public void setTransListener(onTranslateClick listener) {
        this.transListener = listener;
    }

    public void setChangeFgmtListener(onChangeFgmtClick listener){
        this.changeFgmtListener = listener;
    }



    public static MainFragment getFragment(String msg){
        MainFragment testFragment= new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("msg",msg);
        testFragment.setArguments(bundle);

        return testFragment;

    }


    /**
     * 介面=============================
     */
    public interface onTranslateClick{
        void onClick(String msg);
    }

    public interface onChangeFgmtClick {
        void onClick(View view);
    }
}
