# jsf_tutorial

A tutorial for building a **JavaServer Faces** web application — Java EE 8, JSF 2.3, PrimeFaces, EclipseLink, MySQL — combining an identity/RBAC domain and an e-commerce domain into a single web application, deployed as a WAR on Tomcat 9.

Organized into Git branches that progressively build the application.

This document is the **complete specification** of the project: it is meant to be followed step by step to implement each branch.

## Table of contents

- [Tech stack](#tech-stack)
- [Data model](#data-model)
- [Project structure](#project-structure)
- [Standard user feedback convention](#standard-user-feedback-convention)
- [Branching strategy](#branching-strategy)
- [feature/core-architecture](#featurecore-architecture)
- [feature/auth](#featureauth)
- [feature/categories](#featurecategories)
- [feature/products](#featureproducts)
- [feature/customers](#featurecustomers)
- [feature/orders](#featureorders)
- [Order of work](#order-of-work)
- [Code conventions](#code-conventions)
- [Concepts covered](#concepts-covered)
- [How to follow this tutorial](#how-to-follow-this-tutorial)

## Tech stack

| Component | Choice |
|---|---|
| Language | Java 8 |
| Platform | Java EE 8 (`javax.*`) |
| View technology | JSF 2.3 (Mojarra reference implementation) |
| UI components | PrimeFaces 13.0.x |
| Dashboard/admin template | AdminFaces `admin-template` 1.5.2 (Bootstrap + AdminLTE, `/admin.xhtml`), extended by `templates/layout.xhtml` |
| Managed beans | CDI (`@Named`, `@RequestScoped`/`@ViewScoped`/`@SessionScoped`) |
| ORM | EclipseLink (JPA 2.2) |
| Application server | Apache Tomcat 9.x |
| Database | MySQL 8.0 |
| Password hashing | jBCrypt |
| Email | JavaMail (`com.sun.mail:javax.mail`), sent via `EmailService`/`EmailServiceImpl`; MailHog for local dev (SMTP catcher, no real delivery), real SMTP host via `MAIL_HOST`/`MAIL_PORT` env vars in production |
| Build | Maven, `war` packaging |
| Tests | JUnit 5, Mockito (services/DAOs), Arquillian (container integration tests), Selenium (end-to-end UI flows) |
| CI/CD | GitHub Actions (`mvn verify` for unit/integration tests; Selenium suite documented as a separate, manually triggered job) |
| Containerization | Docker, docker-compose (MySQL only; Tomcat 9 runs locally, not containerized) |

## Data model

Both EERs combined into one schema, since this is a single application with one database — `customers.user_id` is a real foreign key to `users.id`.

```
users (id, first_name, last_name, email, password, enabled, account_locked)
    │ N──N (via role_user)
roles (id, role_name)
    │ N──N (via role_permission)
permissions (id, resource, action)

activation_tokens (id, user_id, token, created_at, expires_at, validated_at)
blacklisted_tokens (id, user_id, token, jti, blacklisted_at, created_at, expires_at, validated_at)
password_reset_tokens (id, user_id, token, type, expiry_date)

categories (id, category_name)
    │ 1
    │
    │ N
products (id, category_id, product_name, unit_price)
    │ 1
    │
    │ N
orders (id, customer_id, product_id, quantity, total)
    │ N
    │
    │ 1
customers (id, user_id, first_name, last_name, telephone, email, address)
    │
    └─ user_id: FK → users.id
```

## Project structure

```
jsf_tutorial/
├── pom.xml                                     (packaging=war)
├── docker-compose.yml                          (mysql only, local dev convenience; Tomcat 9 runs locally)
├── .github/workflows/ci.yml                    (mvn verify: unit + Arquillian integration tests)
├── src/
│   ├── main/
│   │   ├── java/com/edgareldy/jsftutorial/
│   │   │   ├── entity/
│   │   │   │   ├── User.java, Role.java, Permission.java
│   │   │   │   ├── ActivationToken.java, BlacklistedToken.java, PasswordResetToken.java
│   │   │   │   ├── Category.java, Product.java, Customer.java, Order.java
│   │   │   ├── dao/
│   │   │   │   ├── BaseDao.java                 (contract: findById, findAll, save, delete — generic)
│   │   │   │   ├── impl/BaseDaoImpl.java         (EntityManager-based implementation)
│   │   │   │   ├── UserDao.java, RoleDao.java, ActivationTokenDao.java, PasswordResetTokenDao.java,
│   │   │   │   │   CategoryDao.java, ProductDao.java, CustomerDao.java, OrderDao.java
│   │   │   │   │   (each extends BaseDao, adds specific queries)
│   │   │   │   └── impl/ (one *DaoImpl per interface above)
│   │   │   ├── service/
│   │   │   │   ├── UserService.java, EmailService.java, CategoryService.java, ProductService.java,
│   │   │   │   │   CustomerService.java, OrderService.java   (contracts)
│   │   │   │   └── impl/ (one *ServiceImpl per interface, CDI @ApplicationScoped — no EJB container on Tomcat)
│   │   │   ├── config/
│   │   │   │   └── EntityManagerProducer.java    (CDI producer for EntityManager — no @PersistenceContext on Tomcat)
│   │   │   ├── bean/
│   │   │   │   ├── LoginBean.java, RegisterBean.java, ActivateAccountBean.java,
│   │   │   │   │   ForgotPasswordBean.java, ResetPasswordBean.java  (@Named, backing the auth pages)
│   │   │   │   ├── CategoryBean.java, ProductBean.java   (@Named @ViewScoped, admin CRUD pages)
│   │   │   │   ├── CustomerProfileBean.java
│   │   │   │   └── OrderBean.java
│   │   │   ├── security/
│   │   │   │   ├── SessionUserHolder.java        (@Named @SessionScoped, current logged-in user)
│   │   │   │   ├── AuthFilter.java                (@WebFilter, blocks unauthenticated/unauthorized page access)
│   │   │   │   └── PasswordHasher.java            (jBCrypt wrapper)
│   │   │   ├── exception/
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── BusinessRuleException.java
│   │   │   │   └── CustomExceptionHandlerFactory.java (JSF's global exception-handling mechanism)
│   │   │   └── util/
│   │   │       └── FacesMessageUtil.java          (see "Standard user feedback convention")
│   │   ├── resources/
│   │   │   └── META-INF/persistence.xml           (EclipseLink + MySQL JPA unit)
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml                        (AuthFilter mapping, welcome page)
│   │       │   ├── faces-config.xml                (exception handler factory registration)
│   │       │   └── beans.xml                       (CDI activation, empty)
│   │       ├── resources/
│   │       │   └── css/theme.css                   (small overrides on top of a PrimeFaces theme)
│   │       ├── templates/
│   │       │   ├── layout.xhtml                    (extends AdminFaces' /admin.xhtml: sidebar menu, growl, ui:insert content — logged-in pages only)
│   │       │   └── auth-layout.xhtml                (minimal, sidebar-free: public pages only)
│   │       ├── auth/
│   │       │   ├── login.xhtml, register.xhtml, activate.xhtml,
│   │       │   │   forgot-password.xhtml, reset-password.xhtml
│   │       ├── errors/
│   │       │   └── access-denied.xhtml
│   │       ├── admin/
│   │       │   ├── categories.xhtml, products.xhtml, orders.xhtml
│   │       ├── shop/
│   │       │   ├── catalog.xhtml, product-detail.xhtml, place-order.xhtml
│   │       ├── customer/
│   │       │   ├── profile.xhtml, my-orders.xhtml
│   │       └── index.xhtml
│   └── test/
│       └── java/com/edgareldy/jsftutorial/
│           ├── unit/          (JUnit 5 + Mockito: services, DAOs mocked)
│           ├── integration/   (Arquillian: DAOs against a real EntityManager, packaged as a micro-deployment)
│           └── e2e/           (Selenium: full page flows against a running Tomcat + MySQL)
```

## Standard user feedback convention

Every managed bean reports success/failure to the page through a single utility, kept consistent everywhere.

```java
public final class FacesMessageUtil {
    public static void addInfo(String summary) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));
    }
    public static void addError(String summary) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
    }
}
```

- Every page's template includes a PrimeFaces `<p:growl>` (or `<p:messages>` on form-heavy pages) bound to the global message queue, so any bean calling `FacesMessageUtil` surfaces consistently, regardless of which page triggered it
- `CustomExceptionHandlerFactory` catches anything a managed bean action didn't handle itself, logs it, and adds a generic `FacesMessageUtil.addError(...)` rather than letting a raw stack trace reach the browser
- No managed bean ever calls `FacesContext.addMessage` directly — always through `FacesMessageUtil`, so the convention can be changed in one place if needed

## Branching strategy

| Branch | Role |
|---|---|
| `master` | Stable, deployable code. No direct commits, only merges from `develop`. |
| `develop` | Integration branch. |
| `feature/core-architecture` | Maven/WAR setup, JPA/EclipseLink configuration, Facelets template, base DAO, exception handling, Docker dev environment. |
| `feature/auth` | Login, registration, account activation, password reset, session-based access control. |
| `feature/categories` | Admin-only category management pages. |
| `feature/products` | Admin-only product management pages, linked to `categories` via a foreign key. |
| `feature/customers` | Customer profile page, linked to `users` via a foreign key. |
| `feature/orders` | Order placement, customer order history, admin order listing. |

## feature/core-architecture

### Tasks

- [x] `pom.xml`: `war` packaging, dependencies on `javax.faces-api`, PrimeFaces 13.0.x, EclipseLink, MySQL Connector/J, jBCrypt
- [x] `META-INF/persistence.xml`: EclipseLink persistence unit pointing at MySQL, schema generation enabled for local development
- [x] `BaseDao<T>`/`BaseDaoImpl<T>`: generic `EntityManager`-based CRUD (`findById`, `findAll`, `save`, `delete`), every specific DAO extends it
- [x] `templates/layout.xhtml`: extends AdminFaces' `/admin.xhtml` (sidebar menu, logo, head), a `<p:growl>` bound globally, `<ui:insert name="content">` for page bodies
- [x] `CustomExceptionHandlerFactory` + `faces-config.xml` registration: catches unhandled exceptions from any managed bean action, logs them, adds a generic error `FacesMessage`
- [x] `FacesMessageUtil`
- [x] `AuthFilter` skeleton (`@WebFilter("/*")`) — no rules yet, just the plumbing; `feature/auth` fills in the actual authorization logic
- [x] `docker-compose.yml`: `mysql` service, for local development (Tomcat 9 runs locally, not containerized)
- [x] `.github/workflows/ci.yml`: `mvn verify` running the unit and Arquillian integration test suites

## feature/auth

### Pages

| URL | Bean | Access | Description |
|---|---|---|---|
| `/auth/login.xhtml` | `LoginBean` | Public | Authenticates, starts the session |
| `/auth/register.xhtml` | `RegisterBean` | Public | Creates a disabled account + activation token |
| `/auth/activate.xhtml?token=...` | `ActivateAccountBean` | Public | Enables the account |
| `/auth/forgot-password.xhtml` | `ForgotPasswordBean` | Public | Generates a password-reset token |
| `/auth/reset-password.xhtml?token=...` | `ResetPasswordBean` | Public | Consumes the token, updates the password |

### Tasks

- [x] `User`, `Role`, `Permission` entities (`@ManyToMany` via `role_user`/`role_permission` join tables), `ActivationToken`, `BlacklistedToken`, `PasswordResetToken`
- [x] `UserDao`, `RoleDao`, `ActivationTokenDao`, `PasswordResetTokenDao` + implementations
- [x] `EmailService` (interface) + `EmailServiceImpl` implementation (JavaMail): `sendActivationEmail`/`sendPasswordResetEmail`, each building a link back to `/auth/activate.xhtml?token=...`/`/auth/reset-password.xhtml?token=...` from a configurable base URL (`APP_BASE_URL` env var), sent off the request thread
- [x] `UserService` (interface) + implementation: registration (creates a disabled account + activation token, triggers `sendActivationEmail`), activation (validates the token, enables the account, keeps the token row with `validated_at` set), login verification, forgotten password (creates a password-reset token, triggers `sendPasswordResetEmail`), password reset (validates the token, updates the password, consumes/removes the token) — all password comparisons go through `PasswordHasher` (jBCrypt), never a plain-text comparison
- [x] `SessionUserHolder` (`@Named @SessionScoped`): holds the authenticated `User` (or `null`) and their resolved permissions for the duration of the HTTP session
- [x] `AuthFilter` filled in: reads `SessionUserHolder`, redirects unauthenticated users hitting a non-public page to `/auth/login.xhtml`, and users lacking the required role hitting `/admin/*` to an "access denied" page
- [x] `LoginBean`, `RegisterBean`, `ActivateAccountBean`, `ForgotPasswordBean`, `ResetPasswordBean` (`@Named @RequestScoped`), each calling `UserService` and reporting outcome via `FacesMessageUtil`
- [x] `templates/auth-layout.xhtml`: a minimal, sidebar-free public-page template (unlike `layout.xhtml`'s AdminLTE dashboard chrome, which only makes sense once a user is logged in) — the five `.xhtml` pages under `auth/` (plus `errors/access-denied.xhtml`) extend it, using PrimeFaces `<p:inputText>`/`<p:password>`/`<p:commandButton>`
- [x] Unit tests (`UserService`, `PasswordHasher`, `EmailService` message-building), Arquillian integration tests (`UserDao` against a real `EntityManager`), Selenium e2e test covering register → activate → login

## feature/categories

Admin-only. Depends on `feature/auth`'s `AuthFilter` being in place to restrict `/admin/*`.

### Pages

| URL | Bean | Access | Description |
|---|---|---|---|
| `/admin/categories.xhtml` | `CategoryBean` | ADMIN | List/create/edit/delete categories |

### Tasks

- [x] `Category` entity
- [x] `CategoryDao` + implementation
- [x] `CategoryService` (interface) + implementation: plain CRUD only on this branch, no business rule yet, `Product` (and anything to protect a category from) doesn't exist until `feature/products`
- [x] `CategoryBean` (`@Named @ViewScoped`, so the PrimeFaces `<p:dataTable>` state survives AJAX postbacks within the page)
- [x] `categories.xhtml`: PrimeFaces `<p:dataTable>` with inline row actions, `<p:dialog>` for the create/edit form
- [x] Unit tests, Arquillian integration tests, Selenium e2e test covering create → edit → delete

## feature/products

Admin-only. Depends on `feature/categories` (`Product.category` is a real foreign key to `Category`).

### Pages

| URL | Bean | Access | Description |
|---|---|---|---|
| `/admin/products.xhtml` | `ProductBean` | ADMIN | List/create/edit/delete products, filter by category |

### Tasks

- [ ] `Product` entity (`@ManyToOne` to `Category`)
- [ ] `ProductDao` + implementation (includes a find-by-category query and a count-by-category query)
- [ ] `ProductService` (interface) + implementation
- [ ] `ProductBean` (`@Named @ViewScoped`)
- [ ] `products.xhtml`: PrimeFaces `<p:dataTable>` with inline row actions, category filter, `<p:dialog>` for the create/edit form
- [ ] Business rule (now that `Product` exists): `CategoryService.delete` rejects a category that still has products with a `BusinessRuleException`, surfaced via `FacesMessageUtil.addError`, checked via `ProductDao`'s count-by-category query
- [ ] Unit tests, Arquillian integration tests, Selenium e2e test covering create → edit → filter by category, plus attempted delete of a non-empty category on `admin/categories.xhtml` (expects the rejection message)

## feature/customers

### Pages

| URL | Bean | Access | Description |
|---|---|---|---|
| `/customer/profile.xhtml` | `CustomerProfileBean` | Authenticated | View/edit the current user's customer profile |

### Tasks

- [ ] `Customer` entity, `user_id` as a `@ManyToOne` (or `@OneToOne`) foreign key to `User`
- [ ] `CustomerDao` + implementation
- [ ] `CustomerService` (interface) + implementation
- [ ] `CustomerProfileBean` (`@Named @ViewScoped`): loads/creates the profile for `SessionUserHolder`'s current user
- [ ] `profile.xhtml`
- [ ] Unit, integration, and e2e tests

## feature/orders

### Pages

| URL | Bean | Access | Description |
|---|---|---|---|
| `/shop/catalog.xhtml` | `ProductBean` (reused, read-only mode) | Public | Browse products |
| `/shop/place-order.xhtml?productId=...` | `OrderBean` | Authenticated | Create an order for the current customer |
| `/customer/my-orders.xhtml` | `OrderBean` | Authenticated | The current customer's order history |
| `/admin/orders.xhtml` | `OrderBean` | ADMIN | All orders |

### Tasks

- [ ] `Order` entity
- [ ] `OrderDao` + implementation
- [ ] `OrderService` (interface) + implementation: computes `total = quantity * product.unitPrice`, resolves the customer from `SessionUserHolder`
- [ ] `OrderBean` (`@Named @ViewScoped`), reused across the three order-related pages with different display modes
- [ ] `place-order.xhtml`, `my-orders.xhtml`, `admin/orders.xhtml`
- [ ] Unit, integration, and e2e tests, including a full Selenium flow: browse catalog → place an order → see it in "my orders" → see it in the admin order list

## Order of work

1. `feature/core-architecture` → Pull Request to `develop`
2. `feature/auth` (depends on `core-architecture`) → Pull Request to `develop`
3. `feature/categories` (depends on `auth` for `/admin/*` protection) → Pull Request to `develop`
4. `feature/products` (depends on `categories`) → Pull Request to `develop`
5. `feature/customers` (depends on `auth`) → Pull Request to `develop`
6. `feature/orders` (depends on `products`, `customers`) → Pull Request to `develop`
7. `develop` → `master`

## Code conventions

- Root package: `com.edgareldy.jsftutorial`
- DAO and service layers follow a contract/implementation pattern: interface at the root of `dao/`/`service/`, implementation in `dao/impl/`/`service/impl/`
- Every managed bean reports outcomes exclusively through `FacesMessageUtil`, never a direct `FacesContext.addMessage` call
- No managed bean talks to a DAO directly — always through a service, even for a single-entity lookup
- `@ViewScoped` for any bean backing a page with AJAX postbacks (data tables, dialogs); `@RequestScoped` for simple single-submission forms (login, register); `@SessionScoped` reserved for `SessionUserHolder` alone
- Passwords are never compared or stored as plain text — always through `PasswordHasher`

## Concepts covered

- JSF 2.3 with Facelets templating (`ui:insert`/`ui:composition`)
- CDI-managed beans and scopes (`@RequestScoped`, `@ViewScoped`, `@SessionScoped`)
- PrimeFaces components (`p:dataTable`, `p:dialog`, `p:growl`, `p:link`)
- AdminFaces `admin-template` (Bootstrap + AdminLTE dashboard layout, sidebar menu)
- JPA/EclipseLink entity mapping, including `@ManyToMany` join tables
- The DAO + service contract/implementation layering
- Session-based authentication and filter-based authorization (`@WebFilter`)
- A single, centralized user-feedback convention (`FacesMessageUtil`)
- Global exception handling in JSF (`ExceptionHandlerFactory`/`ExceptionHandlerWrapper`)
- Password hashing (jBCrypt)
- Testing at three levels: JUnit/Mockito (unit), Arquillian (container integration), Selenium (end-to-end UI)

## How to follow this tutorial

1. Clone the repository and check out `develop`
2. Follow the branches in order: `feature/core-architecture` → `feature/auth` → `feature/categories` → `feature/products` → `feature/customers` → `feature/orders`
3. Run `docker-compose up` for MySQL, then deploy the WAR to the local Tomcat 9 (`mvn package`, then copy `target/jsf_tutorial.war` to `/opt/tomcat9/webapps/`, or use the Tomcat Maven plugin)
4. Access the application at `http://localhost:8080/jsf_tutorial/`
