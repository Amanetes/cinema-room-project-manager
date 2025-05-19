package cinema

import cinema.FULL_TICKET_PRICE
import kotlin.div
import kotlin.io.path.fileVisitor
import kotlin.times

const val FULL_TICKET_PRICE = 10
const val BACKROW_TICKET_PRICE = 8

fun main() {
    println("Enter the number of rows:")
    val rows = readln().toInt()
    println("Enter the number of seats in each row:")
    val seatsPerRow = readln().toInt()

    val cinemaRoom = Cinema(rows, seatsPerRow)

    while (true) {
        cinemaRoom.showMenu()

        when (readln()) {
            "1" -> cinemaRoom.showSeats()
            "2" -> cinemaRoom.buyTicket()
            "3" -> cinemaRoom.showStatistics()
            "0" -> return
        }
    }
}

class Cinema(val rows: Int, val seatsPerRow: Int) {
    var totalSeats = rows * seatsPerRow
    var purchasedTickets = 0
    var currentIncome = 0
    val room = MutableList(rows) { MutableList(seatsPerRow) { "S" } }

    fun showMenu() {
        println(
            """
            1. Show the seats
            2. Buy a ticket
            3. Statistics
            0. Exit
            """.trimIndent()
        )
    }

    fun showStatistics() {
        val percent = if (totalSeats == 0) 0.0 else purchasedTickets.toDouble() / totalSeats * 100
        println(
            """
            Number of purchased tickets: $purchasedTickets
            Percentage: ${"%.2f".format(percent)}%
            Current income: $$currentIncome
            Total income: $${calculateTotalIncome()}
            """.trimIndent()
        )
        println()
    }

    fun calculateTotalIncome():Int {
        return if (totalSeats <= 60) {
            totalSeats * FULL_TICKET_PRICE
        } else {
            val frontRows = rows / 2 // разделяем по рядам, а не по кол-ву всех мест
            val backRows = rows - frontRows
            (frontRows * seatsPerRow * FULL_TICKET_PRICE) + (backRows * seatsPerRow * BACKROW_TICKET_PRICE)
        }
    }

    fun showSeats() {
        println("Cinema:")
        print("  ")
        (1..room[0].size).forEach { print("$it ") }
        println()

        room.forEachIndexed { index, row ->
            print("${index + 1} ")
            println(row.joinToString(" "))
        }
        println()
    }

    fun buyTicket() {
        while (true) {
            try {
                println("Enter a row number:")
                val rowNumber = readln().toInt()
                println("Enter a seat number in that row:")
                val seatNumber = readln().toInt()

                if (rowNumber !in 1..rows || seatNumber !in 1..seatsPerRow) {
                    throw WrongInputException("Wrong input!")
                }

                if (room[rowNumber - 1][seatNumber - 1] == "B") {
                    throw SeatOccupiedException("That ticket has already been purchased!")
                }

                room[rowNumber - 1][seatNumber - 1] = "B"

                val ticketPrice = calculateTicketPrice(rowNumber)

                purchasedTickets++
                currentIncome += ticketPrice
                println("Ticket price: $$ticketPrice\n")
                break
            } catch (e: WrongInputException) {
                println(e.message)
            } catch (e: SeatOccupiedException) {
                println(e.message)
            }
        }
    }

    fun calculateTicketPrice(selectedRow: Int): Int {
        val totalSeats = rows * seatsPerRow
        if (totalSeats <= 60) return FULL_TICKET_PRICE

        val frontRows = rows / 2
        return if (selectedRow <= frontRows) FULL_TICKET_PRICE else BACKROW_TICKET_PRICE
    }
}

class SeatOccupiedException(message: String) : RuntimeException(message)
class WrongInputException(message: String) : RuntimeException(message)
