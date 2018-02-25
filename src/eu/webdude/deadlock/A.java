package eu.webdude.deadlock;

public class A {

    private final Object redKey = new Object();
    private final Object blueKey = new Object();

    public void a() {
        synchronized (redKey) {
            printInfo("a");
            b();
        }
    }

    public void b() {
        synchronized (blueKey) {
            printInfo("b");
            c();
        }
    }

    public void c() {
        synchronized (redKey) {
            printInfo("c");
        }
    }

    private void printInfo(String methodName) {
        System.out.printf("[%s] I'm in %s()%n", Thread.currentThread().getName(), methodName);
    }
}
