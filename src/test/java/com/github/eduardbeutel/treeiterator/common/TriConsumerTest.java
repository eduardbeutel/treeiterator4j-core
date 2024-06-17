package com.github.eduardbeutel.treeiterator.common;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TriConsumerTest
{

    public class BufferConsumer implements TriConsumer<String,String,String>
    {
        public static String buffer;

        @Override
        public void accept(String s1, String s2, String s3)
        {
            buffer += s1 + s2 + s3;
        }
    }

    @Before
    public void cleanup()
    {
        BufferConsumer.buffer = "";
    }

    @Test
    public void accept()
    {
        BufferConsumer c1 = new BufferConsumer();

        c1.accept("1","2","3");

        assertEquals("123",BufferConsumer.buffer);
    }

    @Test
    public void andThen()
    {
        BufferConsumer c1 = new BufferConsumer();
        BufferConsumer c2 = new BufferConsumer();

        c1.andThen(c2).accept("1","2","3");

        assertEquals("123123",BufferConsumer.buffer);
    }

}
