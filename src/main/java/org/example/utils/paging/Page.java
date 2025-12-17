package org.example.utils.paging;

import java.util.List;

public class Page<E> {
    private List<E> elementsOnPage;
    private int totalElemenCount;

    public Page(List<E> elementsOnPage, int totalElemenCount) {
        this.elementsOnPage = elementsOnPage;
        this.totalElemenCount = totalElemenCount;
    }

    public List<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getTotalElemenCount() {
        return totalElemenCount;
    }
}
