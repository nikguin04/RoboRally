# Roborally

## Prerequisites

Before you begin, ensure you have met the following requirements:

* You have installed the latest version of **Java Development Kit (JDK 17)** and **Java Runtime Environment (JRE 17)**.
* You have a **Windows/Linux/Mac** machine. This guide is OS-agnostic.
* You have installed **Maven**.
* You have installed **JavaFX**.

### Installing JDK 17 and JRE 17

To install JDK 17 and JRE 17, follow these steps:

1. Visit the official Oracle download page.
2. Download the appropriate JDK 17 and JRE 17 according to your operating system.
3. Run the installer and follow the instructions.

### Installing Maven

To install Maven, follow these steps:

1. Visit the official Apache Maven download page.
2. Download the binary zip archive.
3. Extract the archive to your desired location.
4. Add the `bin` directory of the created directory `apache-maven-3.x.y` to the `PATH` environment variable.

### Installing JavaFX

To install JavaFX, follow these steps:

1. Visit the official OpenJFX download page.
2. Download the appropriate JavaFX SDK according to your operating system.
3. Extract the archive to your desired location.
4. Set the `PATH_TO_FX` environment variable to the `lib` directory of the extracted archive.

## Running the program

To run a JavaFX program, navigate to your project directory and run the following command:

```bash
mvn javafx:run
```
