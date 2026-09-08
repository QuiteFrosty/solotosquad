package com.communitytrials.gui;

import java.util.List;

public final class Pagination {

    public static final int ITEMS_PER_PAGE = 45;
    public static final int PREV_SLOT = 48;
    public static final int PAGE_INFO_SLOT = 49;
    public static final int NEXT_SLOT = 50;
    public static final int BACK_SLOT = 45;

    private Pagination() {
    }

    public static <T> List<T> slice(List<T> items, int page) {
        int from = Math.min(page * ITEMS_PER_PAGE, items.size());
        int to = Math.min(from + ITEMS_PER_PAGE, items.size());
        return items.subList(from, to);
    }

    public static int pageCount(int totalItems) {
        return Math.max(1, (int) Math.ceil(totalItems / (double) ITEMS_PER_PAGE));
    }
}
