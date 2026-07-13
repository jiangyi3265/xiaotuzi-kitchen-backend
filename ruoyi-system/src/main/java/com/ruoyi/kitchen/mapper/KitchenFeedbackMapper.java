package com.ruoyi.kitchen.mapper;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenFeedback;

public interface KitchenFeedbackMapper {
    List<KitchenFeedback> selectList(KitchenFeedback query);
    List<KitchenFeedback> selectMy(Long wxUserId);
    int insert(KitchenFeedback feedback);
    int handle(KitchenFeedback feedback);
}
