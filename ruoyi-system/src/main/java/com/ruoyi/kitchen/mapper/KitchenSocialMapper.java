package com.ruoyi.kitchen.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface KitchenSocialMapper {
    int insertGroupRoom(Map<String,Object> p);
    int insertGroupMember(Map<String,Object> p);
    Map<String,Object> selectGroupRoomByCode(String roomCode);
    Map<String,Object> selectGroupRoomByCodeForUpdate(String roomCode);
    Map<String,Object> selectGroupRoomById(Long id);
    Map<String,Object> selectGroupRoomByIdForUpdate(Long id);
    Map<String,Object> selectGroupRoomAnyById(Long id);
    int countGroupMember(@Param("roomId") Long roomId,@Param("userId") Long userId);
    List<Map<String,Object>> selectGroupMembers(Long roomId);
    List<Map<String,Object>> selectGroupItems(Long roomId);
    List<Map<String,Object>> selectGroupPurchasedItems(Long roomId);
    List<Map<String,Object>> selectMyGroupRooms(Long userId);
    int upsertGroupItem(Map<String,Object> p);
    int changeGroupItemQuantity(Map<String,Object> p);
    int deleteEmptyGroupItem(@Param("roomId") Long roomId,@Param("dishId") Long dishId);
    int clearGroupItems(Long roomId);
    int finishGroup(@Param("id") Long id,@Param("ownerUserId") Long ownerUserId);
    Map<String,Object> selectCoupleByUser(Long userId);
    Map<String,Object> selectCoupleByUserForUpdate(Long userId);
    Map<String,Object> selectCoupleByCode(String inviteCode);
    int insertCouple(Map<String,Object> p);
    int bindCouplePartner(@Param("id") Long id,@Param("userId") Long userId);
    int unbindCouple(@Param("id") Long id,@Param("userId") Long userId);
    List<Map<String,Object>> selectCoupleItems(Long coupleId);
    int upsertCoupleItem(Map<String,Object> p);
    int deleteCoupleItem(@Param("coupleId") Long coupleId,@Param("dishId") Long dishId,@Param("userId") Long userId);
    int clearCoupleItems(Long coupleId);
    List<Map<String,Object>> selectAnniversaries(Long coupleId);
    int insertAnniversary(Map<String,Object> p);
    int addFeedCount(Long id);
    List<Map<String,Object>> selectCoupleLeaderboard();
    List<Map<String,Object>> selectAdminGroupRooms(Map<String,Object> p);
    List<Map<String,Object>> selectAdminCoupleSpaces(Map<String,Object> p);
    int updateGroupStatus(@Param("id") Long id,@Param("status") String status);
    int updateCoupleStatus(@Param("id") Long id,@Param("status") String status);
    int insertNotification(Map<String,Object> p);
    List<Map<String,Object>> selectNotifications(Long userId);
    int countUnreadNotifications(Long userId);
    int readNotifications(Long userId);
}
