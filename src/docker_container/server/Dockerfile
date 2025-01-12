# Verwende ein offizielles OpenJDK-Image als Basis
FROM openjdk:11

# Erstelle das Verzeichnis, das der Paketstruktur entspricht
RUN mkdir -p /app/docker_container/server

# Kopiere die Server-Datei in das entsprechende Verzeichnis
COPY ./Server.java /app/docker_container/server/Server.java

# Wechsle in das Verzeichnis /app
WORKDIR /app

# Kompiliere und starte den Server
CMD javac docker_container/server/Server.java && java docker_container.server.Server
