package SleepingBB;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Barber implements Runnable {

    private int barber_id;
    BarberShop barber_shop;

    public Barber(int id, BarberShop shop){
        this.barber_id = id;
        this.barber_shop = shop;
    }

    public String threadInfo() {
        String threadName = Thread.currentThread().getName(); //get current running thread
        String info = "Barber " + barber_id + " | " + threadName;
        return info;
    }

    public String getTime(){
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
    }

    @Override
    public void run() {
        System.out.println(getTime()+" "+threadInfo()+ " created");
        synchronized(this){
            try {
                barber_shop.takeOrSleep(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
