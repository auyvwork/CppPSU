package com.example.cpppsu

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


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
                val view = LocalView.current
                if (!view.isInEditMode) {
                    SideEffect {
                        val window = (view.context as Activity).window
                        val controller = WindowCompat.getInsetsController(window, view)

                        controller.hide(WindowInsetsCompat.Type.navigationBars())

                        controller.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                }
                Surface(

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
            SortDetail(
                "Обмен (Пузырек)",
                "void sort_obmen(int *a, int n) {\n    for (int i = 0; i < n - 1; i++) {\n        for (int j = 0; j < n - i - 1; j++) {\n            if (a[j] > a[j+1]) {\n                int t = a[j];\n                a[j] = a[j+1];\n                a[j+1] = t;\n            }\n        }\n    }\n}",
                "Принцип работы: Алгоритм последовательно сравнивает соседние элементы. Если текущий элемент больше следующего, они меняются местами. За каждый проход самый большой элемент «всплывает» в конец массива.\n\nСложность: O(n²).\nПлюсы: Простота реализации.\nМинусы: Низкая скорость на больших массивах."
            ),
            SortDetail(
                "Выбор",
                "void sort_vybor(int *a, int n) { \n    int k, m;\n    for (int i=n-1; i>0; i--) {\n        k=i; m=a[i];\n        for (int j=0; j<i; j++) \n            if (a[j]>m) { k=j; m=a[k]; }\n        if (k!=i) { a[k]=a[i]; a[i]=m; }\n    }\n}",
                "Принцип работы: Программа проходит по массиву в поисках максимального элемента и меняет его местами с последним элементом. Затем процесс повторяется для оставшейся части массива.\n\nСложность: O(n²).\nОсобенность: Количество перестановок минимально (не более n-1), что полезно, если запись в память «дорогая»."
            ),
            SortDetail(
                "Вставки",
                "void sort_insert(int *m, int n) {\n    int j, r;\n    for (int i=1; i<n; i++) {\n        r=m[i]; j=i-1;\n        while (j>=0 && m[j]>r) {\n            m[j+1]=m[j]; j--;\n        }\n        m[j+1]=r;\n    }\n}",
                "Принцип работы: Массив разделяется на отсортированную и неотсортированную части. На каждом шаге берется элемент из правой части и вставляется на правильное место в левую часть, раздвигая соседей.\n\nСложность: O(n²).\nОсобенность: Очень эффективен на почти отсортированных данных и малых массивах."
            ),
            SortDetail(
                "Шелл",
                "void ShellSort(int a[], int n) {\n    for (int s=n/2; s>0; s/=2)\n        for (int i=s; i<n; i++) {\n            int t=a[i], j=i;\n            while (j>=s && a[j-s]>t) {\n                a[j]=a[j-s]; j-=s;\n            }\n            a[j]=t;\n        }\n}",
                "Принцип работы: Улучшенная сортировка вставками. Сравниваются элементы, находящиеся на большом расстоянии друг от друга. Постепенно это расстояние (шаг) уменьшается до 1.\n\nСложность: От O(n log²n) до O(n²).\nОсобенность: Позволяет элементам быстро перемещаться на свои позиции, минуя долгие одиночные сдвиги."
            ),
            SortDetail(
                "Пирамида (Куча)",
                "void heapSort(int a[], int n) {\n    for (int i=n/2-1; i>=0; i--) heapify(a, n, i);\n    for (int i=n-1; i>0; i--) {\n        swap(a[0], a[i]);\n        heapify(a, i, 0);\n    }\n}",
                "Принцип работы: Сначала массив преобразуется в двоичную кучу (дерево, где корень всегда больше потомков). Затем корень (максимум) забирается в конец, а оставшееся дерево перестраивается.\n\nСложность: O(n log n).\nОсобенность: Стабильно быстрая сортировка, не требующая дополнительной памяти."
            ),
            SortDetail(
                "Хоар (Быстрая)",
                "void quickSort(int a[], int low, int high) {\n    if (low < high) {\n        int p = partition(a, low, high);\n        quickSort(a, low, p);\n        quickSort(a, p + 1, high);\n    }\n}\nint partition(int a[], int l, int h) {\n    int p = a[(l + h) / 2], i = l - 1, j = h + 1;\n    while (true) {\n        do i++; while (a[i] < p);\n        do j--; while (a[j] > p);\n        if (i >= j) return j;\n        swap(a[i], a[j]);\n    }\n}",
                "Принцип работы: Выбирается опорный элемент (pivot). Массив делится на две части: меньше опорного и больше него. Затем алгоритм рекурсивно применяется к каждой части.\n\nСложность: В среднем O(n log n).\nОсобенность: Считается самым быстрым алгоритмом на практике для большинства задач."
            )
        )
    }
}