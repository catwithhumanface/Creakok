package creakok.com.service;

import org.apache.ibatis.annotations.Param;

import creakok.com.domain.Funding;
import creakok.com.domain.Order_Info;
import creakok.com.domain.Funding_payinfo;

public interface PayService {	
	Funding getFundingCheckoutInfo(long funding_index);
	void insertOneOrder(Order_Info order_info);
	void insertFunding_order(Funding_payinfo funding_payinfo);
	Order_Info selectByOrderIndex(long order_index);
	void deleteOneOrder(long order_index);
}
