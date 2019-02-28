package com.example.playware2019;

import android.os.Handler;
import android.util.Log;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.GameType;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;

import java.util.Random;

import static com.livelife.motolibrary.AntData.LED_COLOR_OFF;
import static com.livelife.motolibrary.AntData.LED_COLOR_RED;

public class GameHitTheTarget extends Game {

    MotoSound sound = MotoSound.getInstance();
    MotoConnection connection = MotoConnection.getInstance();
    int currentTile;
    int timeInterval = 1000;
    int timeStep = 100;

    GameHitTheTarget(){
        setName("Hit the target");
        setDescription("A nice game");

        GameType gt = new GameType(1, GameType.GAME_TYPE_SCORE, 10, "Hit target 10 times", 1);
        addGameType(gt);
    }


    Handler handler = new Handler();
    Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            int tile = getRandomTile();
            for (int t: connection.connectedTiles) {
                if (tile == t) {
                    connection.setTileColor(LED_COLOR_RED, tile);
                } else {
                    connection.setTileColor(LED_COLOR_OFF, t);
                }
            }

            currentTile = tile;
            handler.postDelayed(this, timeInterval);  // this: the runnable object

        }
    };

    @Override
    public void onGameStart() {
        super.onGameStart();

        connection.setAllTilesIdle(LED_COLOR_OFF);
        currentTile = connection.randomIdleTile();

        connection.setTileColor(LED_COLOR_RED, currentTile);

        handler.postDelayed(gameRunnable, timeInterval);

    }

    @Override
    public void onGameUpdate(byte[] message) {
        super.onGameUpdate(message);
        int event = AntData.getCommand(message);
        if (event == AntData.EVENT_PRESS){
            int tileId = AntData.getId(message);
            // int color = AntData.getColorFromPress(message);
            if (tileId == currentTile) { // could also be color == LED_COLOR_RED
                timeInterval -= timeStep;
                incrementPlayerScore(1, 0);
            } else {
                timeInterval += timeStep;
            }

            if (timeInterval <= timeStep) {
                timeInterval = timeStep;
            }

            int currentScore = getPlayerScore()[0];
            Log.i("onGameUpdate","Current Score: " + currentScore);
            if (currentScore == selectedGameType.getGoal()){
                stopGame();
            }

        }

    }

    @Override
    public void onGameEnd() {
        super.onGameEnd();
        handler.removeCallbacksAndMessages(null);
        connection.setAllTilesBlink(4, LED_COLOR_RED);

    }

    int getRandomTile() {

        while (true) {
            Random random = new Random();
            int randomTile = random.nextInt(connection.connectedTiles.size()) + 1;
            if (randomTile != currentTile) {
//                Log.i("getRandomTile","Found random " + randomTile);
                return randomTile;
            }
        }
    }
}
