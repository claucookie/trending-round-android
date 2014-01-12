TrendingRound
=============

_Mobivery_

Enlaces de Interés
------------------

* [Localizables](https://docs.mmip.es) *RELLENAR EL LINK*
* [Service definitions](https://github.com/mobivery/service-definitions/blob/master/TrendingRound.xml)

Variables de entorno
--------------------

* `GOOGLE_LOGIN` y `GOOGLE_PASSWORD`: Este proyecto hace uso de las variables de entorno GOOGLE_LOGIN Y GOOGLE_PASSWORD que establecen las credenciales de la cuenta google para la generación de archivos localizables. Más información en el archivo [Locfile](Locfile).
* `ANDROID_HOME` (opcional): Para usar los comandos de build del Rakefile (para CI o compilaciones locales) también necesitaremos la variable `ANDROID_HOME` definida.

Generación de versiones
-----------------------

Se recomienda [Builder Dash](https://github.com/mobivery/builderdash) para la generación correcta de versiones firmadas. Bastaría ejecutar `dash touchdown` para generar la versión de release.

También se pueden generar versiones mediante el Rakefile, con los comandos `rake build:nightly` y `rake build:release`, con resultados similares. Esta forma de generación pasará pronto a estar deprecada, pero se mantiene por retrocompatibilidad de momento.

Certificados
------------

* El certificado de debug que usaremos está en [certs/debug](certs/debug). Ejecutando el task `rake config:debug_key` se guardará una copia de seguridad del keystore de debug de tu máquina en tu directorio home y se copiará el común.
* El certificado de cliente está en [certs/release](certs/release).
* El certificado nightly es el mismo que el de cliente por defecto.

Generación de código y recursos
-------------------------------

También se pueden generar los servicios o los localizables si el proyecto los tuviese.
* `rake generate:ws` generará el código de acceso a servicios REST.
* `rake generate:loc` generará los localizables asociados al proyecto con la última versión de [Localio](https://github.com/mrmans0n/localio).

Para ver qué acciones se pueden hacer en el Rakefile, se puede ejecutar `rake -T` en el directorio del mismo.

Requisitos
----------

* Android 4.0 o superior
* Android Studio
* Ruby 2.0+ para los scripts de build

Cómo realizar una entrega al cliente
------------------------------------

* Forma automática: La aplicación se sube a HockeyApp mediante CI (Integración Continua).
* Forma manual: podemos ejecutar `rake build:release` y se generará un APK firmado en la ruta local `TrendingRound/build/apk/TrendingRound-release.apk`.