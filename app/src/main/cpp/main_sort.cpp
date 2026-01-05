#include <jni.h>
#include <unistd.h>
#include "sorting_algorithms.h"
#include <iostream>
#include <android/log.h>

extern "C" JNIEXPORT void JNICALL
Java_com_example_cpppsu_SortEngine_runSort(JNIEnv* env, 
                                           jobject thiz,
                                           jintArray array, 
                                           jint type, 
                                           jboolean asc, 
                                           jboolean accStart,
                                           jint speedAnim) {
    jclass cls = env->GetObjectClass(thiz);
    jmethodID mid = env->GetMethodID(cls, "onStep", "([III)V");
    jint* elements = env->GetIntArrayElements(array, nullptr);
    int n = env->GetArrayLength(array);


    auto visualizer = [&](int r, int g) {
        env->ReleaseIntArrayElements(array, elements, JNI_COMMIT);
        env->CallVoidMethod(thiz, mid, array, r, g);
        usleep(speedAnim * 10000); // 100 ms шаг-анимации
    };
    auto iter = [&](int* arr, int size) {
        std::string s = "";
        for(int i = 0; i < size; i++) {
            if(arr[i] == -1){
                s += "|";
            }else{
                s += std::to_string(arr[i]) + " ";
            }


        }
        __android_log_print(ANDROID_LOG_INFO, "MY_TAG", "%s", s.c_str());
    };
    __android_log_print(ANDROID_LOG_INFO, "MY_TAG", "kf");
    switch(type) {
        case 0: Sorting::bubbleSort(elements, n, asc, accStart, visualizer,iter); break;
        case 1: Sorting::selectionSort(elements, n, asc, visualizer,iter); break;
        case 2: Sorting::insertionSort(elements, n, asc, visualizer); break;
        case 3: Sorting::shellSort(elements, n, asc, visualizer); break;
        case 4: Sorting::heapSort(elements, n, asc, visualizer); break;
        case 5: Sorting::quickSort(elements, 0, n - 1, asc, visualizer); break;
    }

    env->ReleaseIntArrayElements(array, elements, 0);
}