# JChess

A hot-seat chess game developed as a final project for the Object-Oriented Programming course (TCS). Implemented in Java, showcasing core OOP principles, clean architecture, and design patterns.

##  Authors
* **Karol Helm**
* **Stanisław Wasilewski**

##  Tech Stack
* **Language:** Java
* **GUI Framework:** JavaFX

##  Core Features
The main priority of this project is to deliver a fully functional classic chess experience with the following features:

* **Classic Chess Rules Implementation:**
  * Strict validation of piece movements.
  * Advanced rules support: pinning, castling, and *en passant*.
  * Check and checkmate detection.
* **Synchronized Chess Clocks:** Implemented using multithreading to ensure accurate and independent time tracking for both players.
* **Chess Openings Library:** A built-in library of popular chess openings, demonstrating moves step-by-step.
* **Game History & Notation:** Storing and displaying the course of the game in standard algebraic chess notation.
* **FEN Notation Support:** A custom FEN parser allowing users to load custom game states and convert the current board state back into a FEN string.
* **Material Tracker:** Graphical representation of pieces captured by each player, including real-time calculation of the material difference.

##  Planned / Additional Features
Depending on the development progress and encountered technical challenges, the following features are planned to be added:

* **AI Opponent (Bot):** A single-player mode against a bot powered by the Minimax algorithm and Alpha-Beta pruning.
* **Position Evaluator:** A tool to calculate the current board position, indicating which player has the advantage and the significance of that advantage.
* **Chess Variations:** Support for alternative game modes such as *Chess960* (Fischer Random), *Los Alamos Chess*, and *Duck Chess*.
* **Advanced Match History:** An extended history of recent games, including the evaluation of past positions.
