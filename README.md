## Dokumentering af 

Project: Eksamen - Backend - 3. semester
Af: Lars Grønberg
Klasse: Datamatiker - 3.Sem – Hold B
Dato: 04.11.2024


## Beskrivelse:
Guide API er en RESTful webtjeneste, der giver mulighed for at oprette, læse, opdatere og slette
læger og deres aftaler. API'et understøtter også brugerregistrering og -godkendelse med JWT-token-baseret
autentificering. API'et er bygget med Java og Javalin og bruger en PostgreSQL-database til at gemme data.


## Hvordan det kører

- Opret en database i din lokale Postgres-instans kaldet guide.
- Kør hovedmetoden i Main-klassen for at starte serveren på port 7070 og oprette tabellerne i databasen. For at populere
  databasen med nogle testdata kan du køre endpointet http://localhost:7070/api/?/populate.
- Se alle routes i din browser på http://localhost:7070/api/routes.
- Anmod om endpointet http://localhost:7070/api/guides/ i din browser for at se listen over guides.
- Brug .http-filen i resources-mappen til at teste routes. GET/POST/PUT/DELETE-anmodninger er tilgængelige. Du vil også
  finde securityroutes i .http-filen.


## Hvordan jeg tester:

5.4 Forskelle mellem enhedstest(unit-test) og integrationstest(test af REST-endpoints)

Enhedstest handler om at teste små dele af koden, ofte enkeltstående metoder i klasser, for at sikre, at de fungerer,
som de skal. Disse tests køres i isolation, hvilket betyder, at de ikke afhænger af databaser eller
netværksforbindelser. Derfor er enhedstest hurtigere, fordi de kan køres uden at applikationen behøver at være i drift.
I enhedstest bruger man ofte mocks og stubs til at simulere de dele af koden, som man ikke tester.

Integrationstest tester derimod, hvordan flere dele af systemet arbejder sammen. I denne opgave fokuserer jeg på, hvordan
? interagerer med databasen, for at sikre at CRUD-operationer (oprette, læse, opdatere og slette) fungerer
korrekt i en virkelighedsnær situation. Disse tests kræver oprettelse og sletning af testdata i databasen, hvilket gør
dem langsommere end enhedstest, men de giver et mere præcist billede af, hvordan systemet vil opføre sig i praksis.


## Hvordan jeg fanger og håndterer exceptions

I min kode bruger jeg try-catch-blokke til at fange exceptions, der kan opstå under kørslen. Jeg bruger også
Javalins exception-handling-mekanisme til at fange exceptions og sende passende fejlbeskeder til klienten. Dette gør det
muligt for mig at give brugeren feedback om, hvad der gik galt, og hvordan de kan rette fejlen. Jeg bruger
HTTP-statuskoder til at angive, om en anmodning var vellykket eller mislykket, og jeg inkluderer også fejlbeskeder i
JSON-responsen for at give yderligere information om, hvad der gik galt. Jeg bruger også logning til at registrere
exceptions og andre hændelser, der kan opstå under kørslen, så jeg kan spore og diagnosticere problemer efterfølgende i
logfilerne i mappen logs. I øvrigt bruger jeg også try-with-resources til at sikre, at ressourcer bliver lukket korrekt
hvilket er vigtigt for at undgå lækager og ressourceproblemer når man arbejder med databaser og filer.


## Endpoints og deres response 
GET http://localhost:7070/api/doctors/populate

HTTP/1.1 200 OK
Date: Tue, 29 Oct 2024 17:17:56 GMT
Content-Type: application/json
Content-Length: 55

{
"Message": "The Doctor Database has been populated"
}
Response file saved.
> 2024-10-29T181756.200.json

Response code: 200 (OK); Time: 136ms (136 ms); Content length: 55 bytes (55 B)

POST http://localhost:7070/api/auth/register/

HTTP/1.1 201 Created
Date: Tue, 29 Oct 2024 17:18:24 GMT
Content-Type: application/json
Content-Length: 211

{
"token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJMYXJzIEdyw7huYmVyZyIsInN1YiI6InVzZXIiLCJleHAiOjE3MzAyMjQxMDQsInJvbGVzIjoiVVNFUiIsInVzZXJuYW1lIjoidXNlciJ9.cI_r9L2Tw_gtGAre5sKkP1-_k0MTTMroeGGEQzaNpkU",
"username": "user"
}
Response file saved.
> 2024-10-29T181824.201.json

Response code: 201 (Created); Time: 179ms (179 ms); Content length: 211 bytes (211 B)

POST http://localhost:7070/api/auth/login/

HTTP/1.1 200 OK
Date: Tue, 29 Oct 2024 17:18:35 GMT
Content-Type: application/json
Content-Length: 211

{
"token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJMYXJzIEdyw7huYmVyZyIsInN1YiI6InVzZXIiLCJleHAiOjE3MzAyMjQxMTUsInJvbGVzIjoidXNlciIsInVzZXJuYW1lIjoidXNlciJ9.tRWE23R8r7lJxq-faV3SEIAravMROhsp7JyD979FftU",
"username": "user"
}
Response file saved.
> 2024-10-29T181835.200.json

Response code: 200 (OK); Time: 72ms (72 ms); Content length: 211 bytes (211 B)

POST http://localhost:7070/api/auth/user/addrole/

HTTP/1.1 200 OK
Date: Tue, 29 Oct 2024 17:18:50 GMT
Content-Type: application/json
Content-Length: 37

{
"msg": "Role superman added to user"
}
Response file saved.
> 2024-10-29T181850.200.json

Response code: 200 (OK); Time: 34ms (34 ms); Content length: 37 bytes (37 B)

GET http://localhost:7070/api/guides


6.4 Forskelle mellem almindelige enhedstests og tests udført i denne opgave
Testmiljø:

    Almindelige enhedstests: Udføres typisk i et isoleret miljø, hvor testene ikke interagerer med eksterne systemer som
    databaser eller netværk. Fokus er på at teste individuelle enheder (metoder eller klasser) i koden. Tests udført i
    denne opgave: Inkluderer integration med en database og potentielt andre eksterne tjenester. Disse tests kræver
    opsætning af et testmiljø, hvor data kan oprettes, ændres og slettes, hvilket muliggør en mere realistisk vurdering
    af systemets funktionalitet.

Testtyper:
    Jeg har inkluderet en integrationstest-klasse og en enhedstest-klasse i mit projekt.
    I min enhedstest-klasse tester jeg på CRUD-metoderne i GuideDAO-klassen. Her fokuserer jeg på at validere logikken.
    Jeg kunne bruge mocks og stubs til at simulere eksterne afhængigheder men pga. tidsmangel og for at
    sikre, at metoderne fungerer korrekt, har jeg valgt at teste direkte med en testdatabase og testdata.

    I min integrationstest-klasse tester jeg på de endpoints, der er tilgængelige i API'et. Her fokuserer jeg på at
    validere, at API'et fungerer korrekt, og at det opfylder de krav, der er defineret i specifikationerne. Jeg bruger
    en testdatabase til at oprette, ændre og slette data, så jeg kan teste, hvordan API'et reagerer på forskellige
    anmodninger. Jeg bruger Rest Assured og Hamcrest til at formulere HTTP-anmodninger og validere svar. Dette giver
    mig mulighed for at teste, hvordan hele systemet interagerer gennem API'et, snarere end at teste individuelle
    enheder eller metoder isoleret set.

Datastyring:

    Almindelige enhedstests: Bruger typisk mocks eller stubs for at simulere interaktioner med eksterne afhængigheder.
    Dette gør det muligt at teste logikken uden at bekymre sig om tilstanden af eksterne systemer. Tests udført i denne
    opgave: Kan involvere faktiske databaseopkald, hvilket betyder, at data skal oprettes og ryddes op i testmiljøet.
    Dette kan kræve ekstra kode til at håndtere opsætning og oprydning af testdata.

Fejlfindingsinformation:

    Almindelige enhedstests: Giver ofte mere direkte fejlfindingsinformation, da de fokuserer på en enkelt enhed.
    Hvis en test fejler, ved man, at det er en specifik metode, der skal undersøges. Tests udført i denne opgave: Kan
    være mere komplekse at fejlsøge, fordi fejl kan opstå fra interaktionen mellem flere enheder eller fra eksterne
    systemer, hvilket kan gøre det sværere at identificere den nøjagtige kilde til problemet.

Testdækning:

    Almindelige enhedstests: Tildeler typisk høj dækning på de enkelte metoder, men giver ikke nødvendigvis et komplet
    billede af, hvordan hele systemet fungerer sammen. Tests udført i denne opgave: Stræber efter at dække de vigtigste
    brugerhistorier og systeminteraktioner, hvilket kan hjælpe med at afsløre problemer, der ikke ville blive fanget i
    isolerede enhedstests.

Formålet med Rest Assured

Rest Assured er et Java-bibliotek, der gør det nemt at teste RESTful webtjenester ved at levere en simpel og intuitiv
syntaks til at formulere HTTP-anmodninger og validere svar. Jeg ønsker at teste endpoints på denne måde, fordi det giver
mig mulighed for at verificere, at API'et fungerer som forventet, og at det opfylder de krav, der er defineret i mine
specifikationer. Ved at automatisere tests af mine API-endpoints kan jeg også sikre, at ændringer i koden ikke bryder
eksisterende funktionalitet, hvilket er vigtigt for at opretholde pålideligheden af applikationen.

Opsætning af databasen til tests

Til opsætning af databasen til tests bruger jeg en dedikeret testdatabase, hvor jeg kan oprette, ændre og slette data
uden at påvirke produktionsdataene. I testmiljøet bruger jeg en en separat instans af vores produktionsdatabase for at
sikre, at tests kører i et isoleret miljø. Før hver test rydder jeg op i databasen ved at slette eksisterende data og
indlæse nødvendige testdata for at sikre, at hver test kører i et konsistent og kontrolleret miljø.

Forskelle mellem testing af REST endpoints og enhedstests 

Test af RESTful endpoints adskiller sig fra enhedstests ved, at de fokuserer på at validere, hvordan hele systemet
interagerer gennem API'et, snarere end at teste individuelle enheder eller metoder isoleret set. REST endpoint-tests
involverer typisk at sende HTTP-anmodninger og kontrollere svar, hvilket kræver, at systemet kører som helhed. Dette kan
inkludere at validere HTTP-statuskoder, svardata og hvordan systemet håndterer forskellige input. I modsætning hertil
er mine enhedstest primært fokuseret på at verificere den interne logik af individuelle metoder eller klasser, uden
at tage hensyn til den samlede applikations funktionalitet.


## Afslutningsvis
Jeg vidste godt at nerver og stress ville spille en stor rolle i det projekt. Jeg ér ikke nogen hurtig koder på nuværende
tidspunkt og kan ikke arbejde under tidspres desværre. Jeg har dog gjort mit bedste.









