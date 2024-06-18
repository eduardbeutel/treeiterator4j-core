package com.github.eduardbeutel.treeiterator.common;

import java.util.function.Predicate;

public class PredicateCreator
{
    public static Predicate<String> stringEquals(String other)
    {
        return param -> PredicateCreator.equals(param, other);
    }

    protected static boolean equals(String left, String right)
    {
        if (left == null && right != null) return false;
        if (left != null && right == null) return false;
        if (left == null && right == null) return true;
        return left.equals(right);
    }

}
