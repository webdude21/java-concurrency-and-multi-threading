package eu.webdude.multithreading.interuptthread;

public class ThreadInterupting {

    public static void main(String[] args) throws InterruptedException {

        Runnable runnable = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Running");
            }

            System.out.println("Interrupted, so stoping");
        };

        Thread a = new Thread(runnable);

        a.start();

        Thread.sleep(2);

        a.interrupt();
    }
}
