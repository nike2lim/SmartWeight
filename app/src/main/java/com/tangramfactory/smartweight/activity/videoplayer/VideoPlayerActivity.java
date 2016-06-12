package com.tangramfactory.smartweight.activity.videoplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.utility.DebugLogger;

import java.io.IOException;

public class VideoPlayerActivity extends BaseAppCompatActivity implements SurfaceHolder.Callback2, Handler.Callback{

    public Toolbar toolbar;
    MediaPlayer mMediaPlayer;
    SeekBar mSeekBar;
    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    LinearLayout mControlLayout;
    ImageButton mPlayPauseButton;

    private Thread mStartThread = null;
    private Uri mPlayUri = null;
    private float mVolume = 0.0f;
    int mVideoWidth;
    int mVideoHeight;
    private boolean mIsPrepared = false;
    private int mCurrentBuffer = 0;
    private Handler mPlayTimeHandler = null;
    private Handler taskDelay  = null;
    private Handler closeLayoutDelay  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        setToolbar();
        loadCodeView();
    }

    @Override
    protected void onResume() {
        if(null != mMediaPlayer) {
            mMediaPlayer.start();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(null != mMediaPlayer) {
            mMediaPlayer.pause();
        }
        super.onPause();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bench Press");
        toolbar.findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                startActivity(new Intent(mContext, DeviceSettingActivity.class));
            }
        });
        setSupportActionBar(toolbar);
    }

    protected void loadCodeView() {
        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceview);
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               toogleLayout();
            }
        });
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSeekBar = (SeekBar)findViewById(R.id.seekbar);
        mControlLayout = (LinearLayout)findViewById(R.id.control_layout);
        mPlayPauseButton = (ImageButton)findViewById(R.id.play_pause_btn);

        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mMediaPlayer) {
                    if(mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        mPlayPauseButton.setBackgroundResource(R.drawable.selector_play_btn);
                    }else {
                        mMediaPlayer.start();
                        mPlayPauseButton.setBackgroundResource(R.drawable.selector_pause_btn);
                    }
                }
            }
        });

        initialize(null);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(null != mMediaPlayer) {
                    mMediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    public void initialize(Uri uri) {
        DebugLogger.d(TAG, "[initialize]");

        mPlayUri = uri;
        if(null == uri) {
//            mPlayUri = Uri.parse("android.resource://" + getPackageName() + "/"+R.raw.bench_press);
        }

        taskDelay = new Handler();
        taskDelay.postDelayed(mCallStart, 500);

        closeLayoutDelay = new Handler();
        closeLayoutDelay.postDelayed(toggleLayoutRunnable, 3000);
    }

    private final Runnable mCallStart = new Runnable() {
        @Override
        public void run() {
            startPrePare();
        }
    };

    private final Runnable toggleLayoutRunnable = new Runnable() {
        @Override
        public void run() {
            toogleLayout();
        }
    };

    private void toogleLayout() {
        if(View.VISIBLE == toolbar.getVisibility()) {
            toolbar.setVisibility(View.GONE);
            mControlLayout.setVisibility(View.GONE);
        }else {
            toolbar.setVisibility(View.VISIBLE);
            mControlLayout.setVisibility(View.VISIBLE);
        }
    }

    private synchronized void startPrePare() {

        //OEM Audio의 포커스를 뺏는다.
        AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        if(null != am){
            am.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        //onStop();
        reset();
        if(null == mPlayUri || null == mSurfaceHolder) {
            DebugLogger.d("VideoPlayer", " mPlayURI : " + mPlayUri + "  SurfaceHolder : " + mSurfaceHolder );
            return;
        }

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferUpdateListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
//            mMediaPlayer.setOnErrorListener(mMediaPlayerErrorListener);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setDataSource(mContext, mPlayUri);

            if(null != mMediaPlayer){
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setVolume(mVolume, mVolume);
                mMediaPlayer.setScreenOnWhilePlaying(true);
                mMediaPlayer.prepareAsync();

            }else{

            }
        } catch (IOException ex) {
            DebugLogger.d("VideoPlayer", "startPrePare " + ex.toString());
            mMediaPlayer = null;
        } catch (IllegalArgumentException ex) {
            DebugLogger.d("VideoPlayer", "startPrePare " + ex.toString());
            mMediaPlayer = null;
        } catch (Exception e) {
            DebugLogger.d("VideoPlayer", "startPrePare " + e.toString());
            mMediaPlayer = null;
        }
    }

    /**
     * MediaPlayer Prepare완료 후 호출되는 listener
     * 재생할 수 있는 상태가 된다.
     */
    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            DebugLogger.d(TAG, "[onPrepared]");

            if(mStartThread != null) {
                mStartThread.interrupt();
                mStartThread = null;
            }

            mIsPrepared = true;
            mSeekBar.setMax(getDuration());

            DebugLogger.d(TAG, "onPrepared SetVolume : " + mVolume);
            //mMediaPlayer.setVolume(mVolume, mVolume);
            mVideoWidth = mSurfaceView.getWidth();
            mVideoHeight = mSurfaceView.getHeight();

            mMediaPlayer.start();
            mPlayTimeHandler = new Handler(VideoPlayerActivity.this);
            mPlayTimeHandler.sendEmptyMessageDelayed(0, 500);

            mPlayPauseButton.setBackgroundResource(R.drawable.selector_pause_btn);
        }
    };


    private MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new MediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
//                    mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
                }
            };


    private MediaPlayer.OnBufferingUpdateListener mBufferUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if(mIsPrepared) {
                mCurrentBuffer = percent * (getDuration()/100);
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            DebugLogger.d(TAG, "[onCompletion]");
            mIsPrepared = false;
            onMediaPlayerStop();
            finish();
        }
    };



    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
//        mMediaPlayer.setDisplay(mSurfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mVideoWidth = width;
        mVideoHeight =height;
//        mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(null != mMediaPlayer) {

            if(mMediaPlayer.isPlaying()) {
                mSeekBar.setMax(getDuration());
                mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                mSeekBar.setSecondaryProgress(mCurrentBuffer);
            }
            mPlayTimeHandler.sendEmptyMessageDelayed(0, 500);
        }
        return false;
    }

    /**
     * MediaPlayer Reset
     */
    public void reset() {
        if(null != mPlayTimeHandler) {
            mPlayTimeHandler.removeMessages(0);
            mPlayTimeHandler = null;
        }
        if (mMediaPlayer != null) {
            DebugLogger.d(TAG, "[onReset]");
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void onMediaPlayerStop() {
        if(mStartThread != null) {
            mStartThread.interrupt();
            mStartThread = null;
            return;
        }

        if(null != mPlayTimeHandler) {
            mPlayTimeHandler.removeMessages(0);
            mPlayTimeHandler = null;
        }
        if (mMediaPlayer != null) {
            boolean isSuccess = false;
            try {
                mMediaPlayer.stop();
                isSuccess = true;
            }catch (Exception e) {
                isSuccess = false;
            }

            DebugLogger.d(TAG, "[onStop] isSuccess = " + isSuccess);
            mMediaPlayer.release();
            mMediaPlayer = null;
            mIsPrepared = false;
        }
    }

    private int getDuration() {
        int duration = 0;
        int ms = 0;
        if(null != mMediaPlayer) {

            duration = mMediaPlayer.getDuration();
            // 1000ms 이하 반올림.
            ms = ( ( duration % 1000 ) > 500 ) ? 1 : 0;

            duration = ( duration / 1000 + ms ) * 1000;
        }
        DebugLogger.d(TAG, "[getDuration] duration = " + duration);

        return duration;
    }

    public static AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int arg0) {
        }
    };
}
