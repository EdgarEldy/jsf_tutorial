# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A JavaServer Faces (JSF) tutorial app combining an identity/RBAC domain and an e-commerce domain (categories, products, customers, orders) into one web application. This is a from-scratch Maven/PrimeFaces rewrite of an older Ant/NetBeans version of the same tutorial — that legacy code is preserved on the `legacy/ant-netbeans` branch, not in this branch's history.

Stack: Java 8, Java EE 8 (`javax.*`), JSF 2.3 (Mojarra), PrimeFaces 13.0.x, CDI (via Weld), EclipseLink (JPA 2.2), MySQL 8.0, jBCrypt for password hashing. Runs on **Tomcat 9** (not GlassFish — Tomcat 10+ uses the incompatible `jakarta.*` namespace). Because Tomcat is a bare servlet container (no CDI/JTA of its own), `persistence.xml` is `RESOURCE_LOCAL` and `EntityManager` is obtained through a CDI producer (`config.EntityManagerProducer`) rather than `@PersistenceContext`; services are `@ApplicationScoped` CDI beans, never `@Stateless` EJBs.

**[README.md](README.md) is the complete, authoritative specification** for this rewrite — data model, project structure, code conventions, and the full branch-by-branch task list. Read it before implementing anything; this file is only an operational summary.

## Build & run

Maven (`war` packaging), `groupId com.edgareldy`, `artifactId jsf_tutorial`, root package `com.edgareldy.jsftutorial`.

- `mvn clean package` — builds `target/jsf_tutorial.war`
- `mvn verify` — runs unit + Arquillian integration tests (what CI runs; Selenium e2e is a separate, manually triggered job)
- Local dev: `docker-compose up` starts a MySQL container (`jsf_tutorial_db`, root/root); deploy the WAR to a local Tomcat 9 install, or build the root `Dockerfile` for a containerized app image
- `Dockerfile` at the repo root: multi-stage Maven build → Tomcat 9 runtime image, for `docker build`/CI use, not the everyday local edit loop

## Architecture

Layering: `web/<entity>/*.xhtml` → `@Named` bean (`bean/`) → service (`service/` interface + `service/impl/` implementation) → dao (`dao/` interface + `dao/impl/` implementation, every DAO extending `BaseDao`/`BaseDaoImpl`) → JPA entity (`entity/`). A bean never talks to a dao directly, always through a service — see README.md § Code conventions for the full list of conventions (scopes, `FacesMessageUtil`, `PasswordHasher`, business-rule placement).

This is being built branch by branch per README.md's branching strategy (`feature/core-architecture` → `feature/auth` → `feature/catalog` → `feature/customers` → `feature/orders`, each merged to `develop` via PR). Check README.md's per-branch `### Tasks` checklist for what's done and what's next.

### Adding a new CRUD entity

Follow the pattern end-to-end: JPA entity in `entity/`, `<Entity>Dao`/`<Entity>DaoImpl` (extending `BaseDao`/`BaseDaoImpl`) in `dao/`/`dao/impl/`, `<Entity>Service`/`<Entity>ServiceImpl` in `service/`/`service/impl/` (business rules live here), `@Named` bean in `bean/` wired to the service, and Facelets views under `web/<entity>/` extending `templates/layout.xhtml`.
