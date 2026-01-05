#ifndef SORTING_ALGORITHMS_H
#define SORTING_ALGORITHMS_H

#include <functional>
#include <vector>
#include <string>
#include <android/log.h>

typedef std::function<void(int, int)> VisualizerCallback;
typedef std::function<void(int*, int)> IterCallback;

struct SortStats {
    int comparisons = 0;
    int swaps = 0;
    std::string algoName;

    void reset(std::string name) {
        comparisons = 0;
        swaps = 0;
        algoName = name;
    }

    void print() const {
        __android_log_print(ANDROID_LOG_INFO, "SortStats",
                            "Algorithm: %s | Comparisons: %d | Swaps: %d",
                            algoName.c_str(), comparisons, swaps);
    }
};

namespace Sorting {

    void bubbleSort(int* a, int n, bool ascending, bool fromStart, const VisualizerCallback& sync, IterCallback iter);
    void selectionSort(int* a, int n, bool ascending, VisualizerCallback sync, IterCallback iter);
    void insertionSort(int* a, int n, bool ascending, VisualizerCallback sync);
    void shellSort(int* a, int n, bool ascending, VisualizerCallback sync);
    void heapSort(int* a, int n, bool ascending, VisualizerCallback sync);
    void quickSort(int* a, int low, int high, bool ascending, VisualizerCallback sync);
}

#endif