
<div align="center">

# Android Sorting Visualizer (C++ JNI)

<p>
  <img src="https://img.shields.io/badge/Language-Kotlin%20%2F%20C%2B%2B-blue?style=flat-square" alt="Language">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=flat-square" alt="Platform">
  <img src="https://img.shields.io/badge/Interface-JNI-orange?style=flat-square" alt="JNI">
</p>

<hr />
<h3>Студент: Зотов Ярослав Дмитриевич</h3>
<p><b>Группа: ИТ-12, 1 курс</b></p>
<hr />

</div>

Приложение для визуализации алгоритмов сортировки, реализованных на C++ и интегрированных в Android проект с помощью JNI (Java Native Interface).

## Алгоритмы
В проекте реализованы следующие алгоритмы:

### Алгоритмы сортировки
* **Bubble Sort** (Сортировка пузырьком)
* **Selection Sort** (Сортировка выбором)
* **Insertion Sort** (Сортировка вставками)
* **Shell Sort** (Сортировка Шелла)
* **Heap Sort** (Пирамидальная сортировка)
* **Quick Sort** (Быстрая сортировка)



### Структура проекта (Native часть)
* **sorting_algorithms.h** — Заголовочный файл с определениями типов callback-ов и интерфейсами алгоритмов.
* **sorting_algorithms.cpp** — Полная реализация алгоритмов сортировки и логики сбора статистики.
* **main_sort.cpp** — JNI-прослойка, связь между C++ и Kotlin, управление памятью.
* **SortEngine.kt** — входная точка со стороны kotlin (загрузка библиотеки).
