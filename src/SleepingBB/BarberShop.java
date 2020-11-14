package SleepingBB;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BarberShop {
    private static ArrayBlockingQueue<Customer> waitingChairs;
    private static ArrayBlockingQueue<Barber> barberSleep;
    private static String barberChair;
    private final int nb_waiting_chairs;
    private final int nb_barber_chairs;

    final Lock barberSleepLock = new ReentrantLock();
    final Lock exitDoorLock = new ReentrantLock();
    final Condition barberLock  = barberSleepLock.newCondition();
    final Condition customerLock = barberSleepLock.newCondition();

    final Condition customerExit = exitDoorLock.newCondition();
    final Condition barberBring = exitDoorLock.newCondition();


    public BarberShop(int waiting_chairs, int barber_chairs) {
        this.nb_waiting_chairs = waiting_chairs;
        this.nb_barber_chairs = barber_chairs;
        waitingChairs = new ArrayBlockingQueue<Customer>(nb_waiting_chairs);
        barberSleep = new ArrayBlockingQueue<Barber>(nb_barber_chairs);
        barberChair = "barber"; // true == barber uses, false == customer uses
    }


    public void takeOrSleep(Barber barber) throws InterruptedException {
        Customer customer;

        System.out.println(barber.getTime()+" "+barber.threadInfo()+" looks at waiting chair");
        barberSleepLock.lock();

        while (waitingChairs.remainingCapacity() == nb_waiting_chairs) {
            barberChair = "barber";
            barberSleep.add(barber);
            System.out.println(barber.getTime() + " " + barber.threadInfo() + " " + "sleeps");
            barberLock.await(30000, TimeUnit.MILLISECONDS);
            System.out.println(barber.getTime() + " " + barber.threadInfo() + " " + "wakes up");
        }
        customer = waitingChairs.poll();
        if(customer == null){
            takeOrSleep(barber);
        }

        customerLock.signal();
        System.out.println(customer.getTime()+" "+customer.threadInfo()+ " is awake");

        System.out.println(customer.getTime()+" "+customer.threadInfo()+ " sits on barber chair");
        barberChair = "customer";
        barberSleepLock.unlock();

        System.out.println(customer.getTime()+" "+customer.threadInfo()+ " sleeps on barber chair");
        customer.sleepCut(barber);
        System.out.println(customer.getTime()+" "+customer.threadInfo()+ " got his haircut");

        // bring customer to exit door
        barberShowExit(barber);
        takeOrSleep(barber);
    }

    public void sitOrLeave(Customer customer) throws InterruptedException {
        Barber barber;
        boolean customerSleep = false;
        boolean customerLeave = false;

        synchronized (barberSleep) {
            barber = barberSleep.poll();
            barberSleepLock.lock();

            if (barber != null) { // barber asleep = true
                System.out.println(customer.getTime() + " " + customer.threadInfo() + " gets a chair");
                waitingChairs.add(customer);
                barberLock.signal();
                System.out.println(customer.getTime() + " " + customer.threadInfo() + " wakes the barber up");
                customerLock.await(30000, TimeUnit.MILLISECONDS);

            } else if (waitingChairs.remainingCapacity() == 0) { // no waiting chair left
                System.out.println(customer.getTime() + " " + customer.threadInfo() + " no chair. Does not enter the shop");
                customerLeave = true;

            } else { // still waiting chair available
                System.out.println(customer.getTime() + " " + customer.threadInfo() + " gets a chair");
                waitingChairs.add(customer);
                customerSleep = true;
            }
            barberSleepLock.unlock();
         }

        customerWSSleep(customerSleep, customer);
        customerExit(customerLeave, customer);
        customer.interrupt();
    }

    public void customerWSSleep(boolean customerSleep, Customer customer) throws InterruptedException {
        if(customerSleep){
            System.out.println(customer.getTime()+" "+customer.threadInfo()+" sleeps on waitingChair");
            barberSleepLock.lock();
            customerLock.await(30000, TimeUnit.MILLISECONDS);
            barberSleepLock.unlock();
        }
    }

    public void barberShowExit(Barber barber) throws InterruptedException{
        exitDoorLock.lock();
        System.out.println(barber.getTime()+" "+barber.threadInfo()+" barber show exit door");
        TimeUnit.SECONDS.sleep(5);
        customerExit.signal();
        barberBring.await(30000, TimeUnit.MILLISECONDS);
        exitDoorLock.unlock();
    }

    public void customerExit(boolean customerLeave, Customer customer) throws InterruptedException {
        if(!customerLeave){
            exitDoorLock.lock();
            customerExit.await(30000, TimeUnit.MILLISECONDS);
            System.out.println(customer.getTime()+" "+customer.threadInfo()+" customer exits through exit door");
            barberBring.signal();
            exitDoorLock.unlock();
        }
    }
}
