package com.github.eduardbeutel.treeiterator.common;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Command<Node>
{
    private Predicate<Node> condition;
    private Consumer<IterationStep<Node>> operation;

    public Predicate<Node> getCondition()
    {
        return condition;
    }

    public Consumer<IterationStep<Node>> getOperation()
    {
        return operation;
    }

    public void setCondition(Predicate<Node> condition)
    {
        this.condition = condition;
    }

    public void setOperation(Consumer<IterationStep<Node>> operation)
    {
        this.operation = operation;
    }
}
