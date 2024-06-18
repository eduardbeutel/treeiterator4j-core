package com.github.eduardbeutel.treeiterator.common;

public class IterationStep<Node>
{

    private Node node;
    private String id = "";
    private String path = "";

    private boolean skip = false;
    private boolean remove = false;
    private boolean root = false;

    public IterationStep(Node node, String id, String path)
    {
        this.node = node;
        this.id = id;
        this.path = path;
    }

    public Node getNode()
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

    public void setRemove(boolean remove)
    {
        this.remove = remove;
    }

    public boolean isRemove()
    {
        return remove;
    }

    public boolean isRoot()
    {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }
}
