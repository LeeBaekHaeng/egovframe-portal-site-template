package egovframework.let.uss.olp.qrm.web;

import java.util.Map;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.fdl.security.userdetails.util.EgovUserDetailsHelper;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.com.cmm.ComDefaultCodeVO;
import egovframework.com.cmm.ComDefaultVO;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.service.EgovCmmUseService;
import egovframework.let.uss.olp.qrm.service.EgovQustnrRespondManageService;
import egovframework.let.uss.olp.qrm.service.QustnrRespondManageVO;
import lombok.RequiredArgsConstructor;

/**
 * 설문응답자관리 Controller Class 구현
 * 
 * @author 공통서비스 장동한
 * @since 2009.03.20
 * @version 1.0
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.03.20  장동한          최초 생성
 *   2011.08.31  JJY           경량환경 템플릿 커스터마이징버전 생성
 *   2024.09.26  이백행          컨트리뷰션 롬복 생성자 기반 종속성 주입
 *
 *      </pre>
 */
@Controller
@RequiredArgsConstructor
public class EgovQustnrRespondManageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EgovQustnrRespondManageController.class);

	private final DefaultBeanValidator beanValidator;

	/** EgovMessageSource */
	private final EgovMessageSource egovMessageSource;

	private final EgovQustnrRespondManageService egovQustnrRespondManageService;

	/** EgovPropertyService */
	private final EgovPropertyService propertiesService;

	private final EgovCmmUseService cmmUseService;

	/**
	 * 응답자정보 목록을 조회한다.
	 * 
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrRespondManageVO
	 * @param model
	 * @return "/uss/olp/qrm/EgovQustnrRespondManageList"
	 * @throws Exception
	 */
	@RequestMapping(value = "/uss/olp/qrm/EgovQustnrRespondManageList.do")
	public String EgovQustnrRespondManageList(@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<String, Object> commandMap, QustnrRespondManageVO qustnrRespondManageVO, ModelMap model)
			throws Exception {

		String sSearchMode = commandMap.get("searchMode") == null ? "" : (String) commandMap.get("searchMode");

		// 설문지정보에서 넘어오면 자동검색 설정
		if (sSearchMode.equals("Y")) {
			searchVO.setSearchCondition("QESTNR_ID");
			searchVO.setSearchKeyword(qustnrRespondManageVO.getQestnrId());
		}

		/** EgovPropertyService.sample */
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

		model.addAttribute("resultList", egovQustnrRespondManageService.selectQustnrRespondManageList(searchVO));

		model.addAttribute("searchKeyword",
				commandMap.get("searchKeyword") == null ? "" : (String) commandMap.get("searchKeyword"));
		model.addAttribute("searchCondition",
				commandMap.get("searchCondition") == null ? "" : (String) commandMap.get("searchCondition"));

		int totCnt = egovQustnrRespondManageService.selectQustnrRespondManageListCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);

		return "/uss/olp/qrm/EgovQustnrRespondManageList";
	}

	/**
	 * 응답자정보 목록을 상세조회 조회한다.
	 * 
	 * @param searchVO
	 * @param qustnrRespondManageVO
	 * @param commandMap
	 * @param model
	 * @return "/uss/olp/qrm/EgovQustnrRespondManageDetail"
	 * @throws Exception
	 */
	@RequestMapping(value = "/uss/olp/qrm/EgovQustnrRespondManageDetail.do")
	public String EgovQustnrRespondManageDetail(@ModelAttribute("searchVO") ComDefaultVO searchVO,
			QustnrRespondManageVO qustnrRespondManageVO, @RequestParam Map<String, Object> commandMap, ModelMap model)
			throws Exception {

		String sLocationUrl = "/uss/olp/qrm/EgovQustnrRespondManageDetail";

		String sCmd = commandMap.get("cmd") == null ? "" : (String) commandMap.get("cmd");

		if (sCmd.equals("del")) {
			egovQustnrRespondManageService.deleteQustnrRespondManage(qustnrRespondManageVO);
			sLocationUrl = "redirect:/uss/olp/qrm/EgovQustnrRespondManageList.do";
		} else {
			// 성별코드조회
			ComDefaultCodeVO voComCode = new ComDefaultCodeVO();
			voComCode.setCodeId("COM014");
			model.addAttribute("comCode014", cmmUseService.selectCmmCodeDetail(voComCode));

			// 직업코드조회
			voComCode.setCodeId("COM034");
			model.addAttribute("comCode034", cmmUseService.selectCmmCodeDetail(voComCode));

			model.addAttribute("resultList",
					egovQustnrRespondManageService.selectQustnrRespondManageDetail(qustnrRespondManageVO));
		}

		return sLocationUrl;
	}

	/**
	 * 응답자정보를 수정한다.
	 * 
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrRespondManageVO
	 * @param bindingResult
	 * @param model
	 * @return "/uss/olp/qrm/EgovQustnrRespondManageModify"
	 * @throws Exception
	 */
	@RequestMapping(value = "/uss/olp/qrm/EgovQustnrRespondManageModify.do")
	public String QustnrRespondManageModify(@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<String, Object> commandMap,
			@ModelAttribute("qustnrRespondManageVO") QustnrRespondManageVO qustnrRespondManageVO,
			BindingResult bindingResult, ModelMap model) throws Exception {

		// 0. Spring Security 사용자권한 처리
		Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
		if (!isAuthenticated) {
			model.addAttribute("message", egovMessageSource.getMessage("fail.common.login"));
			return "uat/uia/EgovLoginUsr";
		}

		// 로그인 객체 선언
		LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();

		String sLocationUrl = "/uss/olp/qrm/EgovQustnrRespondManageModify";

		String sCmd = commandMap.get("cmd") == null ? "" : (String) commandMap.get("cmd");

		// 성별코드조회
		ComDefaultCodeVO voComCode = new ComDefaultCodeVO();
		voComCode.setCodeId("COM014");
		model.addAttribute("comCode014", cmmUseService.selectCmmCodeDetail(voComCode));

		// 직업코드조회
		voComCode.setCodeId("COM034");
		model.addAttribute("comCode034", cmmUseService.selectCmmCodeDetail(voComCode));

		if (sCmd.equals("save")) {
			// 서버 validate 체크
			beanValidator.validate(qustnrRespondManageVO, bindingResult);
			if (bindingResult.hasErrors()) {

				return sLocationUrl;
			}
			// 아이디 설정
			qustnrRespondManageVO.setFrstRegisterId(loginVO.getUniqId());
			qustnrRespondManageVO.setLastUpdusrId(loginVO.getUniqId());

			egovQustnrRespondManageService.updateQustnrRespondManage(qustnrRespondManageVO);
			sLocationUrl = "redirect:/uss/olp/qrm/EgovQustnrRespondManageList.do";
		} else {
			model.addAttribute("resultList",
					egovQustnrRespondManageService.selectQustnrRespondManageDetail(qustnrRespondManageVO));
		}

		return sLocationUrl;
	}

	/**
	 * 응답자정보를 등록한다.
	 * 
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrRespondManageVO
	 * @param bindingResult
	 * @param model
	 * @return "/uss/olp/qrm/EgovQustnrRespondManageRegist"
	 * @throws Exception
	 */
	@RequestMapping(value = "/uss/olp/qrm/EgovQustnrRespondManageRegist.do")
	public String QustnrRespondManageRegist(@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<String, Object> commandMap,
			@ModelAttribute("qustnrRespondManageVO") QustnrRespondManageVO qustnrRespondManageVO,
			BindingResult bindingResult, ModelMap model) throws Exception {

		// 0. Spring Security 사용자권한 처리
		Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
		if (!isAuthenticated) {
			model.addAttribute("message", egovMessageSource.getMessage("fail.common.login"));
			return "uat/uia/EgovLoginUsr";
		}

		// 로그인 객체 선언
		LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();

		String sLocationUrl = "/uss/olp/qrm/EgovQustnrRespondManageRegist";

		String sCmd = commandMap.get("cmd") == null ? "" : (String) commandMap.get("cmd");
		LOGGER.info("cmd => {}", sCmd);

		// 성별코드조회
		ComDefaultCodeVO voComCode = new ComDefaultCodeVO();
		voComCode.setCodeId("COM014");
		model.addAttribute("comCode014", cmmUseService.selectCmmCodeDetail(voComCode));

		// 직업코드조회
		voComCode.setCodeId("COM034");
		model.addAttribute("comCode034", cmmUseService.selectCmmCodeDetail(voComCode));

		if (sCmd.equals("save")) {
			// 서버 validate 체크
			beanValidator.validate(qustnrRespondManageVO, bindingResult);
			if (bindingResult.hasErrors()) {

				return sLocationUrl;
			}
			// 아이디 설정
			qustnrRespondManageVO.setFrstRegisterId(loginVO.getUniqId());
			qustnrRespondManageVO.setLastUpdusrId(loginVO.getUniqId());

			egovQustnrRespondManageService.insertQustnrRespondManage(qustnrRespondManageVO);
			sLocationUrl = "redirect:/uss/olp/qrm/EgovQustnrRespondManageList.do";
		}

		return sLocationUrl;
	}

}
