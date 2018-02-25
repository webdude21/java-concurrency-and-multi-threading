package eu.webdude.deadlock;

public class RunningA {

    public static void main(String[] args) throws InterruptedException {
        A a = new A();

        Thread t1 = new Thread(a::a);
        t1.start();
        Thread t2 = new Thread(a::b);
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Finished");
    }
}
