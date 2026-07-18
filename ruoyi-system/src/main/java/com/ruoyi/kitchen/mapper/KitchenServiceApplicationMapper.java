package com.ruoyi.kitchen.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.kitchen.domain.KitchenServiceApplication;

public interface KitchenServiceApplicationMapper {
    List<KitchenServiceApplication> selectList(KitchenServiceApplication query);
    KitchenServiceApplication selectById(Long id);
    KitchenServiceApplication selectByIdForUpdate(Long id);
    KitchenServiceApplication selectLatestByUserAndType(@Param("wxUserId") Long wxUserId, @Param("applicationType") String applicationType);
    int insert(KitchenServiceApplication application);
    int audit(@Param("id") Long id, @Param("auditStatus") String auditStatus, @Param("remark") String remark, @Param("providerId") Long providerId);
    int insertRiderProvider(Map<String,Object> provider);
    int insertChefProvider(Map<String,Object> provider);
}
