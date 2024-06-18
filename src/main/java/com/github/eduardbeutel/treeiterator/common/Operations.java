package com.github.eduardbeutel.treeiterator.common;

import java.util.function.*;

public class Operations<Node>
{
    private TreeIterator iterator;
    public Operations(TreeIterator iterator)
    {
        this.iterator = iterator;
    }

    public Conditions<Node> stop()
    {
        return then(() ->
        {
            throw new StopIterationException();
        });
    }

    public Conditions<Node> then(Runnable runnable)
    {
        return then((node) -> runnable.run());
    }

    public Conditions<Node> then(Consumer<Node> consumer)
    {
        Consumer<IterationStep<Node>> executeConsumer = step -> consumer.accept(step.getNode());
        return thenForStep(executeConsumer);
    }

    public Conditions<Node> then(BiConsumer<Node, String> consumer)
    {
        Consumer<IterationStep<Node>> executeConsumer = step -> consumer.accept(step.getNode(), step.getId());
        return thenForStep(executeConsumer);
    }

    public Conditions<Node> then(TriConsumer<Node, String, String> consumer)
    {
        Consumer<IterationStep<Node>> executableConsumer = step -> consumer.accept(step.getNode(), step.getId(), step.getPath());
        return thenForStep(executableConsumer);
    }

    public Conditions<Node> skip()
    {
        if (TraversalDirection.BOTTOM_UP == iterator.getDirection())
            throw new UnsupportedFeatureException("skip() can not be used in bottomUp() mode.");
        Consumer<IterationStep<Node>> executableConsumer = step -> step.setSkip(true);
        return thenForStep(executableConsumer);
    }

    public Conditions<Node> remove()
    {
        Consumer<IterationStep<Node>> executableConsumer = step ->
        {
            if(step.isRoot()) throw new UnsupportedFeatureException("The root element can not be removed.");
            step.setRemove(true);
        };
        return thenForStep(executableConsumer);
    }

    public Conditions<Node> replace(Node node)
    {
        Consumer<IterationStep<Node>> executableConsumer = step -> step.setReplacement(node);
        return thenForStep(executableConsumer);
    }

    public Conditions<Node> replace(Supplier<Node> supplier)
    {
        Consumer<IterationStep<Node>> executableConsumer = step -> step.setReplacement(supplier.get());
        return thenForStep(executableConsumer);
    }

    public Conditions<Node> replace(Function<Node, Node> function)
    {
        Consumer<IterationStep<Node>> executableConsumer = step -> step.setReplacement(function.apply(step.getNode()));
        return thenForStep(executableConsumer);
    }

    public Conditions<Node> replace(BiFunction<Node, String, Node> function)
    {
        Consumer<IterationStep<Node>> executableConsumer = step -> step.setReplacement(function.apply(step.getNode(), step.getId()));
        return thenForStep(executableConsumer);
    }

    public Conditions<Node> replace(TriFunction<Node, String, String, Node> function)
    {
        Consumer<IterationStep<Node>> executableConsumer = step -> step.setReplacement(function.apply(step.getNode(), step.getId(), step.getPath()));
        return thenForStep(executableConsumer);
    }

    public Conditions<Node> thenForStep(Consumer<IterationStep<Node>> consumer)
    {
        return iterator
                .addOperation(consumer)
                .getConditions()
                ;
    }

    public Conditions<Node> and()
    {
        return iterator.getConditions();
    }

}
