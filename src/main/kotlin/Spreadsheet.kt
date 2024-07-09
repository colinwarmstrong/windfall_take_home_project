package colin.armstrong

import java.io.File

fun main() {
    println("Windfall take home project in progress...\n")
    Spreadsheet().main()
    println("\n")
    println("Windfall take home project complete!")
}

class Cell(val text: String) {
    var totalHasBeenCalculated: Boolean = false
    var runningTotal = 0.00f

    fun addValueToTotal(value: Float) {
        if (this.totalHasBeenCalculated) return
        runningTotal += value
    }

    fun getFormattedTotal(): String {
        return String.format("%.2f", runningTotal)
    }

    fun confirmTotalHasBeenCalculated() {
        this.totalHasBeenCalculated = true
    }
}

class Spreadsheet {

    private val cellSpreadsheet = ArrayList<List<Cell>>()
    private val cellReferenceMap = HashMap<String, Cell>()

    fun main() {
        val file = File("src/main/resources/simple_example.csv")
//        val file = File("src/main/resources/example.csv")

        // Iterate over all CSV cells and Instantiate Cell objects
        // Store these objects in the spreadsheet and reference map data structures
        var currentRow = 1
        file.forEachLine { line ->
            val cellTexts = line.split(",")
            val newCsvRow = createCsvRow(currentRow, cellTexts)
            cellSpreadsheet.add(newCsvRow)
            currentRow++
        }

        // Iterate over each Cell and calculate the cell total
        cellSpreadsheet.forEach { row ->
            row.forEach { cell ->
                calculateCellTotal(cell)
            }
        }

        // Iterate over each spreadsheet row and output formatted cell totals in CSV format
        cellSpreadsheet.forEach { row ->
            val formattedCsvRowOutput = formatCsvRowForOutput(row)
            println(formattedCsvRowOutput)
        }
    }

    private fun createCsvRow(rowNumber: Int, cellTexts: List<String>): List<Cell> {
        var columnLetter = 'A'
        return cellTexts.map { cell ->
            val cellReference = "$columnLetter$rowNumber"
            val createdCell = createCell(cell, cellReference)
            columnLetter++
            createdCell
        }
    }

    private fun formatCsvRowForOutput(row: List<Cell>): String {
        return row.joinToString(",") { cell -> cell.getFormattedTotal() }
    }

    private fun createCell(cellText: String, cellReference: String): Cell {
        val newCell = Cell(cellText)
        this.cellReferenceMap[cellReference] = newCell
        return newCell
    }

    private fun calculateCellTotal(cell: Cell): Float {
        val values = cell.text.split("+", "-").toMutableList()

        var normalizedCellText = cell.text
        if (cell.text[0] != '-') normalizedCellText = "+${cell.text}"

        var i = 0
        while (i < normalizedCellText.length) {
            val char = normalizedCellText[i]
            if (char != '-' && char != '+') {
                i++
                continue
            }

            var newValue: Float

            if (values.first()[0].isLetter()) {
                val referencedCell = cellReferenceMap[values.first()] ?: throw Exception("Invalid cell referenced")
                newValue = if (referencedCell.totalHasBeenCalculated) {
                    referencedCell.runningTotal
                } else {
                    calculateCellTotal(referencedCell)
                }
                values.removeFirst()
            } else {
                newValue = values.removeFirst().toFloat()
            }

            if (char == '+') {
                cell.addValueToTotal(newValue)
            } else {
                cell.addValueToTotal(newValue * -1)
            }
            i++
        }

        cell.confirmTotalHasBeenCalculated()
        return cell.runningTotal
    }
}
