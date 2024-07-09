package colin.armstrong

import java.io.File

fun main() {
    Spreadsheet().main()
}

class Cell(val text: String) {
    var runningTotal = 0.00f
    var totalHasBeenCalculated: Boolean = false

    fun addValueToTotal(value: Float) {
        runningTotal += value
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
//        val csvFile = File("src/main/resources/simple_example.csv")
        val csvFile = File("src/main/resources/example.csv")

        populateSpreadsheetFromCsvFile(csvFile)
        calculateTotalsForAllCells()
        outputFormattedCellTotalsInCsvFormat()
    }

    /* Iterate over all CSV cells and instantiate Cell objects
       Store these objects in the spreadsheet and reference map data structures */
    private fun populateSpreadsheetFromCsvFile(file: File) {
        var currentRow = 1
        file.forEachLine { line ->
            val cellTexts = line.split(",")
            val spreadsheetRow = createSpreadsheetRow(currentRow, cellTexts)
            cellSpreadsheet.add(spreadsheetRow)
            currentRow++
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

    private fun createCell(cellText: String, cellReference: String): Cell {
        val newCell = Cell(cellText)
        this.cellReferenceMap[cellReference] = newCell
        return newCell
    }

    /* Iterate over each Cell and calculate the cell total */
    private fun calculateTotalsForAllCells() {
        cellSpreadsheet.forEach { row ->
            row.forEach { cell ->
                calculateCellTotal(cell)
            }
        }
    }

    private fun calculateCellTotal(cell: Cell): Float {
        // If we've already calculated this cell's total, just return the total
        if (cell.totalHasBeenCalculated) return cell.runningTotal

        // Splitting our cell text on + and - operators gets us the terms we care about (numbers and cell references)
        val terms = cell.text.split("+", "-").toMutableList()

        val normalizedCellText = getNormalizedCellText(cell.text)

        // Iterate over each character in our normalized cell text and perform calculation logic when
        // we reach a + or - operator
        for (char in normalizedCellText) {
            if (char != '+' && char != '-') continue

            // Each time we reach a +/- operator, remove the first term from our list
            val currentTerm = terms.removeFirst()
            // Recursively calculate the value for this term
            val termValue = calculateValueForTerm(currentTerm)

            // Add or subtract term value to cell total based on +/- operator
            if (char == '+') {
                cell.addValueToTotal(termValue)
            } else {
                cell.addValueToTotal(termValue * -1)
            }
        }

        cell.setTotalHasBeenCalculatedToTrue()
        return cell.runningTotal
    }

    // Unless the cell text starts with a '-', add an implicit '+' to the front to make conditional
    // logic more straightforward
    private fun getNormalizedCellText(cellText: String): String {
        return if (cellText[0] == '-') {
            cellText
        } else {
            "+${cellText}"
        }
    }

    // If our term starts with a letter, it's a cell reference. This means we need to get the referenced cell
    // and recursively calculate its total. Otherwise, we just have a number and can return it as a float
    private fun calculateValueForTerm(term: String): Float {
        return if (term[0].isLetter()) {
            val referencedCell = cellReferenceMap[term] ?: throw Exception("Invalid cell referenced")
            calculateCellTotal(referencedCell)
        } else {
            term.toFloat()
        }
    }

    /* Iterate over each spreadsheet row and output formatted cell totals in CSV format */
    private fun outputFormattedCellTotalsInCsvFormat() {
        cellSpreadsheet.forEach { spreadsheetRow ->
            val formattedCsvRowOutput = formatSpreadsheetRowForCsvOutput(spreadsheetRow)
            println(formattedCsvRowOutput)
        }
    }

    private fun formatSpreadsheetRowForCsvOutput(spreadsheetRow: List<Cell>): String {
        return spreadsheetRow.joinToString(",") { cell -> cell.getFormattedTotal() }
    }
}
