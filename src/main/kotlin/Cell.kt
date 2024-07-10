package colin.armstrong

class Cell(val text: String) {
    var runningTotal = 0.00f
    var totalHasBeenCalculated = false

    fun addValueToTotal(value: Float) {
        if (this.totalHasBeenCalculated) return
        this.runningTotal += value
    }

    fun getFormattedTotal(): String {
        return String.format("%.2f", this.runningTotal)
    }
}
