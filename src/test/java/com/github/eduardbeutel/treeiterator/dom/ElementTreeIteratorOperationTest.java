package com.github.eduardbeutel.treeiterator.dom;

import com.github.eduardbeutel.treeiterator.test.XmlUtils;

import org.junit.Test;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;
import static org.junit.Assert.assertTrue;

public class ElementTreeIteratorOperationTest
{

    public static String DEFAULT_DOCUMENT = """
                <library>
                    <book id="1"/>
                    <book/>
                    <book id="2"/>
                </library>
        """;

    @Test
    public void stop()
    {
        // given
        Document document = XmlUtils.createDocument("""
                <library>
                    <book>
                        <title/>
                        <author/>
                    </book>
                </library>
        """);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .always().then(e -> result.add(e.getLocalName()))
                .when(e -> e.getLocalName().toString().equals("title")).stop()
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("library", "book", "title"), result);
    }

    @Test
    public void then()
    {
        // given
        Document document = XmlUtils.createDocument(DEFAULT_DOCUMENT);
        AtomicReference<Boolean> result = new AtomicReference<>(false);

        // when
        ElementTreeIterator.topDown(document)
                .when(e -> "2".equals(e.getAttribute("id"))).then(() -> result.set(true))
                .execute()
        ;

        // then
        assertTrue(result.get().booleanValue());
    }

    @Test
    public void then_nodeArgument()
    {
        // given
        Document document = XmlUtils.createDocument(DEFAULT_DOCUMENT);

        // when
        ElementTreeIterator.topDown(document)
                .when(e -> "2".equals(e.getAttribute("id"))).then(e -> e.setTextContent("content"))
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument("""
                <library>
                    <book id="1"/>
                    <book/>
                    <book id="2">content</book>
                </library>
        """);
        assertThat(document, isIdenticalTo(expectedDocument).ignoreWhitespace());
    }

    @Test
    public void then_nodeAndIdArguments()
    {
        // given
        Document document = XmlUtils.createDocument(DEFAULT_DOCUMENT);

        // when
        ElementTreeIterator.topDown(document)
                .when(e -> "2".equals(e.getAttribute("id"))).then((node, id) -> node.setTextContent(id))
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument("""
                <library>
                    <book id="1"/>
                    <book/>
                    <book id="2">book</book>
                </library>
        """);
        assertThat(document, isIdenticalTo(expectedDocument).ignoreWhitespace());
    }

    @Test
    public void then_nodeAndIdAndPathArguments()
    {
        // given
        Document document = XmlUtils.createDocument(DEFAULT_DOCUMENT);

        // when
        ElementTreeIterator.topDown(document)
                .when(e -> "2".equals(e.getAttribute("id"))).then((node, id, path) -> node.setTextContent(id + path))
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument("""
                <library>
                    <book id="1"/>
                    <book/>
                    <book id="2">book/library/book</book>
                </library>
        """);
        assertThat(document, isIdenticalTo(expectedDocument).ignoreWhitespace());
    }

}
