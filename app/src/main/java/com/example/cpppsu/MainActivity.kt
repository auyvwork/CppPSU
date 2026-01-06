package com.example.cpppsu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color



class MainActivity : ComponentActivity() {
    private val sortList = mutableStateListOf<Int>()
    private var redIdx = mutableIntStateOf(-1)
    private var greenIdx = mutableIntStateOf(-1)
    private lateinit var engine: SortEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        generateArray(15)

        engine = SortEngine { newArr, i, j ->
            if (sortList.size == newArr.size) {
                for (index in newArr.indices) {
                    if (sortList[index] != newArr[index]) {
                        sortList[index] = newArr[index]
                    }
                }
            }
            if (redIdx.intValue != i) redIdx.intValue = i
            if (greenIdx.intValue != j) greenIdx.intValue = j
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().systemBarsPadding(),
                    color = Color.White
                ) {
                    SortScreenContent(
                        sortList = sortList,
                        redIdxState = redIdx,
                        greenIdxState = greenIdx,
                        onGenerate = { generateArray(it) },
                        onStart = { idx, asc, acc, spd, onFinish ->
                            runSort(idx, asc, acc, spd, onFinish)
                        }
                    )
                }
            }
        }
    }

    private fun generateArray(size: Int) {
        sortList.clear()
        repeat(size) { sortList.add((20..250).random()) }
        redIdx.intValue = -1
        greenIdx.intValue = -1
    }

    private fun runSort(sortIndex: Int, isAsc: Boolean, accStart: Boolean, speed: Int, onFinish: () -> Unit) {
        kotlin.concurrent.thread {
            engine.runSort(sortList.toIntArray(), sortIndex, isAsc, accStart, speed)

            runOnUiThread {
                redIdx.intValue = -1
                greenIdx.intValue = -1
                onFinish()
            }
        }
    }

    companion object {
        val SORTS_DATA = listOf(
            SortDetail("Обмен (Пузырек)", "void sort_obmen(int *a, int n) {\n    for (int i = 0; i < n - 1; i++) {\n        for (int j = 0; j < n - i - 1; j++) {\n            if (a[j] > a[j+1]) {\n                int t = a[j];\n                a[j] = a[j+1];\n                a[j+1] = t;\n            }\n        }\n    }\n}", "Самый простой метод, похожий на всплывание пузырьков воздуха в воде..."),
            SortDetail("Выбор", "void sort_vybor(int *a, int n) { \n    int k, m;\n    for (int i=n-1; i>0; i--) {\n        k=i; m=a[i];\n        for (int j=0; j<i; j++) \n            if (a[j]>m) { k=j; m=a[k]; }\n        if (k!=i) { a[k]=a[i]; a[i]=m; }\n    }\n}", "Метод поиска экстремума..."),
            SortDetail("Вставки", "void sort_insert(int *m, int n) {\n    int j, r;\n    for (int i=1; i<n; i++) {\n        r=m[i]; j=i-1;\n        while (j>=0 && m[j]>r) {\n            m[j+1]=m[j]; j--;\n        }\n        m[j+1]=r;\n    }\n}", "Похоже на то, как вы сортируете игральные карты в руке..."),
            SortDetail("Шелл", "void ShellSort(int a[], int n) {\n    for (int s=n/2; s>0; s/=2)\n        for (int i=s; i<n; i++) {\n            int t=a[i], j=i;\n            while (j>=s && a[j-s]>t) {\n                a[j]=a[j-s]; j-=s;\n            }\n            a[j]=t;\n        }\n}", "Улучшенная версия сортировки вставками..."),
            SortDetail("Пирамида (Куча)", "void heapSort(int a[], int n) {\n    for (int i=n/2-1; i>=0; i--) heapify(a, n, i);\n    for (int i=n-1; i>0; i--) {\n        swap(a[0], a[i]);\n        heapify(a, i, 0);\n    }\n}", "Алгоритм выстраивает элементы в специальную древовидную структуру — «кучу»..."),
            SortDetail("Хоар (Быстрая)", "void quickSort(int a[], int low, int high) {\n    if (low < high) {\n        int p = partition(a, low, high);\n        quickSort(a, low, p);\n        quickSort(a, p + 1, high);\n    }\n}\nint partition(int a[], int l, int h) {\n    int p = a[(l + h) / 2], i = l - 1, j = h + 1;\n    while (true) {\n        do i++; while (a[i] < p);\n        do j--; while (a[j] > p);\n        if (i >= j) return j;\n        swap(a[i], a[j]);\n    }\n}", "Принцип «разделяй и властвуй»...")
        )
    }
}