package com.github.eduardbeutel.treeiterator.common;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class TreeIterator<Node>
{

    private Conditions<Node> conditions = new Conditions<>(this);
    private Operations<Node> operations = new Operations<>(this);
    private CommandExecutor<Node> executor = new CommandExecutor<>();
    private TraversalDirection direction;
    private Node root;
    private Optional<Command> currentCommand = Optional.empty();
    private List<Command<Node>> commands = new ArrayList<>();

    public TreeIterator(Node root, TraversalDirection direction)
    {
        this.root = root;
        this.direction = direction;
    }

    public void execute()
    {
        try
        {
            clearLastCommandIfEmpty();
            iterate(root);
        }
        catch (StopIterationException e)
        {
        }
    }

    protected Operations<Node> getOperations()
    {
        return operations;
    }

    protected Conditions<Node> getConditions()
    {
        return conditions;
    }

    protected TreeIterator<Node> addCondition(Predicate<IterationStep<Node>> condition)
    {
        getCurrentCommand().setCondition(condition);
        return this;
    }

    protected TreeIterator<Node> addOperation(Consumer<IterationStep<Node>> operation)
    {
        getCurrentCommand().setOperation(operation);
        newCommand();
        return this;
    }

    protected abstract void iterate(Node node);

    protected void clearLastCommandIfEmpty()
    {
        if (commands.isEmpty()) return;
        int lastIndex = commands.size() - 1;
        Command lastCommand = commands.get(lastIndex);
        if (lastCommand.getCondition() == null ) commands.remove(lastIndex);
    }

    protected void executeCommands(IterationStep<Node> step)
    {
        for (Command command : getCommands())
        {
            executor.execute(command, step);
        }
    }

    protected CommandExecutor<Node> getExecutor()
    {
        return executor;
    }

    protected TraversalDirection getDirection()
    {
        return direction;
    }

    protected List<Command<Node>> getCommands()
    {
        return commands;
    }

    protected Command<Node> getCurrentCommand()
    {
        if(currentCommand.isEmpty()) newCommand();
        return currentCommand.get();
    }

    protected Command<Node> newCommand()
    {
        Command<Node> command = new Command<Node>();
        currentCommand = Optional.of(command);
        commands.add(command);
        return command;
    }

}
