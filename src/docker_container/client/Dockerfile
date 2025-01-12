# Verwende ein offizielles OpenJDK-Image als Basis
FROM openjdk:11

# Erstelle das Verzeichnis, das der Paketstruktur entspricht
RUN mkdir -p /app/docker_container/client

# Kopiere die Client-Datei in das entsprechende Verzeichnis
COPY ./Client.java /app/docker_container/client/Client.java

# Wechsle in das Verzeichnis /app
WORKDIR /app

# Kompiliere und starte den Client
CMD javac docker_container/client/Client.java && java docker_container.client.Client
