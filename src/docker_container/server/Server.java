package docker_container.server;

import java.io.*;
import java.net.*;

public class Server{
    public static void main(String[] args) {
        int port = 12345; // Portnummer für die Kommunikation
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    // Nachricht vom Client empfangen
                    String message = in.readLine();
                    // Antwort senden
                    out.println("Antwort vom Server: " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}