package com.ruoyi.common.core.page;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class TableSupportTest
{
    @AfterEach
    void clearRequest()
    {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void clampsInvalidOrExcessivePagination()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(TableSupport.PAGE_NUM, "-2");
        request.setParameter(TableSupport.PAGE_SIZE, "1000000");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        PageDomain page = TableSupport.getPageDomain();

        assertEquals(1, page.getPageNum());
        assertEquals(TableSupport.MAX_PAGE_SIZE, page.getPageSize());
    }
}
