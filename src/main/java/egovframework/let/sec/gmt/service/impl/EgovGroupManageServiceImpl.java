package egovframework.let.sec.gmt.service.impl;

import java.util.List;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import egovframework.let.sec.gmt.service.EgovGroupManageService;
import egovframework.let.sec.gmt.service.GroupManage;
import egovframework.let.sec.gmt.service.GroupManageVO;
import lombok.RequiredArgsConstructor;

/**
 * 그룹관리에 관한 ServiceImpl 클래스를 정의한다.
 * 
 * @author 공통서비스 개발팀 이문준
 * @since 2009.06.01
 * @version 1.0
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.03.11  이문준          최초 생성
 *   2011.08.31  JJY           경량환경 템플릿 커스터마이징버전 생성
 *   2024.09.28  강동휘          컨트리뷰션 롬복 생성자 기반 종속성 주입
 *
 *      </pre>
 */

@Service
@RequiredArgsConstructor
public class EgovGroupManageServiceImpl extends EgovAbstractServiceImpl implements EgovGroupManageService {

	private final GroupManageDAO groupManageDAO;

	/**
	 * 시스템사용 목적별 그룹 목록 조회
	 * 
	 * @param groupManageVO GroupManageVO
	 * @return List<GroupManageVO>
	 * @exception Exception
	 */
	@Override
	public List<GroupManageVO> selectGroupList(GroupManageVO groupManageVO) throws Exception {
		return groupManageDAO.selectGroupList(groupManageVO);
	}

	/**
	 * 검색조건에 따른 그룹정보를 조회
	 * 
	 * @param groupManageVO GroupManageVO
	 * @return GroupManageVO
	 * @exception Exception
	 */
	@Override
	public GroupManageVO selectGroup(GroupManageVO groupManageVO) throws Exception {
		return groupManageDAO.selectGroup(groupManageVO);
	}

	/**
	 * 그룹 기본정보를 화면에서 입력하여 항목의 정합성을 체크하고 데이터베이스에 저장
	 * 
	 * @param groupManage   GroupManage
	 * @param groupManageVO GroupManageVO
	 * @return GroupManageVO
	 * @exception Exception
	 */
	@Override
	public GroupManageVO insertGroup(GroupManage groupManage, GroupManageVO groupManageVO) throws Exception {
		groupManageDAO.insertGroup(groupManage);
		groupManageVO.setGroupId(groupManage.getGroupId());
		return groupManageDAO.selectGroup(groupManageVO);
	}

	/**
	 * 화면에 조회된 그룹의 기본정보를 수정하여 항목의 정합성을 체크하고 수정된 데이터를 데이터베이스에 반영
	 * 
	 * @param groupManage GroupManage
	 * @exception Exception
	 */
	@Override
	public void updateGroup(GroupManage groupManage) throws Exception {
		groupManageDAO.updateGroup(groupManage);
	}

	/**
	 * 불필요한 그룹정보를 화면에 조회하여 데이터베이스에서 삭제
	 * 
	 * @param groupManage GroupManage
	 * @exception Exception
	 */
	@Override
	public void deleteGroup(GroupManage groupManage) throws Exception {
		groupManageDAO.deleteGroup(groupManage);
	}

	/**
	 * 목록조회 카운트를 반환한다
	 * 
	 * @param groupManageVO GroupManageVO
	 * @return int
	 * @exception Exception
	 */
	@Override
	public int selectGroupListTotCnt(GroupManageVO groupManageVO) throws Exception {
		return groupManageDAO.selectGroupListTotCnt(groupManageVO);
	}
}