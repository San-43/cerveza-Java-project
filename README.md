# MyDB · Gestión de Cervezas

Aplicación de escritorio desarrollada en JavaFX para administrar catálogos relacionados con cervezas. El punto de entrada `MainApp` carga la vista `main.fxml` y aplica estilos si están disponibles.

## Características

- Menú de navegación que abre formularios FXML para distintos catálogos: Cervezas, Marcas, Envases, Producción, Expendios y otros módulos en preparación.
- Acceso a MySQL mediante un helper `Database` que lee credenciales desde `application.properties`.
- Validaciones de formularios y componentes enriquecidos con librerías como ControlsFX y ValidatorFX declaradas en el `pom.xml`.

## Requisitos

- JDK 24 (configurable en el `maven-compiler-plugin`).
- Maven 3; se incluye wrapper (`mvnw`).
- MySQL 8 o compatible; crear la base de datos `mydb` y las tablas necesarias antes de ejecutar.

## Configuración

Ajuste las credenciales de conexión en `src/main/resources/application.properties`.

## Ejecución

```bash
./mvnw clean javafx:run
```

El `javafx-maven-plugin` lanza `org.cerveza.cerveza.MainApp` por defecto.

## Estructura del proyecto

```
src/
  main/
    java/
      org/cerveza/cerveza/
        MainApp.java
        config/Database.java
        controller/…  (controladores de formularios)
        dao/…         (interfaces DAO e implementaciones)
        model/…       (POJOs)
    resources/
      fxml/…          (vistas FXML)
      css/…
      application.properties
```

## Dependencias principales

MySQL Connector/J, JavaFX (controls, FXML, web, swing, media), ControlsFX, FormsFX, ValidatorFX, Ikonli, BootstrapFX, TilesFX y FXGL.

## Estado

No se incluyen pruebas automatizadas. Las contribuciones son bienvenidas mediante *pull requests*.
