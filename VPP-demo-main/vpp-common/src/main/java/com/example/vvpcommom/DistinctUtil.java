package com.example.vvpcommom;

import java.util.List;
import java.util.stream.Collectors;

public class DistinctUtil {

    public static List<String> distinctList(List<String> rawList) {
        return rawList.stream().map(item -> item).distinct().collect(Collectors.toList());
    }
}
