package colin.armstrong

import java.io.File

fun main() {
    Spreadsheet().main()
}

class Cell(val text: String) {
    var runningTotal = 0.00f
    var totalHasBeenCalculated: Boolean = false

    fun addValueToTotal(value: Float) {
        if (this.totalHasBeenCalculated) return
        else runningTotal += value
    }

    fun getFormattedTotal(): String {
        return String.format("%.2f", runningTotal)
    }

    fun setTotalHasBeenCalculatedToTrue() {
        this.totalHasBeenCalculated = true
    }
}

class Spreadsheet {
    private val cellSpreadsheet = ArrayList<List<Cell>>()
    private val cellReferenceMap = HashMap<String, Cell>()

    fun main() {
//        val file = File("src/main/resources/simple_example.csv")
        val file = File("src/main/resources/example.csv")

        createSpreadsheetFromCsvFile(file)
        calculateTotalsForAllCells()
        outputFormattedCellTotalsInCsvFormat()
    }

    /* Iterate over all CSV cells and instantiate Cell objects
       Store these objects in the spreadsheet and reference map data structures */
    private fun createSpreadsheetFromCsvFile(file: File) {
        var currentRow = 1
        file.forEachLine { line ->
            val cellTexts = line.split(",")
            val spreadsheetRow = createSpreadsheetRow(currentRow, cellTexts)
            cellSpreadsheet.add(spreadsheetRow)
            currentRow++
        }
    }

    /* Iterate over each Cell and calculate the cell total */
    private fun calculateTotalsForAllCells() {
        cellSpreadsheet.forEach { row ->
            row.forEach { cell ->
                calculateCellTotal(cell)
            }
        }
    }

    /* Iterate over each spreadsheet row and output formatted cell totals in CSV format */
    private fun outputFormattedCellTotalsInCsvFormat() {
        cellSpreadsheet.forEach { row ->
            val formattedCsvRowOutput = formatCsvRowForOutput(row)
            println(formattedCsvRowOutput)
        }
    }

    private fun createSpreadsheetRow(rowNumber: Int, cellTexts: List<String>): List<Cell> {
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

        /* Unless our cell text starts with a '-' character, add an implicit '+' character to
           the start of our cell text to make conditional logic more straightforward */
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

        cell.setTotalHasBeenCalculatedToTrue()
        return cell.runningTotal
    }
}
