package com.claw.exoplayers;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.ExoTrackSelection;
import androidx.media3.ui.PlayerView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    PlayerView playerView;
    ExoPlayer exoPlayer;
    Context context = this;
    Map<String, String> defaultRequestProperties = new HashMap<>();
    String SampleVideo = "";
    String userAgent = "";
    private DefaultTrackSelector trackSelector;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);  //load in fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // load and hide status bar at top
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        // Configure the behavior of the hidden system bars.
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        // hide bottomsystem bar immerviemode

        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.PlayerView);
        DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(userAgent)
                .setKeepPostFor302Redirects(true)
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                .setDefaultRequestProperties(defaultRequestProperties);
        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context, httpDataSourceFactory);
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(SampleVideo)
                .setMimeType(MimeTypes.APPLICATION_MP4)
                .build();

        MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
        InitlizeWithExo(progressiveMediaSource);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void InitlizeWithExo(MediaSource progressiveMediaSource) {
        ExoTrackSelection.Factory videoselector = new AdaptiveTrackSelection.Factory();
        trackSelector = new DefaultTrackSelector(this, videoselector);
        exoPlayer = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .setSeekForwardIncrementMs(10000)
                .setSeekBackIncrementMs(10000)
                .build();
        playerView.setPlayer(exoPlayer);
        playerView.setKeepScreenOn(true);  // keep screen on == consume user battery;
        exoPlayer.setMediaSource(progressiveMediaSource);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.pause();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        exoPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        exoPlayer.play();
    }
}