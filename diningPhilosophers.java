import java.util.Scanner;
import java.util.concurrent.Semaphore;

class Fork {
    private final int id;
    private boolean forkBeingUsed = false; 
    public Fork(int id) {
        this.id = id;
    }

    public synchronized void take() throws InterruptedException {
        while (forkBeingUsed) {
            wait();
        }
        forkBeingUsed = true;
        System.out.println("Fork " + id + " is being used.");
    }

    public synchronized void putDown() {
        forkBeingUsed = false;
        System.out.println("Fork " + id + " is now put down.");
        notify();
    }

    public int getId() {
        return id;
    }
}

class Waiting {
    private final Semaphore maxPhilosophers;

    public Waiting(int maxAllowed) {
        this.maxPhilosophers = new Semaphore(maxAllowed, true);
    }

    public void wantsToEat() throws InterruptedException {
        maxPhilosophers.acquire();
    }

    public void doneEating() {
        maxPhilosophers.release();
    }
}

class Philosopher implements Runnable {
    private final int philosopherId;
    private final Fork leftFork;
    private final Fork rightFork;
    private final int numMeals;
    private final Waiting waiting;
    private static final int THINKING_TIME = 1000;
    private static final int EATING_TIME = 1000;

    public Philosopher(int philosopherId, Fork leftFork, Fork rightFork, int numMeals, Waiting waiting) {
        this.philosopherId = philosopherId;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.numMeals = numMeals;
        this.waiting = waiting;
    }

    private void thinks() throws InterruptedException {
        System.out.println("Philosopher " + philosopherId + " is thinking.");
        Thread.sleep(THINKING_TIME);
    }

    private void consume() throws InterruptedException {
        System.out.println("Philosopher " + philosopherId + " is consuming a meal.");
        Thread.sleep(EATING_TIME);
    }

    private void acquireForks() throws InterruptedException {
        waiting.wantsToEat();
        leftFork.take();
        rightFork.take();
        System.out.println("Philosopher " + philosopherId + " now has both forks.");
    }

    private void releaseForks() {
        leftFork.putDown();
        rightFork.putDown();
        waiting.doneEating();
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < numMeals; i++) {
                thinks();
                acquireForks();
                consume();
                releaseForks();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Philosopher " + philosopherId + " was interrupted.");
        }
    }
}

public class diningPhilosophers {
    private static final int NUM_PHILOSOPHERS = 5;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("How many rounds would you like? ");
        int NUM_ROUNDS = scanner.nextInt();
        scanner.close();

        Fork[] forks = new Fork[NUM_PHILOSOPHERS];
        Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
        Waiting waiting = new Waiting(NUM_PHILOSOPHERS - 1);

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new Fork(i);
        }

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            Fork leftFork = forks[i];
            Fork rightFork = forks[(i + 1) % NUM_PHILOSOPHERS];
            philosophers[i] = new Philosopher(i + 1, leftFork, rightFork, NUM_ROUNDS, waiting);
            new Thread(philosophers[i], "Philosopher " + (i + 1)).start();
        }
    }
}