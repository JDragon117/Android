package com.example.dell.jdmusic;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ImageButton playMusic;
    private ImageButton pauseMusic;
    private ImageButton nextMusic;
    private ImageButton previousMusic;

    private TextView totalTime_text;
    private TextView playingTime_text;

    private SeekBar playingProcess;

    private int totalTime = 0;
    public int songnum = 0;

    private MediaPlayer mediaPlayer;

    private MyHandler hangler = new MyHandler();
    private boolean flag = true;
    private boolean isFirstStart = true;

    private ArrayList<jiaru> mData;
    private Context mContext;
    private jiaruadapter mAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;
        list = (ListView) findViewById(R.id.list);
        mData =  getMultiData();
        mAdapter = new jiaruadapter(mData, mContext);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                songnum = position;
                initMediaPlayer(position);

            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer arg0) {
                setNextMusic();//如果当前歌曲播放完毕,自动播放下一首.
            }
        });

        playMusic = (ImageButton) findViewById(R.id.play);
        playMusic.setOnClickListener(new myOnClickListener());

        pauseMusic = (ImageButton) findViewById(R.id.pause);
        pauseMusic.setOnClickListener(new myOnClickListener());

        nextMusic = (ImageButton) findViewById(R.id.next);
        nextMusic.setOnClickListener(new myOnClickListener());

        previousMusic = (ImageButton) findViewById(R.id.previous);
        previousMusic.setOnClickListener(new myOnClickListener());

        playingProcess = (SeekBar) findViewById(R.id.seek);
        playingProcess.setOnSeekBarChangeListener(new mySeekBarListener());

        totalTime_text = (TextView) findViewById(R.id.totalTime);
        playingTime_text = (TextView) findViewById(R.id.playingTime);

    }


    public void setTotalTime() {
        totalTime = mediaPlayer.getDuration() / 1000;
        Log.d("MediaPlayerTest", String.valueOf(totalTime));
        String pos = String.valueOf(totalTime / 60 / 10) + String.valueOf(totalTime / 60 % 10)
                + ':' + String.valueOf(totalTime % 60 / 10) + String.valueOf(totalTime % 60 % 10);
        totalTime_text.setText(pos);
        playingProcess.setProgress(0);
        playingProcess.setMax(totalTime);
    }

    public void updateTimepos() {
        int timepos = mediaPlayer.getCurrentPosition()/1000;
        if (timepos >= totalTime-1) {
            timepos = 0;
            flag = false;
        }
        playingProcess.setProgress(timepos);
        int min = timepos/60;
        int second = timepos%60;
        String pos = String.format("%02d:%02d",min,second);
        playingTime_text.setText(pos);

    }

    public void initMediaPlayer(int songnum) {
        try {
            //File file = new File(Environment.getExternalStorageDirectory(), "music.mp3");
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mData.get(songnum).getaPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            setTotalTime();

            if(isFirstStart){
                refreshTimepos();
                isFirstStart = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshTimepos() {
       Timer ti = new Timer();
        ti.schedule(new TimerTask() {
            @Override
            public void run() {
                int timepos = mediaPlayer.getCurrentPosition()/1000;
                if (timepos >= totalTime-1) {
                    timepos = 0;
                    flag = false;
                }
                playingProcess.setProgress(timepos);
                int min = timepos/60;
                int second = timepos%60;
                String pos = String.format("%02d:%02d",min,second);
                Bundle b = new Bundle();
                Message msg = new Message();
                b.putString("time",pos);
                msg.setData(b);
                hangler.sendMessage(msg);

            }
        },0,1000);
    }

    private class mySeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            if(b){
                mediaPlayer.seekTo(playingProcess.getProgress() * 1000);
                updateTimepos();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private class myOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.play:
                    setPlayMusic();
                    break;
                case R.id.pause:
                    setPauseMusic();
                    break;
                case R.id.next:
                    setNextMusic();
                    break;
                case R.id.previous:
                    setPreviousMusic();
                    break;
            }
        }
    }

    public void setPlayMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            flag = true;
        }
    }

    public void setPauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            flag = false;
        }
    }

    public void setNextMusic() {
        flag = true;
        songnum =  songnum + 1;
        initMediaPlayer(songnum);
    }

    class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        // 子类必须重写此方法，接受数据
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);
            // 此处可以更新UI
            Bundle b = msg.getData();
            String time = b.getString("time");
            playingTime_text.setText(time);
        }
    }

    public void setPreviousMusic() {
        if(songnum != 0){
            songnum = songnum - 1;
        }
        initMediaPlayer(songnum);
    }

    public ArrayList<jiaru> getMultiData() {
        String musicPath;
        String musicName;
        String musicArtist;

        ArrayList<jiaru> musicList = new ArrayList<jiaru>();

        // 加入封装音乐信息的代码
        // 查询所有歌曲
        ContentResolver musicResolver = this.getContentResolver();
        Cursor musicCursor = musicResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                null);

        int musicColumnIndex;

        if (null != musicCursor && musicCursor.getCount() > 0) {
            for (musicCursor.moveToFirst(); !musicCursor.isAfterLast(); musicCursor
                    .moveToNext()) {
                jiaru musicDataMap = new jiaru();

                // 取得音乐播放路径
                musicColumnIndex = musicCursor
                        .getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
                musicPath = musicCursor.getString(musicColumnIndex);
                musicDataMap.setaPath(musicPath);

                // 取得音乐的名字
                musicColumnIndex = musicCursor
                        .getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);
                musicName = musicCursor.getString(musicColumnIndex);
                musicDataMap.setaName(musicName);
                // 取得音乐的演唱者
                musicColumnIndex = musicCursor
                        .getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
                musicArtist = musicCursor.getString(musicColumnIndex);
                musicDataMap.setaSpeak(musicArtist);

                musicList.add(musicDataMap);
            }
        }
        return musicList;
    }
}
