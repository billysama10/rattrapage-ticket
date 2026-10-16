# Pense-bête — Rattrapage Ticket (Spring Boot + PostgreSQL + React)

## 0. Dossier neuf + Git
```bash
mkdir rattrapage-ticket && cd rattrapage-ticket
git init -b main
git checkout -b rattrapage/prenom-nom
```
(Si le dépôt est fourni : `git clone <url>` puis `git checkout -b rattrapage/prenom-nom`.)

## 1. PostgreSQL (Docker) — copier docker-compose.yml dans le dossier
```bash
docker compose up -d
docker compose ps
docker compose logs db --tail 20
```
Base : `tickets` / user : `tickets` / mot de passe : `tickets` / port 5432

Requête SQL de vérification (point 4 du sujet) :
```bash
docker exec -it tickets-db psql -U tickets -d tickets -c "SELECT * FROM ticket;"
```

## 2. Backend Spring Boot
Générer le projet (Java 21, Maven) :
```bash
curl https://start.spring.io/starter.zip -d type=maven-project -d language=java -d javaVersion=21 -d groupId=com.kfokam -d artifactId=ticket -d name=ticket -d packageName=com.kfokam.ticket -d dependencies=web,data-jpa,postgresql,validation,lombok -o backend.zip
unzip backend.zip -d backend   # ou Expand-Archive en PowerShell
```
Swagger (à ajouter dans pom.xml, springdoc 3.x pour Spring Boot 4) :
```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>3.1.1</version>
</dependency>
```
`src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tickets
spring.datasource.username=tickets
spring.datasource.password=tickets
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
Lancer :
```bash
cd backend && ./mvnw spring-boot:run      # PowerShell : .\mvnw.cmd spring-boot:run
```
Swagger : http://localhost:8080/swagger-ui/index.html

## 3. Frontend React (Vite)
```bash
npm create vite@latest frontend -- --template react
cd frontend && npm install && npm run dev      # http://localhost:5173
```
CORS côté Spring (obligatoire, sinon le front ne passe pas) : `@CrossOrigin(origins = "http://localhost:5173")` sur le contrôleur, ou proxy Vite.

## 4. Ordre de bataille (2h, validation à 10/20)
| Temps | Quoi | Points |
|---|---|---|
| 0-20 min | Env : Docker, backend, front démarrés | 3 |
| 20-65 min | API : entité, repo, service, contrôleur, DTO, 400/404/409 | 7 |
| 65-105 min | Écran : liste, filtre, 4 états, formulaire, fermeture | 8 |
| 105-120 min | Git : 3 commits, push, PR + 2 captures | 2 |

Piège n°1 du sujet : les erreurs (400/409) affichées lisiblement. Pas d'`alert()`, pas de message générique.
Avant le passage du formateur : backend démarré + au moins 1 ticket en base.

## 5. Git / PR
```bash
git add . && git commit -m "feat(env): docker-compose postgres + squelettes backend/frontend"
git commit -m "feat(api): endpoints tickets avec regles de gestion"
git commit -m "feat(ui): ecran tickets liste, formulaire, fermeture"
git push -u origin rattrapage/prenom-nom
gh pr create --base main --title "Rattrapage : module Ticket" --body "Captures : Swagger + refus 409"
```

## 6. Dépannage
- Port 5432 occupé : `docker ps`, ou changer en `5433:5432` et adapter l'URL JDBC.
- Backend ne se connecte pas : `docker compose ps` doit montrer `healthy`/`running`.
- Docker muet : relancer Docker Desktop, attendre ~1 min.
- Tout remettre à zéro : `docker compose down -v`.

