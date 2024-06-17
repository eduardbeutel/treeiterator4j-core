package com.github.eduardbeutel.treeiterator.common;

import java.util.function.Predicate;

public class Conditions<Node>
{
    private TreeIterator iterator;

    public Conditions(TreeIterator iterator)
    {
        this.iterator = iterator;
    }

    public void execute()
    {
        iterator.execute();
    }

    public Operations<Node> always()
    {
        return when(o -> true);
    }

    public Operations<Node> when(Predicate<Node> condition)
    {
        return iterator
                .addCondition(condition)
                .getOperations()
                ;
    }

    public Operations<Node> whenNot(Predicate<Node> condition)
    {
        return when(condition.negate());
    }

}
