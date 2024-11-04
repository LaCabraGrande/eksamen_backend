# Start with Amazon Corretto 17 Alpine base image
FROM amazoncorretto:17-alpine
# FROM angiver basisbilledet, som Docker vil bygge videre på.
# Amazon Corretto 17 (Java 17) på Alpine Linux bruges, da Alpine er en letvægts Linux-distribution.

# Install curl on Alpine
RUN apk update && apk add --no-cache curl
# Denne kommando opdaterer pakkelisten (`apk update`) og installerer `curl` uden cache (`apk add --no-cache curl`).
# `curl` kan bruges til at hente data fra en URL og er ofte nyttig til debugging og integration.

# Install bash on Alpine
RUN apk add --no-cache bash
# Installerer `bash` på Alpine Linux. Bash er en shell, som kan bruges til mere avancerede scripts.
# `--no-cache` sikrer, at ingen cachede pakker gemmes, hvilket holder containeren mindre.

# Copy the jar file into the image
COPY target/app.jar /app.jar
# Kopierer applikationens jar-fil (`app.jar`) fra `target`-mappen på værtsmaskinen til rodmappen (`/`) i containeren.
# Det er vigtigt, at `app.jar` eksisterer i `target`-mappen, når du bygger containeren.

# Expose the port your app runs on
EXPOSE 7070
# Informerer Docker om, at containeren vil lytte på port 7070.
# Dette hjælper med netværksrouting, når containeren køres.

# Command to run your app
CMD ["java", "-jar", "/app.jar"]
# `CMD` definerer standardkommandoen, der eksekveres, når containeren starter.
# Her køres `java -jar /app.jar`, som starter applikationen.

