# How to run

1. Build project with command `./gradlew build`
2. Start Wallet Server with command `./gradlew :wallet-server:bootRun`
3. In separate console start Wallet Client with command:
    
    `java -jar ./wallet-client/build/libs/wallet-client-1.0-SNAPSHOT-all.jar -u <number of users> -t <number of thread for each user> -r <number of rounds in thread>`

# Solution description

Solution is based on pessimistic locking of accounts (not users)
