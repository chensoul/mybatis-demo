package io.github.rosestack.myapp.util;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.stream.Collectors;

public interface PageUtils {
    static com.baomidou.mybatisplus.extension.plugins.pagination.Page fromPageable(Pageable pageable) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page page =
                com.baomidou.mybatisplus.extension.plugins.pagination.Page.of(
                        pageable.getPageNumber() + 1, pageable.getPageSize(), true);
        page.setOptimizeCountSql(false);

        if (pageable.getSort().isSorted()) {
            page.setOrders(pageable.getSort().stream()
                    .map(order -> order.getDirection().equals(Sort.Direction.ASC)
                            ? OrderItem.asc(order.getProperty())
                            : OrderItem.desc(order.getProperty()))
                    .collect(Collectors.toList()));
        }
        return page;
    }
}
