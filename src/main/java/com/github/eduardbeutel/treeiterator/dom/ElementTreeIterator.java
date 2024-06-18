package com.github.eduardbeutel.treeiterator.dom;

import com.github.eduardbeutel.treeiterator.common.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class ElementTreeIterator extends TreeIterator<Element>
{

    private Document document;

    public static Conditions<Element> topDown(Document document)
    {
        return new ElementTreeIterator(document, TraversalDirection.TOP_DOWN).getConditions();
    }

    public static Conditions<Element> bottomUp(Document document)
    {
        return new ElementTreeIterator(document, TraversalDirection.BOTTOM_UP).getConditions();
    }

    protected ElementTreeIterator(Document document, TraversalDirection direction)
    {
        super(document.getDocumentElement(), direction);
        this.document = document;
    }

    @Override
    protected void iterate(Element element)
    {
        IterationStep<Element> step = createFirstStep(element);
        iterateStep(step);
        if (step.isReplace()) document.replaceChild(step.getReplacement(), step.getNode());
    }

    @Override
    protected boolean isLeaf(Element element)
    {
        int nrChildren = element.getChildNodes().getLength();
        for (int i = 0; i < nrChildren; i++)
        {
            Node childNode = element.getChildNodes().item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) return false;
        }
        return true;
    }

    protected void iterateStep(IterationStep<Element> step)
    {
        if (TraversalDirection.TOP_DOWN == getDirection())
        {
            executeCommands(step);
            if (step.isSkip()) return;
        }

        List<Element> toRemove = null;
        List<IterationStep<Element>> toReplace = null;
        NodeList children = step.getNode().getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
        {
            org.w3c.dom.Node childNode = children.item(i);
            if (childNode.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) continue;
            Element childElement = (Element) childNode;

            IterationStep<Element> childStep = createChildStep(step, childElement, getId(childElement));
            iterateStep(childStep);

            if (childStep.isRemove())
            {
                if (toRemove == null) toRemove = new ArrayList<>();
                toRemove.add(childStep.getNode());
            }
            else if (childStep.isReplace())
            {
                if (toReplace == null) toReplace = new ArrayList<>();
                toReplace.add(childStep);
            }
        }

        remove(step.getNode(), toRemove);
        replace(step.getNode(), toReplace);

        if (TraversalDirection.BOTTOM_UP == getDirection()) executeCommands(step);
    }

    protected IterationStep<Element> createChildStep(IterationStep<Element> parentStep, Element child, String childId)
    {
        String childPath = parentStep.getPath() + "/" + childId;
        return new IterationStep<>(child, childId, childPath);
    }

    protected IterationStep<Element> createFirstStep(Element element)
    {
        String rootId = getId(element);
        String rootPath = "/" + rootId;
        IterationStep<Element> step = new IterationStep<>(element, rootId, rootPath);
        step.setRoot(true);
        return step;
    }

    protected String getId(Element element)
    {
        String id = element.getLocalName();
        if (id == null) throw new RuntimeException("Please use DocumentBuilderFactory.setNamespaceAware(true).");
        return id;
    }

    protected void remove(Element parent, List<Element> children)
    {
        if (children == null) return;
        children.forEach(child -> parent.removeChild(child));
    }

    protected void replace(Element parent, List<IterationStep<Element>> children)
    {
        if (children == null) return;
        children.forEach(child -> parent.replaceChild(child.getReplacement(), child.getNode()));
    }

}
