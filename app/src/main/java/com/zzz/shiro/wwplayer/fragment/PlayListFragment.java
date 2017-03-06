package com.zzz.shiro.wwplayer.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;


import com.zzz.shiro.wwplayer.R;
import com.zzz.shiro.wwplayer.adapter.BtnListenerInterface;
import com.zzz.shiro.wwplayer.adapter.PlaylistAdapter;
import com.zzz.shiro.wwplayer.playlist.Playlist;
import com.zzz.shiro.wwplayer.playlist.PlaylistDAO;
import com.zzz.shiro.wwplayer.utils.DlgUtil;
import com.zzz.shiro.wwplayer.utils.StringTool;

import java.util.Date;
import java.util.List;

/**
 * Created by wc on 2016/8/22.
 */
public class PlayListFragment extends Fragment {

    private String className = "PlayListFragment";
    private int idx = 0;

    //CardView List
    private View mView;
    private ListView listview;
    private PlaylistAdapter playlistAdapter;


    private ImageButton ib;
    private AlertDialog detailDlg;
    private List<Playlist> playlistList;

    private onChangeFmtClick changeFmtListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    public void initComponent(){
        //注意! 如果這邊的getActivity() 改成用inflater.inflate取得的layoutout 會造成set無效
        ib = (ImageButton) mView.findViewById(R.id.ib_addPlaylist);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener commonDialogOnClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                EditText et = (EditText) detailDlg.findViewById(R.id.et_addplaylist);
                                String name = et.getText()==null?null:et.getText().toString();

                                if(StringTool.isEmpty(name))
                                    break;

                                Playlist old = new Playlist();
                                old.setBuildTime(new Date());
                                old.setName(name);
                                new PlaylistDAO(getActivity()).save(old);

                                playlistList = new PlaylistDAO(getActivity()).getAll();
                                playlistAdapter.reload(playlistList);

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };


                LayoutInflater inflater = getActivity().getLayoutInflater();
                View layout = inflater.inflate(R.layout.dlg_detail,null);
                detailDlg =  new AlertDialog.Builder(getActivity()).setTitle("新增")
                        .setPositiveButton("確定",commonDialogOnClickListener)
                        .setNegativeButton("關閉",commonDialogOnClickListener)
                        .setView(layout).show();
                detailDlg.setCanceledOnTouchOutside(true);
            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Log.d(className,"listview click");

                Playlist pl = playlistList.get(position);
                changeFmtListener.onClick(pl==null ?"":pl.getName());

            }


        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment 把view建立起來
        Log.d(className,"onCreateView!222 ");


        mView = inflater.inflate(R.layout.myfragment2, container, false);
        listview = (ListView) mView.findViewById(R.id.lv_playlist);

        configRecyclerView();
        initComponent();



        return mView;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void configRecyclerView() {
        playlistList = new PlaylistDAO(getActivity()).getAll();
        for(Playlist pp:playlistList){
            if(pp.getMusicList()==null)
                return;
            for(String str:pp.getMusicList()){
                Log.d(className," == " + str);
            }
        }

        playlistAdapter = new PlaylistAdapter(getActivity(),playlistList);
        playlistAdapter.setBtnClickListener(new BtnListenerInterface() {
            @Override
            public void onClick(final int position) {
                Log.d(className,"btn click "+position);

                openActionDlg(position);
            }
        });
        listview.setAdapter(playlistAdapter);
        playlistAdapter.notifyDataSetChanged();

    }



    private void openActionDlg(final int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setItems(new String[]{"刪除", "更改名稱"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(className,"選到:" +i);

                        if(i==0){//選到刪除

                            DlgUtil.getInstance().getDeleteDialog(getActivity())
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {


                                        new PlaylistDAO(getActivity()).delete(playlistList.get(position));

                                        playlistAdapter.reload(new PlaylistDAO(getActivity()).getAll());

                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //放生
                                        dialogInterface.dismiss();
                                    }
                            }).show();

                        }
                        else if(i==1){ //選到更改名稱

                            final Playlist pl = playlistList.get(position);
                            String name = pl.getName();

                            View layout =  getActivity().getLayoutInflater().inflate(R.layout.dlg_detail,null);
                            final EditText ed = (EditText) layout.findViewById(R.id.et_addplaylist);
                            ed.setText(name);

                            new AlertDialog.Builder(getActivity())
                                .setTitle("改")
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        String name = ed.getText()==null?null:ed.getText().toString();

                                        if(StringTool.isEmpty(name))
                                            return;

                                        pl.setName(name);
                                        new PlaylistDAO(getActivity()).save(pl);
                                        playlistAdapter.reload(playlistList);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setView(layout)
                                .show();
                        }
                    }
                }
        ).setNegativeButton("取消",null).create().show();
    }


    public void setChangeFmtListener(onChangeFmtClick listener){
        this.changeFmtListener = listener;
    }


    /**
     * 介面=============================
     */

    public interface onChangeFmtClick {
        void onClick(String listName);
    }
}
