package edu.umd.umiacs.newsstand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

/**************************************************
 *
 *  Splash screen class.
 *
 *  To view the splash screen, you must exit the
 *  application, by tapping the "back" button from
 *  the main screen of NewsStand.
 *
 ***************************************************/

public class SplashScreen extends Activity {

    protected boolean _active = false;  // set this to true to display splash screen
    protected int _splashTime = 3000; // time to display the splash screen in ms
    private boolean go;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        final SplashScreen splash = this;

        // thread for displaying the SplashScreen
        Thread splashThread = new Thread() {
            @Override
            public void run() {
            	go = true;
            	while (go) {
                try {
                    int waited = 0;
                    while(_active && (waited < _splashTime)) {
                        sleep(100);
                        if(_active) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    Intent i = new Intent();
                    i.setClass(splash, NewsStand.class);
                	startActivity(i);
                    //stop();
                	go = false;
                }
            	}
            }
        };
        splashThread.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }

}
