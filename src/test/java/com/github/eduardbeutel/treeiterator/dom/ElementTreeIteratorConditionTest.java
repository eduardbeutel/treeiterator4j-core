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
        Document document = XmlUtils.createDocument("""
                <m:document xmlns:m="http://my.namespace.com">
                    <m:book1/>
                    <m:book2/>
                    <m:book1/>
                </m:document>
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
    public void whenPath()
    {
        // given
        Document document = XmlUtils.createDocument("""
                <library>
                    <book>
                        <title />
                        <author />
                        <author />
                        <author />
                    </book>
                </library>
        """);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenPath("/library/book/author").then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("author", "author", "author"), result);
    }

    @Test
    public void whenPath_rootElement()
    {
        // given
        Document document = XmlUtils.createDocument(DEFAULT_DOCUMENT);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenPath("/library").then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("library"), result);
    }

    @Test
    public void whenIdMatches()
    {
        // given
        Document document = XmlUtils.createDocument("""
                <library>
                    <my-book/>
                    <book/>
                    <not-a-Book/>
                </library>        
        """);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenIdMatches(".*book.*").then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("my-book", "book"), result);
    }

    @Test
    public void whenPathMatches()
    {
        // given
        Document document = XmlUtils.createDocument("""
                <library>
                    <book>
                        <title />
                        <author />
                        <author />
                        <author />
                    </book>
                </library>                
        """);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenPathMatches("/.*/author").then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("author", "author", "author"), result);
    }

    @Test
    public void whenRoot()
    {
        // given
        Document document = XmlUtils.createDocument("""
                <library>
                    <book>
                        <title />
                    </book>
                </library>                
        """);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenRoot().then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("library"), result);
    }

    @Test
    public void whenLeaf()
    {
        // given
        Document document = XmlUtils.createDocument("""
                <library>
                    <book>
                        <title />
                        <author />
                    </book>
                </library>                
        """);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenLeaf().then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("title", "author"), result);
    }

    @Test
    public void whenNotLeaf()
    {
        // given
        Document document = XmlUtils.createDocument("""
                <library>
                    <book>
                        <title />
                        <author />
                    </book>
                </library>                
        """);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenNotLeaf().then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("library", "book"), result);
    }

    @Test
    public void and()
    {
        // given
        Document document = XmlUtils.createDocument("""
                <library>
                    <book>
                        <title />
                        <author />
                    </book>
                    <book/>
                </library>                
        """);
        List<String> firstResult = new ArrayList<>();
        List<String> secondResult = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenLeaf().and().whenIdMatches(".*aut.*").then(e -> firstResult.add(e.getLocalName()))
                .whenId("book").and().whenNot(e -> e.hasAttribute("id")).and().whenNotLeaf().then(e -> secondResult.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("author"), firstResult);
        assertEquals(Arrays.asList("book"), secondResult);
    }

}
