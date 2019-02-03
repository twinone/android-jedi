# Producción

En este apartado aprenderemos a preparar nuestra app para publicarla en Google Play.

## Estrategia de monetización

En primer lugar tenemos que decidir como monetizar nuestra app. Existen varias [estrategias](https://developer.android.com/distribute/best-practices/earn/):
* [Publicidad (AdMob)](https://developers.google.com/admob/android/quick-start)
* App de pago
* [In-App Purchases](https://developer.android.com/google/play/billing/billing_overview)

Como una app de pago es trivial (es una opción al publicarla), explicaremos como mostrar publicidad en Android.

## Obfuscación

Opcionalmente antes de publicar nuestra app podremos obfuscarla con [ProGuard](https://developer.android.com/studio/build/shrink-code)

De esta manera reducimos el tamaño total de nuestra APK y además lo haremos (un poco) más dificil para un atacante de leer nuestro código original.

## Publicación en Google Play Store

Para publicar una app en Google Play necesitamos varias cosas:
- Una cuenta de Google Play
- Icono, título, descripción y capturas de pantalla de nuestra app
- Nuestra APK posiblemente obfuscada
