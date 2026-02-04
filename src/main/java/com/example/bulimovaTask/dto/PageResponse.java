package com.example.bulimovaTask.dto;

import java.util.List;

public class PageResponse<T> {

    private List<T> content;
    private long total;

    public PageResponse(List<T> content, long total) {
        this.content = content;
        this.total = total;
    }

    public List<T> getContent() {
        return content;
    }

    public long getTotal() {
        return total;
    }
}