package com.example.playware2019;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.GameType;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;
import com.livelife.motolibrary.OnAntEventListener;

public class GameActivity extends AppCompatActivity implements OnAntEventListener, Game.OnGameEventListener {

    MotoConnection connection = MotoConnection.getInstance();
    MotoSound sound = MotoSound.getInstance();

    ColorRace colorRace;
    GameHitTheTarget hitTarget;
    LinearLayout gameTypeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        connection.registerListener(GameActivity.this);
        connection.setAllTilesToInit();
        colorRace = new ColorRace();
        gameTypeContainer = findViewById(R .id.gameTypeContainer);
        for (final GameType gt: colorRace.getGameTypes()){
            Button b = new Button(this);
            b.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    colorRace.selectedGameType = gt;
                    sound.playStart();
                    colorRace.startGame();

                }
            });
            b.setText(gt.getName());
            gameTypeContainer.addView(b);

        }

        hitTarget = new GameHitTheTarget();
        for (final GameType gt: hitTarget.getGameTypes()){
            Button b = new Button(this);
            b.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    hitTarget.selectedGameType = gt;
                    hitTarget.setOnGameEventListener(GameActivity.this);
                    sound.playStart();
                    hitTarget.startGame();

                }
            });
            b.setText(gt.getName());
            gameTypeContainer.addView(b);

        }
    }

    @Override
    public void onMessageReceived(byte[] bytes, long l) {
        colorRace.addEvent(bytes);
        hitTarget.addEvent(bytes);
    }

    @Override
    public void onAntServiceConnected() {

    }

    @Override
    public void onNumbersOfTilesConnected(int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.registerListener(this);
    }

    @Override
    public void onGameTimerEvent(int i) {

    }

    @Override
    public void onGameScoreEvent(int i, int i1) {

    }

    @Override
    public void onGameStopEvent() {

    }

    @Override
    public void onSetupMessage(String s) {

    }

    @Override
    public void onGameMessage(String s) {

    }

    @Override
    public void onSetupEnd() {

    }
}
