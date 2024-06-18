package com.github.eduardbeutel.treeiterator.common;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Conditions<Node>
{

    public Predicate<IterationStep<Node>> IS_ROOT_CONDITION = step -> step.getPath().lastIndexOf('/') == 0;

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
        Predicate<IterationStep<Node>> executablePredicate = step -> PredicateCreator.stringEquals(id).test(step.getId());
        return whenForStep(executablePredicate);
    }

    public Operations<Node> whenPath(String path)
    {
        Predicate<IterationStep<Node>> executablePredicate = step -> PredicateCreator.stringEquals(path).test(step.getPath());
        return whenForStep(executablePredicate);
    }

    public Operations<Node> whenIdMatches(String pattern)
    {
        Predicate<IterationStep<Node>> executablePredicate = step -> PredicateCreator.stringMatches(pattern).test(step.getId());
        return whenForStep(executablePredicate);
    }

    public Operations<Node> whenPathMatches(String pattern)
    {
        Predicate<IterationStep<Node>> executablePredicate = step -> PredicateCreator.stringMatches(pattern).test(step.getPath());
        return whenForStep(executablePredicate);
    }

    public Operations<Node> whenRoot()
    {
        Predicate<IterationStep<Node>> executablePredicate = IS_ROOT_CONDITION;
        return whenForStep(executablePredicate);
    }

    public Operations<Node> whenLeaf()
    {
        Predicate<IterationStep<Node>> executablePredicate = step -> iterator.isLeaf(step.getNode());
        return whenForStep(executablePredicate);
    }

    public Operations<Node> whenNotLeaf()
    {
        Predicate<IterationStep<Node>> executablePredicate = step -> !iterator.isLeaf(step.getNode());
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
