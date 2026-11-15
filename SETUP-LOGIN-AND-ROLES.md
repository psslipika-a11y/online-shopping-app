# Login, Admin/User Roles and Order Cancellation

## What changed
- Homepage heading is now **Online Shopping Web Application**.
- Login and user registration pages are included.
- New registrations are always USER accounts.
- ADMIN accounts can add and delete products and view all orders.
- USER accounts can browse, add to cart, checkout, view their own orders and cancel eligible orders.
- An order can be cancelled only while its status is `PLACED`.
- Cancelling an order restores stock for the products in that order.
- Passwords are stored using BCrypt hashing.

## Create the first administrator account
The application does not contain a hard-coded admin password. Before deploying to Minikube, create a Kubernetes Secret. Replace the example password with a strong password of your own:

```bash
kubectl create secret generic admin-secret --from-literal=ADMIN_PASSWORD='CHOOSE_A_STRONG_PASSWORD' --dry-run=client -o yaml | kubectl apply -f -
```

The initial admin username is `admin`. To use a different username, edit `ADMIN_USERNAME` in `deployment.yaml` before deploying.

The first startup with this secret creates the admin account in the `user_accounts` table. The password is stored as a BCrypt hash. If an admin account already exists, changing the Kubernetes secret does not automatically change its database password.

## Build and deploy
1. Make sure Docker Desktop is running.
2. Make sure Minikube is running: `minikube start --driver=docker`.
3. Create the `admin-secret` using the command above.
4. Run your Jenkins job `Online-Shopping-CI-CD`, or build the project using `./mvnw clean package`.
5. Apply the Kubernetes files if the pipeline has not already done so:
   `kubectl apply -f deployment.yaml`
   `kubectl apply -f service.yaml`
6. Check rollout: `kubectl rollout status deployment/online-shopping-deployment`
7. Open the service: `minikube service online-shopping-service`

## Database changes
Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`; it should add the `user_accounts` table and the new order ownership/status/product reference columns. Back up `shopping_db` before deploying. Existing orders have no account owner and cannot be shown in a user's personal order history. Older order items have no product ID, so automatic stock restoration for those old orders is not possible.

## Important
This is a student/demo implementation. For a production website, use HTTPS, review CSRF and session settings, add account recovery and rate limiting, protect secrets with a dedicated secret manager, and implement proper order fulfilment/refund rules. Cash on Delivery remains a demo payment method.

## Fix for long product image URLs
The `Product.imageUrl` field is now mapped as `LONGTEXT` for new/updated schemas. Since MySQL may not automatically change an existing column during Hibernate schema update, run this once in MySQL if the old `image_url` column still has a short VARCHAR type:

```sql
USE shopping_db;
ALTER TABLE product MODIFY COLUMN image_url LONGTEXT;
```
