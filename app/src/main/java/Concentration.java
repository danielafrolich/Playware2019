import android.content.Context;
import android.os.Handler;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.GameType;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.livelife.motolibrary.AntData.CMD_SET_IDLE;
import static com.livelife.motolibrary.AntData.LED_COLOR_OFF;
import static com.livelife.motolibrary.AntData.LED_COLOR_RED;

public class Concentration extends Game {

    MotoConnection connection = MotoConnection.getInstance();
    MotoSound sound = MotoSound.getInstance();
    HashMap<Integer, Integer> hiddenColors = new HashMap<>();
    ArrayList<Integer> selectedTiles = new ArrayList<>();
    public Handler showColorHandler;

    public Concentration(Context context){
        setName("Concentration");
        setMaxPlayers(1);
        setGameId(3);
        GameType gt1 = new GameType(1, GameType.GAME_TYPE_SPEED, 1, "default", 1);
        addGameType(gt1);
        sound.initializeSounds(context);

    }

    public boolean isOdd(int num) {
        boolean odd = true;
        if(num % 2 == 0){
            odd = false;
        }
        return odd;
    }

    @Override
    public void onGameStart() {
        super.onGameStart();

        showColorHandler = new Handler();
        selectedTiles.clear();
        ArrayList<Integer> shuffled = new ArrayList<>(connection.connectedTiles);
        Collections.shuffle(shuffled);

        int col = 0;
        int tile = 1;
        for (Integer t: shuffled){
            connection.setTileColor(AntData.LED_COLOR_WHITE, t);
            hiddenColors.put(t, AntData.allColors().get(col));
            tile++;
            if(isOdd(tile)){
                col++;
            }
        }

        if(isOdd(shuffled.size())){
            Integer last = shuffled.get(shuffled.size()-1);
            connection.setTileColor(LED_COLOR_OFF, last);
        }
    }

    @Override
    public void onGameUpdate(byte[] message) {
        super.onGameUpdate(message);

        int event = AntData.getCommand(message);
        int tileId = AntData.getId(message);
        if(event == AntData.EVENT_PRESS){
            if(selectedTiles.size() == 0){
                selectedTiles.add(tileId);
                int currColor = hiddenColors.get(tileId);
                connection.setTileColor(currColor, tileId);
                sound.playPress1();
            }else if(selectedTiles.size() == 1){
                int prevTile = selectedTiles.get(0);
                int prevColor = hiddenColors.get(prevTile);
                if(prevTile == tileId){
                    return;
                }

                int currColor = hiddenColors.get(tileId);
                selectedTiles.add(tileId);
                connection.setTileColor(currColor, tileId);
                if(prevColor == currColor){
                    incrementPlayerScore(1, 0);
                    sound.playMatched();
                    showColorHandler.postDelayed(showColorMatchedRunnable, 1000);
                }else{
                    sound.playError();
                    showColorHandler.postDelayed(showColorErrorRunnable, 1000);
                }
            }
        }
    }

    public Runnable showColorMatchedRunnable = new Runnable() {
        public void run() {
            if (selectedTiles.size() == 2) {
                for (int t: selectedTiles) {
                    connection.setTileIdle(LED_COLOR_OFF, t);
                }
                selectedTiles.clear();
            }
            // Check end session
            int matched = 0;
            for(Integer t:connection.connectedTiles) {
                AntData data = connection.getCurrentDataForTile(t);
                int status = AntData.getCommand(data.getPayload());
                if(status == CMD_SET_IDLE) {
                    matched++;
                }
            }
            if(matched == connection.connectedTiles.size()) {
                stopGame();
            }
        }
    };


    Runnable showColorErrorRunnable = new Runnable() {
        @Override
        public void run() {
            if(selectedTiles.size() == 2){
                for(int t: selectedTiles){
                    connection.setTileColor(LED_COLOR_OFF, t);
                }

                selectedTiles.clear();
            }
        }
    };

    @Override
    public void onGameEnd() {
        super.onGameEnd();
        connection.setAllTilesBlink(4, LED_COLOR_RED);
    };
}

