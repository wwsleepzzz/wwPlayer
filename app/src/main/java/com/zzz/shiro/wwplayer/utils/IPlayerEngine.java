package com.zzz.shiro.wwplayer.utils;

import android.media.MediaPlayer.OnCompletionListener;


import com.zzz.shiro.wwplayer.Song;

import java.util.List;
import java.util.Map;

public interface IPlayerEngine {

	void play();

	void reset();

	void stop();

	void pause();

	void previous();

	void next();

	void skipTo(int index);

	void forward(int time);

	void rewind(int time);

	boolean isPlaying();

	boolean isPause();

	String getPlayingPath();



	void setPlayingPath(String path);

	void setMediaPathList(List<String> pathList);

	void start();

	void setOnCompletionListener(OnCompletionListener onCompletionListener);

	void setPlaybackMode(PlayerEngineImpl.PlaybackMode playbackMode);

	PlayerEngineImpl.PlaybackMode getPlayMode();

	String getCurrentTime();

	String getDurationTime();

	int getDuration();

	int getCurrentPosition();

	void release();

	String getTime(int timeM);

	//----===============================================by Judy
	String getCurrentSongName();

	void setMusicMap(Map<String, Song> musicMap);
	Map<String, Song> getMusicMap();
	Song getCurrentSong();
	void setOriIdx(int oirIdx);
	int getOriIdx();

}
