package com.github.eduardbeutel.treeiterator.common;

public class IterationStep<IdNode>
{

    private IdNode node;
    private String id;
    private String path;
    private boolean skip;

    public IterationStep(IdNode node, String id, String path)
    {
        this.node = node;
        this.id = id;
        this.path = path;
    }

    public IdNode getNode()
    {
        return node;
    }

    public String getId()
    {
        return id;
    }

    public String getPath()
    {
        return path;
    }

    public void setSkip(boolean skip)
    {
        this.skip = skip;
    }

    public boolean isSkip()
    {
        return skip;
    }
}
