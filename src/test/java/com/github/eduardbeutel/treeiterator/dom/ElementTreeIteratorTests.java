package com.github.eduardbeutel.treeiterator.dom;

import com.github.eduardbeutel.treeiterator.test.XmlUtils;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ElementTreeIteratorTests
{

    public static String DEFAULT_DOCUMENT = """
                <library id="l1">
                    <book id="b1" />
                    <book/>
                    <book id="b2" />
                </library>
        """;

    @Test
    public void topDown()
    {
        // given
        Document document = XmlUtils.createDocument(DEFAULT_DOCUMENT);
        List<String> ids = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .when(e -> e.hasAttribute("id")).then(e -> ids.add(e.getAttribute("id").toString()))
                .execute()
        ;

        // then
        List<String> expected = Arrays.asList("l1", "b1", "b2");
        assertEquals(expected, ids);
    }

    @Test
    public void bottomUp_simple()
    {
        // given
        Document document = XmlUtils.createDocument(DEFAULT_DOCUMENT);
        List<String> ids = new ArrayList<>();

        // when
        ElementTreeIterator.bottomUp(document)
                .when(e -> e.hasAttribute("id")).then(e -> ids.add(e.getAttribute("id").toString()))
                .execute()
        ;

        // then
        List<String> expected = Arrays.asList("b1", "b2", "l1");
        assertEquals(expected, ids);
    }

    @Test
    public void bottomUp_complex()
    {
        // given
        Document document = XmlUtils.createDocument("""
                <library>
                    <books>
                        <book1>
                            <title1/>
                            <author1/>
                        </book1>
                        <book2>
                            <title2/>
                            <author2/>
                        </book2>
                    </books>  \s
                    <newspapers>
                        <newspaper1>
                            <name1/>
                            <date1/>
                        </newspaper1>
                        <newspaper2>
                            <name2/>
                            <date2/>
                        </newspaper2>
                    </newspapers>
                </library>
        """);
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.bottomUp(document)
                .always().then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList(
                "title1", "author1", "book1", "title2", "author2", "book2", "books",
                "name1", "date1", "newspaper1", "name2", "date2", "newspaper2", "newspapers",
                "library"
        ), result);
    }
}
