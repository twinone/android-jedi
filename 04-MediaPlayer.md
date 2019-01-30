# Permissions, Services, Notifications & more

## Permissions
En este apartado veremos como funcionan los [permisos en Android](https://developer.android.com/guide/topics/permissions/overview).

## Services
Veremos como crear un `Service` siguiendo la [documentación](https://developer.android.com/guide/components/services)

## Notifications
Veremos como construir [notificaciones](https://developer.android.com/guide/topics/ui/notifiers/notifications), modificarlas, cancelarlas y recibir eventos de ellas.

## MediaPlayer
Veremos como utilizar el MediaPlayer para reproducir música o vídeo.

## Proyecto Guiado
Como pequeño proyecto, haremos un reproductor de música con el [`MediaPlayer`](https://developer.android.com/reference/android/media/MediaPlayer)
La idea es que obtenga la lista de música que existe en el movil y la reproduzca.

Para esto seguiremos los siguientes pasos:

* Descargamos en el emulador algunas canciones del [Free Music Archive](http://freemusicarchive.org/)
* Crearemos una Activity para mostrar la música y controlar nuestro Service.
* Crearemos un `Service` para reproducir la música
* Crearemos una `Notification` para poder hacer un `startForeground()`
* Añadimos el permiso de [READ_EXTERNAL_STORAGE](https://developer.android.com/reference/android/Manifest.permission.html#READ_EXTERNAL_STORAGE) para poder leer la música
* Obtenemos la lista de canciones existentes en el dispositivo con `MediaStore` y `ContentResolver`.
* Implementaremos la comunicación del Activity con el Service.

