# Franchise API (Reactive, Hexagonal)

API reactiva para gestionar Franquicias, Sucursales (Branches) y Productos con stock.

## Stack
- Java 21 (LTS), Spring Boot 3 (WebFlux, Validation, Reactive MongoDB)
- Arquitectura Hexagonal
- springdoc-openapi (Swagger UI)
- Testing: JUnit 5, Reactor Test, WebFluxTest, (placeholder Testcontainers pendiente si se agrega contenedor Mongo)
- JaCoCo (>=80%)

## Arquitectura (paquetes principales)
```
com.example.franchise
  domain.model (POJOS)
  domain.port (interfaces repos)
  application.usecase (casos de uso)
  infrastructure.adapter.repository.mongo (documents + adapters)
  infrastructure.adapter.rest (controllers + dto + error handler)
  infrastructure.openapi (OpenAPI config)
  infrastructure.logging (helpers Reactor)
```

## Endpoints (prefijo `/api/v1`)
1. POST /franchises
2. PATCH /franchises/{franchiseId}
3. POST /franchises/{franchiseId}/branches
4. PATCH /franchises/{franchiseId}/branches/{branchId}
5. POST /franchises/{franchiseId}/branches/{branchId}/products
6. DELETE /franchises/{franchiseId}/branches/{branchId}/products/{productId}
7. PATCH /franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock
8. PATCH /franchises/{franchiseId}/branches/{branchId}/products/{productId}
9. GET /franchises/{franchiseId}/branches/max-stock-products (aggregation)

## OpenAPI / Swagger
- UI: http://localhost:8080/swagger-ui.html
- Docs: http://localhost:8080/v3/api-docs

## Perfiles y Mongo
`application.yml` soporta perfiles: `local`, `docker`, `test` y usa la variable `SPRING_DATA_MONGODB_URI` para configurar la conexión. En el perfil `docker` la app ya apunta por defecto a `mongodb` (nombre del servicio en Docker Compose) y base `franchise_db` con `authSource=admin`.

## Docker
Multi-stage build en `Dockerfile` y `docker-compose.yml` con servicios:
- mongodb (mongo:7)
- app

Levantar solo Mongo:
```bash
docker compose up mongodb
```
App + Mongo (con build):
```bash
docker compose up --build
```

## Diagrama (Mermaid)
```mermaid
flowchart LR
  Client((Client)) -->|HTTP REST| API[Spring Boot WebFlux]
  subgraph App
    API --> UseCases[application.usecase]
    UseCases --> Ports[domain.port]
    Ports --> MongoAdapter[infrastructure.adapter.repository.mongo]
  end
  MongoAdapter -->|Reactive Mongo| Mongo[(MongoDB)]
```

## Variables de entorno
- `SPRING_DATA_MONGODB_URI` (por ejemplo: `mongodb://root:secret@mongodb:27017/franchise_db?authSource=admin`)
- `SERVER_PORT` (opcional, default 8080)

## DBeaver / Alternativas para ver MongoDB
Atención: En versiones recientes, DBeaver Community no incluye driver nativo para MongoDB. Opciones:

1) Compass (recomendado, gratuito)
   - Instala MongoDB Compass y conéctate con la siguiente cadena:
     ```
     mongodb://root:secret@localhost:27017/franchise_db?authSource=admin
     ```

2) Extensión “MongoDB for VS Code”
   - Instala la extensión en VS Code, crea una conexión con la misma URI.

3) Línea de comando (mongosh)
   - Desde tu máquina:
     ```bash
     mongosh "mongodb://root:secret@localhost:27017/franchise_db?authSource=admin"
     ```
   - O dentro del contenedor:
     ```bash
     docker exec -it mongodb mongosh -u root -p secret --authenticationDatabase admin franchise_db
     ```

Si usas DBeaver PRO, sí incluye driver para MongoDB y puedes configurar:
- Host: `localhost` (en red de Docker: `mongodb`)
- Puerto: `27017`
- Usuario: `root`  |  Password: `secret`
- Database: `franchise_db`  |  Auth DB: `admin`

## Seed de datos
Opción HTTP (requiere API arriba y `jq` instalado):
```bash
bash scripts/seed-http.sh
```

Opción mongosh (si prefieres cargar directo en DB):
```bash
mongosh "mongodb://root:secret@localhost:27017/franchise_db?authSource=admin" scripts/seed-mongosh.js
```

## Ejemplos CURL
```bash
curl -X POST http://localhost:8080/api/v1/franchises -H 'Content-Type: application/json' -d '{"name":"ACME"}'
```

## CI
Workflow GitHub Actions: `.github/workflows/ci.yml` ejecuta build + coverage gate (80%).

## CD (Docker image)
- Workflow `.github/workflows/cd-docker.yml` publica la imagen en GHCR al hacer push a `main` o tags `v*.*.*`.
- La imagen queda en `ghcr.io/<owner>/<repo>:<tag>`.

## Pending / Futuro
- Añadir Testcontainers para integración real (MongoDB). 
- Más validaciones de unicidad.
- Terraform opcional.

## Diseño y Decisiones
La solución sigue arquitectura hexagonal:
- `domain.model` y `domain.port` no dependen de frameworks.
- `application.usecase` orquesta reglas usando solo puertos.
- `infrastructure.adapter.*` implementa puertos (Mongo) y expone REST.

Reactividad: Se usan operadores `map`, `flatMap`, `filter`, `switchIfEmpty`, `zip` (nuevo endpoint agregando sucursales) y señales `doOnNext`, `doOnError`, `doOnComplete` (agregación). No se añadió `merge`/`concat` al no existir casos de unión competidora; se justifica evitar complejidad innecesaria.

Persistencia: MongoDB reactivo; puede apuntar a cluster cloud vía `MONGODB_URI`.

Tests: Unit + integración (Testcontainers etiquetados con tag `integration`). Por defecto se excluyen para permitir entornos sin Docker. Ejecutar integración: `./gradlew integrationTest -PintegrationTests`.

Logging: SLF4J; se registran eventos de creación, actualización y agregaciones. Se añadió `CorrelationIdFilter` que:
- Genera/propaga header `X-Correlation-Id`.
- Inserta el ID en MDC (`correlationId`) y Reactor context.
- Permite trazar peticiones en logs distribuidos.

Observabilidad: Se añadió `RequestMetricsFilter` que registra un timer `http.server.reactive.request` (tags: method, status, path) usando Micrometer. Patrón de logs configurado en `logback-spring.xml` incluye el correlationId.

REST: Recursos y acciones siguen convención HTTP (POST crea, PATCH modifica, DELETE elimina). Respuesta de actualización de stock ahora devuelve el producto completo consistente.

## Nuevo Endpoint (Ejemplo zip)
`GET /api/v1/franchises/{id}/with-branches` combina la franquicia y su lista de sucursales usando `Mono.zip`.

## Ejecución de Tests
- Unit tests: `./gradlew test`
- Integración (opt-in, requiere Docker):
  - `./gradlew integrationTest -PwithIntegration`
  - o incluir en el ciclo `check`: `./gradlew check -PwithIntegration`
  - Alternativa usando system property: `./gradlew test -DincludeIntegration=true`

## Próximos Pasos Sugeridos
- Añadir IaC (Terraform) para Mongo administrado y despliegue.
- Pipeline CD y despliegue en nube.
- Correlation ID en logs.
```
