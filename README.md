# android-jedi

## Curso de Android de JEDI, Enero de 2019


En este repositorio podréis encontrar todo el material que utilizaremos durante el curso, además de bibliografía útil.

El índice de la asignatura está en los documentos en Markdown numerados de este repositorio.



Links útiles
* [Android Studio](https://developer.android.com/studio/)
* [Android developers](https://developer.android.com/)
* [Developer guide](https://developer.android.com/guide/)


#### Proyecto final

Añadid a @twinone y a @pereverges (**importante, ambos!**) como colaboradores en github al proyecto. En el repositorio tiene que haber exactamente lo siguiente:
- O bien la carpeta completa del proyecto, o bien un zip que contiene el proyecto completo. 
- Un fichero README.md, con:
  * Vuestro email con el que os habéis dado de alta en el curso
  * Una **breve** descripción (max 1 parrafo) de qué hace la app
  * Una breve lista de componentes o conceptos que habéis aprendido en el curso y utilizado en vuestra app (RecyclerView, MediaPlayer, etc)
  * Una breve lista de componentes o conceptos que habéis añadido que **no** se han dado durante el curso (investigación propia).
  

*Aseguraos que el formato del zip es zip y no cualquier otra cosa, y de que el proyecto compila correctamente y no faltan recursos, etc. En caso contrario suspenderéis el curso.*
*Aseguraos que el packagename del proyecto es com.jediupc.helloandroid y ningún otro.*


#### Ideas de proyectos
* **App de notas de voz** (propuesta de un alumno). Tiene que tener como mínimo:
  - Una Activity principal que muestra las notas guardadas utilizando un RecyclerView, con su fecha, duración, un botón para reproducir el audio y una barra de progreso. Mientras se reproduce el audio debe mostrarse el tiempo que lleva en formato mm:ss.
  - Seleccionar varios audios para poder borrarlos.
  - Un botón para grabar un audio nuevo (Floating Action Button). Este botón puede abrir un nuevo activity si lo consideráis necesario. Mientras se graba un audio tiene que mostrarse la duración actual.
  
* **App de to-do list** (tipo lista de la compra, pero con múltiples listas). Tiene que tener como mínimo:
  - Un Activity principal que muestra las listas, por lo menos los títulos y tenga un botón para añadir una lista. Este Activity tendrá la opción de marcar varias listas para poder borrarlas.
  - Un Activity editor de listas, al que se accede haciendo click sobre una de las listas del Activity principal. En el ActionBar aparecerá el titulo de la lista, y un botón en el menú del ActionBar para cambiar el título. En este activity podremos ver las opciones de la lista, además de marcar/desmarcar, añadir, quitar y reordenar las opciones.
  
* **Calculadora de notas** (propuesta de un alumno). Tiene que tener como mínimo:
  - Un Activity principal, que muestre una lista de asignaturas que cursa el usuario, con un botón que añada una nueva asignatura en un nuevo Activity. Se permitirá seleccionar varias asignaturas para borrarlas.
  - Al crear una asignatura nueva, se introducirá el título y una lista de {peso, nombre}, por ejemplo: `0.2 Lab`, `0.4 Parcial 1`, `0.4 Parcial 2`. La app tiene que comprobar que la suma de pesos sea 1 antes de permitir añadir la asignatura. Tiene que haber un botón de guardar y otro de cancelar.
  - Al hacer click sobre una asignatura ya creada, se mostrarán los pesos y nombres introducidos al crearla, y se permitirá introducir las notas obtenidas durante el curso, momento en el cual se recalculará el total acumulado hasta ahora.
  


