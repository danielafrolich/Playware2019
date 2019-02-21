package com.example.playware2019;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.GameType;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;

import static com.livelife.motolibrary.AntData.LED_COLOR_OFF;
import static com.livelife.motolibrary.AntData.LED_COLOR_RED;

public class ColorRace extends Game {

    MotoConnection connection = MotoConnection.getInstance();
    MotoSound sound = MotoSound.getInstance();

    ColorRace(){
        setName("Color Race");
        setDescription("A nice game");

        GameType gt = new GameType(1, GameType.GAME_TYPE_TIME, 30, "1 Player 30 sec", 1);
        addGameType(gt);

        GameType gt2 = new GameType(2, GameType.GAME_TYPE_TIME, 60, "1 Player 1 min", 1);
        addGameType(gt2);

        GameType gt3 = new GameType(3, GameType.GAME_TYPE_TIME, 120, "1 Player 2 min", 1);
        addGameType(gt3);
    }

    @Override
    public void onGameStart() {
        super.onGameStart();
        connection.setAllTilesIdle(LED_COLOR_OFF);

        int randomTile = connection.randomIdleTile();
        connection.setTileColor(LED_COLOR_RED, randomTile);
    }

    @Override
    public void onGameUpdate(byte[] message) {
        super.onGameUpdate(message);

        int event = AntData.getCommand(message);
        int tileId = AntData.getId(message);

        if (event == AntData.EVENT_PRESS){
            incrementPlayerScore(1, 0);
            sound.playMatched();

            int randomTile = connection.randomIdleTile();
            connection.setTileIdle(LED_COLOR_OFF, tileId);

            connection.setTileColor(LED_COLOR_RED, randomTile);
        }
    }

    @Override
    public void onGameEnd() {
        super.onGameEnd();

        connection.setAllTilesBlink(4, LED_COLOR_RED);
    }
}

