package com.github.eduardbeutel.treeiterator.dom;

import com.github.eduardbeutel.treeiterator.common.UnsupportedFeatureException;
import com.github.eduardbeutel.treeiterator.test.XmlUtils;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;
import static org.junit.Assert.assertTrue;

public class ElementTreeIteratorOperationTest
{

    public static String DOC1 = """
                <library>
                    <book id="1"/>
                    <book/>
                    <book id="2"/>
                </library>
        """;

    public static String DOC2 = """
                <library>
                    <book>
                        <title />
                        <author />
                    </book>
                </library>
        """;

    @Test
    public void stop()
    {
        // given
        Document document = XmlUtils.createDocument(DOC2);
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
        Document document = XmlUtils.createDocument(DOC1);
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
        Document document = XmlUtils.createDocument(DOC1);

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
        Document document = XmlUtils.createDocument(DOC1);

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
        Document document = XmlUtils.createDocument(DOC1);

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

    @Test
    public void skip()
    {
        // given
        Document document = XmlUtils.createDocument("""
                <library>
                    <book1>
                        <title1 />
                    </book1>
                    <book2>
                        <title2 />
                    </book2>
                    <book3>
                        <title3 />
                    </book3>
                </library>                
        """);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenId("book2").skip()
                .always().then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("library", "book1", "title1", "book3", "title3"), result);
    }

    @Test(expected = UnsupportedFeatureException.class)
    public void skip_usedInBottomUpMode_throwsException()
    {
        // given
        Document document = XmlUtils.createDocument(DOC1);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.bottomUp(document)
                .whenId("book").skip()
                .always().then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then -> UnsupportedFeatureException
    }

    @Test
    public void remove_topDown()
    {
        // given
        Document document = XmlUtils.createDocument(DOC2);

        // when
        ElementTreeIterator.topDown(document)
                .whenId("book").remove()
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument(
                "<library></library>"
        );
        assertThat(document, isIdenticalTo(expectedDocument).ignoreWhitespace());
    }

    @Test
    public void remove_bottomUp()
    {
        // given
        Document document = XmlUtils.createDocument("""
                <library>
                    <books>
                        <book>
                            <title />
                            <author />
                        </book>
                        <book>
                            <title />
                            <author />
                        </book>
                    </books>
                </library>
        """);

        // when
        ElementTreeIterator.bottomUp(document)
                .whenId("book").remove()
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument(
                "<library><books></books></library>"
        );
        assertThat(document, isIdenticalTo(expectedDocument).ignoreWhitespace());
    }

    @Test(expected = UnsupportedFeatureException.class)
    public void removeRoot_indirectly_throwsException()
    {
        // given
        Document document = XmlUtils.createDocument(DOC1);

        // when
        ElementTreeIterator.topDown(document)
                .whenId("library").remove()
                .execute()
        ;

        // then -> UnsupportedFeatureException
    }

    @Test(expected = UnsupportedFeatureException.class)
    public void removeRoot_directly_throwsException()
    {
        // given
        Document document = XmlUtils.createDocument(DOC1);

        // when
        ElementTreeIterator.topDown(document)
                .whenRoot().remove()
                .execute()
        ;

        // then -> UnsupportedFeatureException
    }

    @Test
    public void replace()
    {
        // given
        Document document = XmlUtils.createDocument(DOC2);
        Element replacement = document.createElement("anotherBook");

        // when
        ElementTreeIterator.topDown(document)
                .whenId("book").replace(replacement)
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument(
                "<library><anotherBook /></library>"
        );
        assertThat(document, isIdenticalTo(expectedDocument).ignoreWhitespace());
    }

    @Test
    public void replace_root()
    {
        // given
        Document document = XmlUtils.createDocument(DOC2);
        Element replacement = document.createElement("anotherBook");

        // when
        ElementTreeIterator.topDown(document)
                .whenId("library").replace(() -> replacement)
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument(
                "<anotherBook />"
        );
        assertThat(document, isIdenticalTo(expectedDocument).ignoreWhitespace());
    }

    @Test
    public void replace_nodeArgument()
    {
        // given
        Document document = XmlUtils.createDocument(DOC2);
        Element replacement = document.createElement("anotherBook");

        // when
        ElementTreeIterator.topDown(document)
                .whenId("book").replace((node) ->
                {
                    replacement.setTextContent(node.getLocalName());
                    return replacement;
                })
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument(
                "<library><anotherBook>book</anotherBook></library>"
        );
        assertThat(document, isIdenticalTo(expectedDocument).ignoreWhitespace());
    }

    @Test
    public void replace_nodeAndIdArgument()
    {
        // given
        Document document = XmlUtils.createDocument(DOC2);
        Element replacement = document.createElement("anotherBook");

        // when
        ElementTreeIterator.topDown(document)
                .whenId("book").replace((node, id) ->
                {
                    replacement.setTextContent(id + "/" + node.getLocalName());
                    return replacement;
                })
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument(
                "<library><anotherBook>book/book</anotherBook></library>"
        );
        assertThat(document, isIdenticalTo(expectedDocument).ignoreWhitespace());
    }

    @Test
    public void replace_nodeAndIdAndPathArgument()
    {
        // given
        Document document = XmlUtils.createDocument(DOC2);
        Element replacement = document.createElement("anotherBook");

        // when
        ElementTreeIterator.topDown(document)
                .whenId("book").replace((node, id, path) ->
                {
                    replacement.setTextContent(id + "/" + node.getLocalName() + path);
                    return replacement;
                })
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument(
                "<library><anotherBook>book/book/library/book</anotherBook></library>"
        );
        assertThat(document, isIdenticalTo(expectedDocument).ignoreWhitespace());
    }

    @Test
    public void set()
    {
        // given
        Document document = XmlUtils.createDocument(DOC2);
        AtomicReference<Element> ref = new AtomicReference<Element>();

        // when
        ElementTreeIterator.topDown(document)
                .whenLeaf().set(ref)
                .execute()
        ;

        // then
        assertEquals("author", ref.get().getLocalName());
    }

    @Test
    public void collect()
    {
        // given
        Document document = XmlUtils.createDocument(DOC2);
        List<Element> leafs = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenLeaf().collect(leafs)
                .execute()
        ;

        // then
        assertEquals(2, leafs.size());
        assertEquals("title", leafs.get(0).getLocalName());
        assertEquals("author", leafs.get(1).getLocalName());
    }

    @Test
    public void collectById()
    {
        // given
        Document document = XmlUtils.createDocument(DOC2);
        Map<String, Element> elements = new LinkedHashMap<>();

        // when
        ElementTreeIterator.topDown(document)
                .always().collectById(elements)
                .execute()
        ;

        // then
        assertEquals(4, elements.size());
        Arrays.asList("library", "book", "title", "author")
                .forEach(item -> assertEquals(item, elements.get(item).getLocalName()));
    }

    @Test
    public void collectByPath()
    {
        // given
        Document document = XmlUtils.createDocument(DOC2);
        Map<String, Element> elements = new LinkedHashMap<>();

        // when
        ElementTreeIterator.topDown(document)
                .always().collectByPath(elements)
                .execute()
        ;

        // then
        assertEquals(4, elements.size());
        assertEquals("library", elements.get("/library").getLocalName());
        assertEquals("book", elements.get("/library/book").getLocalName());
        assertEquals("title", elements.get("/library/book/title").getLocalName());
        assertEquals("author", elements.get("/library/book/author").getLocalName());
    }

}
