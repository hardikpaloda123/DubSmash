package com.dekhoapp.android.app.base;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Hardik on 8/5/2017.
 */
public class MediaPlayerManager {

    private static final String TAG = MediaPlayerManager.class.getSimpleName();

    private static Context mContext;

    private static MediaPlayer mediaPlayer;

    private static MediaPlayerManager _instance;

    public static MediaPlayerManager getInstance(Context context){
        Log.d(TAG,"getInstance");
        mContext = context;
        if(_instance == null){
            _instance = new MediaPlayerManager();
        }
        return _instance;
    }


    private void initialize(){
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
    }

    public void playSong(String songUrl){
        Log.d(TAG,"playSong : songUrl = " + songUrl );
        initialize();
        if(mediaPlayer!=null){
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(onPreparedListener);
                mediaPlayer.setOnErrorListener(onErrorListener);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e){
                e.printStackTrace();
            }
            mediaPlayer.start();
        }else{
            Log.d(TAG,"mediaPlayer is null");
        }
    }


    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            Log.d(TAG,"onError : i = "  + i  + " i1 = " + i1);
            return false;
        }
    };

    private MediaPlayer.OnPreparedListener onPreparedListener =new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            Log.d(TAG,"onPrepared");
        }
    };

    public void stopSong(){
        Log.d(TAG,"stopSong");
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }else {
            Log.d(TAG,"mediaPlayer is null");
        }
    }

}
