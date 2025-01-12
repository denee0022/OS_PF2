package docker_container.client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static List<Long> minLatencies = new ArrayList<>();
    private static final int NUM_TRIALS = 1000; // Anzahl der Messungen (Trials)
    private static final int NUM_COMMUNICATIONS_PER_TRIAL = 100; // Anzahl der Kommunikationen pro Trial
    private static final int port = 12345; // Portnummer für die Kommunikation
    private static final String serverAddress = "docker_container-server-1"; // IP des Servers im Docker-Container

    public static void main(String[] args) {
        for (int trial = 0; trial < NUM_TRIALS; trial++) {
            long trialMinLatency = Long.MAX_VALUE; // Minimale Latenz für das aktuelle Trial

            for (int message = 0; message < NUM_COMMUNICATIONS_PER_TRIAL; message++) {
                try (Socket socket = new Socket(serverAddress, port);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    String messageContent = "Testnachricht " + message;
                    // Nachricht an den Server senden
                    out.println(messageContent);

                    // Zeitmessung starten
                    long startTime = System.nanoTime();

                    // Antwort vom Server empfangen
                    String response = in.readLine();

                    // Zeitmessung beenden
                    long endTime = System.nanoTime();
                    long latency = endTime - startTime;

                    // Minimale Latenz für dieses Trial ermitteln
                    if (latency < trialMinLatency) {
                        trialMinLatency = latency;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            minLatencies.add(trialMinLatency);
        }

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
        try (PrintWriter writer = new PrintWriter(new FileWriter("/app/output/socket_docker_min_latencies.csv"))) {
            writer.println("Trial,MinLatency(ns)");
            for (int i = 0; i < minLatencies.size(); i++) {
                writer.printf("%d,%d%n", i + 1, minLatencies.get(i));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
