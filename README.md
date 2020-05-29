# multiplicationtable
CLI to exercise multiplication to 10.
Support different languages: en/GB, en/US, nl/BE, fr/BE, nl/NL, fr/FR.
Written in Java 8
Build: gradle assemble
Execute:  java -jar build/libs/multiplicationtable.jar
    This will execute the exercices with the default nl/BE language.
Execute in a different language: java -jar build/libs/multiplicationtable.jar [language] [country]
    example from english: java -jar build/libs/multiplicationtable.jar en GB
