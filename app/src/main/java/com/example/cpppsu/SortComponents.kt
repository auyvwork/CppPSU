package com.example.cpppsu

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


@Composable
fun SortBar(value: Int, isRed: Boolean, isGreen: Boolean, modifier: Modifier = Modifier) {
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
        uncheckedTrackColor = Color(0xFFBDBDBD)
    )
    val sliderColors = SliderDefaults.colors(
        thumbColor = Color.Black,
        activeTrackColor = Color.Black
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp).padding(top = 40.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("AlgoVisual", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp, top = 24.dp))

        Box(
            modifier = Modifier.height(280.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color.White),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                sortList.forEachIndexed { idx, valItem ->
                    SortBar(
                        value = valItem,
                        isRed = idx == redIdxState.value,
                        isGreen = idx == greenIdxState.value,
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
            onValueChange = { arraySize = it; onGenerate(it.toInt()) },
            valueRange = 5f..17f,
            enabled = !isRunning,
            colors = sliderColors
        )

        Text(text = "Скорость анимации: ${speedAnim.toInt()}", fontSize = 14.sp)
        Slider(
            value = speedAnim,
            steps = 5,
            onValueChange = { speedAnim = it },
            valueRange = 1f..100f,
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
                    text = { Text(text = d.name, color = if (selectedSort == i) (if(isRunning) Color.Gray else Color.Black) else Color.Gray) }
                )
            }
        }


        Column(
            Modifier
                .padding(vertical = 16.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(text = "Направление (Возрастание)", color = if(isRunning) Color.Gray else Color.Black)
                Switch(checked = isAsc, onCheckedChange = { isAsc = it }, colors = blackSwitchColors, enabled = !isRunning)
            }
            if (selectedSort < 2) {
                HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(text = "Накопление в начале", color = if(isRunning) Color.Gray else Color.Black)
                    Switch(checked = accStart, onCheckedChange = { accStart = it }, colors = blackSwitchColors, enabled = !isRunning)
                }
            }
        }

        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onGenerate(arraySize.toInt()) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                enabled = !isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0), contentColor = Color.Black)
            ) { Text("Сброс") }

            Button(
                onClick = {
                    isRunning = true
                    onStart(selectedSort, isAsc, accStart, speedAnim.toInt()) { isRunning = false }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                enabled = !isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) { Text(if (isRunning) "В процессе..." else "Пуск") }
        }

        val currentSortData = MainActivity.SORTS_DATA[selectedSort]
        Text("Реализация (C++)", modifier = Modifier.padding(top = 24.dp, bottom = 8.dp), fontWeight = FontWeight.Bold)
        Box(Modifier.fillMaxWidth().animateContentSize().heightIn(min = 50.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF1E1E1E))) {
            Text(text = currentSortData.fullCode, modifier = Modifier.padding(12.dp), color = Color(0xFF00E676), fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        }
        Text("Описание", modifier = Modifier.padding(top = 16.dp, bottom = 4.dp), fontWeight = FontWeight.Bold)
        Text(text = currentSortData.description, fontSize = 14.sp, color = Color.DarkGray)
        Spacer(Modifier.height(40.dp))
    }
}
