Demo bruker med en del test data er tilgjengelig på telefonnummer: 44556677

Om appen:

Treningsprogrammer og aktiviteter:
Vi har brukt AppProgramTypes som en oversikt over tilgjengelige aktivitets kategorier.
Disse er predefinert og kan ikke redigeres av brukere.
Vi har diffrensiert på inne- og ute- aktivitet med å bruke data for fargekoden tilknyttet program typen.
Ved opprettelse av et UserProgram får bruker valg om inne eller ute og vi har gjort det slik at
det er kun inne aktiviter som får mulighet til å knyttes til forskjellige UserExercise, mens ute aktivitet
er ment som mere generelle økter som løping etc uten behov for tilknyttede øvelser.


Datalagring og -behandling:
Vi har brukt API'et i stor grad, og all data brukt i appen lagres og hentes via det.
I tillegg har vi satt opp en lokal room database som fungerer som mellomlagring av data for den gitte brukeren.
Data som trengs for visning i UI hentes kun fra Room DB og legges i LiveData/StateFlow i ViewModel for bruk.

Dataflyten går slik: API -> Room DB -> ViewModel
Det er kun ved app oppstart, login eller trykk på 'oppdater data' i toolbaren at det gjøres en
fullstening oppfrisking av brukerens data.
Dette gjør at appen ikke er avhengig av konstant kontakt med API for å fungere og begrenser også
antallet nødvendige forespørsler til API betraktelig.

Når brukeren foretar seg noe som lager/endrer/sletter data feks ved opprettelse av et UserProgram sendes først en POST over nettverk til API.
Responsen sjekkes og hvis den ble lagret ok så INSERT'es det nye UserProgram til Room DB.
Dette holder mellomlagringen i sync og det er ikke nødvendig en fullstending oppfriskning av relevant table i Room DB.
Hvis data endres via en annen mobilenhet vil appen automatisk komme i sync igjen ved app restart eller trykk på 'oppdater data'.

Vi hadde en liten vision om å prøve å gjøre appen så sømløs som mulig i bruk selv ved tap av internet.
Slik den er nå så får bruker varsel hvis det ikke er kontakt med server ved forsøk på å hente data.
Målet var også at selv ved opprettelse av uansett ny data skulle appen håndtere det uten internet.
Dette er implementert for opprettelse av en ny UserProgramSession hvor det er mulig å registrere en 
treningsøkt uten nettverk. UserProgramSession med tilhørende UserProgramSessionData/Photos lagres i Room DB
på vanlig vis og i en ekstra 'offline table' for data som ikke er POST'et til API enda.
Ved restart eller 'oppdater data' sjekkes det først om offline table innholder noe data.
Hvis ja, POST'es dette først til API og tømmer offline table, før all oppfrisket data hentes ned igjen fra API og data kommer i sync.
Vi fikk dessverre ikke laget offline handling for andre aspekter grunnet tidsbegrensninger.


Bruk av GPS:
Vi har brukt 'Google Play Services Location API' for sporing av posisjonsdata.
Ved registering av en økt kan bruke huke av for 'bruk GPS'.
Det vil da komme en popup om å gi tilatelse for bruk av telefonens stedstjenester som må godkjennes
for at appen skal ha rettigheter til å utføre tracking.


Bruk av Maps:
Vi har brukt Google sitt API 'Maps SDK for Android' for å kunne vise kart med track over hvor man
har beveget seg utifra gps koordinater lagret i UserSessionData float som latitude og longitude.
Oppsett for visning av Map i appen er gjort i forhold til veiledningen til Google.


Bruk av Kamera:
Vi har lagt inn mulighet for bruker å ta nytt bilde eller bruke eksisterende fra biblotek ved opprettelse
av nye UserExercise eller UserProgramSession.
Siden API'et ikke er egnet for å laste opp bilde data, men kun har mulighet for å legge inn en bilde URL
må bildet først lastes opp til en host hvor det kan hentes ned igjen med URL.
Dette finnes garantert lett tilgjengelige API'ersom kan få dette til, men vi valgte ikke dessverre ikke å
bruke tid på å sette oppe dette. Bilde blir derfor ikke gjort noe med etter takning.




[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/aLfmFsvB)
