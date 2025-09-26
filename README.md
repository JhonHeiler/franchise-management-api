# Franchise API (Reactive, Hexagonal)

[![CI](https://github.com/JhonHeiler/franchise-management-api/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/JhonHeiler/franchise-management-api/actions/workflows/ci.yml)
[![coverage](https://img.shields.io/badge/coverage-91%25-brightgreen.svg)](#cobertura-de-pruebas-jacoco)

API reactiva para gestionar Franquicias, Sucursales (Branches) y Productos con stock.

## Stack
- Java 21 (LTS), Spring Boot 3 (WebFlux, Validation, Reactive MongoDB)
- Arquitectura Hexagonal
- springdoc-openapi (Swagger UI)
- Testing: JUnit 5, Reactor Test, WebFluxTest, (placeholder Testcontainers pendiente si se agrega contenedor Mongo)
- JaCoCo (>=80%)

## Quick Start (sin Docker, perfil en memoria)
Ejecuta la API sin necesidad de Mongo usando repositorios in-memory.

```bash
chmod +x scripts/run-inmem.sh
./scripts/run-inmem.sh
# Abrir: http://localhost:8080/
```

Si el puerto 8080 está ocupado:
```bash
export SERVER_PORT=9090
./scripts/run-inmem.sh
```

## Evidencia (Swagger UI)
Captura de la especificación generada automáticamente (springdoc-openapi):

![Swagger UI](docs/images/swagger-ui.png)

La UI expone los endpoints requeridos bajo el prefijo `/api/v1` (franquicias, sucursales, productos y agregación de máximo stock por sucursal).

## Quick Start con Mongo local
Levanta un contenedor Mongo y apunta la app a él:
```bash
docker run -d --name mongo -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=root \
  -e MONGO_INITDB_ROOT_PASSWORD=secret mongo:7

export SPRING_DATA_MONGODB_URI='mongodb://root:secret@localhost:27017/franchise_db?authSource=admin'
chmod +x scripts/run-mongo.sh
./scripts/run-mongo.sh
```
Swagger: http://localhost:8080/swagger-ui.html


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
| # | Método | Ruta | Descripción | Req Body | Respuesta (HTTP) |
|:-:|:------:|------|-------------|----------|------------------|
|1|POST|`/franchises`|Crear franquicia|`{ "name": "ACME" }`|`201` + `{id,name}`|
|2|PATCH|`/franchises/{franchiseId}`|Renombrar franquicia|`{ "name": "Nuevo" }`|`204`|
|3|POST|`/franchises/{franchiseId}/branches`|Agregar sucursal|`{ "name": "Lima" }`|`201` + `{id,franchiseId,name}`|
|4|PATCH|`/franchises/{franchiseId}/branches/{branchId}`|Renombrar sucursal|`{ "name": "Lima Centro" }`|`204`|
|5|POST|`/franchises/{franchiseId}/branches/{branchId}/products`|Agregar producto|`{ "name": "Burger", "stock": 10 }`|`201` + `{id,branchId,name,stock}`|
|6|DELETE|`/franchises/{franchiseId}/branches/{branchId}/products/{productId}`|Eliminar producto|—|`204`|
|7|PATCH|`/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock`|Actualizar stock|`{ "stock": 25 }`|`200` + producto actualizado|
|8|PATCH|`/franchises/{franchiseId}/branches/{branchId}/products/{productId}`|Renombrar producto|`{ "name": "Burger XL" }`|`204`|
|9|GET|`/franchises/{franchiseId}/branches/max-stock-products`|Producto con mayor stock por sucursal (agregación)|—|`200` + `[{branchId,branchName,productId,productName,stock}]`|
|10|GET|`/franchises/{franchiseId}/with-branches`|Franquicia + sucursales (ejemplo uso `zip`) |—|`200` + aggregate|

### Ejemplos de Requests / Responses

Crear franquicia:
```bash
curl -s -X POST http://localhost:8080/api/v1/franchises \
  -H 'Content-Type: application/json' \
  -d '{"name":"ACME"}'
```
Respuesta:
```json
{ "id": "f123", "name": "ACME" }
```

Agregar sucursal:
```bash
curl -s -X POST http://localhost:8080/api/v1/franchises/f123/branches \
  -H 'Content-Type: application/json' \
  -d '{"name":"Lima"}'
```

Agregar producto:
```bash
curl -s -X POST http://localhost:8080/api/v1/franchises/f123/branches/b456/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Burger","stock":15}'
```

Actualizar stock:
```bash
curl -s -X PATCH http://localhost:8080/api/v1/franchises/f123/branches/b456/products/p789/stock \
  -H 'Content-Type: application/json' \
  -d '{"stock":42}'
```

Agregación (máximo stock por sucursal):
```bash
curl -s http://localhost:8080/api/v1/franchises/f123/branches/max-stock-products
```
Respuesta:
```json
[
  {"branchId":"b456","branchName":"Lima","productId":"p789","productName":"Burger","stock":42}
]
```

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

## Criterios de Aceptación (Checklist)

| Criterio | Implementado | Evidencia / Nota |
|----------|--------------|------------------|
| 1. Spring Boot WebFlux + Arquitectura Hexagonal | ✔ | Paquetes domain / application / infrastructure |
| 2. Persistencia en proveedor (MongoDB) | ✔ | Reactive Mongo + opción in-memory para desarrollo |
| 3. Encadenar flujos (map, flatMap, switchIfEmpty, zip) | ✔ | Use Cases y controllers (ver `GetFranchiseWithBranchesUseCase`) |
| 4. Uso correcto señales Reactor | ✔ | `doOnError`, flujos completan correctamente (no bloqueos) |
| 5. Logging adecuado | ✔ | SLF4J + CorrelationIdFilter + patrón logback |
| 6. Cobertura >60% (objetivo 80%) | ✔ | JaCoCo gate configurado (objetivo 80%) |
| 7. README documentado | ✔ | Este documento |
| 8. APIs RESTful | ✔ | CRUD + verbos correctos + códigos HTTP |
| Funcional 1 Crear franquicia | ✔ | POST /franchises |
| Funcional 2 Agregar sucursal | ✔ | POST /franchises/{id}/branches |
| Funcional 3 Agregar producto | ✔ | POST /.../products |
| Funcional 4 Eliminar producto | ✔ | DELETE /.../products/{productId} |
| Funcional 5 Modificar stock producto | ✔ | PATCH /.../products/{id}/stock |
| Funcional 6 Producto mayor stock por sucursal | ✔ | GET /franchises/{id}/branches/max-stock-products |
| Plus Docker | ✔ | Dockerfile + compose |
| Plus Renombrar franquicia | ✔ | PATCH /franchises/{id} |
| Plus Renombrar sucursal | ✔ | PATCH /franchises/{id}/branches/{branchId} |
| Plus Renombrar producto | ✔ | PATCH /.../products/{productId} |
| Plus Explicar decisiones diseño | ✔ | Sección Diseño y Decisiones |
| Plus Endpoint agregado zip (aggregate) | ✔ | GET /franchises/{id}/with-branches |
| IaC / Cloud despliegue | ☐ | Pendiente (Terraform opcional) |

## Operadores Reactor Utilizados
- `map`: Transformaciones simples de dominio a DTO.
- `flatMap`: Encadenar persistencia (guardar y luego recuperar, etc.).
- `filter`: Validaciones condicionales (existencia/estado) en repositorios.
- `switchIfEmpty`: Errores de negocio cuando no se encuentra entidad.
- `zip`: Combinar franquicia + sucursales en `GetFranchiseWithBranchesUseCase`.
- (Justificación) No se incluyeron `merge` / `concat` al no haber flujos competitivos; se prioriza claridad.

## Errores y Manejo
Se retornan errores reactivos con `Mono.error(...)` para entidades no encontradas. Spring maneja la serialización en JSON. (Se puede extender con un `@ControllerAdvice` para códigos específicos si se requiere).

## Observabilidad
- Micrometer Timer custom: latencia de peticiones HTTP (filtro `RequestMetricsFilter`).
- Correlation ID propagado mediante header `X-Correlation-Id` y MDC.
- Actuator (health, info) habilitable añadiendo dependencia `spring-boot-starter-actuator` (ya incluida) y exponiendo endpoints en `application.yml`.

## Perfil `inmem`
Provee repositorios en memoria para ejecutar sin Mongo: útil en evaluaciones rápidas o CI liviano.

## Estrategia de Cobertura
- Objetivo 80% global (aceptación >60%).
- Tests de casos de uso cubren lógica principal; controles y DTOs triviales no penalizan significativamente.
- Próximo paso: agregar pruebas de agregación con Mongo (Testcontainers) para robustecer.

## Despliegue Docker Rápido
```bash
docker compose up --build
# API en http://localhost:8080
```

## Despliegue Imagen (Pull)
Si se publica en GHCR / Docker Hub (ver sección CD), ejecutar:
```bash
docker run --rm -p 8080:8080 ghcr.io/<owner>/<repo>:main
```

## Roadmap Futuro
- IaC (Terraform) para Mongo Atlas / DocumentDB / Cosmos DB.
- Autenticación / Autorización (Keycloak / OAuth2) si se requiere seguridad.
- Métricas Prometheus + dashboards Grafana.
- Pruebas de carga (Gatling / k6) para validar back-pressure.

---
Si necesitas más detalle o extender la documentación (p.ej. modelo de errores estructurados, versionado de API), abrir un issue o PR.

## CI
Workflow GitHub Actions: `.github/workflows/ci.yml` ejecuta build + coverage gate (80%).

## CD (Docker image)
- GHCR (GitHub Container Registry)
  - Workflow: `.github/workflows/cd-docker.yml`
  - Evento: push a `main` o tags `v*.*.*`
  - Destino: `ghcr.io/<owner>/<repo>:<tag>`
  - Requisitos:
    - Nada extra: usa `${{ secrets.GITHUB_TOKEN }}` por defecto.
  - Tips:
    - Si quieres que sea público, ve al paquete (Packages) del repo y cambia visibilidad.
  - Pull de ejemplo:
    ```bash
    docker pull ghcr.io/<owner>/<repo>:main
    docker run --rm -p 8080:8080 ghcr.io/<owner>/<repo>:main
    ```

- Docker Hub (multi-arch amd64/arm64)
  - Workflow: `.github/workflows/cd-dockerhub.yml`
  - Evento: push a `main` o tags `v*.*.*`
  - Destino: `docker.io/<DOCKERHUB_USERNAME>/<repo_name>:<tag>`
  - Requisitos (configurar en Settings > Secrets and variables > Actions):
    - `DOCKERHUB_USERNAME`: tu usuario de Docker Hub.
    - `DOCKERHUB_TOKEN`: token de acceso (crearlo en Docker Hub > Security > New Access Token).
  - El workflow detecta el nombre del repo automáticamente (`${{ github.event.repository.name }}`).
  - Pull de ejemplo:
    ```bash
    docker pull <DOCKERHUB_USERNAME>/<repo_name>:main
    docker run --rm -p 8080:8080 <DOCKERHUB_USERNAME>/<repo_name>:main
    ```
  - Multi-arch: publica manifiesto para linux/amd64 y linux/arm64 (útil para Apple Silicon).

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

## Cobertura de Pruebas (JaCoCo)

La suite actual supera el umbral de aceptación (≥ 60%) y se sitúa alrededor del 90% en la capa de casos de uso, con >90% de instrucciones a nivel global según el último reporte.

Capturas del reporte JaCoCo (generado en `build/reports/jacoco/test/html/index.html`):

![Paquete application.usecase](docs/images/jacoco-usecases.png)

![Resumen global](docs/images/jacoco-summary.png)

Cómo regenerar y ver el reporte localmente:

```bash
./gradlew test jacocoTestReport --no-daemon
# abrir build/reports/jacoco/test/html/index.html en el navegador
```

Si estás en WSL y quieres verlo desde Windows, puedes servirlo con Python:

```bash
cd build/reports/jacoco/test/html
python3 -m http.server 8000 --bind 0.0.0.0
# Navega a http://localhost:8000/
```

Las imágenes de arriba deben ubicarse en `docs/images/` con los nombres:
- `jacoco-usecases.png`
- `jacoco-summary.png`

Si prefieres, sube tus capturas a esa ruta y se verán automáticamente en el README.
```
