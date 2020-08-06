package creakok.com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import creakok.com.domain.Funding;
import creakok.com.domain.Funding_category;
import creakok.com.mapper.FundingMapper;
import creakok.com.vo.FundingVo;
import lombok.extern.log4j.Log4j;


@Service
@Log4j
public class FundingServiceImpl implements FundingService {
	@Autowired
	private FundingMapper fundingMapper;
	@Override
	public FundingVo getFundingVo(int currentPage, int pageSize, String filterBy, String categoryBy) {
		long totalCount = fundingMapper.selectCount();
		
		FundingVo fundingVo = new FundingVo(currentPage, totalCount, pageSize, filterBy, categoryBy, null);
		
		List<Funding> list = fundingMapper.selectPerPage(fundingVo);
		for(Funding funding : list) {
			funding.setFunding_category_name(fundingMapper.selectPerPageCategory(funding));
			funding.setPercentage(100.0*funding.getFunding_amount()/funding.getFunding_goal());
			funding.setRestdays((funding.getFunding_edate().getTime()-funding.getFunding_wdate().getTime())/(1000*60*60*24));
		}	
	
		
		return new FundingVo(currentPage, totalCount, pageSize, filterBy, categoryBy, list);
	}
	
	@Override
	public Funding getFunding(long funding_index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FundingVo getFundingVoByFilter(String filter, int currentPage, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FundingVo getFundingVoByKeyword(String keyword, int currentPage, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(Funding funding) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edit(Funding funding) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(long funding_index) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Funding_category> getFunding_category() {
		List<Funding_category> list = fundingMapper.getFunding_category();
		return list;
	}



}
