#include "sorting_algorithms.h"
#include <algorithm>
#include <vector>
#include <android/log.h>

struct SortStats {
    int comparisons = 0;
    int swaps = 0;
    std::string name;

    void reset(std::string n) {
        name = n;
        comparisons = 0;
        swaps = 0;
    }

    void print() const {
        __android_log_print(ANDROID_LOG_INFO, "SortStats",
                            "%s: Comparisons: %d, Swaps: %d",
                            name.c_str(), comparisons, swaps);
    }
};

namespace Sorting {

    void sendIterationUpdate(int* arr, int n, int mark, IterationCallback iteration) {
        if (!iteration) return;
        std::vector<int> displayVec(arr, arr + n);
        if (mark >= 0 && mark < n) {
            displayVec[mark] = -1;
        }
        iteration(displayVec.data(), n);
    }

    void bubbleSort(int* arr, int n, bool asc, bool fromStart, const VisualizerCallback& sync, IterationCallback iteration) {
        SortStats stats;
        stats.reset("Bubble Sort");

        for (int i = 0; i < n - 1; i++) {
            for (int j = (fromStart ? n - 1 : 0); (fromStart ? j > i : j < n - i - 1); (fromStart ? j-- : j++)) {
                int left = fromStart ? j - 1 : j;
                int right = fromStart ? j : j + 1;

                stats.comparisons++;
                sync(left, right);

                if (asc ? (arr[left] > arr[right]) : (arr[left] < arr[right])) {
                    std::swap(arr[left], arr[right]);
                    stats.swaps++;
                }
            }
            sendIterationUpdate(arr, n, fromStart ? i : n - 1 - i, iteration);
        }
        stats.print();
    }

    void selectionSort(int* arr, int n, bool asc, const VisualizerCallback& sync, IterationCallback iteration) {
        SortStats stats;
        stats.reset("Selection Sort");

        for (int i = 0; i < n - 1; i++) {
            int targetIdx = i;
            for (int j = i + 1; j < n; j++) {
                stats.comparisons++;
                sync(j, targetIdx);
                if (asc ? (arr[j] < arr[targetIdx]) : (arr[j] > arr[targetIdx])) {
                    targetIdx = j;
                }
            }
            if (targetIdx != i) {
                std::swap(arr[i], arr[targetIdx]);
                stats.swaps++;
                sync(i, targetIdx);
            }
            sendIterationUpdate(arr, n, i, iteration);
        }
        stats.print();
    }

    void insertionSort(int* arr, int n, bool asc, const VisualizerCallback& sync) {
        SortStats stats;
        stats.reset("Insertion Sort");

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0) {
                stats.comparisons++;
                sync(j, j + 1);
                if (asc ? (arr[j] > key) : (arr[j] < key)) {
                    arr[j + 1] = arr[j];
                    stats.swaps++;
                    j--;
                } else break;
            }
            arr[j + 1] = key;
            sync(j + 1, i);
        }
        stats.print();
    }

    void shellSort(int* arr, int n, bool asc, const VisualizerCallback& sync) {
        SortStats stats;
        stats.reset("Shell Sort");

        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = arr[i];
                int j;
                for (j = i; j >= gap; j -= gap) {
                    stats.comparisons++;
                    sync(j, j - gap);
                    if (asc ? (arr[j - gap] > temp) : (arr[j - gap] < temp)) {
                        arr[j] = arr[j - gap];
                        stats.swaps++;
                    } else break;
                }
                arr[j] = temp;
                sync(j, i);
            }
        }
        stats.print();
    }

    void heapify(int* a, int n, int i, bool asc, const VisualizerCallback& sync, SortStats& stats) {
        int extreme = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n) {
            stats.comparisons++;
            if (asc ? a[left] > a[extreme] : a[left] < a[extreme]) extreme = left;
        }
        if (right < n) {
            stats.comparisons++;
            if (asc ? a[right] > a[extreme] : a[right] < a[extreme]) extreme = right;
        }
        if (extreme != i) {
            std::swap(a[i], a[extreme]);
            stats.swaps++;
            sync(i, extreme);
            heapify(a, n, extreme, asc, sync, stats);
        }
    }

    void heapSort(int* arr, int n, bool asc, const VisualizerCallback& sync) {
        SortStats stats;
        stats.reset("Heap Sort");

        for (int i = n / 2 - 1; i >= 0; i--) heapify(arr, n, i, asc, sync, stats);
        for (int i = n - 1; i > 0; i--) {
            std::swap(arr[0], arr[arr[i] ? 0 : i]);
            std::swap(arr[0], arr[i]);
            stats.swaps++;
            sync(0, i);
            heapify(arr, i, 0, asc, sync, stats);
        }
        stats.print();
    }

    void quickSortRecursive(int* a, int low, int high, bool asc, const VisualizerCallback& sync, SortStats& stats) {
        if (low < high) {
            int pivot = a[(low + high) / 2];
            int i = low, j = high;
            while (i <= j) {
                while (asc ? a[i] < pivot : a[i] > pivot) { stats.comparisons++; i++; }
                while (asc ? a[j] > pivot : a[j] < pivot) { stats.comparisons++; j--; }
                if (i <= j) {
                    if (i != j) {
                        std::swap(a[i], a[j]);
                        stats.swaps++;
                        sync(i, j);
                    }
                    i++; j--;
                }
            }
            if (low < j) quickSortRecursive(a, low, j, asc, sync, stats);
            if (i < high) quickSortRecursive(a, i, high, asc, sync, stats);
        }
    }

    void quickSort(int* a, int low, int high, bool asc, const VisualizerCallback& sync) {
        SortStats stats;
        stats.reset("Quick Sort");
        quickSortRecursive(a, low, high, asc, sync, stats);
        stats.print();
    }
}