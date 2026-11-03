
# Practical Design Patterns Lab

## Project Overview

This project contains five independent Java programs demonstrating
common software design patterns.

## Tasks

Task 1: Secure Data Exporter Pipeline
Patterns: Strategy + Decorator
File: Task1.java

Task 2: Legacy Stock Ticker with Rate Limiting & Caching
Patterns: Adapter + Proxy
File: Task2.java

Task 3: Cross-Platform Alert & Incident Engine
Patterns: Bridge + Strategy
File: Task3.java

Task 4: E-Commerce Surcharge & Customs Pipeline
Patterns: Adapter + Decorator
File: Task4.java

Task 5: Remote Cloud Asset Downloader & Security Gate
Patterns: Bridge + Proxy
File: Task5.java

## Requirements

Java JDK 11 or higher.
No external libraries required.

## How to Run

Open the project in IntelliJ IDEA.

Run each task individually by executing its main method.

Alternatively, use the terminal:

javac Task1.java Task2.java Task3.java Task4.java Task5.java

java Task1
java Task2
java Task3
java Task4
java Task5

## Expected Behavior

Task 1:
The exporter formats report data and applies decorators.

Task 2:
The proxy caches market prices and limits upstream requests.

Task 3:
Alerts use different communication channels and dispatch policies.

Task 4:
The adapter converts currency and decorators add pricing charges.

Task 5:
The proxy controls access to storage drivers based on user roles.

## Design Pattern Summary

Strategy: Selects algorithms dynamically.
Decorator: Adds behavior through object composition.
Adapter: Converts incompatible interfaces.
Proxy: Controls access to another object.
Bridge: Separates abstractions from implementations.
