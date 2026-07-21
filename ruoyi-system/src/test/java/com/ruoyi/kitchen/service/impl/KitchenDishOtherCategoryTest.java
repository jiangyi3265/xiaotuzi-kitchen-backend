package com.ruoyi.kitchen.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.kitchen.domain.KitchenCategory;
import com.ruoyi.kitchen.domain.KitchenDish;
import com.ruoyi.kitchen.mapper.KitchenCategoryMapper;
import com.ruoyi.kitchen.mapper.KitchenDishMapper;

@ExtendWith(MockitoExtension.class)
class KitchenDishOtherCategoryTest
{
    @Mock
    private KitchenDishMapper dishMapper;

    @Mock
    private KitchenCategoryMapper categoryMapper;

    @InjectMocks
    private KitchenDishServiceImpl service;

    @Test
    void acceptsActiveOtherRootCategory()
    {
        when(categoryMapper.selectKitchenCategoryById(11L)).thenReturn(category(11L, "私房菜", "0"));
        when(categoryMapper.selectKitchenCategoryById(900L)).thenReturn(category(900L, "同城", "0"));
        KitchenDish dish = dish(11L, "900");
        when(dishMapper.insertKitchenDish(dish)).thenReturn(1);

        assertEquals(1, service.insertKitchenDish(dish));
    }

    @Test
    void rejectsPrivateCategoryAsOtherPlacement()
    {
        when(categoryMapper.selectKitchenCategoryById(11L)).thenReturn(category(11L, "私房菜", "0"));
        when(categoryMapper.selectKitchenCategoryById(12L)).thenReturn(category(12L, "私房菜", "0"));

        assertThrows(ServiceException.class, () -> service.insertKitchenDish(dish(11L, "12")));
    }

    private static KitchenDish dish(Long categoryId, String otherCategoryId)
    {
        KitchenDish dish = new KitchenDish();
        dish.setDishName("测试菜品");
        dish.setCategoryId(categoryId);
        dish.setTodayType(otherCategoryId);
        return dish;
    }

    private static KitchenCategory category(Long id, String displayArea, String status)
    {
        KitchenCategory category = new KitchenCategory();
        category.setId(id);
        category.setParentId(0L);
        category.setDisplayArea(displayArea);
        category.setStatus(status);
        return category;
    }
}
