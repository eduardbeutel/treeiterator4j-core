package com.github.eduardbeutel.treeiterator.common;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CommandExecutor<Node>
{
    public void execute(Command<Node> command, IterationStep<Node> step)
    {
        if (evaluateCondition(command.getCondition(), step))
        {
            executeOperation(command.getOperation(), step);
        }
    }

    protected boolean evaluateCondition(Predicate<IterationStep<Node>> condition, IterationStep<Node> step)
    {
        return condition.test(step);
    }

    protected void executeOperation(Consumer<IterationStep<Node>> operation, IterationStep<Node> step)
    {
        operation.accept(step);
    }

}
