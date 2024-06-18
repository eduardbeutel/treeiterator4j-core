package com.github.eduardbeutel.treeiterator.dom;

import com.github.eduardbeutel.treeiterator.common.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ElementTreeIterator extends TreeIterator<Element>
{

    public static Conditions<Element> topDown(Document document)
    {
        return new ElementTreeIterator(document.getDocumentElement(), TraversalDirection.TOP_DOWN).getConditions();
    }

    public static Conditions<Element> bottomUp(Document document)
    {
        return new ElementTreeIterator(document.getDocumentElement(), TraversalDirection.BOTTOM_UP).getConditions();
    }

    public static Conditions<Element> topDown(Element element)
    {
        return new ElementTreeIterator(element, TraversalDirection.TOP_DOWN).getConditions();
    }

    public static Conditions<Element> bottomUp(Element element)
    {
        return new ElementTreeIterator(element, TraversalDirection.BOTTOM_UP).getConditions();
    }

    protected ElementTreeIterator(Element root, TraversalDirection direction)
    {
        super(root, direction);
    }

    @Override
    protected void iterate(Element element)
    {
        IterationStep<Element> step = createFirstStep(element);
        iterateStep(step);
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
        if (TraversalDirection.TOP_DOWN == getDirection()) executeCommands(step);

        NodeList children = step.getNode().getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
        {
            org.w3c.dom.Node childNode = children.item(i);
            if (childNode.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) continue;
            Element childElement = (Element) childNode;

            IterationStep<Element> childStep = createChildStep(step, childElement, getId(childElement));
            iterateStep(childStep);
        }

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
        return new IterationStep<>(element, rootId, rootPath);
    }

    protected String getId(Element element)
    {
        String id = element.getLocalName();
        if (id == null) throw new RuntimeException("Please use DocumentBuilderFactory.setNamespaceAware(true).");
        return id;
    }

}
