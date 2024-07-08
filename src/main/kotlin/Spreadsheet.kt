package colin.armstrong

import java.io.File

fun main() {
    println("Windfall take home project in progress...\n")
    Spreadsheet().main()
    println("\n")
    println("Windfall take home project complete!")
}

class Cell {
    private val values = ArrayList<Float>()

    fun addValue(value: Float) {
        values.add(value)
    }

    fun formattedValue(): String {
        val sum = values.sum()
        return String.format("%.2f", sum)
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
            val s = row.joinToString(separator = ",") { cell -> cell.formattedValue() }
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
                newCell.addValue(newValue)
            } else {
                newCell.addValue(newValue * -1)
            }
            i++
        }

        return newCell
    }
}
