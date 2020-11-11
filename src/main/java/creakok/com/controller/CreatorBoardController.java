package creakok.com.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import creakok.com.domain.Board;
import creakok.com.domain.Comment;
import creakok.com.domain.Creator;
import creakok.com.domain.Pagination;
import creakok.com.filesetting.Path;
import creakok.com.service.BoardCommentService;
import creakok.com.service.CreatorBoardService;
import creakok.com.vo.ListResult;
import lombok.extern.log4j.Log4j;

@Log4j
@Controller
public class CreatorBoardController {
	@Autowired
	private CreatorBoardService creatorBoardService;
	@Autowired
	private BoardCommentService boardCommentService;
	
	@RequestMapping("board_page")
	public ModelAndView getListResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String board_cpStr = request.getParameter("board_cp");
		String board_filterBy = request.getParameter("board_filterBy");
		String creator_name = request.getParameter("creator_name");
		String board_c_code = request.getParameter("board_c_code");
		String board_searchName = request.getParameter("board_searchName");
		
		HttpSession session = request.getSession();
		
		// 정렬할때 creator_name 안 넘어와서 현재 session에 있는 크리에이터 정보(the Creator)를 가져와서 입력해준다.
		// 잘 사용하면 session.setAttribute("theCreator") 한번만 해줘도 될 것 같다.
		if (creator_name == null) {
			Creator theCreator= (Creator) session.getAttribute("theCreator");
			creator_name = theCreator.getCreator_name();
		}
		
		//(1) cp 
		int board_cp = 1;
		if(board_cpStr == null) {
			Object board_cpObj = session.getAttribute("board_cp");
			if(board_cpObj != null) {
				board_cp = (Integer)board_cpObj;
			}
		}else {
			board_cpStr = board_cpStr.trim();
			board_cp = Integer.parseInt(board_cpStr);
		}
		session.setAttribute("board_cp", board_cp);
		
		//(2) ps 
		int board_ps = 20;
		session.setAttribute("board_ps", board_ps);
		
		if (board_c_code==null) { 
			String board_c_codeTemp = (String) session.getAttribute("board_c_code");
			if (board_c_codeTemp !=null) {
				board_c_code = board_c_codeTemp;
				log.info("@@@@@@@@@@@@"+ board_c_code);
			} else {
				board_c_code = "#####";
			}
		}
		session.setAttribute("board_c_code", board_c_code);
		
		if (board_searchName==null) { 
			session.removeAttribute("board_searchName");
			String board_searchNameTemp = (String) session.getAttribute("board_searchName");
			if (board_searchNameTemp !=null) {
				board_searchName = board_searchNameTemp;
				log.info("@@@@@@@@@@@@"+ board_searchName);
			} else {
				board_searchName = "#####";
			}
		}		
		session.setAttribute("board_searchName", board_searchName);
		
		ListResult listResult = null;
		
		if (board_filterBy==null) {
			session.removeAttribute("board_filterBy");
			String board_filterByTemp = (String) session.getAttribute("board_filterBy");
			if (board_filterByTemp !=null) {
				board_filterBy = board_filterByTemp;
				log.info("@@@@@@@@@@@@"+ board_filterBy);
			} else {
				board_filterBy = "BOARD_INDEX";
			}
			log.info("@@@@@@@ 필터 적용 안된 listResult가져옴");
			listResult = creatorBoardService.getListResultS(board_cp, board_ps, board_filterBy, creator_name);
		} else {
			log.info("@@@@@@@ 필터 적용된 listResult가져옴");
			log.info("@@@@@@@@@ board_filterBy : " + board_filterBy);
			listResult = creatorBoardService.getListResultByFilterS(board_cp, board_ps, board_filterBy, creator_name);
		}
		
		session.setAttribute("board_filterBy", board_filterBy);
		
		listResult.setCurrentPage(board_cp);
		listResult.setPageSize(board_ps);
		/* 게시글 번호 순서대로 일정하게 나오게 하기 */
		listResult = setConBoardNumber(listResult);
		
		ModelAndView mv  = new ModelAndView();
		mv.setViewName("community");
		mv.addObject("listResult", listResult);
		
		// 해당 페이지의 크리에이터 정보 세션에 올리기
		Creator theCreator = creatorBoardService.getCreator(creator_name);
		session.setAttribute("theCreator", theCreator);
		
		//대표컨텐츠 쏴주기
		String[] contentList = null;
		String contentOneLine = theCreator.getCreator_main_content();
		if (contentOneLine != null) contentList = contentOneLine.split("@");
		mv.addObject("contentList", contentList);
		
		
		log.info("@@@@@@@@@@ listResult : " + listResult);
		
		return mv;
	}
	
	@RequestMapping("board_content")
	public ModelAndView content(HttpServletRequest request, HttpServletResponse response, HttpSession session, 
			@RequestParam long board_index) {
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("community_board_content2");
		
		Board board = creatorBoardService.contentS(board_index);
		
		// 게시글의 댓글 조회
		List<Comment> commentList = (List<Comment>) boardCommentService.getComment(board_index);
		mv.addObject("commentList", commentList);
		
		// 조회수 무한정 증가하는 것을 억제를 위한 로직
		Cookie[] cookies = request.getCookies();
		// 비교하기 위한 새로운 쿠키
        Cookie viewCookie = null;
        
        // 쿠키가 있을 경우
        if (cookies != null && cookies.length > 0) {
            for (int i = 0; i < cookies.length; i++) {
                // Cookie의 name이 cookie + board_index와 일치하는 쿠키를 viewCookie에 넣어 
                if (cookies[i].getName().equals("cookie"+board_index)) { 
                    log.info("처음 쿠키가 생성한 뒤 들어옴");
                    viewCookie = cookies[i];
                }
            }
        }
        
        if (board != null) {
        	log.info("System - 해당 게시글로 넘어감");
            
            mv.addObject("board", board);
 
            // 만일 viewCookie가 null일 경우 쿠키를 생성해서 조회수 증가 로직을 처리함.
            if (viewCookie == null) {    
                log.info("cookie 없음");
                
                // 쿠키 생성(이름, 값)
                Cookie newCookie = new Cookie("cookie"+board_index, "|" + board_index + "|");
                                
                // 쿠키 추가
                response.addCookie(newCookie);
 
                // 쿠키를 추가 시키고 조회수 증가시킴
                boolean result = creatorBoardService.plusView(board_index);
                
                if(result) {
                	log.info("조회수 증가");
                }else {
                    log.info("조회수 증가 에러");
                }
            }
            // viewCookie가 null이 아닐 경우 쿠키가 있으므로 조회수 증가 로직을 처리하지 않음.
            else {
            	log.info("cookie 있음");
                
                // 쿠키 값 받아
                String value = viewCookie.getValue();
                
                log.info("cookie 값 : " + value);
            }
        } 
        return mv;
	}
	// 글 작성 view 이동 
	@GetMapping("board_write")
	public ModelAndView boardWrite(HttpServletRequest request) {
		ModelAndView mv  = new ModelAndView();
		mv.setViewName("community_board_write");
			
		return mv;
	}
	// 글 작성 backend 작업 
	@PostMapping("board_write")
	public String write(Board board) {
		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@");
		log.info("@@@@@@@@@@@ board.getBoard_index() : " + board.getBoard_index());
		creatorBoardService.insertBoard(board);
		return "redirect: /board_content?board_index="+ board.getBoard_index();
	}
	
	// 글 수정 view 이동
	@GetMapping("board_update")
	public ModelAndView update(long board_index) {
		Board board = creatorBoardService.getBoard(board_index);
		ModelAndView mv = new ModelAndView("community_board_update", "board", board);
				
		return mv;
	}
	
	// 글 수정 backend 작업
	@PostMapping("board_update")
	public String update(Board board) {
		creatorBoardService.edit(board);
		return "redirect:board_content?board_index="+board.getBoard_index();
	}
	
	// 글 삭제
	@RequestMapping("board_delete")
	public String deleteBoard(@RequestParam long board_index, @RequestParam String creator_name) {
		creatorBoardService.deleteBoard(board_index, creator_name);
		return "redirect:board_page#fix_point";
	}

	// 글 검색
	@RequestMapping("board_search")
	public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String board_cpStr = request.getParameter("board_cp");
		String board_psStr = request.getParameter("board_ps");
		String board_filterBy = request.getParameter("board_filterBy");
		String board_c_code = request.getParameter("board_c_code");
		String board_searchName = request.getParameter("board_searchName");
		
		HttpSession session = request.getSession();		
		Creator theCreator = (Creator) session.getAttribute("theCreator");
		String creator_name = theCreator.getCreator_name();
		//(1) cp 
		int board_cp = 1;
		
		if(board_cpStr == null) {
			Object board_cpObj = session.getAttribute("board_cp");
			if(board_cpObj != null) {
				board_cp = (Integer)board_cpObj;
			}
		}else {
			board_cpStr = board_cpStr.trim();
			board_cp = Integer.parseInt(board_cpStr);
		}
	
		session.setAttribute("board_cp", board_cp);
		
		//(2) ps 
		int board_ps = 3;
		if(board_psStr == null) {
			Object board_psObj = session.getAttribute("board_ps");
			if(board_psObj != null) {
				board_ps = (Integer)board_psObj;
			}
		}else {
			board_psStr = board_psStr.trim();
			int board_psParam = Integer.parseInt(board_psStr);
			
			Object board_psObj = session.getAttribute("board_ps");
			if(board_psObj != null) {
				int board_psSession = (Integer)board_psObj;
				if(board_psSession != board_psParam) {
					board_cp = 1;
					session.setAttribute("board_cp", board_cp);
				}
			}else {
				if(board_ps != board_psParam) {
					board_cp = 1;
					session.setAttribute("board_cp", board_cp);
				}
			}
			
			board_ps = board_psParam;
		}
		session.setAttribute("board_ps", board_ps);		
		
		if (board_filterBy==null) { 
			String board_filterByTemp = (String) session.getAttribute("board_filterBy");
			if (board_filterByTemp !=null) {
				board_filterBy = board_filterByTemp;
				log.info("@@@@@@@@@@@@"+ board_filterBy);
			} else {
				board_filterBy = "BOARD_INDEX";
			}
		} else {
			log.info("@@@@@@@@@@@@"+ board_filterBy);
		}
		session.setAttribute("filterBy", board_filterBy);
		

		
		if (board_c_code==null) { 
			String board_c_codeTemp = (String) session.getAttribute("board_c_code");
			if (board_c_codeTemp !=null) {
				board_c_code = board_c_codeTemp;
				log.info("@@@@@@@@@@@@"+ board_c_code);
				session.setAttribute("board_c_code", board_c_code);
			} else {
				board_c_code = "#####";
				session.setAttribute("board_c_code", board_c_code);
			}
		} else {
			log.info("@@@@@@@@@@@@"+ board_c_code);
		}
		session.setAttribute("board_c_code", board_c_code);
		
		if (board_searchName==null) { 
			String board_searchNameTemp = (String) session.getAttribute("board_searchName");
			log.info("@@@@@@@@@@@@"+ board_searchNameTemp);
			if (board_searchNameTemp !=null) {
				board_searchName = board_searchNameTemp;
				log.info("@@@@@@@@@@@@"+ board_searchName);
				session.setAttribute("board_searchName", board_searchName);
			} else {
				board_searchName = "#####";
				session.setAttribute("board_searchName", board_searchName);
			}
		} else {
			log.info("@@@@@@@@@@@@"+ board_searchName);
		}
		session.setAttribute("board_searchName", board_searchName);
		
		ListResult listResult = creatorBoardService.getListResultBySearchS(board_cp, board_ps, board_filterBy, board_c_code, board_searchName, creator_name);
		listResult.setBoard_searchName(board_searchName);
		listResult.setBoard_c_code(board_c_code);
		
		listResult.setCurrentPage(board_cp);
		listResult.setPageSize(board_ps);
		/* 게시글 번호 순서대로 일정하게 나오게 하기 */
		listResult = setConBoardNumber(listResult);

		request.setAttribute("listResult", listResult);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("community");
		
		return  mv;
	}
	
	// 댓글 작성
	@RequestMapping("comment_write")
	public String writeComment(Comment comment) {
		boardCommentService.writeComment(comment);
		return "redirect:board_content?board_index="+comment.getBoard_index();
	}
	
	// 댓글 수정
	@RequestMapping("comment_update")
	public ModelAndView updateComment(HttpServletRequest request) {
		String comment_indexStr = request.getParameter("comment_index");
		String comment_content = request.getParameter("comment_content");
		String board_indexStr = request.getParameter("board_index");
		
		long comment_index = Long.valueOf(comment_indexStr); 
		long board_index = Long.valueOf(board_indexStr);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("redirect:board_content?board_index="+board_index);
		boardCommentService.updateComment(comment_index, comment_content);
		
		return mv;
	}
	// 댓글 삭제
	@RequestMapping("comment_delete")
	public String deleteComment(long board_index, long comment_index) {
		
		boardCommentService.deleteComment(comment_index);
		return "redirect:board_content?board_index="+board_index;
	}
	
	// 답글 작성 view로 가기 위한 맵핑
	// refer를 받아와야 할 것 같다..
	@GetMapping("board_answer")
	public ModelAndView boardAnswer(long board_index) {
		Board board = creatorBoardService.getBoard(board_index);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("community_board_answer");
		mv.addObject("board", board);
		
		return mv;
	}
	// 답글 작성 backend 처리 로직
	@PostMapping("board_answer")
	public String boardAnswer(Board board) {
		/* 원글의 board_index를 가져와서 답글의 board_parent에 넣어준다. 
		 * CONNECT BY 로 부모자식관계형성 
		 */
		long board_indexParent = board.getBoard_index();
		board.setBoard_parent(board_indexParent);	
		
		// level, 순번 처리로직 
		// 현재 글보다 큰 순번을 가진 글(with the same refer)의 순번을 +1 씩 증가시킨다.
		creatorBoardService.updateSunbun(board);
		// 현재 게시글의 level에 +1, 순번에 +1을 해주고 insert한다.
		creatorBoardService.insertAnswer(board);
		return "redirect:board_content?board_index="+board.getBoard_index();
	}
	
	@PostMapping(value="/uploadSummernoteImageFileCommunity")
	@ResponseBody
	public String uploadSummernoteImageFileCommunity(@RequestParam("file_detail_pic") MultipartFile multipartFile) {
		String fileRoot = Path.IMG_STORE_COMMUNITY;	//저장될 외부 파일 경로
		String originalFileName = multipartFile.getOriginalFilename();	//오리지날 파일명
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));	//파일 확장자			
		String savedFileName = UUID.randomUUID() + extension;	//저장될 파일 명
		
		File targetFile = new File(fileRoot + savedFileName);	
		
		try {
			InputStream fileStream = multipartFile.getInputStream();
			FileUtils.copyInputStreamToFile(fileStream, targetFile);	//파일 저장
	
		} catch (IOException e) {
			FileUtils.deleteQuietly(targetFile);	//저장된 파일 삭제
		
			e.printStackTrace();
		}
	
		String str = "/summernoteImageCommunity/"+savedFileName;
		return str;
	}
	
	/* 게시글 번호를 커뮤니티에 상관없이 일정하게 나오게 하는 기능 */
	private ListResult setConBoardNumber(ListResult listResult) { // setContinuousBoardNumber
		// 게시글 리스트를 매개변수로 받아와서 글번호를 일정하게 정렬한 후 리스트를 반환합니다.
		long board_list_number;
		List<Board> list = listResult.getList();
		int listSize = list.size();
		long totalCount = listResult.getTotalCount();
		int board_cp = listResult.getCurrentPage();
		int board_ps = listResult.getPageSize();
		log.info("@@@@ totalCount : " + totalCount);
		for(int i = 0; i < listSize; i++) {
			board_list_number = totalCount - ((board_cp - 1) * board_ps) - i;
			list.get(i).setBoard_list_number(board_list_number);
		}
		return listResult;
	}

}
