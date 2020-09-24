<%@ page contentType="text/html; charset=utf-8" 
import="creakok.com.domain.Member_origin, creakok.com.domain.Member_category, creakok.com.filesetting.Path"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="description" content="">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <!-- The above 4 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    
    <!-- Title -->
    <title>CREAKOK</title>

    <!-- Favicon -->
    <link rel="icon" href="img/core-img/creakok.ico">

    <!-- Core Stylesheet -->
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

    
    <!-- BOTO TEST -->
    <meta name="description" content="Boto Photo Studio HTML Template">
    <meta name="keywords" content="photo, html">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Stylesheets -->
    <link rel="stylesheet" href="css/css_boto/bootstrap.min.css"/>
    <link rel="stylesheet" href="css/css_boto/font-awesome.min.css"/>
    <link rel="stylesheet" href="css/css_boto/slicknav.min.css"/>
    <link rel="stylesheet" href="css/css_boto/fresco.css"/>
    <link rel="stylesheet" href="css/css_boto/slick.css"/>
  
    <!-- Main Stylesheets -->
    <link rel="stylesheet" href="css/css_boto/style.css"/>

    <!-- hcbae Stylesheets -->
    <link rel="stylesheet" href="css/hcbae_tumblbug_part.css"/>
    <link rel="stylesheet" href="css/hcbae_wadiz_part.css">
    <link rel="stylesheet" href="css/hcbae_css.css">
    
  <link rel="stylesheet" href="css/only_goods_review_board_jsp.css">
</head>

<body>
    <!-- Preloader -->
    <div class="preloader d-flex align-items-center justify-content-center">
        <div class="preloader-circle"></div>
        <div class="preloader-img">
            <img src="img/core-img/creakok.png" alt="">
        </div>
    </div>

    <!-- ##### Header Area Start ##### -->
    <header class="header-area">
    <div id="header_div">
    <jsp:include page="creakok_header.jsp" flush="true"/>
    </div>
    </header>
    <!-- ##### Header Area End ##### -->

    <!-- ##### Breadcrumb Area Start ##### -->
    <div class="breadcrumb-area">
        <!-- Top Breadcrumb Area -->
        <div class="top-breadcrumb-area bg-img bg-overlay d-flex align-items-center justify-content-center" style="background-image: url(img/bg-img/24.jpg);">
            <h2>My Page</h2>
        </div>

        <div class="container"> <!--Home>My Page-->
            <div class="row">
                <div class="col-12">
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="#"><i class="fa fa-home"></i> Home</a></li>
                            <li class="breadcrumb-item active" aria-current="page">My Page</li>
                        </ol>
                    </nav>
                </div>
            </div>
        </div> <!--end of Home>My Page-->


        <div class="container"> 
          <!-- Nav tabs -->
            <div class="row"> <!--My Page Tabs Headers-->
                <div class="col-12">
                    <div class="most__search__tab">
                        <ul class="nav nav-tabs nav-pills" role="tablist">
                            <c:if test="${member.member_email == Member_category.SUPER_ACCOUNT}">
                            <li class="nav-item">
                                <a class="hcbae-nav nav-link" data-toggle="tab" href="#standby_creator" role="tab">크리에이터 신청명단</a>
                            </li>
                            </c:if>
                        
                            <li class="nav-item">
                                <a class="hcbae-nav nav-link active" data-toggle="tab" href="#jjim-list" role="tab">좋아요 리스트</a>
                            </li>
                            <li class="nav-item">
                                <a class="hcbae-nav nav-link" data-toggle="tab" href="#member-modify" role="tab">회원정보수정</a>
                            </li>
                            <li class="nav-item">
                                <a class="hcbae-nav nav-link" data-toggle="tab" href="#my_order" role="tab">주문내역</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div> <!--end of My Page Tabs Headers-->

        
            <!-- Tab panes -->
            <div class="tab-content"> <!--My Page Tabs Contents-->
                <div id="standby_creator" class="container tab-pane" >
                    <h3>크리에이터 신청 리스트</h3>
                      <table style="">
                      <colgroup>
                          <col width="12%">
                          <col width="12%">
                          <col width="20%">
                          <col width="20%">
                          <col width="30%">
                          <col width="6%">
                      </colgroup>
                       <thead>
                          <tr>
                              <th style="padding:1.5px !important;">이름</th>
                              <th style="padding:1.5px !important;">이메일</th>
                              <th style="padding:1.5px !important;">소개</th>
                              <th style="padding:1.5px !important;">대표사진</th>
                              <th style="padding:1.5px !important;">배너사진</th>
                              <th style="padding:1.5px !important;">등록/취소</th>
                          </tr>
                      </thead>  
                      <tbody>
                        <c:if test="${empty standby_list}">
                          <tr>
                            <td colspan="6" style="border-bottom:1px solid black">신청 내역이 없습니다.</td>   
                          </tr>
                        </c:if>
                        <c:if test="${!empty standby_list}">
                          <c:set var="listCount" value="0"/>
                          <c:forEach items="${standby_list}" var="creator">
                             <tr class="order_click_tr">
                                <td style="padding:3px !important;">${creator.creator_name}</td>
                                <td style="padding:3px !important;">${creator.member_email}</td>
                                <td style="padding:3px !important;">${creator.creator_profile_content}</td>
                                <td style="padding:3px !important;"><img src="${Path.IMG_STORE_COMMUNITY_SHORT}${creator.creator_profile_photo}"></td>
                                <td style="padding:3px !important;"><img src="${Path.IMG_STORE_COMMUNITY_SHORT}${creator.creator_banner_photo}"></td>
                                <td style="padding:3px !important;">
                                  <input type="button" value="등록" onclick="addCreator_standby('${creator.creator_name}')"><br/>
                                  <input type="button" value="삭제" onclick="deleteCreator_standby('${creator.creator_name}')">
                                  <script type="text/javascript">
                                  function deleteCreator_standby(creator_name){
                                      let result = confirm(creator_name+'을 크리에이터 신청 리스트에서 삭제하겠습니까?')
                                      if(result){
                                          location.href="deleteCreator_standby.do?creator_name="+creator_name;    
                                      }
                                  }
                                  function addCreator_standby(creator_name){
                                      let result = confirm(creator_name+'을 크리에이터로 등록하겠습니까?')
                                      if(result){
                                          location.href="addCreator_standby.do?creator_name="+creator_name;    
                                      }
                                  }
                                  </script>
                                  
                                </td>
                              </tr>
                          <c:set var="listCount" value="${listCount + 1}"/>
                          </c:forEach>
                        </c:if>
                      </tbody>
                  </table>
                </div>
                
                
                <div id="jjim-list" class="container tab-pane active">
                    <h3>좋아요 리스트</h3>
                    <p>리스트를 어떻게 표시할까?</p>
                    
                    <div id="mypage_funding_area"></div>
                    
                    <div id="mypage_goods_area"></div>
                    
                    
                    <script type="text/javascript">
                    function readFundingLikeList(){
                      if('${member.member_email}' == '') {
                            return;
                        }
                        
                        var xmlHttpFundingLikeList = new XMLHttpRequest();
                        xmlHttpFundingLikeList.onreadystatechange = function() {
                             if (xmlHttpFundingLikeList.readyState == 4 && xmlHttpFundingLikeList.status == 200) {
                               //var returnV = xmlHttpFundingLikeList.responseText; 
                                 //console.log(returnV);
                                 document.getElementById("mypage_funding_area").innerHTML= xmlHttpFundingLikeList.responseText;
                             }
                        };
                        xmlHttpFundingLikeList.open("GET", "readFundingLikeList.do?member_email=${member.member_email}", true); // true for asynchronous
                        xmlHttpFundingLikeList.send();
                    }
                    readFundingLikeList();
                    
                    function readGoodsLikeList(){
                        if('${member.member_email}' == '') {
                            return;
                        }
                        
                        var xmlHttpGoodsLikeList = new XMLHttpRequest();
                        xmlHttpGoodsLikeList.onreadystatechange = function() {
                             if (xmlHttpGoodsLikeList.readyState == 4 && xmlHttpGoodsLikeList.status == 200) {
                                 //var returnV = xmlHttpGoodsLikeList.responseText; 
                                 //console.log(returnV);
                                 document.getElementById("mypage_goods_area").innerHTML= xmlHttpGoodsLikeList.responseText;
                             }
                        };
                        xmlHttpGoodsLikeList.open("GET", "readGoodsLikeList.do?member_email=${member.member_email}", true); // true for asynchronous
                        xmlHttpGoodsLikeList.send();
                    }
                    readGoodsLikeList();
                    </script>
                    
                </div>
              
                <div id="member-modify" class="container tab-pane fade">
                
                    <div class="hcbae-member-modify-area">
                        <h3>회원 이메일</h3>
                        <label>${member.member_email}</label> 
                        <label style="float:right;">가입일 : ${member.member_joindate}</label>
                    </div>
                    <div class="hcbae-member-modify-area">
                        <h3>이름</h3>
                        <label>${member.member_name}</label>
                        
                        <span class="hcbae-member-modify-button">
                        <input class="btn" type="button" data-target="#member-name-area" data-toggle="collapse" value="수정" onclick="changeButtonText(this)">
                        </span>
                        <div id="member-name-area" class="collapse">
                          <div class="hcbae-member-modify-input">
                            <label>새로운 이름을 입력해주세요.</label><br>
                            <input type="text" class="hcbae-mypage-input-box-border" id="nowName" placeholder="새로운 이름" onKeyUp="checkName(this);">
                            <label id="checkName_Id"></label>
                          </div>
                          <span>
                          <input class="btn" type="button" value="변경하기" onclick="changeName('${member.member_email}')">
                          </span>
                        </div>
                    </div>
                    
                    <div class="hcbae-member-modify-area">
                        <h3>프로필 사진</h3>
                        <p>${member.member_profile_pic}</p>
                        
                        <c:if test="${!empty member.member_profile_pic}">
                          <img style="height:100px;" src="${member.member_profile_pic}">                                
                        </c:if>
                        
                        <span class="hcbae-member-modify-button">
                        
                        <c:if test="${member.member_origin_code==Member_origin.SIGNUP_NORMAL}">
                          <input class="btn" type="button" data-target="#profile-pic-area" data-toggle="collapse" value="수정" onclick="changeButtonText(this)">
                        </c:if>

                        </span>
                        <div id="profile-pic-area" class="collapse">
                          <p>사진 저장 관련하여 정리 되고 프로필 사진을 표시할 수 있게 되면 추가 수정하자.</p>
                        </div>
                    </div>
                    <c:if test="${member.member_origin_code==Member_origin.SIGNUP_NORMAL}">
                    <div class="hcbae-member-modify-area">
                        <h3>비밀번호</h3>
                        <span class="hcbae-member-modify-button">
                        <input class="btn" type="button" data-target="#password-area" data-toggle="collapse" value="수정" onclick="changeButtonText(this); clearModifyPasswordArea();">
                        </span>
                        
                        <div id="password-area" class="collapse">

                          <div class="hcbae-member-modify-input">
                            <label>현재 비밀번호를 입력해주세요.</label><br>
                            <input type="password" class="hcbae-mypage-input-box-border" id="nowPassword" placeholder="현재 비밀번호" onKeyUp="checkPassword('${member.member_email}');">
                            <label id="checkPasswordResult"></label>
                          </div>
                          
                          <div class="hcbae-member-modify-input">
                            <label>새로운 비밀번호를 입력해주세요.</label><br>
                            <input type="password" class="hcbae-mypage-input-box-border" id="newPassword" placeholder="새로운 비밀번호" >
                            <br><br>
                            <input type="password" class="hcbae-mypage-input-box-border" id="newPasswordAgain" placeholder="새로운 비밀번호 확인" onKeyUp="checkNewPassword()">
                            <label id="checkNewPasswordResult"></label>
                          </div>
                          
                          <span>
                          <input class="btn" type="button" value="변경하기" onclick="changePassword('${member.member_email}')">
                          </span>

                        </div>
                    </div>
                    </c:if>
                    
                    <c:if test="${member.member_category_code == Member_category.MEMBER_NORMAL}">
                    <div class="hcbae-member-modify-area">
                        <h3>크리에이터 신청하기</h3>
                        <span class="hcbae-member-modify-button">
                        <input class="btn" type="button" data-target="#craetor-join-area" data-toggle="collapse" value="수정" onclick="changeButtonText(this)">
                        </span>
                        
                        <div id="craetor-join-area" class="collapse">
                        <p>크리에이터로 등록되면, 펀딩/굿즈 등록과 커뮤니티 게시판 생성을 할 수 있습니다.<br>
                                                크리에이터 신청을 하면, 크리콕 내부 검토 후에 크리에이터로 등록해 드립니다.</p>
                        
                        <span>
                          <c:if test="${empty CreatorStandbyExist}">
                            <script>
                            function joinCreator(){
                                const popupWidth = 850;
                                const popupHeight = 750;
                                const popupX = (window.screen.width/2)-(popupWidth/2);
                                const popupY= (window.screen.height/2)-(popupHeight/2);
                                window.open('joinCreator.do', '크리에어터 신청하기', 'status=no, height=' + popupHeight  + ', width=' + popupWidth  + ', left='+ popupX + ', top='+ popupY);
                            }
                            </script>
                            <input class="btn" type="button" value="신청하기" onclick="joinCreator();">
                          </c:if>
                          <c:if test="${!empty CreatorStandbyExist}">
                            <script>
                            function joinCreatorUpdate(member_email){
                                const popupWidth = 850;
                                const popupHeight = 750;
                                const popupX = (window.screen.width/2)-(popupWidth/2);
                                const popupY= (window.screen.height/2)-(popupHeight/2);
                                window.open('joinCreatorUpdate.do?member_email='+member_email, '크리에어터 신청 수정하기', 'status=no, height=' + popupHeight  + ', width=' + popupWidth  + ', left='+ popupX + ', top='+ popupY);
                            }
                            </script>
                            <input class="btn" type="button" value="신청 수정하기" onclick="joinCreatorUpdate('${member.member_email}');">
                          </c:if>

                        </span>
                        </div>   
                    </div>
                    </c:if>
                    
                    <div class="hcbae-member-modify-area">
                        <h3>수정할 정보가 뭐가 더 있을까?</h3>
                        <span class="hcbae-member-modify-button">
                        <input class="btn" type="button" data-target="#add-modify-area" data-toggle="collapse" value="수정" onclick="changeButtonText(this)">
                        </span>
                        
                        <div id="add-modify-area" class="collapse">
                        <p>수정할 내용을 어떻게 수정할 지 고민해보고 추가하자.</p>
                        </div>   
                    </div>
                    
                    <div class="hcbae-member-modify-area">
                        <h3>회원탈퇴</h3>
                        <span class="hcbae-member-modify-button">
                        <input class="btn" type="button" data-target="#secession-area" data-toggle="collapse" value="수정">
                        </span>
                        
                        <div id="secession-area" class="collapse">
                            <p>CREAKOK을 탈퇴하시겠습니까? (굳이? ㅠㅠ)</p>
                            <c:if test="${member.member_origin_code==Member_origin.SIGNUP_NORMAL}">
                              <div class="hcbae-member-modify-input">
                                <label>현재 비밀번호를 입력해주세요.</label><br>
                                <input type="password" class="hcbae-mypage-input-box-border" id="secessionPassword" placeholder="현재 비밀번호" onKeyUp="checkSecession('${member.member_email}')">
                                <label id="checkSecessionResult"></label>
                              </div>
                              
                              <span>
                              <input class="btn" type="button" value="탈퇴하기" onclick="secessionPassword('${member.member_email}')">
                              </span>              
                            </c:if>
                            <c:if test="${member.member_origin_code!=Member_origin.SIGNUP_NORMAL}">
                              <span>
                              <input class="btn" type="button" value="탈퇴하기" onclick="secessionSocial('${member.member_email}')">
                              </span>
                            </c:if>
                        </div>                           
                    </div>
                    
                </div>
                
                
                
                <!--  주문 내역   -->
                 <div id="my_order" class="container tab-pane" style="margin-bottom:10%">
                    <c:if test="${empty order_info}">
                       <h3>주문 내역(총  0개)</h3>              
                    </c:if>
                    <h3>주문 내역(총 ${order_count}개)</h3>
                    <p style="margin-bottom:20px">주문 번호를 클릭하시면 해당 주문에 대한 상세내역을 확인하실 수 있습니다.</p>
                    
                    <div id="mypage_funding_area"></div>
                    
                    <div id="mypage_goods_area"></div>
                    
                    <table style="">
                    <colgroup>
                        <col width="5%">
                        <col width="15%">
                        <col width="20%">
                        <col width="30%">
                        <col width="15%">
                        <col width="15%">
                    </colgroup>
                     <thead>
                        <tr>
                            <th style="padding:1.5px !important;">No</th>
                            <th style="padding:1.5px !important;">주문일자</th>
                            <th style="padding:1.5px !important;">주문번호</th>
                            <th style="padding:1.5px !important;">상품명</th>
                            <th style="padding:1.5px !important;">결제금액</th>
                            <th style="padding:1.5px !important;">결제상태</th>
                        </tr>
                    </thead>  
                    <tbody>
                      
                      <c:if test="${empty order_info.order_list}">
                          <tr>
                           <td colspan="6" style="border-bottom:1px solid black">
                                                                주문 내역이 없습니다.
                           </td>   
                           </tr>            
                        </c:if>
                        
                        <c:if test="${!empty order_info.order_list}">
                         <c:forEach items="${order_info.order_list}" var="order_info">
                             <tr class="order_click_tr">
                                 <td style="padding:3px !important;">${order_info.order_index}</td>
                                 <td style="padding:3px !important;">${order_info.buy_date}</td>
                                 <td style="padding:3px !important;"  class="order_click_td"><a href="member_orderdetail.do?order_indexStr=${order_info.order_index}&member_email=${order_info.member_email}" style="color:black;">${order_info.buyer_buyid}</a></td>
                                 <td style="padding:3px !important;">${order_info.buy_product_name}</td>
                                 <td style="padding:3px !important;">${order_info.buyer_pay_price} 원</td>
                                 <td id="review_view" style="padding:1.5px !important;">
                                    <c:if test="${order_info.buyer_pay_ok == 'ready'}">
                                        	미결제
                                     </c:if>
                                     <c:if test="${order_info.buyer_pay_ok == 'paid'}">
                                        	결제완료
                                     </c:if>
                                     <c:if test="${order_info.buyer_pay_ok == 'cancelled'}">
                                                                                        결제취소
                                     </c:if>
                                     <c:if test="${order_info.buyer_pay_ok == 'failed'}">
                                                                                        결제실패
                                     </c:if>
                                 </td>
                              </tr>
                        </c:forEach>
                     </c:if>
                     
                      <tr>
                            <td colspan="6" align="center" style="border-bottom:0px">
                             <c:forEach begin="1" end="${order_info.order_totalPageCount}" var="i">
                                      <a href="member_order.do?order_cp=${i}&member_email=${order_info.member_email}#fix_point" style="color:black; font-size:11pt">
                                  <c:choose> 
                                  <c:when test="${i==order_info.order_cp}">
                                      <strong>${i}</strong>
                                  </c:when>
                                  <c:otherwise>
                                      ${i}
                                  </c:otherwise>
                                  </c:choose>
                              </a>
                              
                              &nbsp;
                              </c:forEach>
                            </td>
                       </tr>
                    </tbody>
                </table>
                </div>
                
            </div> <!--My Page Tabs Contents-->
        </div>
    </div>





    <!-- Footer Bottom Area -->
    <div id="footer_div">
    <jsp:include page="creakok_footer.jsp" flush="true"/>
    </div>
    <!-- Footer Bottom Area End ##### -->

    <!-- ##### All Javascript Files ##### -->
    <!-- jQuery-2.2.4 js -->
    <script src="js/jquery/jquery-2.2.4.min.js"></script>
    <!-- Popper js -->
    <script src="js/bootstrap/popper.min.js"></script>
    <!-- Bootstrap js -->
    <script src="js/bootstrap/bootstrap.min.js"></script>
    <!-- All Plugins js -->
    <script src="js/plugins/plugins.js"></script>
    <!-- Active js -->
    <script src="js/active.js"></script>
    <script src="js/hcbae_js.js"></script>

    
    <jsp:include page="Language.jsp" flush="false">
    <jsp:param name="page_name" value="${requestScope['javax.servlet.forward.request_uri']}"/>
    </jsp:include>
    
</body>

</html>