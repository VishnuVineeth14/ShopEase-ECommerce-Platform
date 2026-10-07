# Full Stack Shopping Cart Application - Workflow & Architecture

Welcome to the ShopEase Full Stack Shopping Cart Application! This guide is designed for beginners (freshers) to understand how the entire application works, from the user clicking a button on the screen to the data being saved in the database.

The application is divided into three main layers:
1. **Frontend (Vue.js 3):** What the user sees and interacts with.
2. **Backend (Spring Boot 3):** The brain of the application that handles business logic and security.
3. **Database (MongoDB):** Where all the data is permanently stored.

---

## 1. Frontend (Vue.js 3)

The frontend is built using **Vue.js 3** along with **Vite** (for fast building) and **Pinia** (for state management).

### File Tree
```text
frontend/
├── index.html            # The main HTML file where the Vue app is injected.
├── package.json          # Lists all the project dependencies (libraries) and scripts (like 'npm run dev').
├── vite.config.js        # Configuration for Vite, the build tool that runs the development server.
└── src/
    ├── main.js           # The entry point of the Vue app; initializes Vue, Pinia, and the Router.
    ├── App.vue           # The root component that contains the Navbar and a placeholder for other pages (<router-view>).
    ├── router/
    │   └── index.js      # Defines the navigation paths (URLs) and which component to load for each URL.
    ├── services/
    │   └── api.js        # Configures Axios for making HTTP requests to the backend, adding the security token automatically.
    ├── stores/
    │   ├── auth.js       # Pinia store that handles user login, logout, and keeps track of the current user's role.
    │   └── cart.js       # Pinia store that keeps track of the items in the shopping cart across the application.
    ├── components/
    │   └── Navbar.vue    # The top navigation bar; changes its links based on whether the user is an Admin, Seller, or Customer.
    ├── assets/
    │   └── main.scss     # The main styling file containing custom CSS variables and visual designs.
    └── views/
        ├── auth/         # Contains LoginView.vue and RegisterView.vue for user authentication.
        ├── admin/        # Views for Admin (Dashboard, Users, Categories, Orders management).
        ├── seller/       # Views for Seller (Dashboard, Products management, Seller Orders).
        └── customer/     # Views for Customer (Shop, Wishlist, Cart, Checkout, Order History).
```

### Important Concepts & How the Flow Works
* **Single Page Application (SPA):** Instead of loading a new web page every time you click a link, Vue simply swaps out components inside `App.vue`. This is handled by **Vue Router** (`router/index.js`), making the app feel extremely fast.
* **State Management (Pinia):** In a complex app, multiple pages need to know if you are logged in or how many items are in your cart. **Pinia** (`stores/auth.js` and `cart.js`) acts as a global memory that any component can read from and write to.
* **API Interceptors:** Whenever the frontend asks the backend for data (e.g., fetching products), it uses `services/api.js`. This file automatically attaches your JWT (JSON Web Token) to the request so the backend knows who you are. If a token expires, the interceptor catches the `401 Unauthorized` error and safely logs you out.
* **Component Flow:** 
  1. A user visits `/login`. `LoginView.vue` is displayed.
  2. The user submits the form. The component calls a function in the `auth` store.
  3. The `auth` store uses `api.js` to send a POST request to the backend.
  4. The backend replies with a token and user details. The store saves this token in `localStorage`.
  5. The router redirects the user to their specific dashboard based on their role (Admin, Seller, or Customer).

---

## 2. Backend (Java + Spring Boot 3)

The backend is built using **Java** and the **Spring Boot** framework. It provides REST APIs for the frontend to consume.

### File Tree
```text
backend/
├── pom.xml                                   # Maven configuration file listing all Java dependencies (Spring Web, MongoDB, JWT).
└── src/main/java/com/shopease/
    ├── ShopEaseApplication.java              # The main class that starts the Spring Boot backend server.
    ├── config/
    │   ├── CorsConfig.java                   # Tells the backend which frontend URLs are allowed to request data.
    │   ├── SecurityConfig.java               # Configures Spring Security (who can access what URLs, login rules).
    │   ├── OpenApiConfig.java                # Sets up Swagger UI for testing the API documentation.
    │   └── DataSeeder.java                   # Automatically inserts dummy users and products into the database when the app starts.
    ├── controller/                           # The "Endpoints". Receives HTTP requests from the frontend and sends HTTP responses.
    │   ├── AuthController.java               # Handles /api/auth/login and /api/auth/register.
    │   ├── ProductController.java            # Handles fetching products for customers.
    │   └── [OtherControllers]                # Cart, Order, Admin, Seller controllers.
    ├── service/                              # The "Brain". Contains the business logic and rules of the application.
    │   ├── AuthService.java                  # Checks passwords, generates tokens, registers new users.
    │   ├── ProductService.java               # Handles filtering, creating, and deleting products.
    │   └── [OtherServices]                   # Cart, Order, User services.
    ├── repository/                           # The "Database Communicators". Interfaces that directly talk to MongoDB.
    │   └── UserRepository.java               # Has methods like findByEmail() to get user data from the DB.
    ├── model/                                # The "Data Blueprints". Represents how data looks in the database.
    │   └── User.java, Product.java           # Java classes mapping to MongoDB collections.
    ├── dto/                                  # Data Transfer Objects. Structures used to send/receive data cleanly (e.g., LoginRequest).
    ├── security/                             # Security mechanisms.
    │   ├── JwtService.java                   # Creates and validates JSON Web Tokens.
    │   └── JwtAuthenticationFilter.java      # Intercepts every incoming request to check if a valid token is present.
    └── exception/                            # Error handling.
        └── GlobalExceptionHandler.java       # Catches errors (like "User Not Found") and sends a clean error message to the frontend.
```

### Important Concepts & How the Flow Works
* **REST API:** The backend exposes URLs (Endpoints) like `GET /api/products` or `POST /api/auth/login`. The frontend calls these URLs, and the backend replies with JSON data.
* **The Layered Architecture (Controller -> Service -> Repository):**
  1. **Controller:** The receptionist. It receives a request (e.g., "Add an item to the cart"), checks if the request is formatted correctly, and passes the actual work to the Service.
  2. **Service:** The manager. It contains the business rules. It checks things like "Is the product in stock?" or "Does this user exist?". Once verified, it asks the Repository to save the changes.
  3. **Repository:** The warehouse worker. It takes Java objects and saves them directly into MongoDB, or fetches data from MongoDB and gives it back to the Service.
* **Security & JWT Flow:** 
  1. When a user logs in, `AuthService` verifies their password.
  2. `JwtService` creates a unique string of characters (a Token) containing the user's ID and Role.
  3. For every subsequent request, the frontend sends this token.
  4. The `JwtAuthenticationFilter` intercepts the request, reads the token, verifies it hasn't been tampered with, and tells Spring Security "This request is from an Admin, let them pass."

---

## 3. Database (MongoDB)

MongoDB is a NoSQL database. Instead of tables and rows like traditional databases (SQL), it stores data in **Collections** of **Documents** (which look exactly like JSON objects).

### Key Collections (Models)
* **Users Collection (`users`):** Stores user details (name, email, encrypted password, and their Role: `ADMIN`, `SELLER`, or `CUSTOMER`).
* **Products Collection (`products`):** Stores item details (name, price, stock quantity, image URL, category ID, and the ID of the Seller who created it).
* **Categories Collection (`categories`):** Groups products together (e.g., Electronics, Shoes).
* **Carts Collection (`carts`):** Each Customer has one cart. It stores an array of items (Product ID, Name, Quantity) they intend to buy.
* **Orders Collection (`orders`):** When a user checks out, their cart is converted into an Order. It tracks Payment Status, Order Status (Pending, Shipped, Delivered), and shipping details.

### Important Concepts & How the Flow Works
* **Document-Based Storage:** Because the frontend uses JSON (JavaScript Object Notation), and the backend converts it easily, MongoDB is perfect because it stores data as BSON (Binary JSON). There's no complex mapping required.
* **Relationships without Joins:** Unlike SQL databases where you have strict foreign key relationships, MongoDB uses references. For example, a Product document simply saves a `categoryId` string. When the backend needs the category name, it does a separate quick lookup, keeping queries fast and scalable.
* **No Schema enforcement by DB:** MongoDB doesn't enforce columns. However, our Spring Boot backend models (`User.java`, `Product.java`) act as a strict schema, ensuring bad data never makes it into the database.

---

### Summary of a Complete Action (Adding a Product to Cart)
1. **Frontend:** The User clicks "Add to Cart" on `ProductsView.vue`. The frontend `cart.js` store calls `api.post('/cart/add', { productId })` with their JWT token.
2. **Backend Security:** `JwtAuthenticationFilter` reads the token, recognizes the user is a `CUSTOMER`, and allows the request.
3. **Backend Controller:** `CartController` receives the request and passes it to `CartService`.
4. **Backend Service:** `CartService` asks the `ProductRepository` if the item is in stock. It then asks the `CartRepository` for the user's current cart. It adds the item to the cart object.
5. **Database:** `CartRepository` saves the updated cart back into MongoDB.
6. **Response:** The backend returns a success message to the frontend.
7. **Frontend Update:** The `cart.js` store updates the cart count, and the red badge on the Navbar instantly changes from `0` to `1`!

---

## 4. Microservices Architecture (Spring Boot 3 + MongoDB + IntelliJ IDEA)

In the microservices design, the single monolith backend is split into **4 specialized microservices** coordinated through an **API Gateway**:

```text
                      Vue.js 3 Frontend (Port 5173)
                                    |
                                    v
                     API Gateway (Spring Boot: Port 8080)
                                    |
     +---------------------+--------+--------+---------------------+
     |                     |                 |                     |
     v                     v                 v                     v
User & Auth Service   Product Service   Cart Service          Order Service
  (Port 8081)           (Port 8082)       (Port 8083)           (Port 8084)
     |                     |                 |                     |
     v                     v                 v                     v
  MongoDB:              MongoDB:          MongoDB:              MongoDB:
shopease_user_db    shopease_product_db  shopease_cart_db     shopease_order_db
```

### 1. Service Breakdown & MongoDB Collections
1. **User Service (`port 8081`):**
   - Collections: `users`
   - Handles login, registration, JWT issuing/verification, user profiles, and admin user status.
2. **Product Service (`port 8082`):**
   - Collections: `categories`, `products`, `reviews`
   - Handles catalog browsing, category management, seller product CRUD, reviews/ratings, and inventory stock control.
3. **Cart Service (`port 8083`):**
   - Collections: `carts`, `wishlists`
   - Handles shopping cart items, wishlist items, quantity adjustment, and moves between wishlist and cart.
4. **Order Service (`port 8084`):**
   - Collections: `orders`
   - Handles checkout, payments (Card & COD), stock deduction and cancellation restocking (via REST to Product Service), seller order tracking, and analytical dashboards.
5. **API Gateway (`port 8080`):**
   - Acts as the single frontend entry point on port 8080.
   - Forwards `/api/*` requests to the proper microservice while preserving tokens, headers, and CORS compatibility.

### 2. Opening & Running in IntelliJ IDEA
1. Open IntelliJ IDEA -> **File -> Open...** -> select the `microservices` folder.
2. IntelliJ detects the parent `pom.xml` and loads all 5 Spring Boot modules (`user-service`, `product-service`, `cart-service`, `order-service`, `api-gateway`).
3. Shared run configurations are already pre-configured under `microservices/.run/`.
4. Run each service (or use the **Services** tool window / Dashboard in IntelliJ to run all with one click).
5. All services connect directly to MongoDB (`mongodb://localhost:27017`) and seed default admin/seller/customer accounts and product catalog automatically!

