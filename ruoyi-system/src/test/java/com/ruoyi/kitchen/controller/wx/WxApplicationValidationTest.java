package com.ruoyi.kitchen.controller.wx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.lenient;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.kitchen.domain.KitchenRegionApplication;
import com.ruoyi.kitchen.domain.KitchenServiceApplication;
import com.ruoyi.kitchen.mapper.KitchenRegionApplicationMapper;
import com.ruoyi.kitchen.mapper.KitchenServiceApplicationMapper;
import com.ruoyi.kitchen.mapper.KitchenWxUserMapper;
import com.ruoyi.kitchen.util.WxTokenService;

@ExtendWith(MockitoExtension.class)
class WxApplicationValidationTest
{
    @Mock private KitchenServiceApplicationMapper serviceMapper;
    @Mock private KitchenRegionApplicationMapper regionMapper;
    @Mock private KitchenWxUserMapper wxUserMapper;
    @Mock private WxTokenService tokenService;
    @Mock private HttpServletRequest request;

    private WxServiceApplicationController serviceController;
    private WxRegionApplicationController regionController;

    @BeforeEach
    void setUp()
    {
        serviceController = new WxServiceApplicationController();
        ReflectionTestUtils.setField(serviceController, "mapper", serviceMapper);
        ReflectionTestUtils.setField(serviceController, "wxUserMapper", wxUserMapper);
        ReflectionTestUtils.setField(serviceController, "tokenService", tokenService);
        regionController = new WxRegionApplicationController();
        ReflectionTestUtils.setField(regionController, "mapper", regionMapper);
        ReflectionTestUtils.setField(regionController, "wxUserMapper", wxUserMapper);
        ReflectionTestUtils.setField(regionController, "tokenService", tokenService);
        lenient().when(tokenService.getRequiredUserId(request)).thenReturn(7L);
    }

    @Test
    void serviceApplicationRejectsMissingRoleSpecificInformationBeforeWriting()
    {
        KitchenServiceApplication application = new KitchenServiceApplication();
        application.setApplicationType("0");
        application.setApplicantName("张三");
        application.setPhone("13800138000");
        application.setProvince("四川省");
        application.setCity("成都市");
        application.setDistrict("武侯区");

        AjaxResult result = serviceController.apply(application, request);

        assertEquals("请填写配送交通工具", result.get("msg"));
        verifyNoInteractions(serviceMapper, wxUserMapper);
    }

    @Test
    void regionApplicationRejectsInvalidPhoneBeforeWriting()
    {
        KitchenRegionApplication application = new KitchenRegionApplication();
        application.setApplicantName("张三");
        application.setPhone("123");
        application.setProvince("四川省");
        application.setCity("成都市");
        application.setDistrict("武侯区");

        AjaxResult result = regionController.apply(application, request);

        assertEquals("请输入正确的11位手机号", result.get("msg"));
        verifyNoInteractions(regionMapper, wxUserMapper);
    }

    @Test
    void concurrentApplicationChecksUseReadCommittedIsolation() throws Exception
    {
        Transactional serviceTx = WxServiceApplicationController.class
                .getMethod("apply", KitchenServiceApplication.class, HttpServletRequest.class)
                .getAnnotation(Transactional.class);
        Transactional regionTx = WxRegionApplicationController.class
                .getMethod("apply", KitchenRegionApplication.class, HttpServletRequest.class)
                .getAnnotation(Transactional.class);

        assertEquals(Isolation.READ_COMMITTED, serviceTx.isolation());
        assertEquals(Isolation.READ_COMMITTED, regionTx.isolation());
    }
}
