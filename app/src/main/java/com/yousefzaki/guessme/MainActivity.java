package com.hanynemr.guessme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, ShakeDetector.ShakeListener {
    TextView rightWrongText;
    TextView countText;
    ImageView soundIcon;
    Button startButton;
    Random r=new Random();
    int x;
    byte wrongs;
    boolean gameStarted;

    ArrayList<TextView> views=new ArrayList<>();

    TextToSpeech tts;//memory leaks

    boolean soundOn=true;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rightWrongText=findViewById(R.id.rightWrongText);
        soundIcon=findViewById(R.id.soundIcon);
        countText=findViewById(R.id.countText);
        startButton=findViewById(R.id.startButton);
        tts=new TextToSpeech(this,this);
        pref=getPreferences(MODE_PRIVATE);
        soundOn=pref.getBoolean("sound",true);
        if (soundOn){
            soundIcon.setImageResource(R.drawable.ic_sound_on);
        }else{
            soundIcon.setImageResource(R.drawable.ic_sound_off);
        }

        Sensey.getInstance().init(this);
        Sensey.getInstance().startShakeDetection(this);
    }

    @Override
    protected void onDestroy() {
        tts.stop();
        tts.shutdown();
        Sensey.getInstance().stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor=pref.edit();
        editor.putBoolean("sound",soundOn);
        editor.apply();
        super.onBackPressed();
    }

    public void start(View view) {
        for (TextView tv : views){
            tv.setEnabled(true);
        }
        views.clear();
        wrongs=0;
        rightWrongText.setText("");
        countText.setText("");
        gameStarted=true;
        x=r.nextInt(9)+1;
        Toast.makeText(this, "number ="+x, Toast.LENGTH_SHORT).show();
    }

    public void answer(View view) {
        if (!gameStarted){
            YoYo.with(Techniques.Shake).duration(700).repeat(3).playOn(startButton);
//            Toast.makeText(this, "please click start", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView tv= (TextView) view;
        YoYo.with(Techniques.Tada).duration(500).repeat(3).playOn(tv);

        YoYo.with(Techniques.FadeIn).duration(1000).playOn(rightWrongText);
        YoYo.with(Techniques.FadeIn).duration(1000).playOn(countText);

        tv.setEnabled(false);
        views.add(tv);
        int number= Integer.parseInt(tv.getText().toString());

        if (soundOn)
            tts.speak(""+number,TextToSpeech.QUEUE_FLUSH,null,null);

        if (number==x){
            rightWrongText.setText("right");
            gameStarted=false;
        }else{
            rightWrongText.setText("wrong");
            wrongs++;
            countText.setText(""+wrongs);
        }
        if (wrongs==3){
            Toast.makeText(this, "game over", Toast.LENGTH_SHORT).show();
            gameStarted=false;
        }

    }

    @Override
    public void onInit(int i) {
//        tts.setLanguage(new Locale("ar"));
//        tts.setPitch(0.7f);
//        tts.setSpeechRate(0.7f);
    }

    public void change(View view) {
        if (soundOn){
            soundIcon.setImageResource(R.drawable.ic_sound_off);
            soundOn=false;
        }else{
            soundIcon.setImageResource(R.drawable.ic_sound_on);
            soundOn=true;
        }
    }

    @Override
    public void onShakeDetected() {

    }

    @Override
    public void onShakeStopped() {

        start(startButton);
    }
}