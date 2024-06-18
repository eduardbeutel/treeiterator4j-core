package com.github.eduardbeutel.treeiterator.common;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CommandExecutor<Node>
{
    public void execute(Command<Node> command, IterationStep<Node> step)
    {
        if (evaluateConditions(command.getConditions(), step))
        {
            executeOperation(command.getOperation(), step);
        }
    }

    private boolean evaluateConditions(List<Predicate<IterationStep<Node>>> conditions, IterationStep<Node> step)
    {
        boolean result = true;
        for (Predicate<IterationStep<Node>> condition : conditions)
        {
            result &= condition.test(step);
        }
        return result;
    }

    protected void executeOperation(Consumer<IterationStep<Node>> operation, IterationStep<Node> step)
    {
        operation.accept(step);
    }

}
