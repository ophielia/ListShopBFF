# ListShopBFF

## Project 
This is a back end for front end project for mobile clients.  It's written in Kotlin and uses KMM. It handles the common 
functionality used by the clients: network calls, persistence and business logic. The network calls are to a backend server using REST.

## Architecture
The architecture of the project is based on the following principles:
- Separation of concerns: The project is divided into modules that handle specific tasks, such as network calls, persistence, and business logic.
- Use of KMM: The project uses KMM to share code between iOS and Android clients, reducing code duplication and improving maintainability.
- Use of Kotlin: The project is written in Kotlin, a modern and expressive language that provides powerful features for building complex applications.
- Use of dependency injection: The project uses dependency injection to decouple components and make them more testable and maintainable.
- Use of clean architecture: The project follows the principles of clean architecture, which separates the concerns of the application into distinct layers, making it easier to understand and maintain.

## Technology Stack
The project uses the following technologies:
- Kotlin: The project is written in Kotlin, a modern and expressive language that provides powerful features for building complex applications.
- KMM: The project uses KMM to share code between iOS and Android clients, reducing code duplication and improving maintainability.
- SqlDelight: The project uses SqlDelight to handle database operations and queries.
- REST: The project uses REST to communicate with the backend server.
- Ktor: The project uses Ktor to handle network calls and communication with the backend server.
- Mokkery: The project uses Mokkery to mock network calls and test the code.


## Testing
The project uses the following testing frameworks:
- JUnit: The project uses JUnit to write unit tests for the code.
- Mockito: The project uses Mockito to mock dependencies and test the code.
- Ktor Test: The project uses Ktor Test to write integration tests for the network calls.
- Mokkery: The project uses Mokkery to mock network calls and test the code.






