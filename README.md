# ShopEase - Full Stack Shopping Cart Application

A complete full-stack e-commerce web application with role-based access control built using Vue.js 3 and Spring Boot 3.

## Requirements

- **Java Version:** 17
- **Maven Version:** 3.8+
- **Node.js Version:** 18+
- **npm Version:** 9+
- **Database:** MongoDB 6.0+ (Running locally on default port 27017 or Atlas)

## Project Architecture
- **Frontend:** Vue.js 3, Vite, Pinia (State Management), Vue Router, Bootstrap 5, Axios.
- **Backend:** Java 17, Spring Boot 3, Spring Security, JWT Auth, Spring Data MongoDB.
- **Database:** MongoDB.

## Demo Accounts

The application automatically seeds the database with the following demo accounts on first startup:

- **Admin Account:** 
  - Email: `admin@example.com`
  - Password: `Admin@123`

- **Seller Account:**
  - Email: `seller@example.com`
  - Password: `Seller@123`

- **Customer Account:**
  - Email: `customer@example.com`
  - Password: `Customer@123`

## MongoDB Setup

1. **Local Setup:**
   Ensure MongoDB is installed and running on your local machine on the default port `27017`.
   The application will automatically connect to `mongodb://localhost:27017/shopease_db` and create the `shopease_db` database.

2. **MongoDB Atlas (Optional):**
   If you prefer using a cloud database, edit `backend/src/main/resources/application.properties` and change the `spring.data.mongodb.uri` property to your Atlas connection string.

## Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Clean and install dependencies (Ensure Maven is installed):
   ```bash
   mvn clean install
   ```
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
   *The backend server will start on `http://localhost:8080`.*

## Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install npm dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```
   *The frontend application will start on `http://localhost:5173`.*

## URLs

- **Frontend Application:** `http://localhost:5173`
- **Backend API:** `http://localhost:8080/api`
- **Swagger Documentation:** `http://localhost:8080/swagger-ui.html`

## Testing Workflow

1. **Customer Flow:**
   - Register a new customer or login using the demo customer account.
   - Navigate to the **Products** page.
   - Add products to your cart.
   - Go to your cart and proceed to **Checkout**.
   - Fill in your details and place an order using Mock Card Payment or Cash on Delivery.
   - View your order history under **My Orders**.

2. **Seller Flow:**
   - Login using the demo seller account.
   - View your dashboard statistics.
   - Go to **My Products** and click "Add Product" to list a new item.
   - Ensure the item now appears for customers.

3. **Admin Flow:**
   - Login using the demo admin account.
   - View the overall platform statistics in the Dashboard.

## Troubleshooting
- **Backend fails to start:** Check if MongoDB is running locally on port 27017.
- **CORS Errors:** Ensure the frontend is running exactly on `http://localhost:5173` as configured in `application.properties`.
- **JWT Errors:** If you restart the backend, you might need to login again since tokens expire and secret keys might rotate depending on your config.
