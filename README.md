# ShopEase: Full-Stack E-Commerce and Shopping Cart System

## Abstract

ShopEase is a full-stack e-commerce application designed to support online product
discovery, shopping-cart management, order processing, wishlist operations, product
reviews, and role-based administration. The system is implemented using a Vue.js
frontend, a Spring Boot REST backend, MongoDB persistence, and JWT-based
authentication. The application provides separate workflows for customers, sellers,
and administrators.

**Keywords—** E-commerce, Spring Boot, Vue.js, MongoDB, REST API, JWT, role-based
access control, shopping cart.

## 1. Introduction

The objective of ShopEase is to provide a modular and secure foundation for an online
shopping platform. The application separates presentation, business logic, data
access, and persistence concerns. This architecture supports independent development
of the backend and frontend while maintaining a clear REST-based communication layer.

The project is suitable for academic demonstration, software engineering practice,
and future extension into a production-ready commerce platform.

## 2. Technology Stack

| Layer | Technologies |
|---|---|
| Frontend | Vue.js 3, Vite, Vue Router, Pinia, Axios, Bootstrap 5, Sass |
| Backend | Java 17, Spring Boot 3.2.5, Spring Web, Spring Security, Spring Data MongoDB |
| Authentication | JSON Web Tokens (JWT), BCrypt password hashing |
| Database | MongoDB 6.0 or later |
| API documentation | OpenAPI/Swagger |
| Build tools | Maven 3.8 or later, npm 9 or later |
| Development tools | Spring Tool Suite/IntelliJ IDEA for backend, Visual Studio Code for frontend |

## 3. System Architecture

ShopEase follows a layered client-server architecture:

1. The Vue.js client provides role-specific user interfaces and communicates with the
   backend through Axios-based HTTP requests.
2. Spring Boot exposes REST endpoints through controller classes.
3. Service classes implement validation, authorization-aware business logic, and
   transaction workflows.
4. Repository interfaces provide MongoDB data access.
5. Spring Security and the JWT filter authenticate requests and enforce role-based
   authorization.

```text
Vue.js 3 Client
      |
      | HTTP/JSON + JWT Bearer Token
      v
Spring Boot REST API
      |
      +-- Controllers
      +-- Services
      +-- Repositories
      +-- Spring Security / JWT
      v
MongoDB Database
```

## 4. Functional Features

### 4.1 Authentication and Authorization

- Customer registration and login.
- JWT-based stateless authentication.
- BCrypt password hashing.
- Role-based access control for `ADMIN`, `SELLER`, and `CUSTOMER` users.
- Protected routes for carts, wishlists, orders, seller operations, and administration.
- Validation and structured handling of unauthorized and forbidden requests.

### 4.2 Customer Features

- Browse products and categories.
- Search products by keyword.
- Filter products by category and price range.
- Sort product results.
- View product details and reviews.
- Add, update, remove, and clear cart items.
- Add and remove wishlist products.
- Move wishlist items to the cart.
- Checkout using mock card payment or cash on delivery.
- View order history and order details.
- Cancel eligible orders.
- Submit product reviews.
- View a customer dashboard.

### 4.3 Seller Features

- View seller dashboard statistics.
- View seller-owned products.
- Create products.
- Update product information, pricing, and inventory.
- Delete seller-owned products.
- View seller orders.
- Update order status.

### 4.4 Administrator Features

- View platform dashboard statistics.
- View and search users.
- Filter users by role.
- Enable or disable user accounts.
- Delete users.
- Create, update, and delete categories.
- Delete products.
- View all orders.

### 4.5 Data and API Features

- MongoDB document persistence for users, products, categories, carts, orders,
  wishlists, and reviews.
- DTO-based request and response handling.
- Bean Validation for incoming requests.
- Centralized exception handling.
- Consistent API response formatting.
- OpenAPI/Swagger documentation.
- Development data seeding for demonstration accounts, categories, and products.

## 5. Project Structure

```text
ShopEase/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/shopease/
│       │   ├── ShopEaseApplication.java
│       │   ├── config/          # CORS, security, Swagger, and data seeding
│       │   ├── controller/      # REST API endpoints
│       │   ├── dto/             # Request and response objects
│       │   ├── exception/       # Custom exceptions and global handler
│       │   ├── model/           # MongoDB domain models and enums
│       │   ├── repository/      # Spring Data MongoDB repositories
│       │   ├── security/        # JWT filter, service, and user details
│       │   └── service/         # Application and business logic
│       └── resources/
│           └── application.properties
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── assets/              # Sass and frontend assets
│       ├── components/          # Reusable Vue components
│       ├── router/              # Vue Router configuration
│       ├── services/            # Axios API service
│       ├── stores/              # Pinia authentication and cart state
│       └── views/               # Admin, seller, customer, and auth screens
├── .env.example                # Safe environment-variable template
├── .gitignore                  # Local files and secrets excluded from Git
├── explain.md                  # Additional implementation notes
└── README.md
```

Generated directories such as `backend/target`, `frontend/node_modules`, and
`frontend/dist` are intentionally excluded from version control.

## 6. Prerequisites

Install the following software before starting the application:

- Java Development Kit (JDK) 17 or later.
- Maven 3.8 or later.
- Node.js 18 or later and npm 9 or later.
- MongoDB 6.0 or later, running locally or available through MongoDB Atlas.
- Spring Tool Suite or IntelliJ IDEA for running the backend.
- Visual Studio Code for running and modifying the frontend.

Verify the installations:

```bash
java -version
mvn -version
node --version
npm --version
```

## 7. Database Configuration

### 7.1 Local MongoDB

Start MongoDB on the default local port, `27017`. ShopEase uses the database named
`shopease_db` by default. MongoDB creates the database and collections when data is
first written.

### 7.2 MongoDB Atlas or Another MongoDB Server

Do not commit a connection string containing a username or password. Override the
default configuration through the Spring Boot run configuration or an ignored local
profile. For example:

```text
SPRING_DATA_MONGODB_URI=mongodb+srv://<username>:<password>@<cluster>/<database>
```

The angle-bracket values are placeholders and must not be committed as real
credentials.

## 8. Running the Backend in Spring Boot

### 8.1 Import the Backend

1. Open Spring Tool Suite or IntelliJ IDEA.
2. Select **Open** or **Import Existing Maven Project**.
3. Choose the `backend` directory.
4. Allow Maven to download and index the project dependencies.
5. Confirm that the project uses JDK 17 or a compatible newer JDK.

### 8.2 Configure the JWT Secret

For local development, the application generates a temporary signing key if
`JWT_SECRET` is not configured. Existing tokens become invalid when the backend is
restarted.

For a persistent environment, add the following environment variable to the Spring
Boot run configuration:

```text
JWT_SECRET=<strong-base64-encoded-secret>
```

Never place the real value in `application.properties`, source code, screenshots, or
Git history.

### 8.3 Start the Backend

1. Open `backend/src/main/java/com/shopease/ShopEaseApplication.java`.
2. Run the `ShopEaseApplication` Spring Boot application.
3. Confirm that the backend is available at:

```text
http://localhost:8080
```

The backend can also be started from a terminal:

```bash
cd backend
mvn spring-boot:run
```

Swagger documentation is available at:

```text
http://localhost:8080/swagger-ui.html
```

## 9. Running the Frontend in Visual Studio Code

1. Open the project folder in Visual Studio Code.
2. Open a terminal in VS Code.
3. Navigate to the frontend directory:

```bash
cd frontend
```

4. Install the frontend dependencies:

```bash
npm install
```

5. Start the Vite development server:

```bash
npm run dev
```

6. Open the URL printed by Vite, normally:

```text
http://localhost:5173
```

The frontend expects the backend to be running at `http://localhost:8080`.

## 10. Demonstration Accounts

The development data seeder creates the following accounts on first startup:

| Role | Email | Password |
|---|---|---|
| Administrator | `admin@example.com` | `Admin@123` |
| Seller | `seller@example.com` | `Seller@123` |
| Customer | `customer@example.com` | `Customer@123` |

These credentials are for local demonstration only. They must be changed or removed
before any public or production deployment.

## 11. Important API Resource Groups

| Resource | Base path | Purpose |
|---|---|---|
| Authentication | `/api/auth` | Registration and login |
| Products | `/api/products` | Product browsing, search, filtering, and sorting |
| Categories | `/api/categories` | Public category queries |
| Cart | `/api/cart` | Customer cart operations |
| Wishlist | `/api/wishlist` | Customer wishlist operations |
| Orders | `/api/orders` | Checkout, order history, cancellation, and seller status updates |
| Reviews | `/api/reviews` | Product review retrieval and creation |
| Customer | `/api/customer` | Customer dashboard |
| Seller | `/api/seller` | Seller products, dashboard, and order operations |
| Administrator | `/api/admin` | User, category, product, and order administration |

## 12. Security and Configuration Practices

- JWT secrets are supplied through environment variables and are not committed.
- `.env` files, local Spring profiles, private keys, certificates, credentials, logs,
  IDE metadata, and generated build directories are ignored by Git.
- Passwords are stored using BCrypt hashing rather than plaintext persistence.
- Stateless JWT authentication is used instead of server-side sessions.
- Endpoint permissions are restricted by role.
- Input validation is applied to request DTOs.
- CORS is restricted to local development origins by default.
- Demo credentials must not be reused in production.
- If a secret was previously committed, rotate it and remove it from repository history
  using an appropriate repository-history tool before publishing the repository.

## 13. Verification Workflow

After starting MongoDB, the backend, and the frontend, verify the following sequence:

1. Open the frontend and register or log in as a customer.
2. Browse products, search by keyword, and apply filters.
3. Add a product to the cart and modify its quantity.
4. Add a product to the wishlist and move it to the cart.
5. Complete checkout and verify the order history.
6. Log in as the seller and create or update a product.
7. Update an order status from the seller dashboard.
8. Log in as the administrator and verify user, category, product, and order controls.
9. Open Swagger and verify that protected endpoints require authentication.

## 14. Suggested Feature Improvements

The following improvements are recommended for a production release:

- Replace mock payments with a PCI-compliant payment provider.
- Add refresh tokens, token revocation, and configurable token expiration.
- Add email verification, password reset, and multi-factor authentication.
- Move demo-account seeding behind an explicit development-only profile.
- Add automated unit, integration, controller, and end-to-end tests.
- Add pagination and indexed search for large product and order collections.
- Add image upload storage using a secure object-storage provider.
- Add inventory reservation and transactional checkout handling.
- Add audit logs for administrative operations.
- Add rate limiting, security headers, request tracing, and centralized monitoring.
- Add CI checks for dependency vulnerabilities, secret scanning, formatting, and tests.
- Add containerized deployment and separate development, staging, and production profiles.

## 15. Conclusion

ShopEase demonstrates a complete full-stack shopping workflow using a modern Vue.js
client and a layered Spring Boot REST backend. Its modular organization, MongoDB
persistence, JWT authentication, role-based access control, and separate customer,
seller, and administrator workflows provide a strong foundation for continued
development and production hardening.
