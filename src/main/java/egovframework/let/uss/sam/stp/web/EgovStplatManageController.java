package egovframework.let.uss.sam.stp.web;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.fdl.security.userdetails.util.EgovUserDetailsHelper;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.com.cmm.LoginVO;
import egovframework.let.uss.sam.stp.service.EgovStplatManageService;
import egovframework.let.uss.sam.stp.service.StplatManageDefaultVO;
import egovframework.let.uss.sam.stp.service.StplatManageVO;
import lombok.RequiredArgsConstructor;

/**
 *
 * 약관내용을 처리하는 비즈니스 구현 클래스
 * 
 * @author 공통서비스 개발팀 박정규
 * @since 2009.04.01
 * @version 1.0
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.04.01  박정규          최초 생성
 *   2024.09.24  강동휘          컨트리뷰션 롬복 생성자 기반 종속성 주입
 *
 *      </pre>
 */
@Controller
@RequiredArgsConstructor
public class EgovStplatManageController {

	private final EgovStplatManageService stplatManageService;

	/** EgovPropertyService */
	private final EgovPropertyService propertiesService;

	// Validation 관련
	private final DefaultBeanValidator beanValidator;

	/**
	 * 개별 배포시 메인메뉴를 조회한다.
	 * 
	 * @param model
	 * @return "/uss/sam/stp/EgovMain"
	 * @throws Exception
	 */
	@RequestMapping(value = "/uss/sam/stp/EgovMain.do")
	public String EgovMain(ModelMap model) throws Exception {
		return "/uss/sam/stp/EgovMain";
	}

	/**
	 * 메뉴를 조회한다.
	 * 
	 * @param model
	 * @return "/uss/sam/stp/EgovLeft"
	 * @throws Exception
	 */
	@RequestMapping(value = "/uss/sam/stp/EgovLeft.do")
	public String EgovLeft(ModelMap model) throws Exception {
		return "/uss/sam/stp/EgovLeft";
	}

	/**
	 * 약관정보 목록을 조회한다.
	 * 
	 * @param searchVO
	 * @param model
	 * @return "/uss/sam/stp/EgovStplatListInqire"
	 * @throws Exception
	 */
	@RequestMapping(value = "/uss/sam/stp/StplatListInqire.do")
	public String selectStplatList(@ModelAttribute("searchVO") StplatManageDefaultVO searchVO, ModelMap model)
			throws Exception {

		/** EgovPropertyService.SiteList */
		searchVO.setPageUnit(propertiesService.getInt("pageUnit"));
		searchVO.setPageSize(propertiesService.getInt("pageSize"));

		/** pageing */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(searchVO.getPageUnit());
		paginationInfo.setPageSize(searchVO.getPageSize());

		searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
		searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		model.addAttribute("resultList", stplatManageService.selectStplatList(searchVO));

		int totCnt = stplatManageService.selectStplatListTotCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);

		return "/uss/sam/stp/EgovStplatListInqire";
	}

	/**
	 * 약관정보상세내용을 조회한다.
	 * 
	 * @param stplatManageVO
	 * @param searchVO
	 * @param model
	 * @return "/uss/sam/stp/EgovStplatDetailInqire"
	 * @throws Exception
	 */
	@RequestMapping("/uss/sam/stp/StplatDetailInqire.do")
	public String selectStplatDetail(StplatManageVO stplatManageVO,
			@ModelAttribute("searchVO") StplatManageDefaultVO searchVO, ModelMap model) throws Exception {

		StplatManageVO vo = stplatManageService.selectStplatDetail(stplatManageVO);

		model.addAttribute("result", vo);

		return "/uss/sam/stp/EgovStplatDetailInqire";
	}

	/**
	 * 약관정보를 등록하기 위한 전 처리
	 * 
	 * @param searchVO
	 * @param model
	 * @return "/uss/sam/stp/EgovStplatCnRegist"
	 * @throws Exception
	 */
	@RequestMapping("/uss/sam/stp/StplatCnRegistView.do")
	public String insertStplatCnView(@ModelAttribute("searchVO") StplatManageDefaultVO searchVO, Model model)
			throws Exception {

		model.addAttribute("stplatManageVO", new StplatManageVO());

		return "/uss/sam/stp/EgovStplatCnRegist";

	}

	/**
	 * 약관정보를 등록한다.
	 * 
	 * @param searchVO
	 * @param stplatManageVO
	 * @param bindingResult
	 * @return "forward:/uss/sam/stp/StplatListInqire.do"
	 * @throws Exception
	 */
	@RequestMapping("/uss/sam/stp/StplatCnRegist.do")
	public String insertStplatCn(@ModelAttribute("searchVO") StplatManageDefaultVO searchVO,
			@ModelAttribute("stplatManageVO") StplatManageVO stplatManageVO, BindingResult bindingResult)
			throws Exception {

		beanValidator.validate(stplatManageVO, bindingResult);

		if (bindingResult.hasErrors()) {

			return "/uss/olh/wor/EgovStplatCnRegist";

		}

		// 로그인VO에서 사용자 정보 가져오기
		LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();

		String frstRegisterId = loginVO.getUniqId();

		stplatManageVO.setFrstRegisterId(frstRegisterId); // 최초등록자ID
		stplatManageVO.setLastUpdusrId(frstRegisterId); // 최종수정자ID

		stplatManageService.insertStplatCn(stplatManageVO);

		return "forward:/uss/sam/stp/StplatListInqire.do";
	}

	/**
	 * 약관정보를 수정하기 위한 전 처리
	 * 
	 * @param useStplatId
	 * @param searchVO
	 * @param model
	 * @return "/uss/sam/stp/EgovStplatCnUpdt"
	 * @throws Exception
	 */
	@RequestMapping("/uss/sam/stp/StplatCnUpdtView.do")
	public String updateStplatCnView(@RequestParam("useStplatId") String useStplatId,
			@ModelAttribute("searchVO") StplatManageDefaultVO searchVO, ModelMap model) throws Exception {

		StplatManageVO stplatManageVO = new StplatManageVO();

		// Primary Key 값 세팅
		stplatManageVO.setUseStplatId(useStplatId);

		// 변수명은 CoC 에 따라
		model.addAttribute(selectStplatDetail(stplatManageVO, searchVO, model));

		// 변수명은 CoC 에 따라 JSTL사용을 위해
		model.addAttribute("stplatManageVO", stplatManageService.selectStplatDetail(stplatManageVO));

		return "/uss/sam/stp/EgovStplatCnUpdt";
	}

	/**
	 * 약관정보를 수정 처리한다.
	 * 
	 * @param searchVO
	 * @param stplatManageVO
	 * @param bindingResult
	 * @return "forward:/uss/sam/stp/StplatListInqire.do"
	 * @throws Exception
	 */
	@RequestMapping("/uss/sam/stp/StplatCnUpdt.do")
	public String updateStplatCn(@ModelAttribute("searchVO") StplatManageDefaultVO searchVO,
			@ModelAttribute("stplatManageVO") StplatManageVO stplatManageVO, BindingResult bindingResult)
			throws Exception {

		// Validation
		beanValidator.validate(stplatManageVO, bindingResult);

		if (bindingResult.hasErrors()) {

			return "/uss/olh/wor/EgovStplatCnUpdt";

		}

		// 로그인VO에서 사용자 정보 가져오기
		LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();

		String lastUpdusrId = loginVO.getUniqId();

		stplatManageVO.setLastUpdusrId(lastUpdusrId); // 최종수정자ID

		stplatManageService.updateStplatCn(stplatManageVO);

		return "forward:/uss/sam/stp/StplatListInqire.do";
	}

	/**
	 * 약관정보를 삭제 처리한다.
	 * 
	 * @param stplatManageVO
	 * @param searchVO
	 * @return "forward:/uss/sam/stp/StplatListInqire.do"
	 * @throws Exception
	 */
	@RequestMapping("/uss/sam/stp/StplatCnDelete.do")
	public String deleteStplatCn(StplatManageVO stplatManageVO,
			@ModelAttribute("searchVO") StplatManageDefaultVO searchVO) throws Exception {

		stplatManageService.deleteStplatCn(stplatManageVO);

		return "forward:/uss/sam/stp/StplatListInqire.do";
	}

}
