package com.example.playware2019;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;
import com.livelife.motolibrary.OnAntEventListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnAntEventListener
{
    MotoConnection connection;
    MotoSound sound;
    Button paringButton;
    Button randColor;
    Button startGameButton;
    TextView statusTextView;
    boolean isParing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connection=MotoConnection.getInstance();
        connection.startMotoConnection(MainActivity.this);
        connection.saveRfFrequency(16); //(Group No.)*10+6
        connection.setDeviceId(1); //Your group number
        connection.registerListener(MainActivity.this);

        connection = MotoConnection.getInstance();
        sound = MotoSound.getInstance();

        paringButton = findViewById(R .id.paringButton);
        paringButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!isParing){
                    connection.pairTilesStart();
                    paringButton.setText("Stop Paring");
                } else {
                    connection.pairTilesStop();
                    paringButton.setText("Start Paring");
                }
                isParing = !isParing;
            }
        });

        statusTextView = findViewById(R. id.statusTextView);
        startGameButton = findViewById(R. id.startGameButton);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection.unregisterListener(MainActivity.this);
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        sound.initializeSounds(this);

        randColor = findViewById(R. id.randColor);
        randColor.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Random r = new Random();
                connection.setAllTilesColor(r.nextInt( 7)+1);
            }
        });
    }

    @Override
    public void onMessageReceived(byte[] bytes, long l) {
    }
    @Override
    public void onAntServiceConnected() {
        connection.setAllTilesToInit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        connection.unregisterListener(MainActivity.this);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        //connection.startMotoConnection(MainActivity.this);
        connection.registerListener(MainActivity.this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.stopMotoConnection();
        connection.unregisterListener(MainActivity.this);
    }

    @Override
    public void onNumbersOfTilesConnected(final int i) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(i + " connected tiles");
            }
        });
    }
}



