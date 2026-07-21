package com.ruoyi.kitchen.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.kitchen.domain.KitchenCategory;
import com.ruoyi.kitchen.mapper.KitchenCategoryMapper;

@ExtendWith(MockitoExtension.class)
class KitchenCategoryDynamicAreaTest
{
    @Mock
    private KitchenCategoryMapper mapper;

    @InjectMocks
    private KitchenCategoryServiceImpl service;

    @Test
    void createsOtherRootCategoryForDynamicOtherTabs()
    {
        KitchenCategory category = new KitchenCategory();
        category.setCatName("同城美食");
        category.setParentId(0L);
        category.setDisplayArea("同城");
        when(mapper.insertKitchenCategory(category)).thenReturn(1);

        assertEquals(1, service.insertKitchenCategory(category));
        assertEquals("同城", category.getDisplayArea());
        assertEquals(1, category.getCatLevel());
        assertEquals("0", category.getAncestors());
        verify(mapper).insertKitchenCategory(category);
    }

    @Test
    void childCategoryInheritsParentDisplayArea()
    {
        KitchenCategory parent = new KitchenCategory();
        parent.setId(9L);
        parent.setParentId(0L);
        parent.setDisplayArea("同城");
        parent.setStatus("0");
        when(mapper.selectKitchenCategoryById(9L)).thenReturn(parent);

        KitchenCategory category = new KitchenCategory();
        category.setCatName("夜宵");
        category.setParentId(9L);
        category.setDisplayArea("0");
        when(mapper.insertKitchenCategory(category)).thenReturn(1);

        service.insertKitchenCategory(category);

        assertEquals("同城", category.getDisplayArea());
        assertEquals(2, category.getCatLevel());
        assertEquals("0,9", category.getAncestors());
    }
}
