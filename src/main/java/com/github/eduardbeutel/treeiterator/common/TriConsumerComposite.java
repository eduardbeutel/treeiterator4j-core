package com.github.eduardbeutel.treeiterator.common;

public class TriConsumerComposite<S, T, U> implements TriConsumer<S, T, U>
{

    private TriConsumer<S, T, U> first;
    private TriConsumer<? super S, ? super T, ? super U> second;

    public TriConsumerComposite(TriConsumer<S, T, U> first, TriConsumer<? super S, ? super T, ? super U> second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public void accept(S s, T t, U u)
    {
        first.accept(s,t,u);
        second.accept(s,t,u);
    }

}
