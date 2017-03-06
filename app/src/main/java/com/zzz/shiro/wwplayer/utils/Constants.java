package com.zzz.shiro.wwplayer.utils;

import android.provider.MediaStore;

/**
 * Created by wc on 2016/9/28.
 */
public class Constants {

    public static final int Tab_0 = 0;
    public static final int Tab_1 = 1;
    public static final int Tab_2 = 2;
    public static final int Tab_3 = 3;
    public static final int Tab_4 = 4;


    public static final int album_width = 100;
    public static final int album_height = 100;


    public class BundleId{
        public static final String idx = "idx";
        public static final String playList = "playList";
    }

    public static String[] AudioProj = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.TITLE
    };

}
