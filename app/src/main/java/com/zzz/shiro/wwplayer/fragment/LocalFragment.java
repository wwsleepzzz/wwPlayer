package com.zzz.shiro.wwplayer.fragment;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zzz.shiro.wwplayer.BottomBar;
import com.zzz.shiro.wwplayer.R;
import com.zzz.shiro.wwplayer.Song;
import com.zzz.shiro.wwplayer.adapter.BtnListenerInterface;
import com.zzz.shiro.wwplayer.adapter.MyRecyclerViewAdapter;
import com.zzz.shiro.wwplayer.adapter.MyRecyclerViewHolder;
import com.zzz.shiro.wwplayer.playlist.Playlist;
import com.zzz.shiro.wwplayer.playlist.PlaylistDAO;
import com.zzz.shiro.wwplayer.utils.Constants;
import com.zzz.shiro.wwplayer.utils.DlgUtil;
import com.zzz.shiro.wwplayer.utils.PicUtil;
import com.zzz.shiro.wwplayer.utils.StringTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by wc on 2016/8/22.
 */
public class LocalFragment extends Fragment
    implements MyRecyclerViewAdapter.AdapterOnItemClickInterface {

    private String className = "LocalFragment";
    private int idx = 0;
    private String plName =null;

    //CardView List
    private View mView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecyclerViewAdapter mRecyclerViewAdapter;


    private LinkedList<Song> songList = null;
    private static BottomBar bottomBar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className,"onCreate");
        if (getArguments() != null) {
            plName = (String) getArguments().get(Constants.BundleId.playList);
        }





        songList = new LinkedList<Song>();


        if(bottomBar ==null){
            bottomBar = BottomBar.getInstance(getActivity());
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment 把view建立起來
        Log.d(className,"onCreateView! ");


        mView = inflater.inflate(R.layout.myfragment, container, false);
//        TextView textView = (TextView) view.findViewById(R.id.textView2);
//        textView.setText("第"+(idx +1)+"頁");



        mRecyclerView = (RecyclerView) mView.findViewById(R.id.id_recyclerview);

        configRecyclerView();

        return mView;
    }



    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void configRecyclerView() {


        mLayoutManager =
                new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);

        if(plName == null){
            getByLocal(); //取本地all
        }
        else{
            getByPlayList(); //由列表取出
        }

        mRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(),songList );
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter.setOnItemClickListener(this);
        mRecyclerViewAdapter.setBtnClickListener(new BtnListenerInterface() {
            @Override
            public void onClick(final int position) {
                Log.d(className,"btn click "+position);

                openActionDlg(position);
            }
        });


        bottomBar.init();
        bottomBar.setData(getMusicList(),getMusicMap());
        closePlayIcon();

    }

    /**
     * 關掉Play的圖示
     */
    private void closePlayIcon(){
        Song song = bottomBar.belmotPlayer.getPlayerEngine().getCurrentSong();

        if(song ==null)
            return;

        int i = findCurrentIndex(songList,song.getPathId());
        if(i <0)
            return;
        View vv = mRecyclerView.getChildAt(i);
        ImageView image_play = (ImageView) vv.findViewById(R.id.imageView2);
        image_play.setBackgroundResource(0);
    }

    public Cursor getRealPathByAudioName(String name) {
        ContentResolver resolver = getActivity().getContentResolver();
        String[] proj = {             MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE};
        String selection = MediaStore.Audio.Media.DISPLAY_NAME + " = ?";

        String[] selectionArgs = new String[] { name };

        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, selection,
                selectionArgs, null);

        return cursor;
    }



    public Cursor getMediasByPath(String path) {
        ContentResolver resolver = getActivity().getContentResolver();
        // String
        // selection=MediaStore.Audio.Media.DATA+" like '/mnt/sdcard/Recording/%'";
        String selection = MediaStore.Audio.Media.DATA + " like ?";
        String[] selectionArgs = { path + "%" };
        // String selection = MediaStore.Audio.Media.DATA + " like " +"'"
        // +path+"%'";
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Constants.AudioProj, selection,
                selectionArgs, null);
        return cursor;
    }


    /**
     * 取得列表的歌
     */
    private void getByPlayList(){
        PlaylistDAO pDao = new PlaylistDAO(getActivity());
        Playlist pl = pDao.getByName(plName);
        List<String> list = pl.getMusicList();


        for(String str:list){
            Log.d(className,"歌曲id = "+str);

            Cursor cursor = getMediasByPath(str);
            fillUp(cursor);
        }
    }




    /**
     * 從本地取出
     */
    private void getByLocal(){


        songList = new LinkedList<Song>();
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        fillUp(cursor);

//        txtSongName.setText("共有 " + songList.size() + " 首歌曲");

    }


    @Override public void aOnItemClick(View view, final int position, final MyRecyclerViewHolder holder) {
        Log.d(className, "myOnItemClick" + position);
        if(songList.get(position)==null)
            return;


        /*
        if(mediaPlayer == null){
            doPlay(songList.get(position).getId());
        }
        else{
            if(!isPause){//己播放
                doPause();
            }
            else{
                doPlay(songList.get(position).getId());
            }
        }



        holder.image_album.setImageBitmap(songList.get(position).getPic());
        holder.image_play.setBackgroundResource(R.drawable.play);

        */


        Song song = null;

        if (null != bottomBar.belmotPlayer.getPlayerEngine()) {

            song = songList.get(position);

            if(song == null || song.getPathId()==null)
                return;

            if(bottomBar.belmotPlayer.getPlayerEngine().isPlaying()){//正播放

                if(song.getPathId().equals(bottomBar.belmotPlayer.getPlayerEngine().getPlayingPath())){
                    //同首
                    bottomBar.belmotPlayer.getPlayerEngine().pause();
                    holder.image_play.setBackgroundResource(0);  //處理cardView的play圖
                    setPlayingStatus(song,false);
                    bottomBar.closeBottomBar();
                }
                else{//不同首

                    mRecyclerViewAdapter.resetPlayingStatus();
                    bottomBar.belmotPlayer.getPlayerEngine().reset();
                    bottomBar.closeBottomBar();


                    //再處理這首
                    playing(holder ,song);
                }

            }
            else{ //未播放
                playing(holder ,song);
            }


            //播完後
            bottomBar.belmotPlayer.getPlayerEngine().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    belmotPlayer.getPlayerEngine().release(); //當我們呼叫 release() 方法時，則會釋放掉這個被佔用的資源
                    closePicture(holder,position);
                    bottomBar.closeBottomBar();
                }
            });


            bottomBar.belmotPlayer.getPlayerEngine().setOriIdx(position);
        }

        mRecyclerViewAdapter.notifyDataSetChanged();



//        holder.image_album.setBackgroundResource(R.drawable.play);
//        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.play);
//        holder.image_album.setImageBitmap(PicUtil.adjustOpacity(bitmap,40));

    }

    /**
     * 取出data填到List中
     * @param cursor
     */
    public void fillUp(Cursor cursor){
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever(); //取得媒體

        if (cursor == null) {
            Log.d("=======>", "查詢錯誤");
        } else if (!cursor.moveToFirst()) {
            Log.d("=======>", "沒有媒體檔");
        } else {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            int singerCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

            byte[] picByte;
            Bitmap songImage = null;
            long thisId;
            String thisTitle;
            String thisAlbum;
            String pathId;
            String singer;

            do {

                thisId = cursor.getLong(idColumn);
                thisTitle = cursor.getString(titleColumn);
                thisAlbum = cursor.getString(albumColumn);
                pathId = cursor.getString(column_index);
                singer = cursor.getString(singerCol);

                Log.d(className,"pathId="+pathId);
                songImage = null;

                try{
                    metaRetriver.setDataSource(pathId);
                }catch(Exception e){
                    Log.d(className,"error!");
                    e.printStackTrace();
                    continue;
                }

                try {
                    picByte = metaRetriver.getEmbeddedPicture();
                    if(picByte!=null){

                        //改圖片大小
                        songImage = PicUtil.resize(picByte);

                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


                Log.d("=======>", "id: " + thisId +
                        ", title: " + thisTitle +
                        ", singer: " + singer);
                songList.add(new Song(thisId, thisTitle, thisAlbum,songImage ,pathId ,singer));
            } while (cursor.moveToNext());


        }
    }


    @Override
    public void aOnItemLongClick(View view, int position) {

    }


    /**
     * 播放
     * @param holder
     * @param song
     */
    private void playing(MyRecyclerViewHolder holder, Song song){
        bottomBar.belmotPlayer.getPlayerEngine().setPlayingPath(song.getPathId());
        if(bottomBar.belmotPlayer.getPlayerEngine().isPause()){
            bottomBar.belmotPlayer.getPlayerEngine().start();
        }
        else{
            bottomBar.belmotPlayer.getPlayerEngine().play();
        }



        //處理cardView
        setPlayingStatus(song,true);
        holder.image_album.setImageBitmap(song.getPic()); //重新set圖片
        holder.image_play.setBackgroundResource(R.drawable.play);


        //處理bottom bar
        bottomBar.setBarData();
    }


    /**
     * 給播放狀態
     * @param song
     * @param b
     */
    public void setPlayingStatus(Song song,boolean b){
        List<Song> songList = new LinkedList<>();
        List<Song> songList2 = mRecyclerViewAdapter.getSongList();
        songList.addAll(songList2);


        for(int i=0;i<songList.size();i++){
            Song ss = songList.get(i);
            if(ss.getId()==song.getId()){
                ss.setPlaying(b);
                songList.set(i,ss);
                break;
            }
        }
        mRecyclerViewAdapter.setSongList(songList);
    }



    /**
     * 關掉圖片
     * @param holder
     * @param position
     */
    private void closePicture(MyRecyclerViewHolder holder,int position){
        holder.image_album.setImageBitmap(songList.get(position).getPic()); //重新set圖片
        holder.image_play.setBackgroundResource(0);
    }








    /**
     * 將songList轉成 List<String> 裡面只裝MediaStore.Audio.Media.DATA
     * @return
     */
    public List<String> getMusicList(){
        List<String> musicList = new ArrayList<String>();

        if(null == songList || songList.isEmpty())
            return musicList;


        int i = 0;
        for(Song song:songList){
            musicList.add(i,song.getPathId());
            i++;
        }

        return musicList;
    }


    public Map getMusicMap(){
        Map<String,Song> musicMap = new HashMap<String, Song>() {
        };

        if(null == songList || songList.isEmpty())
            return musicMap;


        int i = 0;
        for(Song song:songList){
            musicMap.put(song.getPathId(),song);
        }

        return musicMap;
    }


    /**
     * 找出這首歌在播放list的index
     */
    public int findCurrentIndex(List<Song> songList, String pathId){
        int rtn = -1;
        if(StringTool.isEmpty(pathId))
            return rtn;


        int i=0;
        for(Song song:songList){
            if(StringTool.isEmpty(song.getPathId()) &&
                    song.getPathId().equals(pathId)){
                rtn = i;
            }
            i++;
        }

        return rtn;
    }


    private void openActionDlg(final int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setItems(new String[]{"加入播放清單", "刪除"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(className,"選到:" +i);

                        if(i==0){//選到0

                            Song song = songList.get(position);
                            if(song ==null)
                                return;

                            openAddPlayListDlg(song.getPathId());

                        }
                        else if(i==1){ //選到1


                            DlgUtil.getInstance().getDeleteDialog(getActivity())
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {


                                            //TODO
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //放生
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }
                    }
                }
        ).setNegativeButton("取消",null).create().show();
    }

    private void openAddPlayListDlg(final String pathId){
        final PlaylistDAO playlistDAO =new PlaylistDAO(getActivity());
        List<Playlist> playlists = playlistDAO.getAll();

        final String[] plAarray = new String[playlists.size()];
        int i=0;
        for(Playlist pl:playlists){
            plAarray[i] = pl.getName();
            i++;
        }


        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setItems(plAarray,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(className,"選到:" +i);


                        Playlist playlist = playlistDAO.getByName(plAarray[i]);

                        if(playlist ==null)
                            return;


                        List<String> sList = playlist.getMusicList();

                        if(sList == null)
                            sList = new ArrayList<String>();


                        sList.add(pathId);
                        playlist.setMusicList(sList);
                        playlistDAO.save(playlist);

                        if(i==0){//選到0



                        }
                        else if(i==1){ //選到1


                            DlgUtil.getInstance().getDeleteDialog(getActivity())
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {


                                            //TODO
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //放生
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }
                    }
                }
        ).setNegativeButton("取消",null).create().show();

    }

}
