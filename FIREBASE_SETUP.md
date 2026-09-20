# Firebase setup - FormatikLab Tank Android v0.2.0

1. Crea il progetto Firebase.
2. Aggiungi una app Android con package: `lab.formatik.tank`.
3. Scarica `google-services.json`.
4. Copialo in `app/google-services.json`.
5. Ricompila l'APK.

Senza `google-services.json` l'app continua a funzionare come portale WebView, ma FCM resta disattivato.
Il token FCM viene registrato sul Tank Server dopo il login cliente tramite `/api/v1/push/register`.

Nota: questa build usa minSdk 23 (Android 6.0), coerente con i requisiti correnti di FCM.
