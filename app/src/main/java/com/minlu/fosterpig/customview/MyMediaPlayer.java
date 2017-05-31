package com.minlu.fosterpig.customview;


import android.media.MediaPlayer;

import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.observer.Observers;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.ViewsUitls;

public class MyMediaPlayer extends MediaPlayer implements Observers {

    public MyMediaPlayer() {
        super();
    }

    @Override
    public void update(int distinguishNotified, int position, int cancelOrderBid) {
        if (distinguishNotified == StringsFiled.OBSERVER_MEDIA_PLAYER_PAUSE) {
            this.pause();
        }
        if (distinguishNotified == StringsFiled.OBSERVER_MEDIA_PLAYER_IS_PLAYING) {
            SharedPreferencesUtil.saveboolean(ViewsUitls.getContext(),StringsFiled.MEDIA_IS_PLAYING,this.isPlaying());
        }
    }
}
