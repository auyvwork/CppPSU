package com.example.cpppsu

class SortEngine(private val callback: (IntArray, Int, Int) -> Unit) {
    fun onStep(arr: IntArray, i: Int, j: Int) {
        callback(arr, i, j)
    }
    external fun runSort(arr: IntArray, type: Int, asc: Boolean, accStart: Boolean,speed: Int)

    companion object {
        init { System.loadLibrary("cpppsu") }
    }
}
