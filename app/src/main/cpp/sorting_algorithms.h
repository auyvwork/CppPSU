#ifndef SORTING_ALGORITHMS_H
#define SORTING_ALGORITHMS_H

#include <functional>
#include <string>

typedef std::function<void(int, int)> VisualizerCallback;


namespace Sorting {
    void bubbleSort(int* a, int n, bool ascending, bool fromStart, const VisualizerCallback& sync);
    void selectionSort(int* a, int n, bool ascending, const VisualizerCallback& sync);
    void insertionSort(int* a, int n, bool ascending, const VisualizerCallback& sync);
    void shellSort(int* a, int n, bool ascending, const VisualizerCallback& sync);
    void heapSort(int* a, int n, bool ascending, const VisualizerCallback& sync);
    void quickSort(int* a, int low, int high, bool ascending, const VisualizerCallback& sync);
}

#endif