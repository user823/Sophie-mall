package com.sophie.sophiemall.common.algorithm;

import java.util.function.IntPredicate;

public class Arrays {
    public static int binary(int left, int right, IntPredicate fn) {
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (!fn.test(mid)) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }
}
