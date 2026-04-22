# Air Combat Simulation

This is a school project for the course **"Applied Distributed Systems"**.

## Description

This project implements a simple air combat simulation between two sides.  
Each side controls a group of aircraft that can patrol, move across a grid, and engage enemy targets using missiles.

The system is built around a **central radar service**, which acts as the **source of truth** for all flying objects (aircraft and missiles).  
All entities continuously report their positions to the radar and receive information about nearby objects.

## Goals

The main goal of this project was to practice concepts from distributed systems, including:

- Communication over **sockets**
- Use of **RPC (gRPC)** for service interaction
- Handling **concurrent processes and threads**
- Managing **shared state** in a distributed environment
- Designing systems with **eventual consistency**

## Key Components

- **Aircraft**
    - Move across the grid
    - Send position updates
    - Scan surroundings using radar

- **Missiles**
    - Track targets
    - Confirm hits via radar
    - Remove themselves from the system after completion

- **Radar Service**
    - Centralized tracking of all flying objects
    - Provides scan results and hit confirmation
    - Communicates via gRPC

- **Command Center**
    - Collects and processes data
    - Maintains state of friendly and enemy units
    - Issues commands to aircraft

- **Console Visualization**
    - Displays a grid-based tactical map
    - Shows positions of aircraft, missiles, and base

---

This project focuses on demonstrating distributed system principles rather than building a fully realistic simulation.