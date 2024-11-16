package com.mycompany.myapp.util;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface PageUtils {
    static <T> Page<T> createPageFromList(List<T> list, Pageable pageable) {
        if (list == null) {
            throw new IllegalArgumentException("To create a Page, the list mustn't be null!");
        }

        int startOfPage = pageable.getPageNumber() * pageable.getPageSize();
        if (startOfPage > list.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        int endOfPage = Math.min(startOfPage + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(startOfPage, endOfPage), pageable, list.size());
    }

    static com.baomidou.mybatisplus.extension.plugins.pagination.Page fromPageRequest(Pageable pageable) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page page =
                com.baomidou.mybatisplus.extension.plugins.pagination.Page.of(
                        pageable.getPageNumber() + 1, pageable.getPageSize(), true);
        page.setOptimizeCountSql(false);

        if (pageable.getSort() != null && pageable.getSort().isSorted()) {
            page.setOrders(pageable.getSort().stream()
                    .map(order -> order.getDirection().equals(Sort.Direction.ASC)
                            ? OrderItem.asc(order.getProperty())
                            : OrderItem.desc(order.getProperty()))
                    .collect(Collectors.toList()));
        }
        return page;
    }
}
