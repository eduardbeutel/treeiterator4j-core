package com.github.eduardbeutel.treeiterator.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Command<Node>
{
    private List<Predicate<IterationStep<Node>>> conditions = new ArrayList<>();
    private Consumer<IterationStep<Node>> operation;

    public List<Predicate<IterationStep<Node>>> getConditions()
    {
        return conditions;
    }

    public void addCondition(Predicate<IterationStep<Node>> condition)
    {
        conditions.add(condition);
    }

    public Consumer<IterationStep<Node>> getOperation()
    {
        return operation;
    }

    public void setOperation(Consumer<IterationStep<Node>> operation)
    {
        this.operation = operation;
    }

}
