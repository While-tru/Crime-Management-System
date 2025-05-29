#!/bin/bash

# Use a specific path for the MySQL connector JAR
MYSQL_JAR="lib/mysql-connector-j-8.0.33.jar"

# Check if the JAR exists
if [ ! -f "$MYSQL_JAR" ]; then
    echo "MySQL connector JAR not found at $MYSQL_JAR"
    echo "Creating lib directory and downloading the connector..."
    
    # Create lib directory if it doesn't exist
    mkdir -p lib
    
    # Download MySQL connector
    curl -L -o "$MYSQL_JAR" "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar"
    
    if [ ! -f "$MYSQL_JAR" ]; then
        echo "Failed to download MySQL connector. Please download it manually."
        exit 1
    fi
fi

echo "Using MySQL connector: $MYSQL_JAR"

# Compile the application
echo "Compiling the application..."
javac -cp "src/main/java:$MYSQL_JAR" src/main/java/com/mycompany/cmsystem/CMSystem.java

# Run the application
echo "Running the application..."
java -cp "src/main/java:$MYSQL_JAR" com.mycompany.cmsystem.CMSystem