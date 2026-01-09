# business-app
A simple shell application for managing `Person` objects.

## Building the project
To build the project, navigate to the project's root directory and run the following command:
```sh
./mvnw clean install
```

## Running the Application
To run the application, use the following command:
```sh
java -jar .\business-app\target\business-app-0.0.1-SNAPSHOT.jar
```

## Shell command examples
```sh
# print all commends
help

# add a new Person
person insert "Denis"
person insert "Anton"

# print a list of Person
person all

# add a new Person with an error
person insert "Maxiiim"

# update an existing Person
person update 3 "Maxim"

# delete a Person by ID
person del-by-id 3
```