# FormatikLab Tank Android v0.1.1

App Android leggera per FormatikLab Tank Server v0.3.0.

## Funzioni

- Accesso tramite username/password cliente del Tank Server.
- Sessione mantenuta tramite cookie HTTP-only del server.
- Dashboard WebView: mostra tutti i sensori associati al cliente, esattamente come la pagina web responsive.
- Aggiornamento automatico demandato alla dashboard server (v0.2.5+).
- Pulsante Aggiorna.
- Pulsante Server per modificare l'indirizzo del backend.
- Pulsante Esci per terminare la sessione.
- Pagina locale di errore se il server non è raggiungibile.
- Link esterni aperti nel browser di sistema.

## Server predefinito per il test LAN

`http://192.168.68.99:8081`

Puoi cambiarlo dall'icona ingranaggio dell'app.

> Nota: l'HTTP in chiaro è abilitato solo per consentire i test sulla LAN. Per l'uso fuori casa/cliente è fortemente consigliato pubblicare il server con HTTPS e usare un dominio.

## Requisiti

- Android Studio recente
- Android SDK 35
- JDK 17
- Android 5.0 (API 21) o superiore sul telefono
- FormatikLab Tank Server v0.3.0 con credenziali cliente configurate

## Compilazione APK debug

1. Apri questa cartella con Android Studio (`File > Open`).
2. Attendi il Gradle Sync. Se Android Studio propone di scaricare Gradle/SDK 35, accetta.
3. Seleziona `Build > Build App Bundle(s) / APK(s) > Build APK(s)`.
4. L'APK debug verrà creato normalmente in:
   `app/build/outputs/apk/debug/app-debug.apk`

## APK release firmato

In Android Studio:
`Build > Generate Signed App Bundle / APK > APK`

Crea/seleziona il tuo keystore e genera la release firmata.

## Login cliente

L'app apre `/login` del Tank Server. Dopo il login il server reindirizza a `/dashboard`, che mostra tutti i dispositivi associati al cliente autenticato.

## Sicurezza

- La password non viene salvata nell'app.
- L'app usa la sessione/cookie generata dal server.
- Cambiando server dall'app i cookie vengono cancellati.
- Prima della distribuzione a clienti reali, passa a HTTPS e imposta un `APP_SECRET` robusto sul server.

## Compilazione online con GitHub Actions

Il repository include `.github/workflows/build-apk.yml`.

1. Carica tutti i file di questo progetto nella root di un repository GitHub.
2. Apri la scheda **Actions**.
3. Seleziona **Build Android APK**.
4. Premi **Run workflow** e poi **Run workflow**.
5. Al termine apri il job completato e scarica l'artifact **FormatikLab-Tank-APK**.
6. Dentro l'artifact trovi `FormatikLab-Tank-v0.1.1-debug.apk`, installabile direttamente su un dispositivo Android per i test.

La build usa JDK 17, Gradle 8.9 e genera un APK debug.
