# CineReserve

A cinema ticket booking app built with Java Swing. Users pick a movie and showtime, choose seats from a seat map, and confirm their booking.

## Features

- Choose from 4 movies and 4 showtimes
- Interactive seat map with 6 rows (A to F) and 8 seats per row
- Color-coded seats: green = available, orange = selected, red = reserved
- Live total price at ₱250 per ticket
- 5-minute countdown that releases your selected seats if the booking is not confirmed
- Booking confirmation showing the chosen seats and the total
- Reserved seats are tracked separately for each movie and showtime

## Built With

- Java
- Java Swing (GUI)
- NetBeans IDE

## How to Run

1. Download or clone this repository.
2. Open the `CineReserve` folder as a project in NetBeans (requires Java JDK 8 or newer).
3. Run `CineReserve.java`.

## Notes

Reservations are stored in memory, so they reset when the app is closed.
