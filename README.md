# SecSys Dockerized Stack

This repository packages the SecSys frontend (Vue 2 + Element UI), backend (Spring Boot + MyBatis-Plus) and MySQL database into a single Docker-based deployment. The provided Compose file builds both applications from source, provisions the database with the bundled schema, and exposes the SPA via nginx with the API proxied under `/prod-api`.

## Project Layout

```
client/          Vue.js front-end (Vue CLI 4)
server/          Spring Boot back-end (Maven multi-module)
database/        SQL dumps and seed data (grad_wygl.sql)
docker/          Runtime scripts and nginx config
Dockerfile       Multi-stage build for client + server
docker-compose.yml
```

## Prerequisites

- Docker Engine 20+ and Docker Compose v2
- Optional: Node.js / Maven locally if you want to develop outside Docker

## Quick Start

```bash
cd secsys
docker compose up --build -d
```

What happens:

1. `mysql` service starts with credentials defined in `docker-compose.yml` (defaults: user `secsys` / password `secsys123`) and automatically loads `database/grad_wygl.sql`.
2. `secsys` service builds the Vue client + Spring Boot backend, copies the artifacts into a runtime image with nginx + Temurin JRE, and waits for MySQL to become healthy before launching.
3. nginx serves the built SPA on port `8100` and proxies API calls that start with `/prod-api` to the backend running on port `8099` inside the container. The backend port is **not** exposed to the host.

Verify:

- Frontend + API proxy: http://localhost:8100
- Logs: `docker compose logs -f`
- Stop the stack: `docker compose down`

### Resetting the Database

The MySQL data is stored in the named volume `secsys_mysql-data`. To re-import from `database/grad_wygl.sql`, remove the volume then recreate the stack:

```bash
docker compose down
docker volume rm secsys_mysql-data
docker compose up --build -d
```

## Development Tips

- Frontend environment files (`client/.env.*`) point their `VUE_APP_BASE_API(_PRO)` to `/prod-api`, so local dev behind nginx behaves like production.
- Backend DB credentials live in `server/base-web/src/main/resources/application-test.yml`. Override them with environment variables if you deploy elsewhere.
- Update Dockerfile stages if you upgrade Node, Maven, or JDK versions.

## Troubleshooting

| Issue | Resolution |
| --- | --- |
| `docker` stuck on image pull | Ensure `/etc/docker/daemon.json` uses working registry mirrors or use `docker pull` manually with VPN. |
| Maven dependency timeout | The Dockerfile installs `docker/maven/settings.xml` to use Aliyun mirrors; adjust if needed. |
| nginx port conflicts | Modify `docker-compose.yml` port mapping (e.g., `8200:80`) and rebuild. |
| Need to change DB creds | Update Compose `MYSQL_*` env vars and mirror them in `application-test.yml` or via Spring env variables. |

## Cleaning Up

`.gitignore` excludes build artifacts (node_modules, Maven `target/`, logs, etc.). Run `git clean -fdx` carefully if you want to reset the working tree after confirming nothing important will be removed.
