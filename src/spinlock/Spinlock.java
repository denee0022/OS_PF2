package spinlock;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Spinlock {

    private static final int NUM_TRIALS = 1000; // Anzahl der Messungen (Trials)
    private static final int NUM_COMMUNICATIONS_PER_TRIAL = 100; // Anzahl der Kommunikationen pro Trial
    private static List<Long> minLatencies = new ArrayList<>();

    // Atomare Variable als Spinlock
    private static final AtomicBoolean spinlock = new AtomicBoolean(false);

    public static void main(String[] args)  {
        for (int i = 0; i < NUM_TRIALS; i++) {
            long trialMinLatency = Long.MAX_VALUE; // Startwert für minimale Latenz im aktuellen Trial
            //System.out.println((i + 1) + ". Trial");
            // Wiederhole mehrere Kommunikationsrunden innerhalb eines Trials
            for (int j = 0; j < NUM_COMMUNICATIONS_PER_TRIAL; j++) {
                //System.out.println((j + 1) + ". Kommunikation");
                // Thread 1: Simuliert eine Arbeit und versucht, den Spinlock zu setzen
                Thread thread1 = new Thread(() -> {
                    // Versuche, den Spinlock zu bekommen
                    while (!spinlock.compareAndSet(false, true)) {
                        // Busy Waiting: Warte, bis der Spinlock frei ist
                        //System.out.println("Thread 1 waiting");
                    }
                    // Kommunikation hat stattgefunden
                    //System.out.println("Thread 1 completed");
                    spinlock.set(false);
                });

                // Thread 2: Simuliert eine Arbeit und versucht, den Spinlock zu setzen
                Thread thread2 = new Thread(() -> {
                    // Versuche, den Spinlock zu bekommen
                    while (!spinlock.compareAndSet(false, true)) {
                        // Busy Waiting: Warte, bis der Spinlock frei ist
                        //System.out.println("Thread 2 waiting");
                    }
                    // Kommunikation hat stattgefunden
                    //System.out.println("Thread 2 success");
                    spinlock.set(false);
                });

                // Startzeit messen
                long startTime = System.nanoTime();
                // Beide Threads starten
                thread1.start();
                thread2.start();

                try {
                    thread1.join();
                    thread2.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // Endzeit messen
                long endTime = System.nanoTime();
                long duration = endTime - startTime; // Dauer in Nanosekunden
                // Minimale Latenz im aktuellen Trial finden
                trialMinLatency = Math.min(trialMinLatency, duration);
                // Reset des Spinlocks für den nächsten Kommunikationsversuch
                spinlock.set(false);
            }
            // Minimale Latenz des aktuellen Trials zur Liste hinzufügen
            minLatencies.add(trialMinLatency);
        }
        System.out.println("Spinlock:");
        // Berechnung der minimalen Latenz und des Konfidenzintervalls
        calculatecalculateConfidenceInterval(minLatencies);
        // Speichern der minimalen Latenzen in einer CSV-Datei
        toCSV(minLatencies);
    }
    private static void calculatecalculateConfidenceInterval(List<Long> minLatencies) {
        double meanMinLatency = minLatencies.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double stdevMinLatency = Math.sqrt(minLatencies.stream().mapToLong(latency -> (long) ((latency - meanMinLatency) * (latency - meanMinLatency))).average().orElse(0.0));
        double confidenceInterval = 1.96 * (stdevMinLatency / Math.sqrt(minLatencies.size()));
        double lowerBound = meanMinLatency - confidenceInterval;
        double upperBound = meanMinLatency + confidenceInterval;

        System.out.printf("Mean Minimum Latency: %.2f ns%n", meanMinLatency);
        System.out.printf("95%% Confidence Interval: ±%.2f ns%n", confidenceInterval);
        System.out.printf("95%% Confidence Interval: [%.2f; %.2f] ns%n", lowerBound, upperBound);
    }

    private static void toCSV(List<Long> minLatencies){
        try (PrintWriter writer = new PrintWriter(new FileWriter("min_latencies_spinlock.csv"))) {
            writer.println("Trial,MinLatency(ns)");
            for (int i = 0; i < minLatencies.size(); i++) {
                writer.printf("%d,%d%n", i + 1, minLatencies.get(i));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}