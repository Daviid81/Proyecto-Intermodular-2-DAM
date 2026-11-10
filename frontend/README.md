# Frontend Android

Hemos preparado la estructura de paquetes para el futuro proyecto de Android Studio con Kotlin y Jetpack Compose. Los paquetes están en `app/src/main/java/es/dam/controlparental/`.

- `ui/screens` contendrá las pantallas de Compose. Es la vista de la aplicación.
- `ui/viewmodel` guardará el estado de las pantallas y coordinará las acciones del usuario.
- `data/remote` contendrá el cliente HTTP que se comunica con Spring Boot.
- `data/repository` organizará cómo obtiene los datos la interfaz a través del cliente HTTP.
