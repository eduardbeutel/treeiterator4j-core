package com.github.eduardbeutel.treeiterator.dom;

import com.github.eduardbeutel.treeiterator.test.XmlUtils;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ElementTreeIteratorConditionTest
{

    public static String DEFAULT_DOCUMENT = """
                <library>
                    <book id="1"/>
                    <book/>
                    <book id="2"/>
                </library>
        """;

    @Test
    public void when()
    {
        // given
        Document document = XmlUtils.createDocument(DEFAULT_DOCUMENT);
        List<String> firstResult = new ArrayList<>();
        List<String> secondResult = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .when(e -> e.hasAttribute("id")).then(e -> firstResult.add(e.getLocalName()))
                .when(e -> e.hasAttribute("id")).then(e -> secondResult.add(e.getLocalName()))
                .execute()
        ;

        // then
        List<String> expectedResult = Arrays.asList("book", "book");
        assertEquals(expectedResult, firstResult);
        assertEquals(expectedResult, secondResult);
    }

    @Test
    public void whenNot()
    {
        // given
        Document document = XmlUtils.createDocument(DEFAULT_DOCUMENT);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenNot(e -> e.hasAttribute("id")).then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("library", "book"), result);
    }

    @Test
    public void always()
    {
        // given
        Document document = XmlUtils.createDocument(DEFAULT_DOCUMENT);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .always().then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("library", "book", "book", "book"), result);
    }

    @Test
    public void whenId()
    {
        // given
        Document document = XmlUtils.createDocument("""
            <library>
                <book1/>
                <book2/>
                <book1/>
            </library>                
        """);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenId("book1").then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("book1", "book1"), result);
    }

    @Test
    public void whenId_xmlWithNamespace()
    {
        // given
        Document document = XmlUtils.createDocument(
                "<m:document xmlns:m=\"http://my.namespace.com\">\n" +
                        "    <m:book1/>\n" +
                        "    <m:book2/>\n" +
                        "    <m:book1/>\n" +
                        "</m:document>"
        );
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenId("book1").then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("book1", "book1"), result);
    }
}
