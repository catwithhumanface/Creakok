package creakok.com.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import creakok.com.domain.Contact;
import creakok.com.domain.Creator;
import creakok.com.domain.Funding_payinfo;
import creakok.com.domain.LoginResult;
import creakok.com.domain.Member;
import creakok.com.domain.Member_category;
import creakok.com.domain.Member_origin;
import creakok.com.domain.Order_Info;
import creakok.com.filesetting.Path;
import creakok.com.kakao.KakaoLogin;
import creakok.com.service.CreatorService;
import creakok.com.service.MemberService;
import creakok.com.vo.Goods_ReviewVo;
import creakok.com.vo.Member_FundingPayInfoVo;
import creakok.com.vo.Member_OrderInfoVo;
import lombok.extern.log4j.Log4j;

@Log4j
@Controller
public class MemberController {

	@Autowired
	private MemberService mService;
	
	@Autowired
	private CreatorService cService;
	
	@ResponseBody
	@GetMapping("member_readMemberOrign.do")
	public String readMemberOrign(String member_email) {
		return mService.checkMemberOrigin(member_email);
	}	
	
	
	@RequestMapping("findPassword.do")
	public String findPassword() {
		return "findPassword";
	}
	
	@RequestMapping("socialLoginFail.do")
	public String socialLoginFail() {
		return "socialLoginFail";
	}
	

	@ResponseBody
	@GetMapping("member_readEmail.do")
	public String readEmail(String member_email) {
		Member member = mService.checkEmailExist(member_email);
		//log.info("###"+member);
		if(member != null) return "exist";
		else return "not_exist";
	}
	
	@GetMapping("member_changeName.do")
	public String changeName(String member_email, String member_name, HttpSession session) {
		Member member = new Member();
		member.setMember_email(member_email);
		member.setMember_name(member_name);
		mService.changeName(member);

		session.removeAttribute("member");
		session.setAttribute("member", mService.getMemberInfoS(member_email) );
		return "redirect:/member_mypage.do";
	}
	
	@ResponseBody
	@GetMapping("member_readName.do")
	public String readName(String member_name) {
		Member member = mService.checkNameExist(member_name);
		boolean member_exist = (member != null) ? true : false;
		
		Creator creator = cService.checkNameExist(member_name);
		boolean creator_exist = (creator != null) ? true : false;
		
		Creator creator_standby = cService.checkNameExist_standby(member_name);
		boolean creator_standby_exist = (creator_standby != null) ? true : false;
		
		log.info("### ????:"+member_exist+"/"+creator_exist+"/"+creator_standby_exist );
		
		//if(member != null) return "exist";
		//else return "not_exist";
		
		if(member_exist | creator_exist |creator_standby_exist) return "exist";
		else return "not_exist";
	}
	
	@Transactional
	@GetMapping("secessionMember.do")
	public String secessionMember(String member_email,HttpSession session) {
		log.info("### member_email:"+member_email);
		
		Member member = mService.getMemberInfoS(member_email); // member_email로 member_name까지 가져온다 
		
		// 멤버가 크리에이터일 경우
		if (member.getMember_category_code() == Member_category.MEMBER_CREATOR) {
			Creator creator = mService.getCreator(member);
			mService.delCreatorRefData(member, creator);	// 크리에이터 탈퇴를 위한 관련 참조 자료 삭제 & 크리에이터 삭제	
		}
		
		session.removeAttribute("member");
		
		mService.secessionMemberS(member_email); // 멤버에서 삭제
		return "redirect:/";
	}
	
	
	@PostMapping("member_signup.do")
	public String signup(String member_name, String member_email, String member_password) {
		//log.info("### member_name:"+member_name );
		//log.info("### member_email:"+member_email );
		//log.info("### member_password:"+member_password );
		Member member = new Member();
		member.setMember_category_code(Member_category.MEMBER_NORMAL);
		member.setMember_origin_code(Member_origin.SIGNUP_NORMAL);
		
		member.setMember_name(member_name);
		member.setMember_email(member_email);
		member.setMember_password(member_password);

		mService.signupMemberS(member);
		
		return "login";
	}
	
	@RequestMapping("privacy")
	public String member_privacy() {
		return "privacy";
	}
	
	@RequestMapping("terms-of-use")
	public String terms_of_use() {
		return "terms-of-use";
	}
	
	@RequestMapping("member_serviceAgreements.do")
	public ModelAndView serviceAgreements(String member_name, String member_email, String member_password) {
		ModelAndView mav = new ModelAndView("service-agreements");
		mav.addObject("new_member_name", member_name);
		mav.addObject("new_member_email", member_email);
		mav.addObject("new_member_password", member_password);
		return mav;
	}
	
	@RequestMapping("member_joinwithEmail.do")
	public String joinwithEmail() {
		return "joinwithEmail";
	}
	
	@RequestMapping("member_join.do")
	public String join() {
		return "join";
	}
	
	@GetMapping("member_changePassword.do")
	public String changePassword(String member_email, String new_password, HttpSession session) {
		//log.info("### new_password:"+new_password);
		mService.changeMemberPasswordS(member_email, new_password);
		session.removeAttribute("member");
		return "redirect:/";
	}
	
	@ResponseBody
	@GetMapping("member_readPassword.do")
	public String readPassword(String member_email, String member_password) {
		//log.info("### member_email: "+member_email);
		//log.info("### member_password: "+member_password);
		return Integer.toString( mService.compareMemberPasswordS(member_email, member_password) );
		
		//return 0;
	}
	
	@RequestMapping("member_mypage.do")
	public ModelAndView mypage(HttpServletRequest request, HttpSession session) {
		String order_cp = request.getParameter("order_cp");
		Member member = (Member)session.getAttribute("member");
		String member_email = member.getMember_email();
		String focus = request.getParameter("focus");
		
		Member_OrderInfoVo order_infoVo = (Member_OrderInfoVo)session.getAttribute("order_infoVo");
		
		//(1) cp 
		int cp = 1;
		if(order_cp == null) {
			//order_info 세션에 없을 시 nullpointer 오류 해결 위해 이프문 추가
			if(order_infoVo!=null) {
				Object cpObj = order_infoVo.getOrder_cp();
				if(cpObj != null) {
					cp = (Integer)cpObj;
				} else if(cpObj == null){
					cp = 1;
				}
			}else {
				cp=1;
			}
		}else {
			order_cp = order_cp.trim();
			cp = Integer.parseInt(order_cp);
		}
		
		//(2) ps 
		int ps = 5;	
	

		Member_OrderInfoVo order_list = mService.selectPerPageOrder(cp, ps, member_email);
		session.setAttribute("order_infoVo", order_list);
		order_list.setOrder_cp(cp);
		order_list.setOrder_ps(ps);
		order_list.setMember_email(member_email);
		
		if(order_list.getOrder_list().size() == 0) {
			if(cp > 1) {	
				int cp2 = cp-1;
				order_list.setOrder_cp(cp2);
			}
		}   
		
		//log.info("######################################member_email: "+member_email);
		//log.info("######################################order_info: "+order_info);
		
		long order_count = mService.selectOrderCount(member_email);
		
		ModelAndView mv = new ModelAndView();
		if(focus!=null) {
			if(focus.equals("funding")) {
				mv.setViewName("mypage_focus_funding");
			}
		}else {
			mv.setViewName("mypage");
		}
		if(member_email.equals(Member_category.SUPER_ACCOUNT) ) {
			List<Creator> standby_list = cService.readAllCreatorStandby();
			for(Creator c : standby_list) {
				//log.info("///////////////:"+c.getCreator_name() );
			}
			mv.addObject("standby_list", standby_list);
		}
		
		Creator cTemp = cService.checkEmailExist_standby(member_email);
		log.info("######## cTemp:"+cTemp);
		if(cTemp != null) {
			mv.addObject("CreatorStandbyExist", true);	
		} else {
			mv.addObject("CreatorStandbyExist", null);
		}

		//펀딩 주문내역
		String fundingpay_cp = request.getParameter("fundingpay_cp");
		
		long funding_pay_count = mService.selectFundingPayCount(member_email);
		
		//(1) cp 펀딩
		int cp_funding = 1;
		if(fundingpay_cp == null) {
			Object cpObj = session.getAttribute("cp_funding");
			if(cpObj != null) {
				cp_funding = (Integer)cpObj;
			} else if(cpObj == null){
				cp_funding = 1;
			}
		}else {
			fundingpay_cp = fundingpay_cp.trim();
			cp_funding = Integer.parseInt(fundingpay_cp);
		}
		
		//(2) ps 
		int ps_funding = 5;	
		Member_FundingPayInfoVo funding_payinfoVo = mService.selectPerPageFundingPay(cp_funding, ps_funding, member_email);
		funding_payinfoVo.setFunding_pay_cp(cp_funding);
		funding_payinfoVo.setFunding_pay_ps(ps_funding);
		funding_payinfoVo.setMember_email(member_email);

		session.setAttribute("funding_pay_info", funding_payinfoVo);
		session.setAttribute("funding_pay_count", funding_pay_count);
	
		session.setAttribute("order_info", order_list);	
		session.setAttribute("order_count", order_count);	
		
		//문의 내역
		List<Contact> contact_list = mService.selectContact();
		log.info("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD contact_list: "+contact_list);
		session.setAttribute("contact_list", contact_list);	
		
		return mv;
	}

	@RequestMapping("member_order.do")
	public ModelAndView member_order(String member_email, HttpServletRequest request, HttpSession session) {
		String order_cp = request.getParameter("order_cp");
		
		Member_OrderInfoVo order_infoVo = (Member_OrderInfoVo)session.getAttribute("order_infoVo");
		
		//(1) cp 
		int cp = 1;
		if(order_cp == null) {
			Object cpObj = order_infoVo.getOrder_cp();
			if(cpObj != null) {
				cp = (Integer)cpObj;
			} else if(cpObj == null){
				cp = 1;
			}
		}else {
			order_cp = order_cp.trim();
			cp = Integer.parseInt(order_cp);
		}
		
		//(2) ps 
		int ps = 5;	

		Member_OrderInfoVo order_list = mService.selectPerPageOrder(cp, ps, member_email);
		order_list.setOrder_cp(cp);
		order_list.setOrder_ps(ps);
		order_list.setMember_email(member_email);
		
		if(order_list.getOrder_list().size() == 0) {
			if(cp > 1) {	
				int cp2 = cp-1;
				order_list.setOrder_cp(cp2);
			}
		}   
		
		//log.info("######################################member_email: "+member_email);
		//log.info("######################################order_info: "+order_info);
		
		long order_count = mService.selectOrderCount(member_email);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("mypage_order");
		mv.addObject("order_info", order_list);	
		mv.addObject("order_count", order_count);	
		
		return mv;
	}
	
	@RequestMapping("member_orderdetail.do")
	public ModelAndView member_orderdetail(String order_indexStr, String member_email, long order_list_number, HttpSession session) {
		long order_index = Long.parseLong(order_indexStr);
			
		Order_Info order_info = mService.selectOneOrderInfo(order_index);
		long order_count = mService.selectOrderCount(member_email);
		//session.setAttribute("list_number", order_list_number);
		
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("mypage_order_detail");
		mv.addObject("order_info", order_info);	
		mv.addObject("order_count", order_count);
		mv.addObject("order_list_number", order_list_number);
		mv.addObject("order_list_number", order_list_number);
		//mv.addObject("order_list_number", order_list_number);
			
		return mv;
	}
	@RequestMapping("member_fundingpayDetail.do")
	public ModelAndView member_fundingpayDetail(String funding_payinfo_index, long funding_list_number, String member_email) {
		long funding_payinfo_indexlong = Long.parseLong(funding_payinfo_index);
			
		Funding_payinfo funding_payinfo = mService.selectFundingPayInfo(funding_payinfo_indexlong);
		long funding_paycount = mService.selectFundingPayCount(member_email);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("mypage_fundingpay_detail");
		mv.addObject("funding_payinfo", funding_payinfo);	
		mv.addObject("funding_paycount", funding_paycount);
		mv.addObject("funding_list_number", funding_list_number);
		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@funding_list_number: "+funding_list_number);
			
		return mv;
	}
	@RequestMapping("member_logout.do")
	public String logout(HttpSession session) {
		session.removeAttribute("member");

		String kakao_code = (String)session.getAttribute("kakao_code");
		KakaoLogin.Logout(kakao_code);
		session.removeAttribute("kakao_code");
		
		return "redirect:/";
	}

	
	@GetMapping("member_login.do")
	public String login(HttpSession session) {
		session.removeAttribute("member");
		return "login";
	}
	
	@PostMapping("member_login.do")
	public String login(String member_email, String member_password, HttpSession session) {

		int result = mService.compareMemberPasswordS(member_email, member_password);
		if(result==LoginResult.LOGIN_OK) {
			//log.info("### LOGIN_OK");
			//return "index";
			session.setAttribute("member", mService.getMemberInfoS(member_email) );
			return "redirect:/";

		} else if(result==LoginResult.LOGIN_PASSWORD_FAIL) {
			//log.info("### LOGIN_PASSWORD_FAIL");
			return "redirect:member_login.do";

		} else if(result==LoginResult.LOGIN_EMAIL_NOT_EXIST) {
			//log.info("### LOGIN_EMAIL_NOT_EXIST");
			return "redirect:member_login.do";
		} 
		return "index";
	}//end of login
	
	
	@RequestMapping("joinCreator.do")
	public String joinCreator() {
		return "joinCreator";
	}
	
	@RequestMapping("joinCreatorUpdate.do")
	public ModelAndView joinCreatorUpdate(String member_email) {
		Creator creator = cService.checkEmailExist_standby(member_email);
		log.info("########### creator:"+creator.toString());
		ModelAndView mv = new ModelAndView();
		mv.setViewName("joinCreatorUpdate");
		mv.addObject("creator", creator);	
		return mv;
	}
	
	@ResponseBody
	@RequestMapping("writeCreatorJoinData.do")
	public String writeCreatorJoinData(Creator creator, @RequestParam List<MultipartFile> creator_pics) {
		//log.info("#########creator:"+creator);
		String path = Path.IMG_STORE_COMMUNITY;
		
		// creator에 저장된 3개의 컨텐츠에서 각각의 고유키만 추출하여 '@'를 구분자로한 하나의 키로 만들어서 컬럼에 저장한다. 
		String creator_main_content = mergeOwnKeysToMainContent(creator);
		creator.setCreator_main_content(creator_main_content);
		
		int nAddResult = cService.addCreatorStandby(creator);
		//log.info("######## nAddResult:"+nAddResult);
		if( nAddResult == 1) { //DB에 기록이 성공했다면. 파일을 저장하자.
			MultipartFile profile = creator_pics.get(0);
			String creator_profile_photo = cService.saveImage(profile, path);
			creator.setCreator_profile_photo(creator_profile_photo);	
			cService.updateCreatorProfileImages(creator);
			
			MultipartFile banner = creator_pics.get(1);
			String creator_banner_photo = cService.saveImage(banner, path);
			creator.setCreator_banner_photo(creator_banner_photo);
			cService.updateCreatorBannerImages(creator);
		}
		return "<script>opener.parent.location.reload(); self.close();</script>";
	}
	
	@ResponseBody
	@RequestMapping("updateCreatorJoinData.do")
	public String updateCreatorJoinData(Creator creator, @RequestParam List<MultipartFile> creator_pics) {
		//log.info("#########creator:"+creator);
		String path = Path.IMG_STORE_COMMUNITY;
		
		//저장된 것을 읽어온다.
		Creator existCreator = cService.checkEmailExist_standby(creator.getMember_email() );
		
		
		// creator에 저장된 3개의 컨텐츠에서 각각의 고유키만 추출하여 '@'를 구분자로한 하나의 키로 만들어서 컬럼에 저장한다. 
		String creator_main_content = mergeOwnKeysToMainContent(creator);
		creator.setCreator_main_content(creator_main_content);
				
		//일단 업데이트를 하고
		int nAddResult = cService.updateCreator_standby(creator);
		//log.info("######## nAddResult:"+nAddResult);
		if( nAddResult == 1 ) { //DB에 기록이 성공했다면. 파일을 저장하자.
			MultipartFile profile = creator_pics.get(0);
			if(profile.getSize() != 0) { //첨부파일이 있으면 새로 저장하자.
				String creator_profile_photo = cService.saveImage(profile, path);
				creator.setCreator_profile_photo(creator_profile_photo);
			} else {
				//첨부파일이 없으면 기존 내용을 그대로 사용하자.
				creator.setCreator_profile_photo(existCreator.getCreator_profile_photo() );
			}
			cService.updateCreatorProfileImages(creator);

			MultipartFile banner = creator_pics.get(1);
			if(banner.getSize() != 0) {
				String creator_banner_photo = cService.saveImage(banner, path);
				creator.setCreator_banner_photo(creator_banner_photo);
			} else {
				creator.setCreator_banner_photo(existCreator.getCreator_banner_photo() );
			}
			cService.updateCreatorBannerImages(creator);
		}
		return "<script>opener.parent.location.reload(); self.close();</script>";
	}
	
	@RequestMapping("addCreator_standby.do")
	public String addCreator_standby(String creator_name, HttpSession session) {
		//log.info("######## creator:"+creator_name);
		Creator creator = cService.checkNameExist_standby(creator_name);
		if(creator != null) {
			int nAddResult = cService.addCreator(creator);
			if( nAddResult == 1) {
				int nDeleteResult = cService.deleteCreator_standby(creator);
				if(nDeleteResult == 1) {
					Member member = new Member();
					member.setMember_email(creator.getMember_email() );
					mService.setMemberCreator( member );
					
					member.setMember_name(creator_name);
					mService.changeName(member);
				}
			}
		}
		return "redirect:/member_mypage.do";
	}
	
	@RequestMapping("deleteCreator_standby.do")
	public String deleteCreator_standby(String creator_name, HttpSession session) {
		//log.info("######## creator:"+creator_name);
		Creator creator = cService.checkNameExist_standby(creator_name);
		if(creator != null) {
			int nDeleteResult = cService.deleteCreator_standby(creator);
			
			//session.removeAttribute("standby_list");
			//List<Creator> standby_list = cService.readAllCreatorStandby();
			//session.setAttribute("standby_list", standby_list);

			//if(nDeleteResult == 1) return "add ok";
		}
		return "redirect:/member_mypage.do";
	}
	// 크리에이터 커뮤니티 정보수정 페이지 이동 
	@RequestMapping("creatorCommunityUpdate.do")
	public ModelAndView creatorCommunityUpdate(String member_email) {
		log.info("!!!!!!!!!!! member_email : " +member_email);
		Creator creator = cService.checkEmailExist(member_email);
		log.info("########### creator:"+creator);
		ModelAndView mv = new ModelAndView();
		mv.setViewName("creatorCommunityUpdate");
		mv.addObject("creator", creator);	
		return mv;
	}
	// 크리에이터 커뮤니티 정보수정
	@ResponseBody
	@RequestMapping("updateCreatorCommunity.do")
	public String updateCreatorCommunity(Creator creator, @RequestParam List<MultipartFile> creator_pics) {
		//log.info("#########creator:"+creator);
		String path = Path.IMG_STORE_COMMUNITY;
		
		//저장된 것을 읽어온다.
		Creator existCreator = cService.checkEmailExist(creator.getMember_email() );
		
		
		// creator에 저장된 3개의 컨텐츠에서 각각의 고유키만 추출하여 '@'를 구분자로한 하나의 키로 만들어서 컬럼에 저장한다. 
		String creator_main_content = mergeOwnKeysToMainContent(creator);
		creator.setCreator_main_content(creator_main_content);
				
		//일단 업데이트를 하고
		int nUpdateResult = cService.updateCreator(creator);
		//log.info("######## nAddResult:"+nAddResult);
		if( nUpdateResult == 1 ) { //DB에 기록이 성공했다면. 파일을 저장하자.
			MultipartFile profile = creator_pics.get(0);
			if(profile.getSize() != 0) { //첨부파일이 있으면 새로 저장하자.
				String creator_profile_photo = cService.saveImage(profile, path);
				creator.setCreator_profile_photo(creator_profile_photo);
			} else {
				//첨부파일이 없으면 기존 내용을 그대로 사용하자.
				creator.setCreator_profile_photo(existCreator.getCreator_profile_photo() );
			}
			cService.updateCreatorProfileImagesInCreator(creator);

			MultipartFile banner = creator_pics.get(1);
			if(banner.getSize() != 0) {
				String creator_banner_photo = cService.saveImage(banner, path);
				creator.setCreator_banner_photo(creator_banner_photo);
			} else {
				creator.setCreator_banner_photo(existCreator.getCreator_banner_photo() );
			}
			cService.updateCreatorBannerImagesInCreator(creator);
		}
		
		log.info("@@@@@2 creator : " + creator);
		return "<script>opener.parent.location.reload(); self.close();</script>";
	}
	
	// 유튜브 동영상의 주소에서 고유키를 얻는 메소드
	public String getOwnKeyOfContent(String content) {
		String urlOnSearchBar = "https://www.youtube.com/watch?v=";
		String urlOnShare = "https://youtu.be/";
		
		int indexOfSeperator = content.indexOf("=");
		
		if (content.contains(urlOnSearchBar)) {
			log.info("%%%%%% root 1");
			//content = content.replaceAll(urlOnSearchBar, ""); 이걸 쓰면 replace가 안되지..?
			content = content.substring(indexOfSeperator + 1);
			return content;
		} else if (content.contains(urlOnShare)) {
			log.info("%%%%%% root 2");
			content = content.replaceAll(urlOnShare, "");
			return content;
		}
		
		return "";
	}
	
	// 유튜브 동영상 3개의 고유키를 하나의 키로 합친다. 
	public String mergeOwnKeysToMainContent(Creator creator) {
		String con1 = creator.getCreator_content1();
		String con2 = creator.getCreator_content2();
		String con3 = creator.getCreator_content3();

		String ownKeyOfContent1 = getOwnKeyOfContent(con1);
		String ownKeyOfContent2 = getOwnKeyOfContent(con2);
		String ownKeyOfContent3 = getOwnKeyOfContent(con3);
		
		// 입력받은 3개의 컨텐츠를 '@'를 구분자 합쳐서 반환한다. 
		return ownKeyOfContent1 + "@" + ownKeyOfContent2 + "@" + ownKeyOfContent3;
	}
	@RequestMapping("qna_answer_ok.do")
	public String qna_answer_ok(String contact_index) {
		long contact_index2 = Long.parseLong(contact_index);
		mService.updateAnswer(contact_index2);
		
		return "redirect:/member_mypage.do";
	}
}
