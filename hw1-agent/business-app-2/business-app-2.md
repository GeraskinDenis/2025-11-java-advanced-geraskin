# business-app-2 (example)
This is an example of a simple console application for managing `Person` objects with a built
-in method call counter.
In this example, the counter is added to the `GetAll` method of the `PersonServiceImpl` class,
and the duration of data collection is 20 seconds (`duration = 20000L`).

## Building
```shell
mvnw clean package
```

## Launching
```shell
java -jar target/business-app-2-3.5.9.jar
```

## Shell command examples
```shell
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