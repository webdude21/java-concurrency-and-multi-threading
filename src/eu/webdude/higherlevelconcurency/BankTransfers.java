package eu.webdude.higherlevelconcurency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BankTransfers {

    public static void main(String[] args) {
        Random random = new Random();
        BankAccount batmantsAccount = new BankAccount("Batman");
        BankAccount robinsAccount = new BankAccount("Robin");
        List<Future<?>> futures = new ArrayList<>();

        List<BankAccount> bankAccounts = Arrays.asList(batmantsAccount, robinsAccount);

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        Runnable batmanGivesToRobin = () -> {
            int transactionSum = random.nextInt(1000);
            System.out.printf("Batman gives Robin %d dollars!%n", transactionSum);
            batmantsAccount.add(transactionSum);
            robinsAccount.substract(transactionSum);
        };

        Runnable robinGivesToBatman = () -> {
            int transactionSum = random.nextInt(1000);
            System.out.printf("Robin gives Batman %d dollars!%n", transactionSum);
            robinsAccount.add(transactionSum);
            batmantsAccount.substract(transactionSum);
        };


        Runnable printState = () -> {
            bankAccounts.forEach(bankAccount -> System.out.println(bankAccount.getAccountInfo()));
            System.out.println("The average of all accounts is " + (int) bankAccounts
                    .stream()
                    .mapToInt(BankAccount::getCurrentAmount)
                    .average().orElse(0));
        };

        for (int i = 0; i < 100_000; i++) {
            Future<?> future = executorService.submit(i % 2 == 0 ? robinGivesToBatman : batmanGivesToRobin);
            futures.add(future);
            if (i % 5 == 0) {
                futures.add(executorService.submit(printState));
            }
        }

        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        System.out.println("The final result is: ");
        printState.run();
    }

    public static class BankAccount {
        private List<Integer> statements;
        private int currentAmount;
        private String accountHolderName;
        private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private Lock readLock = readWriteLock.readLock();
        private Lock writeLock = readWriteLock.writeLock();

        public BankAccount(String accountHolderName) {
            this(10_000, accountHolderName, new ArrayList<>());
            statements.add(10_000);
        }

        public BankAccount(int startAmount, String accountHolderName, List<Integer> statements) {
            this.statements = statements;
            this.currentAmount = startAmount;
            this.accountHolderName = accountHolderName;
        }

        public int getCurrentAmount() {
            try {
                readLock.lock();
                return currentAmount;
            } finally {
                readLock.unlock();
            }
        }

        public void add(int sum) {
            try {
                writeLock.lock();
                statements.add(sum);
                currentAmount += sum;
            } finally {
                writeLock.unlock();
            }
        }

        public void substract(int sum) {
            try {
                writeLock.lock();
                statements.add(-sum);
                currentAmount -= sum;
            } finally {
                writeLock.unlock();
            }
        }

        public String getAccountInfo() {
            try {
                readLock.lock();
                String balance = String.format("Balance of %s is %d", accountHolderName, currentAmount);
                return balance;
            } finally {
                readLock.unlock();
            }
        }
    }
}
