# Velocity Limits Challenge - Java Spring Boot Application

This Java Spring Boot application enforces velocity limits on customer accounts, processing fund load attempts and responding with acceptance or rejection based on the account's daily and weekly limits.

## Features

- RESTful API for processing load requests
- In-memory H2 database for storing customer load data
- Custom error handling and logging
- Unit and integration tests
- Standalone Java program to read input from a file, process it through the API, and write output to another file

## Requirements

- Java 17
- Maven

## Setup

1. Clone the repository: `https://github.com/ivanandrejic/velocity-limits-challenge`
2. Navigate to the project directory
3. Build the project using Maven:

    `mvn clean install`


## Running the Application

1. Start the Spring Boot application:
    `mvn spring-boot:run`
2. The application will be running at `http://localhost:8080`.

## Usage

To use the standalone Java program `LoadFundsProcessor`, compile and run it while the Spring Boot application is running:

`javac LoadFundsProcessor.java`

`java LoadFundsProcessor`


Place an `input.txt` file in the same directory as the compiled Java program. The program will read the input file, send load requests to the `/load` API endpoint, and write the output to an `output.txt` file in the same directory.

## Testing

To run the unit and integration tests, execute the following command:

    `mvn test`



