/*
 * ====================================================================
 * 龙果学院： www.roncoo.com （微信公众号：RonCoo_com）
 * 超级教程系列：《微服务架构的分布式事务解决方案》视频教程
 * 讲师：吴水成（水到渠成），840765167@qq.com
 * 课程地址：http://www.roncoo.com/course/view/7ae3d7eddc4742f78b0548aa8bd9ccdb
 * ====================================================================
 */
package com.roncoo.pay.service.user.api.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roncoo.pay.common.core.enums.PayWayEnum;
import com.roncoo.pay.common.core.enums.PublicEnum;
import com.roncoo.pay.common.core.enums.PublicStatusEnum;
import com.roncoo.pay.common.core.page.PageBean;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.common.core.utils.StringUtil;
import com.roncoo.pay.service.user.api.RpPayProductService;
import com.roncoo.pay.service.user.api.RpPayWayService;
import com.roncoo.pay.service.user.api.RpUserPayConfigService;
import com.roncoo.pay.service.user.api.RpUserPayInfoService;
import com.roncoo.pay.service.user.dao.RpUserPayConfigDao;
import com.roncoo.pay.service.user.entity.RpPayProduct;
import com.roncoo.pay.service.user.entity.RpPayWay;
import com.roncoo.pay.service.user.entity.RpUserPayConfig;
import com.roncoo.pay.service.user.entity.RpUserPayInfo;
import com.roncoo.pay.service.user.exceptions.PayBizException;

/**
 * @类功能说明： 用户支付配置service实现类
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：广州领课网络科技有限公司（龙果学院：www.roncoo.com）
 * @作者：zh
 * @创建时间：2016-5-18 上午11:14:10
 * @版本：V1.0
 */
@Service("rpUserPayConfigService")
public class RpUserPayConfigServiceImpl implements RpUserPayConfigService{

	@Autowired
	private RpUserPayConfigDao rpUserPayConfigDao;
	@Autowired
	private RpPayProductService rpPayProductService;
	@Autowired
	private RpPayWayService rpPayWayService;
	@Autowired
	private RpUserPayInfoService rpUserPayInfoService;
	
	@Override
	public void saveData(RpUserPayConfig rpUserPayConfig) {
		rpUserPayConfigDao.insert(rpUserPayConfig);
	}

	@Override
	public void updateData(RpUserPayConfig rpUserPayConfig) {
		rpUserPayConfigDao.update(rpUserPayConfig);
	}

	@Override
	public RpUserPayConfig getDataById(String id) {
		return rpUserPayConfigDao.getById(id);
	}

	@Override
	public PageBean listPage(PageParam pageParam, RpUserPayConfig rpUserPayConfig) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("productCode", rpUserPayConfig.getProductCode());
		paramMap.put("userNo", rpUserPayConfig.getUserNo());
		paramMap.put("userName", rpUserPayConfig.getUserName());
		paramMap.put("productName", rpUserPayConfig.getProductName());
		paramMap.put("status", PublicStatusEnum.ACTIVE.name());
		return rpUserPayConfigDao.listPage(pageParam, paramMap);
	}

	/**
	 * 根据商户编号获取已生效的支付配置
	 *
	 * @param userNo
	 * @return
	 */
	@Override
	public RpUserPayConfig getByUserNo(String userNo) {
		return rpUserPayConfigDao.getByUserNo(userNo, PublicEnum.YES.name());
	}
	
	/**
	 * 根据商户编号获取支付配置
	 * @param userNo
	 * @param auditStatus
	 * @return
	 */
	@Override
	public RpUserPayConfig getByUserNo(String userNo, String auditStatus){
		return rpUserPayConfigDao.getByUserNo(userNo, auditStatus);
	}
	
	
	/**
	 * 根据支付产品获取已生效数据
	 */
	@Override
	public List<RpUserPayConfig> listByProductCode(String productCode){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("productCode", productCode);
		paramMap.put("status", PublicStatusEnum.ACTIVE.name());
		paramMap.put("auditStatus", PublicEnum.YES.name());
		return rpUserPayConfigDao.listBy(paramMap);
	}
	
	/**
	 * 根据支付产品获取数据
	 */
	@Override
	public List<RpUserPayConfig> listByProductCode(String productCode, String auditStatus){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("productCode", productCode);
		paramMap.put("status", PublicStatusEnum.ACTIVE.name());
		paramMap.put("auditStatus", auditStatus);
		return rpUserPayConfigDao.listBy(paramMap);
	}
	
	/**
	 * 创建用户支付配置
	 */
	@Override
	public void createUserPayConfig(String userNo, String userName, String productCode, String productName, Integer riskDay,
			String fundIntoType, String isAutoSett, String appId, String merchantId, String partnerKey,
			String ali_partner, String ali_sellerId, String ali_key)  throws PayBizException{
		
		RpUserPayConfig payConfig = rpUserPayConfigDao.getByUserNo(userNo, null);
		if(payConfig != null){
			throw new PayBizException(PayBizException.USER_PAY_CONFIG_IS_EXIST,"用户支付配置已存在");
		}
		
		RpUserPayConfig rpUserPayConfig = new RpUserPayConfig();
		rpUserPayConfig.setUserNo(userNo);
		rpUserPayConfig.setUserName(userName);
		rpUserPayConfig.setProductCode(productCode);
		rpUserPayConfig.setProductName(productName);
		rpUserPayConfig.setStatus(PublicStatusEnum.ACTIVE.name());
		rpUserPayConfig.setAuditStatus(PublicEnum.YES.name());
		rpUserPayConfig.setRiskDay(riskDay);
		rpUserPayConfig.setFundIntoType(fundIntoType);
		rpUserPayConfig.setIsAutoSett(isAutoSett);
		rpUserPayConfig.setPayKey(StringUtil.get32UUID());
		rpUserPayConfig.setPaySecret(StringUtil.get32UUID());
		rpUserPayConfig.setId(StringUtil.get32UUID());
		saveData(rpUserPayConfig);
		
		//查询支付产品下有哪些支付方式
		List<RpPayWay> payWayList = rpPayWayService.listByProductCode(productCode);
		Map<String, String> map = new HashMap<String, String>();
		//过滤重复数据
		for(RpPayWay payWay : payWayList){
	        map.put(payWay.getPayWayCode(), payWay.getPayWayName());
		}
		
		for (String key : map.keySet()) {
			if(key.equals(PayWayEnum.WEIXIN.name())){
				//创建用户第三方支付信息
				RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(userNo, PayWayEnum.WEIXIN.name());
				if(rpUserPayInfo == null){
					rpUserPayInfo = new RpUserPayInfo();
					rpUserPayInfo.setId(StringUtil.get32UUID());
					rpUserPayInfo.setCreateTime(new Date());
					rpUserPayInfo.setAppId(appId);
					rpUserPayInfo.setMerchantId(merchantId);
					rpUserPayInfo.setPartnerKey(partnerKey);
					rpUserPayInfo.setPayWayCode(PayWayEnum.WEIXIN.name());
					rpUserPayInfo.setPayWayName(PayWayEnum.WEIXIN.getDesc());
					rpUserPayInfo.setUserNo(userNo);
					rpUserPayInfo.setUserName(userName);
					rpUserPayInfo.setStatus(PublicStatusEnum.ACTIVE.name());
					rpUserPayInfoService.saveData(rpUserPayInfo);
				}else{
					rpUserPayInfo.setEditTime(new Date());
					rpUserPayInfo.setAppId(appId);
					rpUserPayInfo.setMerchantId(merchantId);
					rpUserPayInfo.setPartnerKey(partnerKey);
					rpUserPayInfo.setPayWayCode(PayWayEnum.WEIXIN.name());
					rpUserPayInfo.setPayWayName(PayWayEnum.WEIXIN.getDesc());
					rpUserPayInfoService.updateData(rpUserPayInfo);
				}
				
			}else if(key.equals(PayWayEnum.ALIPAY.name())){
				//创建用户第三方支付信息
				RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(userNo, PayWayEnum.ALIPAY.name());
				if(rpUserPayInfo == null){
					rpUserPayInfo = new RpUserPayInfo();
					rpUserPayInfo.setId(StringUtil.get32UUID());
					rpUserPayInfo.setCreateTime(new Date());
					rpUserPayInfo.setAppId(ali_partner);
					rpUserPayInfo.setMerchantId(ali_sellerId);
					rpUserPayInfo.setPartnerKey(ali_key);
					rpUserPayInfo.setPayWayCode(PayWayEnum.ALIPAY.name());
					rpUserPayInfo.setPayWayName(PayWayEnum.ALIPAY.getDesc());
					rpUserPayInfo.setUserNo(userNo);
					rpUserPayInfo.setUserName(userName);
					rpUserPayInfo.setStatus(PublicStatusEnum.ACTIVE.name());
					rpUserPayInfoService.saveData(rpUserPayInfo);
				}else{
					rpUserPayInfo.setEditTime(new Date());
					rpUserPayInfo.setAppId(ali_partner);
					rpUserPayInfo.setMerchantId(ali_sellerId);
					rpUserPayInfo.setPartnerKey(ali_key);
					rpUserPayInfo.setPayWayCode(PayWayEnum.ALIPAY.name());
					rpUserPayInfo.setPayWayName(PayWayEnum.ALIPAY.getDesc());
					rpUserPayInfoService.updateData(rpUserPayInfo);
				}
			}
		}
		
		
		
	}
	
	/**
	 * 删除支付产品
	 * @param userNo
	 */
	@Override
	public void deleteUserPayConfig(String userNo) throws PayBizException{
		
		RpUserPayConfig rpUserPayConfig = rpUserPayConfigDao.getByUserNo(userNo, null);
		if(rpUserPayConfig == null){
			throw new PayBizException(PayBizException.USER_PAY_CONFIG_IS_NOT_EXIST,"用户支付配置不存在");
		}
		
		rpUserPayConfig.setStatus(PublicStatusEnum.UNACTIVE.name());
		rpUserPayConfig.setEditTime(new Date());
		updateData(rpUserPayConfig);
	}
	
	/**
	 * 修改用户支付配置
	 */
	@Override
	public void updateUserPayConfig(String userNo, String productCode, String productName, Integer riskDay, String fundIntoType,
			String isAutoSett, String appId, String merchantId, String partnerKey,
			String ali_partner, String ali_sellerId, String ali_key)  throws PayBizException{
		RpUserPayConfig rpUserPayConfig = rpUserPayConfigDao.getByUserNo(userNo, null);
		if(rpUserPayConfig == null){
			throw new PayBizException(PayBizException.USER_PAY_CONFIG_IS_NOT_EXIST,"用户支付配置不存在");
		}
		
		rpUserPayConfig.setProductCode(productCode);
		rpUserPayConfig.setProductName(productName);
		rpUserPayConfig.setRiskDay(riskDay);
		rpUserPayConfig.setFundIntoType(fundIntoType);
		rpUserPayConfig.setIsAutoSett(isAutoSett);
		rpUserPayConfig.setEditTime(new Date());
		updateData(rpUserPayConfig);
		
		//查询支付产品下有哪些支付方式
		List<RpPayWay> payWayList = rpPayWayService.listByProductCode(productCode);
		Map<String, String> map = new HashMap<String, String>();
		//过滤重复数据
		for(RpPayWay payWay : payWayList){
			map.put(payWay.getPayWayCode(), payWay.getPayWayName());
		}
				
		for (String key : map.keySet()) {
			if(key.equals(PayWayEnum.WEIXIN.name())){
				//创建用户第三方支付信息
				RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(userNo, PayWayEnum.WEIXIN.name());
				if(rpUserPayInfo == null){
					rpUserPayInfo = new RpUserPayInfo();
					rpUserPayInfo.setId(StringUtil.get32UUID());
					rpUserPayInfo.setCreateTime(new Date());
					rpUserPayInfo.setAppId(appId);
					rpUserPayInfo.setMerchantId(merchantId);
					rpUserPayInfo.setPartnerKey(partnerKey);
					rpUserPayInfo.setPayWayCode(PayWayEnum.WEIXIN.name());
					rpUserPayInfo.setPayWayName(PayWayEnum.WEIXIN.getDesc());
					rpUserPayInfo.setUserNo(userNo);
					rpUserPayInfo.setUserName(rpUserPayConfig.getUserName());
					rpUserPayInfo.setStatus(PublicStatusEnum.ACTIVE.name());
					rpUserPayInfoService.saveData(rpUserPayInfo);
				}else{
					rpUserPayInfo.setEditTime(new Date());
					rpUserPayInfo.setAppId(appId);
					rpUserPayInfo.setMerchantId(merchantId);
					rpUserPayInfo.setPartnerKey(partnerKey);
					rpUserPayInfo.setPayWayCode(PayWayEnum.WEIXIN.name());
					rpUserPayInfo.setPayWayName(PayWayEnum.WEIXIN.getDesc());
					rpUserPayInfoService.updateData(rpUserPayInfo);
				}
						
			}else if(key.equals(PayWayEnum.ALIPAY.name())){
				//创建用户第三方支付信息
				RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(userNo, PayWayEnum.ALIPAY.name());
				if(rpUserPayInfo == null){
					rpUserPayInfo = new RpUserPayInfo();
					rpUserPayInfo.setId(StringUtil.get32UUID());
					rpUserPayInfo.setCreateTime(new Date());
					rpUserPayInfo.setAppId(ali_partner);
					rpUserPayInfo.setMerchantId(ali_sellerId);
					rpUserPayInfo.setPartnerKey(ali_key);
					rpUserPayInfo.setPayWayCode(PayWayEnum.ALIPAY.name());
					rpUserPayInfo.setPayWayName(PayWayEnum.ALIPAY.getDesc());
					rpUserPayInfo.setUserNo(userNo);
					rpUserPayInfo.setUserName(rpUserPayConfig.getUserName());
					rpUserPayInfo.setStatus(PublicStatusEnum.ACTIVE.name());
					rpUserPayInfoService.saveData(rpUserPayInfo);
				}else{
					rpUserPayInfo.setEditTime(new Date());
					rpUserPayInfo.setAppId(ali_partner);
					rpUserPayInfo.setMerchantId(ali_sellerId);
					rpUserPayInfo.setPartnerKey(ali_key);
					rpUserPayInfo.setPayWayCode(PayWayEnum.ALIPAY.name());
					rpUserPayInfo.setPayWayName(PayWayEnum.ALIPAY.getDesc());
					rpUserPayInfoService.updateData(rpUserPayInfo);
				}
			}
		}
	}
	
	/**
	 * 审核
	 * @param userNo
	 * @param auditStatus
	 */
	@Override
	public void audit(String userNo, String auditStatus){
		RpUserPayConfig rpUserPayConfig = getByUserNo(userNo, null);
		if(rpUserPayConfig == null){
			throw new PayBizException(PayBizException.USER_PAY_CONFIG_IS_NOT_EXIST,"支付配置不存在！");
		}
		
		if(auditStatus.equals(PublicEnum.YES.name())){
			//检查是否已关联生效的支付产品
			RpPayProduct rpPayProduct = rpPayProductService.getByProductCode(rpUserPayConfig.getProductCode(), PublicEnum.YES.name());
			if(rpPayProduct == null){
				throw new PayBizException(PayBizException.PAY_PRODUCT_IS_NOT_EXIST,"未关联已生效的支付产品，无法操作！");
			}
			
			//检查是否已设置第三方支付信息
		}
		rpUserPayConfig.setAuditStatus(auditStatus);
		rpUserPayConfig.setEditTime(new Date());
		updateData(rpUserPayConfig);
	}
	
	/**
	 * 根据商户key获取已生效的支付配置
	 * @param payKey
	 * @return
	 */
	public RpUserPayConfig getByPayKey(String payKey){
	    Map<String , Object> paramMap = new HashMap<String , Object>();
	    paramMap.put("payKey", payKey);
	    paramMap.put("status", PublicStatusEnum.ACTIVE.name());
	    paramMap.put("auditStatus", PublicEnum.YES.name());
	    return rpUserPayConfigDao.getBy(paramMap);
	}
}