package SleepingBB;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String args[]) throws InterruptedException {
        int nb_of_barbers = 1;
        int nb_of_waitingChair = 2;
        Random random = new Random();
        int label = 0;

        ExecutorService executor = Executors.newFixedThreadPool(10);

        System.out.println("Beginning of the day. Barbers get in the shop and prepare themselves....");
        BarberShop shop = new BarberShop(nb_of_waitingChair, nb_of_barbers);

        Runnable barber = new Barber(0, shop);
        executor.execute(barber);

        TimeUnit.SECONDS.sleep(1);

        System.out.println("------------------------------------------------------------------------");
        System.out.println("The shop is now open to customers....");

        while(true){
            TimeUnit.SECONDS.sleep(Math.abs((int) Math.round(random.nextGaussian() * 2+3)));
            Runnable customer = new Customer(label, shop);
            executor.execute(customer);
            label++;
        }
    }
}