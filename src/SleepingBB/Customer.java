package SleepingBB;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Random;

public class Customer implements Runnable {

    int customer_id;
    BarberShop barber_shop;

    // constructor
    public Customer(int id, BarberShop shop){
        this.customer_id = id;
        this.barber_shop = shop;
    }

    public String threadInfo() {
        String threadName = Thread.currentThread().getName(); //get current running thread
        String info = "Customers " + customer_id + " | " + threadName;
        return info;
    }

    public String getTime(){
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
    }

    @Override
    public void run() {
        System.out.println(getTime()+" "+threadInfo() + " created");
        synchronized(this){
            try {
                barber_shop.sitOrLeave(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void sleepCut(Barber barber) throws InterruptedException {
        Random random = new Random();
        System.out.println(barber.getTime()+" "+barber.threadInfo()+" is cutting hair");
        Thread.sleep(Math.abs((int) Math.round(random.nextGaussian() * 2 +8))*1000);
    }

    public void interrupt() {
        Thread.interrupted();
        return;
    }
}