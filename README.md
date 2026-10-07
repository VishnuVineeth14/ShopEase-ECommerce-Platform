# ShopEase: Full-Stack E-Commerce System

## Abstract

ShopEase is a full-stack e-commerce application designed to support online product
discovery, shopping-cart management, order processing, wishlist operations, product
reviews, and role-based administration. The system is implemented using a Vue.js 3
frontend, MongoDB persistence, and Spring Boot REST services secured with JWT-based
authentication. The system supports two interchangeable backend architectural modes:
a layered monolithic Spring Boot REST backend and a distributed **4-Microservices Architecture**
coordinated by an API Gateway.

**Keywords—** E-commerce, Microservices, Spring Boot, Vue.js, MongoDB, API Gateway, REST API, JWT, role-based access control.

---

## 1. Introduction

The objective of ShopEase is to provide a modular, secure, and production-ready foundation for an online shopping platform. The application separates presentation, business logic, data access, and persistence concerns:

1. **Frontend**: A Vue.js 3 single-page application built with Vite and Pinia providing customer, seller, and administrator interfaces.
2. **Backend Option A (Monolith)**: A unified Spring Boot REST application combining all domain services into a single deployable artifact.
3. **Backend Option B (Microservices)**: A decomposed distributed architecture comprising **4 dedicated domain microservices** (`user-service`, `product-service`, `cart-service`, `order-service`) fronted by an `api-gateway` on port 8080.

The project is suitable for academic evaluation, software engineering course projects, enterprise microservices study, and production extension.

---

## 2. Technology Stack

| Layer | Technologies |
|---|---|
| **Frontend** | Vue.js 3, Vite, Vue Router, Pinia, Axios, Bootstrap 5, Sass |
| **Backend Framework** | Java 17/21/24, Spring Boot 3.2.5, Spring Web, Spring Security, Spring Data MongoDB |
| **Microservices Stack** | Spring Boot Multi-Module Maven, Spring RestClient inter-service communication, Spring Boot API Gateway |
| **Authentication & Security** | JSON Web Tokens (JJWT 0.12.5, HMAC-SHA256), BCrypt password hashing, Zero-Committed Secrets policy |
| **Database** | MongoDB 6.0 or later (separate database namespaces per microservice) |
| **API Documentation** | OpenAPI 3.0 / Swagger UI |
| **Build & Package Tools** | Apache Maven 3.8+, Node.js 18+, npm 9+ |
| **Development Environments** | IntelliJ IDEA (backend & microservices), Visual Studio Code (frontend) |

---

## 3. System Architecture

ShopEase can be run in either of two backend configurations, both 100% compatible with the Vue.js frontend without changing any client-side code:

### 3.1 Distributed Microservices Architecture (Recommended)

```mermaid
graph TD
    Client["Vue.js 3 Client (Port 5173)"]
    Gateway["API Gateway (Port 8080)"]

    subgraph Microservices Layer
        US["1. User & Auth Service (Port 8081)"]
        PS["2. Product & Catalog Service (Port 8082)"]
        CS["3. Cart & Wishlist Service (Port 8083)"]
        OS["4. Order & Payment Service (Port 8084)"]
    end

    subgraph MongoDB Persistence
        DB_U[("shopease_user_db")]
        DB_P[("shopease_product_db")]
        DB_C[("shopease_cart_db")]
        DB_O[("shopease_order_db")]
    end

    Client -->|HTTP /api/* + JWT| Gateway
    Gateway -->|/api/auth/*, /api/admin/users/*| US
    Gateway -->|/api/products/*, /api/categories/*, /api/seller/products/*, /api/reviews/*| PS
    Gateway -->|/api/cart/*, /api/wishlist/*| CS
    Gateway -->|/api/orders/*, /api/seller/orders/*, /api/admin/orders/*, /api/*/dashboard| OS

    US --- DB_U
    PS --- DB_P
    CS --- DB_C
    OS --- DB_O

    CS -.->|Stock check| PS
    OS -.->|Clear cart| CS
    OS -.->|Deduct / Restore inventory| PS
```

#### Microservices Domain Breakdown

| Service | Port | Database | Primary Scope |
|---|---|---|---|
| **User Service** | `8081` | `shopease_user_db` | Authentication, registration, JWT issuance, profile, admin user management. |
| **Product Service** | `8082` | `shopease_product_db` | Categories, catalog, search/filters, seller product CRUD, reviews, atomic stock operations. |
| **Cart Service** | `8083` | `shopease_cart_db` | Persistent cart items, wishlist, live stock validation via Product Service, move-to-cart. |
| **Order Service** | `8084` | `shopease_order_db` | Distributed checkout, mock payments, seller fulfillment, cancellation inventory restock, analytics. |
| **API Gateway** | `8080` | *Stateless* | Reverse proxy, CORS enforcement, JWT header propagation, unified frontend endpoint. |

### 3.2 Monolithic Layered Architecture

```text
Vue.js 3 Client (Port 5173)
      |
      | HTTP/JSON + JWT Bearer Token
      v
Spring Boot Monolith REST API (Port 8080)
      |
      +-- Controllers (Auth, Product, Category, Cart, Order, Review, Admin, Seller)
      +-- Services
      +-- Repositories
      +-- Spring Security / JWT
      v
MongoDB Database (shopease_db)
```

---

## 4. Functional Features

### 4.1 Authentication and Authorization
- Customer and Seller registration with validation.
- JWT-based stateless authentication with BCrypt password encryption.
- Role-based authorization for `ADMIN`, `SELLER`, and `CUSTOMER` roles.
- Protected routes across carts, wishlists, orders, seller management, and administrative dashboards.
- Automatic session termination upon token expiration via Axios response interceptors.

### 4.2 Customer Features
- Browse catalog with category filtering, keyword search, price range filtering, and multi-field sorting.
- View detailed product specifications and community customer reviews.
- Manage shopping cart items (add, adjust quantity, remove, and clear).
- Manage personal wishlist and transfer wishlist items to cart in a single click.
- Seamless checkout supporting Mock Card Payment and Cash on Delivery.
- Order history with live order status tracking and customer order cancellation.
- Submit product reviews and star ratings.

### 4.3 Seller Features
- Dedicated seller analytics dashboard (total sales revenue, active orders, low-stock alerts).
- Create, modify, and delete seller-owned products with image URLs and inventory counts.
- View seller-specific order items and update shipping/delivery statuses.

### 4.4 Administrator Features
- Platform-wide dashboard aggregating metrics across users, categories, products, and gross revenue.
- User management: search users, filter by role, activate/deactivate accounts, and delete users.
- Category catalog management (create, update, delete).
- Platform-wide order oversight and inspection.

---

## 5. Project Structure

```text
full_stack_shopping_cart/
├── frontend/                     # Vue.js 3 Client Application (Vite + Pinia)
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── components/          # Reusable components (Navbar, Modals)
│       ├── router/              # Vue Router navigation & route guards
│       ├── services/            # Axios API client (configured for http://localhost:8080/api)
│       ├── stores/              # Pinia state stores (auth, cart)
│       └── views/               # Customer, Seller, Admin, and Auth screens
│
├── microservices/                # Distributed Microservices Architecture (Spring Boot 3)
│   ├── pom.xml                  # Parent Maven Multi-Module descriptor
│   ├── README.md                # Comprehensive microservices architectural manual
│   ├── .run/                    # Pre-configured IntelliJ IDEA Run Configurations
│   │   ├── ApiGatewayApplication.run.xml
│   │   ├── CartServiceApplication.run.xml
│   │   ├── OrderServiceApplication.run.xml
│   │   ├── ProductServiceApplication.run.xml
│   │   └── UserServiceApplication.run.xml
│   ├── api-gateway/             # Port 8080 - Unified reverse proxy router
│   ├── user-service/            # Port 8081 - Auth & User accounts (shopease_user_db)
│   ├── product-service/         # Port 8082 - Catalog, Categories, Reviews (shopease_product_db)
│   ├── cart-service/            # Port 8083 - Cart & Wishlist (shopease_cart_db)
│   └── order-service/           # Port 8084 - Checkout, Orders, Analytics (shopease_order_db)
│
├── backend/                      # Original Monolithic Spring Boot REST API (Port 8080)
│   ├── pom.xml
│   └── src/
│
├── .env.example                 # Safe environment variable template
├── .gitignore                   # Security-hardened gitignore (secrets, targets, IDEs)
├── explain.md                   # Beginner architecture and dataflow explanations
└── README.md                    # Primary system documentation
```

---

## 6. Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher (JDK 21 and 24 fully supported).
- **Maven**: Version 3.8 or higher.
- **Node.js**: Version 18 or higher and **npm** 9 or higher.
- **MongoDB**: Version 6.0 or higher, running locally on `localhost:27017` or via MongoDB Atlas.
- **IDE**: IntelliJ IDEA (for backend & microservices), Visual Studio Code (for frontend).

---

## 7. Database Configuration

Start MongoDB on the default local port, `27017`:
```bash
brew services start mongodb-community
# or
mongod --dbpath /path/to/data
```

- In **Monolith mode**, data is stored in database `shopease_db`.
- In **Microservices mode**, data is cleanly segregated into:
  - `shopease_user_db` (users)
  - `shopease_product_db` (categories, products, reviews)
  - `shopease_cart_db` (carts, wishlists)
  - `shopease_order_db` (orders)

MongoDB automatically creates each database and its collections upon first write.

---

## 8. Running the Backend

You can choose to run either the **Microservices Architecture** (Option A) or the **Monolith** (Option B):

### Option A: Running the Microservices in IntelliJ IDEA (Recommended)

1. **Open the Project:**
   - In IntelliJ IDEA, select **File -> Open...**
   - Select the `microservices` folder:
     `/Users/jhansisiva/Documents/Win college /sem5/web devolopment/full_stack_shopping_cart/microservices`
   - IntelliJ automatically imports `pom.xml` and detects all 5 Spring Boot submodules.
2. **Launch Services:**
   - In IntelliJ, open the **Services** tool window (**View -> Tool Windows -> Services** or `Cmd+8` / `Alt+8`).
   - Run the services in order (or click "Run All"):
     1. `UserServiceApplication` (Port 8081)
     2. `ProductServiceApplication` (Port 8082)
     3. `CartServiceApplication` (Port 8083)
     4. `OrderServiceApplication` (Port 8084)
     5. `ApiGatewayApplication` (Port 8080)
3. **Verify Gateway Health:**
   - The API Gateway is active at `http://localhost:8080/api`.

### Option B: Running the Monolith Backend

1. In IntelliJ IDEA or terminal, open the `backend` directory.
2. Run the `com.shopease.ShopEaseApplication` class, or in terminal:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
3. Available at `http://localhost:8080`.

---

## 9. Running the Frontend

1. Open a terminal and navigate to `frontend`:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
2. Open your browser at `http://localhost:5173`.
3. The frontend points directly to `http://localhost:8080/api` and works identically whether backed by the Monolith or the Microservices API Gateway!

---

## 10. Demonstration Accounts

Default demonstration accounts are seeded automatically on first startup:

| Role | Email | Password | Access Rights |
|---|---|---|---|
| **Administrator** | `admin@example.com` | `Admin@123` | Full dashboard analytics, user status toggle/deletion, category controls, order oversight. |
| **Seller** | `seller@example.com` | `Seller@123` | Seller sales dashboard, inventory management, product CRUD, order status updates. |
| **Customer** | `customer@example.com` | `Customer@123` | Product catalog, cart, wishlist, checkout, review submissions, order history. |

---

## 11. Security and Zero-Secret Git Policy

- **No Committed Secrets**: No hardcoded JWT keys or credentials exist in Git-tracked files. All `application.yml` configs use `${JWT_SECRET:}`.
- **Automated Local Key Resolution**: On local machines, microservices automatically resolve and share an uncommitted HMAC-256 key (`~/.shopease/.jwt_secret`), avoiding token signature mismatches without requiring manual environment setup.
- **Production Overrides**: In staging or production, inject secrets via environment variables:
  ```bash
  export JWT_SECRET="your-256-bit-hex-or-base64-secret"
  export MONGODB_URI="mongodb+srv://user:pass@cluster/db"
  ```
- **Security-Hardened `.gitignore`**: Excludes all `.env`, `.env.*`, `*.key`, `*.pem`, `*.jks`, `target/`, `node_modules/`, `dist/`, `.idea/`, and `.DS_Store` files.
- **Stateless Tokens**: JJWT 0.12.5 signs tokens embedding user identity and role, allowing downstream microservices to validate authorization headers independently.

---

## 12. Verification & Testing Workflow

After starting MongoDB, the backend services, and the frontend, verify the platform:

1. **Authentication**: Register a new user or log in with `customer@example.com` / `Customer@123`.
2. **Product Catalog**: Filter by category, test price search, view product details.
3. **Cart & Wishlist**: Add items to cart, modify quantities, add to wishlist, test "Move to Cart".
4. **Checkout**: Select Mock Card Payment or Cash on Delivery, enter shipping information, and place order.
5. **Inventory Sync**: Verify product stock decreases upon checkout and restores if the order is cancelled.
6. **Seller Portal**: Log in as `seller@example.com` / `Seller@123`, add a new product, and update an order from `PENDING` to `SHIPPED`.
7. **Admin Portal**: Log in as `admin@example.com` / `Admin@123`, inspect aggregated revenue and toggle a user's active status.
8. **Swagger Testing**: Inspect microservice endpoints at `http://localhost:8081/swagger-ui.html`, `http://localhost:8082/swagger-ui.html`, `http://localhost:8083/swagger-ui.html`, and `http://localhost:8084/swagger-ui.html`.

---

## 13. Conclusion

ShopEase illustrates a modern, enterprise-grade e-commerce application. By providing both a monolithic implementation and a cleanly decoupled **4-Microservices Architecture** with MongoDB persistence and an API Gateway, the project serves as an ideal reference implementation for full-stack software development, distributed systems architecture, and secure pair programming.
