package colin.armstrong

import java.io.File

fun main() {
    val pathname = "src/main/resources/base_example.csv"
//    val pathname = "src/main/resources/floats.csv"
//    val pathname = "src/main/resources/integers.csv"
//    val pathname = "src/main/resources/integers_and_floats.csv"
//    val pathname = "src/main/resources/twenty_seven_columns.csv"
//    val pathname = "src/main/resources/direct_circular_reference_error.csv"
//    val pathname = "src/main/resources/indirect_circular_reference_error.csv"
//    val pathname = "src/main/resources/invalid_cell_reference_error.csv"
//    val pathname = "src/main/resources/empty_cell_error.csv"
    val csvFile = File(pathname)
    Spreadsheet().main(csvFile)
}

class Spreadsheet {
    private val cellSpreadsheet = ArrayList<List<Cell>>()
    private val cellReferenceMap = HashMap<String, Cell>()
    private val cellsCurrentlyBeingReferenced = HashSet<Cell>()

    fun main(csvFile: File) {
        populateSpreadsheetFromCsvFile(csvFile)
        calculateTotalsForAllCells()
        outputFormattedCellTotalsInCsvFormat()
    }

    /* Iterate over all CSV cells and instantiate Cell objects
       Store these objects in the spreadsheet and reference map data structures */
    private fun populateSpreadsheetFromCsvFile(file: File) {
        var rowNumber = 1
        file.forEachLine { line ->
            val spreadsheetRow = createSpreadsheetRow(rowNumber, line)
            this.cellSpreadsheet.add(spreadsheetRow)
            rowNumber++
        }
    }

    private fun createSpreadsheetRow(rowNumber: Int, line: String): List<Cell> {
        val cellTexts = line.split(",")
        var columnLetter = 'A'
        return cellTexts.map { cellText ->
            val cellReference = "$columnLetter$rowNumber"
            val createdCell = createCell(cellText, cellReference)
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
        this.cellSpreadsheet.forEach { row ->
            row.forEach { cell ->
                calculateCellTotal(cell)
            }
        }
    }

    // Function containing the bulk of the calculation/recursion logic for this problem
    private fun calculateCellTotal(cell: Cell): Float {
        // If we've already calculated this cell's total, just return the total
        if (cell.totalHasBeenCalculated) return cell.runningTotal

        // Detect circular references using our HashSet to keep track of currently referenced cells
        if (cellsCurrentlyBeingReferenced.contains(cell)) {
            throw Exception("Circular reference detected, aborting process")
        } else {
            cellsCurrentlyBeingReferenced.add(cell)
        }

        // Splitting our cell text on + and - operators gets us the terms we care about (numbers and cell references)
        val terms = cell.text.split("+", "-").toMutableList()

        val normalizedCellText = getNormalizedCellText(cell.text)

        // Iterate over each character in our normalized cell text and perform calculation logic
        for (char in normalizedCellText) {
            // We can simply skip to the next iteration when we're not at a +/- operator
            if (char != '+' && char != '-') continue

            // Each time we reach a +/- operator, remove the current term from our list
            val currentTerm = terms.removeFirst()
            // Recursively calculate the value for the current term
            val termValue = calculateValueForTerm(currentTerm)

            // Add or subtract term value to the cell total based on + or - operator
            if (char == '+') {
                cell.addValueToTotal(termValue)
            } else {
                cell.addValueToTotal(termValue * -1)
            }
        }

        // Remove cell from our set of currently referenced cells
        cellsCurrentlyBeingReferenced.remove(cell)

        // To avoid calculating a cell's total multiple times, set the 'totalHasBeenCalculated' boolean to true
        cell.totalHasBeenCalculated = true

        // Return the newly calculated running total for the cell
        return cell.runningTotal
    }

    // Unless the cell text starts with a '-', add an implicit '+' to the front to make conditional
    // logic more straightforward. Also includes an empty cell error check
    private fun getNormalizedCellText(cellText: String): String {
        if (cellText.isEmpty()) throw Exception("Empty cell detected, aborting process")
        return if (cellText[0] == '-') {
            cellText
        } else {
            "+${cellText}"
        }
    }

    // If our term starts with a letter, it's a cell reference. This means we need to get the referenced cell
    // and recursively calculate its total first. Otherwise, we just have a number and can return it as a float
    private fun calculateValueForTerm(term: String): Float {
        return if (term[0].isLetter()) {
            val referencedCell = this.cellReferenceMap[term]
                ?: throw Exception("Invalid cell reference $term detected, aborting process")
            calculateCellTotal(referencedCell)
        } else {
            term.toFloat()
        }
    }

    /* Iterate over each spreadsheet row and output formatted cell totals in CSV format */
    private fun outputFormattedCellTotalsInCsvFormat() {
        this.cellSpreadsheet.forEach { spreadsheetRow ->
            val formattedCsvRowOutput = formatSpreadsheetRowForCsvOutput(spreadsheetRow)
            println(formattedCsvRowOutput)
        }
    }

    private fun formatSpreadsheetRowForCsvOutput(spreadsheetRow: List<Cell>): String {
        return spreadsheetRow.joinToString(",") { cell -> cell.getFormattedTotal() }
    }
}
