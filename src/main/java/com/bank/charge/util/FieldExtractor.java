
package com.bank.charge.util;

import java.util.*;

public class FieldExtractor {

    @SuppressWarnings("unchecked")
    public static List<Object> extractAll(Object current, String[] tokens) {
        return extractRecursive(current, tokens, 0);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> extractRecursive(Object current,
                                                 String[] tokens,
                                                 int index) {

        if (current == null) return List.of();
        if (index >= tokens.length) return List.of(current);

        String key = tokens[index];
        List<Object> results = new ArrayList<>();

        if (current instanceof Map<?, ?> map) {
            Object next = map.get(key);
            results.addAll(extractRecursive(next, tokens, index + 1));
        } else if (current instanceof List<?> list) {
            for (Object item : list) {
                results.addAll(extractRecursive(item, tokens, index));
            }
        }

        return results;
    }
}
