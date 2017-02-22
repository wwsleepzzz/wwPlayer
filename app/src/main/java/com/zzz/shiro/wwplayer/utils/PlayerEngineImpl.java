package com.zzz.shiro.wwplayer.utils;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;


import com.zzz.shiro.wwplayer.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlayerEngineImpl implements IPlayerEngine {

	public PlayerEngineImpl() {
		if (null == mediaPlayerEngine) {

			Log.i(BelmotPlayer.TAG , "PlayerEngineImpl");

			mediaPlayerEngine = new MediaPlayerEngine();
		}
	}

	public enum PlaybackMode {
		NORMAL, SHUFFLE, REPEAT, SHUFFLE_AND_REPEAT
	}

	private PlaybackMode playbackMode = PlaybackMode.NORMAL;

	private List<Integer> playbackOrder = new ArrayList<Integer>();

	private String path = "";

	private int selectedOrderIndex = 0;

	private static List<String> mediaList = new ArrayList<String>(); //由MediaStore.Audio.Media.DATA取出

	private MediaPlayerEngine mediaPlayerEngine = null;

	private static Map<String,Song> musicMap = null;//key:Media.DATA ; value:Song

	private static int oriIdx;

	@Override
	public void setOnCompletionListener(
			OnCompletionListener onCompletionListener) {
		Log.i(BelmotPlayer.TAG , "setOnCompletionListener");
		mediaPlayerEngine.setOnCompletionListener(onCompletionListener);

	}

	private boolean isEmpty() {
		return mediaList.size() == 0;
	}

	private int getListSize() {
		return mediaList.size();
	}

	public PlaybackMode getPlaybackMode() {
		return playbackMode;
	}

	@Override
	public void setPlaybackMode(PlaybackMode playbackMode) {
		this.playbackMode = playbackMode;
		calculateOrder(true);
	}

	@Override
	public void forward(int time) {
		mediaPlayerEngine.seekTo(time);

	}

	@Override
	public boolean isPlaying() {
		return mediaPlayerEngine.isPlaying();
	}

	@Override
	public boolean isPause() {
		return mediaPlayerEngine.isPause();
	}

	private int getSelectedOrderIndexByPath(String path) {
		int selectedIndex = mediaList.indexOf(path);
		return playbackOrder.indexOf(selectedIndex);

	}

	private String getPathByPlaybackOrderIndex(int index) {
		return mediaList.get(playbackOrder.get(index));
	}


	public String getCurrentSongName(){
		Song song = musicMap.get(getPlayingPath());
		if(null ==song)
			return "";
		else{
			return song.getTitle();
		}
	}

	public Song getCurrentSong(){
		return musicMap.get(getPlayingPath());
	}

	public void setOriIdx(int oriIdx){
		this.oriIdx = oriIdx;
	}

	public int getOriIdx(){
		return oriIdx;
	}

	@Override
	public void previous() {
		if (!isEmpty()) {
			selectedOrderIndex = getSelectedOrderIndexByPath(path);
			selectedOrderIndex--;
			if (selectedOrderIndex < 0) {
				selectedOrderIndex = mediaList.size() - 1;
			}
			this.path = getPathByPlaybackOrderIndex(selectedOrderIndex);
			mediaPlayerEngine.previousOrNext();
		}

	}

	@Override
	public void next() {
		if (!isEmpty()) {
			selectedOrderIndex = mediaList.indexOf(path);
			Log.i(
					BelmotPlayer.TAG ,
					"Line 123 next():Path="
							+ path
							+ "***********selectedOrderIndex="
							+ selectedOrderIndex
							+ "***************************************playbackOrder="
							+ playbackOrder.toArray());
			// selected begins from zero.
			selectedOrderIndex++;
			selectedOrderIndex %= mediaList.size();
			this.path = getPathByPlaybackOrderIndex(selectedOrderIndex);
			Log
					.i(
							BelmotPlayer.TAG ,
							"Line 123 next():next Path="
									+ path
									+ "***********next selectedOrderIndex="
									+ selectedOrderIndex
									+ "***************************************playbackOrder="
									+ playbackOrder.toArray());
			mediaPlayerEngine.previousOrNext();
		}

	}

	@Override
	public void pause() {
		mediaPlayerEngine.pause();

	}

	@Override
	public void play() {
		mediaPlayerEngine.play(path);
	}

	@Override
	public void start() {
		mediaPlayerEngine.start();
	}

	@Override
	public void reset() {
		mediaPlayerEngine.reset();

	}

	@Override
	public void rewind(int time) {
		mediaPlayerEngine.seekTo(time);

	}

	@Override
	public void skipTo(int index) {

	}

	@Override
	public void stop() {
		mediaPlayerEngine.stop();
	}

	@Override
	public String getPlayingPath() {
		return this.path;
	}

	@Override
	public void setPlayingPath(String path) {
		this.path = path;

	}

	@Override
	public void setMediaPathList(List<String> pathList) {
		this.mediaList = pathList;
		calculateOrder(true);

	}



	public void release(){
		mediaPlayerEngine.release();
	}

	public void setMusicMap(Map<String, Song> musicMap) {
		this.musicMap = musicMap;
	}

	public Map<String, Song>  getMusicMap(){return musicMap;}

	private void calculateOrder(boolean force) {
		int beforeSelected = 0;
		if (!playbackOrder.isEmpty()) {
			beforeSelected = playbackOrder.get(selectedOrderIndex);
			playbackOrder.clear();
		}
		Log.i(BelmotPlayer.TAG ,
				"Line 200 calculateOrder():beforeSelected="
						+ beforeSelected
						+ "***********selectedOrderIndex="
						+ selectedOrderIndex);


		for (int i = 0; i < getListSize(); i++) {
			playbackOrder.add(i, i);
		}

		if (null == playbackMode) {
			playbackMode = PlaybackMode.NORMAL;
		}
		switch (playbackMode) {
			case NORMAL: {
				break;
			}
			case SHUFFLE: {
				Collections.shuffle(playbackOrder);
				break;
			}
			case REPEAT: {
				selectedOrderIndex = beforeSelected;
				break;
			}
			case SHUFFLE_AND_REPEAT: {
				break;
			}
			default: {
				break;
			}
		}
	}



	@Override
	public String getTime(int timeMs) {
		int totalSeconds = timeMs / 1000;// 獲取文件有多少秒
		StringBuilder mFormatBuilder = new StringBuilder();
		Formatter mFormatter = new Formatter(mFormatBuilder, Locale
				.getDefault());
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		mFormatBuilder.setLength(0);

		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();// 格式化字符串
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}




	/**=================================================================**/
	private class MediaPlayerEngine extends MediaPlayer {

		private boolean isPause = false; //是否暫停

		public boolean isPause() {
			return isPause;
		}

		@Override
		public void reset() {
			isPause = false;
			super.reset();
		}

		public void play(String path) {
			try {
				this.setDataSource(path);
				if (!isPause) {
					super.prepare(); //準備播放, 同步的方式播放
				}
				super.start();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void stop() throws IllegalStateException {
			isPause = false;
			super.stop();
		}

		@Override
		public void pause() throws IllegalStateException {
			isPause = true;
			super.pause();
		}

		public void previousOrNext() {
			reset();
			Log.i(BelmotPlayer.TAG, "previousOrNext:path = " + path);
			play(path);

		}
	}

	@Override
	public String getCurrentTime() {

		return getTime(mediaPlayerEngine.getCurrentPosition());
	}

	@Override
	public String getDurationTime() {
		return getTime(mediaPlayerEngine.getDuration());
	}

	@Override
	public PlaybackMode getPlayMode() {
		return playbackMode;
	}



	@Override
	public int getDuration() {
		return mediaPlayerEngine.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return mediaPlayerEngine.getCurrentPosition();
	}




}
