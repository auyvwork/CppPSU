package com.example.cpppsu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.concurrent.thread

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
        thread {
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
            SortDetail("Обмен (Пузырек)", "void sort_obmen(int *a, int n) {\n    for (int i = 0; i < n - 1; i++) {\n        for (int j = 0; j < n - i - 1; j++) {\n            if (a[j] > a[j+1]) {\n                int t = a[j];\n                a[j] = a[j+1];\n                a[j+1] = t;\n            }\n        }\n    }\n}", "Самый простой метод, похожий на всплывание пузырьков воздуха в воде. Алгоритм многократно проходит по массиву, сравнивая попарно соседние элементы (j и j+1). Если левый элемент больше правого, они меняются местами. За один полный проход самый большой элемент «всплывает» в самый конец массива, занимая своё законное место. Процесс повторяется для оставшихся элементов, пока весь массив не выстроится по порядку."),
            SortDetail("Выбор", "void sort_vybor(int *a, int n) { \n    int k, m;\n    for (int i=n-1; i>0; i--) {\n        k=i; m=a[i];\n        for (int j=0; j<i; j++) \n            if (a[j]>m) { k=j; m=a[k]; }\n        if (k!=i) { a[k]=a[i]; a[i]=m; }\n    }\n}", "Метод поиска экстремума. Мысленно делим массив на две части: отсортированную и беспорядочную. Алгоритм пробегает по беспорядочной части, находит там самый большой (или самый маленький) элемент и меняет его местами с первым элементом этой части. Теперь отсортированная область увеличилась на один шаг. Мы повторяем поиск для оставшихся чисел, пока не переберем их все."),
            SortDetail("Вставки", "void sort_insert(int *m, int n) {\n    int j, r;\n    for (int i=1; i<n; i++) {\n        r=m[i]; j=i-1;\n        while (j>=0 && m[j]>r) {\n            m[j+1]=m[j]; j--;\n        }\n        m[j+1]=r;\n    }\n}", "Похоже на то, как вы сортируете игральные карты в руке. Вы берете новую карту (элемент r) и смотрите на уже упорядоченные карты слева. Вы сдвигаете карты, которые больше новой, вправо, освобождая место. Как только находите карту меньше новой (или доходите до начала), вы вставляете взятую карту в образовавшееся пустое место. Так массив слева всегда остается отсортированным."),
            SortDetail("Шелл", "void ShellSort(int a[], int n) {\n    for (int s=n/2; s>0; s/=2)\n        for (int i=s; i<n; i++) {\n            int t=a[i], j=i;\n            while (j>=s && a[j-s]>t) {\n                a[j]=a[j-s]; j-=s;\n            }\n            a[j]=t;\n        }\n}", "Улучшенная версия сортировки вставками. Обычные вставки медленные, потому что элементы двигаются только на одну позицию за раз. Метод Шелла сначала сравнивает и сортирует элементы, стоящие далеко друг от друга (на расстоянии шага 's'). Это позволяет легким элементам быстро «прыгнуть» в начало, а тяжелым — в конец. Затем шаг уменьшается, и процесс повторяется. Последний проход — это обычная вставка, но так как массив уже почти упорядочен, она происходит мгновенно."),
            SortDetail("Пирамида (Куча)", "void heapSort(int a[], int n) {\n    for (int i=n/2-1; i>=0; i--) heapify(a, n, i);\n    for (int i=n-1; i>0; i--) {\n        swap(a[0], a[i]);\n        heapify(a, i, 0);\n    }\n}", "Алгоритм выстраивает элементы в специальную древовидную структуру — «кучу», где самый большой элемент (родитель) всегда находится выше своих потомков. Вершина пирамиды — это максимальное число массива. Мы берем эту вершину и отправляем её в самый конец массива (в готовую часть). Оставшуюся структуру перестраиваем заново, чтобы найти нового «царя горы». Повторяем, пока куча не иссякнет."),
            SortDetail("Хоар (Быстрая)", "void quickSort(int a[], int low, int high) {\n    if (low < high) {\n        int p = partition(a, low, high);\n        quickSort(a, low, p);\n        quickSort(a, p + 1, high);\n    }\n}\nint partition(int a[], int l, int h) {\n    int p = a[(l + h) / 2], i = l - 1, j = h + 1;\n    while (true) {\n        do i++; while (a[i] < p);\n        do j--; while (a[j] > p);\n        if (i >= j) return j;\n        swap(a[i], a[j]);\n    }\n}", "Принцип «разделяй и властвуй». Выбирается один элемент-опорная точка (пивот). Все числа меньше пивота перекидываются влево от него, а все, что больше — вправо. Теперь пивот стоит на своем окончательном месте. Затем этот же процесс рекурсивно запускается отдельно для левой части (маленьких чисел) и для правой части (больших чисел), дробя массив на всё более мелкие кусочки, пока он не станет полностью отсортированным.")
        )
    }
}

@Composable
private fun SortBar(value: Int, isRed: Boolean, isGreen: Boolean, modifier: Modifier = Modifier) {
    val animH by animateIntAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "barHeight"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = value.toString(),
            fontSize = if (value < 1000) 10.sp else 7.sp,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animH.dp)
                .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                .drawWithCache {
                    val brush = if (isRed || isGreen) {
                        val c = if (isRed) Color.Red else Color(0xFF388E3C)
                        Brush.verticalGradient(listOf(c, c))
                    } else {
                        Brush.verticalGradient(listOf(Color.Black, Color(0xFF424242)))
                    }
                    onDrawWithContent { drawRect(brush) }
                }
        )
    }
}

@Composable
fun SortScreenContent(
    sortList: List<Int>,
    redIdxState: State<Int>,
    greenIdxState: State<Int>,
    onGenerate: (Int) -> Unit,
    onStart: (Int, Boolean, Boolean, Int, () -> Unit) -> Unit
) {
    var selectedSort by remember { mutableIntStateOf(0) }
    var isAsc by remember { mutableStateOf(true) }
    var accStart by remember { mutableStateOf(false) }

    var isRunning by remember { mutableStateOf(false) }
    var arraySize by remember { mutableFloatStateOf(15f) }
    var speedAnim by remember { mutableFloatStateOf(5f) }

    val blackSwitchColors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = Color.Black,
        uncheckedThumbColor = Color.White,
        uncheckedTrackColor = Color(0xFFBDBDBD),
        disabledCheckedTrackColor = Color.Gray,
        disabledUncheckedTrackColor = Color.LightGray
    )
    val sliderColors = SliderDefaults.colors(
        thumbColor = Color.Black,
        activeTrackColor = Color.Black,
        disabledThumbColor = Color.Gray,
        disabledActiveTrackColor = Color.Gray
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "AlgoVisual",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .height(280.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val rIdx = redIdxState.value
                val gIdx = greenIdxState.value
                sortList.forEachIndexed { idx, valItem ->
                    SortBar(
                        value = valItem,
                        isRed = idx == rIdx,
                        isGreen = idx == gIdx,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(text = "Размер массива: ${arraySize.toInt()}", fontSize = 14.sp)
        Slider(
            value = arraySize,
            steps = 13,
            onValueChange = {
                arraySize = it
                onGenerate(it.toInt())
            },
            valueRange = 5f..17f,
            enabled = !isRunning,
            colors = sliderColors
        )

        Text(text = "Скорость анимации: ${speedAnim.toInt()}", fontSize = 14.sp)
        Slider(
            value = speedAnim,
            steps = 4,
            onValueChange = { speedAnim = it },
            valueRange = 1f..20f,
            enabled = !isRunning,
            colors = sliderColors
        )

        ScrollableTabRow(
            selectedTabIndex = selectedSort,
            containerColor = Color.Transparent,
            edgePadding = 0.dp,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedSort]),
                    color = if(isRunning) Color.Gray else Color.Black
                )
            }
        ) {
            MainActivity.SORTS_DATA.forEachIndexed { i, d ->
                Tab(
                    selected = selectedSort == i,
                    onClick = { selectedSort = i },
                    enabled = !isRunning,
                    text = {
                        Text(
                            text = d.name,
                            color = if (selectedSort == i) {
                                if(isRunning) Color.Gray else Color.Black
                            } else Color.Gray
                        )
                    }
                )
            }
        }

        Column(
            Modifier
                .padding(vertical = 16.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Text(text = "Направление (Возрастание)", color = if(isRunning) Color.Gray else Color.Black)
                Switch(
                    checked = isAsc,
                    onCheckedChange = { isAsc = it },
                    colors = blackSwitchColors,
                    enabled = !isRunning
                )
            }
            if (selectedSort < 2) {
                HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Text(text = "Накопление в конце", color = if(isRunning) Color.Gray else Color.Black)
                    Switch(
                        checked = accStart,
                        onCheckedChange = { accStart = it },
                        colors = blackSwitchColors,
                        enabled = !isRunning
                    )
                }
            }
        }

        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onGenerate(arraySize.toInt()) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                enabled = !isRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE0E0E0),
                    contentColor = Color.Black,
                    disabledContainerColor = Color(0xFFF0F0F0),
                    disabledContentColor = Color.Gray
                )
            ) { Text("Сброс") }

            Button(
                onClick = {
                    isRunning = true
                    onStart(selectedSort, isAsc, accStart, speedAnim.toInt()) {

                        isRunning = false
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                enabled = !isRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    disabledContainerColor = Color.Gray
                )
            ) { Text(if (isRunning) "В процессе..." else "Пуск") }
        }

        Text(
            text = "Реализация (C++)",
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )

        val currentSortData = MainActivity.SORTS_DATA[selectedSort]

        Box(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .heightIn(min = 50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E1E1E))
        ) {
            Text(
                text = currentSortData.fullCode,
                modifier = Modifier.padding(12.dp),
                color = Color(0xFF00E676),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
        }

        Text(
            text = "Описание",
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = currentSortData.description,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
        Spacer(Modifier.height(40.dp))
    }
}

data class SortDetail(val name: String, val fullCode: String, val description: String)