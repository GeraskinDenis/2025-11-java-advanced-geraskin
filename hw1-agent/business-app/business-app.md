# business-app
A simple console application for managing `Person` objects.

## Building
```shell
mvnw clean package
```

## Launching
```sh
java -jar target/business-app-3.5.9.jar
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