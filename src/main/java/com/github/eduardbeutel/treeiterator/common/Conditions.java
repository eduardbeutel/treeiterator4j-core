package com.github.eduardbeutel.treeiterator.common;

import java.util.function.Consumer;
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
        Predicate<IterationStep<Node>> executablePredicate = step -> condition.test(step.getNode());
        return whenForStep(executablePredicate);
    }

    public Operations<Node> whenNot(Predicate<Node> condition)
    {
        return when(condition.negate());
    }

    public Operations<Node> whenId(String id)
    {
        Predicate<IterationStep<Node>> executablePredicate = step -> PredicateCreator.stringEquals(step.getId()).test(id);
        return whenForStep(executablePredicate);
    }

    public Operations<Node> whenPath(String path)
    {
        Predicate<IterationStep<Node>> executablePredicate = step -> PredicateCreator.stringEquals(step.getPath()).test(path);
        return whenForStep(executablePredicate);
    }

    public Operations<Node> whenForStep(Predicate<IterationStep<Node>> condition)
    {
        return iterator
                .addCondition(condition)
                .getOperations()
                ;
    }

}
