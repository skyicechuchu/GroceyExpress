# Groceries Express Project

The Groceries Express Project is a system designed to simulate the interactions between customers, grocery stores, and other entities used by a third-party service to facilitate the placement and delivery of orders.

The system is developed using Java and built with Gradle.

## Project Description

This project involves developing a system that supports a third-party grocery delivery service. Customers place orders with various stores using the service, which then coordinates with grocery stores to find and deliver the items via drones. Upon delivery, the stores get paid electronically by the customer.

The core functionality includes the creation and updating of entities such as customers, stores, drones, and orders; facilitating interactions between these entities; and generating reports about the current state of the system.

## How to Setup and Run

To setup and run this project, you need to have Java and Gradle installed in your system. 

1. Clone this repository to your local machine.
2. Navigate to the project directory.
3. Run `gradle build` to build the project.
4. Run `gradle run` to start the application.

## System Requirements

The developed application must be able to:

- Represent various users including customers and grocery store employees (e.g. drone pilots).
- Track user details such as first name, last name, and phone number.
- Represent distinct stores along with their earned revenues from delivering orders.
- Represent drones used for deliveries, each associated with a single pilot and store.
- Track drone details such as trips made, remaining trips before maintenance, and lifting capacity.
- Represent orders made by customers, with each order associated with specific customers and drones.
- Track order details such as unique item names and weights.
- Generate reports detailing the current state of the system.

## Contributing

Contributions are welcome. Please follow the standard GitHub flow - fork the project, make your changes and submit a pull request.

## Questions and Support

If you encounter any issues or have any questions, please open an issue in this repository.

## License

This project is licensed under the MIT License. See the LICENSE file for details.
