package game.models;

import java.util.Timer;
import java.util.TimerTask;

public class Time extends TimerTask {

    private Timer timer;

    private int minutes;
    private int seconds;

    public Time() {
        this.minutes = 0;
        this.seconds = 0;

        // Increase time every second
        this.timer = new Timer();
        timer.scheduleAtFixedRate(this, 1000, 1000);
    }

    @Override
    public void run() {
        tick();
    }

    private void tick() {
        if (seconds == 59) {
            seconds = 0;
            ++minutes;
        } else {
            ++seconds;
        }
    }
    
    public void add(int penaltySeconds) {
        while (penaltySeconds > 0) {
            tick();
            penaltySeconds--;
        }
    }

    @Override
    public String toString() {
        String prefix = "0";

        String sMinutes;
        String sSeconds;

        sMinutes = (minutes < 10) ? prefix + minutes : minutes + "";
        sSeconds = (seconds < 10) ? prefix + seconds : seconds + "";

        return sMinutes + ":" + sSeconds;
    }
}
