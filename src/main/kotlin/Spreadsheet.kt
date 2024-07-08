package colin.armstrong

import java.io.File

fun main() {
    println("Windfall take home project in progress...\n")
    Spreadsheet().main()
    println("\n")
    println("Windfall take home project complete!")
}

class Cell {
    private var runningTotal = 0.00f

    fun addValueToTotal(value: Float) {
        runningTotal += value
    }

    fun getFormattedTotal(): String {
        return String.format("%.2f", runningTotal)
    }
}

class Spreadsheet {

    fun main() {
//        val file = File("src/main/resources/example.csv")
        val file = File("src/main/resources/simple_example.csv")

        val csvCells = ArrayList<ArrayList<Cell>>()

        file.forEachLine { line ->
            val cells = line.split(",")
            val newCsvRow = cells.map { cell -> createCell(cell) }
            csvCells.add(newCsvRow as ArrayList<Cell>)
        }

        csvCells.forEach { row ->
            val s = row.joinToString(separator = ",") { cell -> cell.getFormattedTotal() }
            println(s)
        }
    }

    private fun createCell(cell: String): Cell {
        val newCell = Cell()

        val values = cell.split(delimiters = arrayOf("+", "-")).toMutableList()

        var cellString = cell
        if (cellString[0] != '-') cellString = "+$cell"

        var i = 0
        while (i < cellString.length) {
            val char = cellString[i]
            if (char != '-' && char != '+') {
                i++
                continue
            }

            val newValue = values.removeFirst().toFloat()

            if (char == '+') {
                newCell.addValueToTotal(newValue)
            } else {
                newCell.addValueToTotal(newValue * -1)
            }
            i++
        }

        return newCell
    }
}
