package com.sisindia.ai.mtrainer.android

/*
lateinit var originalArray: Array<Array<Long>>
lateinit var prefixArray: Array<Array<Long>>
var count : Long = 0

fun main(args: Array<String>) {
    val testCase = readLine()!!.toInt()
    val result = Array<Long>(testCase) {0}
    for(t in 0 until testCase) {
        val metaInputs = readLine()!!.trim().split(" ")
        val rows = metaInputs[0].toInt()
        val cols = metaInputs[1].toInt()
        val k = metaInputs[2].toLong()
        prefixArray = Array(rows) { Array(cols) { 0.toLong() } }
        originalArray = Array(rows) { Array(cols) { 0.toLong() } }
        for (i in 0 until rows) {
            val row = readLine()!!.trim().split(" ")
            for (j in 0 until cols)
                originalArray[i][j] = row[j].toLong()
        }
        calculatePrefixSum(rows, cols)
        traverseMatrix(rows, cols, k)
        result[t] = count
        count = 0
    }
    for(r in result)
        println(r)
}

fun calculatePrefixSum(rows: Int, cols: Int) {
    for (row in 0 until rows)
        for (col in 0 until cols) {
            if (row == 0 && col == 0)
                continue
            else if (col - 1 < 0)
                originalArray[row][col] = originalArray[row - 1][col] + originalArray[row][col]
            else if (row - 1 < 0)
                originalArray[row][col] = originalArray[row][col - 1] + originalArray[row][col]
            else
                originalArray[row][col] =
                    originalArray[row][col - 1] + originalArray[row - 1][col] + originalArray[row][col] - originalArray[row - 1][col - 1]
        }
}

fun traverseMatrix(rows: Int, cols: Int, k: Long) {
    for (row in 0 until rows)
        for (col in 0 until cols) {
            var i = row
            var j = col
            while (i <= rows && j <= cols) {
                val totalItems = (i - row + 1) * (j - col + 1)
                val avg : Double
                avg = if((i-1) < 0 || (j-1) < 0)
                    originalArray[i][j].toDouble() / totalItems
                else
                    (originalArray[i][j].toDouble() - originalArray[i-1][j-1]) / totalItems
                if (avg >= k)
                    count++
                i++
                j++
            }
        }
}*/