package com.github.eduardbeutel.treeiterator.common;

import java.util.Optional;

public class IterationStep<Node>
{

    private Node node;
    private String id = "";
    private String path = "";

    private boolean skip = false;
    private boolean remove = false;
    private boolean root = false;

    private Optional<Node> replacement = Optional.empty();

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

    public boolean isReplace()
    {
        return replacement.isPresent();
    }

    public void setReplacement(Node replacement)
    {
        this.replacement = Optional.of(replacement);
    }

    public Node getReplacement()
    {
        return replacement.get();
    }
}
