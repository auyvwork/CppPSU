#include <jni.h>
#include <unistd.h>
#include "sorting_algorithms.h"
#include <android/log.h>

extern "C" JNIEXPORT void JNICALL
Java_com_example_cpppsu_SortEngine_runSort(JNIEnv* env, jobject thiz, jintArray array,
                                           jint type, jboolean asc, jboolean accStart, jint speedAnim) {

    jclass cls = env->GetObjectClass(thiz);
    jmethodID mid = env->GetMethodID(cls, "onStep", "([III)V");

    jint* elements = env->GetIntArrayElements(array, nullptr);
    int n = env->GetArrayLength(array);

    auto visualizer = [&](int i, int j) {
        if (env->ExceptionCheck()) return; 

        env->ReleaseIntArrayElements(array, elements, JNI_COMMIT);
        env->CallVoidMethod(thiz, mid, array, i, j);

        if (speedAnim > 0) usleep(speedAnim * 1000);
    };

    auto iteration = [&](const int* arr, int size) {
        std::string s = "Pass: ";
        for(int i = 0; i < size; i++) {
            s += (arr[i] == -1 ? "|" : std::to_string(arr[i])) + " ";
        }
        __android_log_print(ANDROID_LOG_DEBUG, "SortEngine", "%s", s.c_str());
    };

    switch(type) {
        case 0: Sorting::bubbleSort(elements, n, asc, accStart, visualizer, iteration); break;
        case 1: Sorting::selectionSort(elements, n, asc, visualizer, iteration); break;
        case 2: Sorting::insertionSort(elements, n, asc, visualizer); break;
        case 3: Sorting::shellSort(elements, n, asc, visualizer); break;
        case 4: Sorting::heapSort(elements, n, asc, visualizer); break;
        case 5: Sorting::quickSort(elements, 0, n - 1, asc, visualizer); break;
        default: break;
    }

    env->ReleaseIntArrayElements(array, elements, 0);
}