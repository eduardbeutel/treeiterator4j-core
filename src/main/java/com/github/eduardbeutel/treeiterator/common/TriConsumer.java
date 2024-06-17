package com.github.eduardbeutel.treeiterator.common;

@FunctionalInterface
public interface TriConsumer<S, T, U>
{
    void accept(S s, T t, U u);

    default TriConsumer<S, T, U> andThen(TriConsumer<? super S,? super T, ? super U> after)
    {
        return new TriConsumerComposite<>(this,after);
    }

}
