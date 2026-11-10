## 1. Organización del proyecto

Hemos separado el servidor Java y la futura aplicación Android en dos carpetas.

- `backend/src/main/java/es/dam/controlparental/` contiene los paquetes del servidor.
  - `controller` recibe las peticiones HTTP y devuelve las respuestas.
  - `service` contiene la lógica de la aplicación.
  - `dao` consulta y guarda datos en PostgreSQL con Spring JDBC. Usamos esta capa para la persistencia del backend.
  - `model` contiene los objetos del proyecto, como un tutor, un menor o una pregunta.
  - `dto` contiene los datos que entran y salen de la API. Así podemos decidir qué campos enviamos a Android sin exponer todo el modelo.
- `backend/src/main/resources/` contiene la configuración de Spring Boot. `backend/src/test/` queda preparado para las futuras pruebas.
- `frontend/` contiene el esqueleto de la aplicación Android. Las pantallas irán en `ui/screens`, sus estados en `ui/viewmodel` y la comunicación con el servidor en `data`. El README de esa carpeta indica dónde se añadirá el proyecto Gradle.
- `.gitignore` excluye la configuración local de IntelliJ y los archivos de compilación.

Los directorios reservados llevan un archivo `.gitkeep` para que Git pueda incluirlos mientras estén vacíos.

## 2. Creación del backend

Creamos el backend con Java 21, Maven y Spring Boot. En `backend/pom.xml` añadimos Spring Web MVC, Spring JDBC y el controlador de PostgreSQL.

Para abrirlo en IntelliJ, seleccionamos `backend/pom.xml` como proyecto Maven, configuramos un JDK 21 o superior y esperamos a que se carguen las dependencias. La clase de arranque es `ControlParentalApplication`.

## 3. Conexión con PostgreSQL

1. Arrancamos PostgreSQL y creamos una base de datos vacía llamada `control_parental`.
2. En **Run → Edit Configurations**, añadimos `DB_PASSWORD` con la contraseña de nuestro usuario. La URL predeterminada es `jdbc:postgresql://localhost:5432/control_parental` y el usuario es `postgres`. Podemos cambiarlos con `DB_URL` y `DB_USER`.
3. Ejecutamos `ControlParentalApplication` y abrimos http://localhost:8080/api/estado. El backend ejecuta `SELECT 1` para comprobar la conexión. Si PostgreSQL responde, devuelve `{"postgres":"conectado"}`.
