# Hello Android
### Android Studio, Android SDK, Hello world, Activities, Layouts, Listeners, Intents y ciclo de vida


## Android Studio, SDK, Hello World.
Podemos descargar Android Studio desde [este enlace](https://developer.android.com/studio/)

Para crear un nuevo proyecto: `File` - `New` - `New Project`.
Le pondremos como nombre `HelloAndroid`, y como company domain `jediupc.com`.
Utilizaremos API level 19, **sin** Instant App Support, y añadimos un Empty Activity con las opciones por defecto.


## Emulador
A continuación, como no asumimos que todo el mundo tenga un móvil con Android, crearemos un emulador.
Para ello vamos en el menú a `Tools` - `AVD Manager` y `Create Virtual Device`.
Utilizaremos un `Pixel XL` con API level 28 (**con Google APIs**), dejando todas las opciones por defecto.

Probamos nuestra app por defecto en el emulador.

## Activity, Layout y ciclo de vida

Un activity es una pantalla visible de nuestra aplicación. Se crea cuando el usuario la abre por primera vez, y se destruye cuando el sistema lo cree necesario. Una explicación más detallada [aquí](https://developer.android.com/guide/components/activities/activity-lifecycle)

Es importante utilizar los callbacks adecuados para cada situación. Utilizaremos principalmente `onCreate`, `onPause`/`onResume`, y a veces `onDestroy`. Inicializaremos los componentes de la UI en `onCreate`.

Añadiremos un botón a la interfaz abriendo el xml `app > res > layout > activity_main.xml`, donde encontraremos un único TextView. Arrastramos un botón a la pantalla. Tambien añadiremos un EditText (en `Text > Plain Text`). Si abrimos el visor XML veremos que se ha creado nuestros componentes con ids `button` y `editText`. Los dejaremos así por ahora.

Veremos en `app > java > com.jediupc.jediandroid > MainActivity.java` la función `onCreate` ya implementada. Por ahora lo único que hace es abrir el XML que hemos editado antes y mostrarlo en la pantalla. Vamos a ejecutarlo en el emulador para verlo.




