package yjkellyjoo.contract.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import yjkellyjoo.contract.model.SourcecodeVo;
import yjkellyjoo.runtime.dao.ContractDaoSupport;

/**
 * SOURCECODE 테이블 관리 DAO
 * @author  yjkellyjoo
 * @since 	2020. 3. 17
 */
@Repository("yjkellyjoo.contract.dao.SourcecodeDao")
public class SourcecodeDao extends ContractDaoSupport {
	
	/**
	 * SOURCECODE 단일 레코드 조회
	 * @param address 키값
	 * @return SOURCECODE 레코드
	 */
	public SourcecodeVo selectData(String address) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("address", address);
		
		return getSqlSession().selectOne("yjkellyjoo.contract.dao.SourcecodeDao.selectData", paramMap);
	}
	
	
	/**
	 * SOURCECODE array 등록
	 * @param SOURCECODE array
	 */
	public void insertAllData(List<SourcecodeVo> contractArray) {
//		for (SourcecodeVo sourcecodeVo : contractArray) {
//			int _index = 0;
//			System.out.println(_index + ": " + sourcecodeVo.getAddress());
//			_index++;
//		}
		getSqlSession().insert("yjkellyjoo.contract.dao.SourcecodeDao.insertAllData", contractArray);
	}


	/**
	 * SOURCECODE 단일 레코드 갱신
	 * @param VO
	 */
	public void updateData(SourcecodeVo contract) {

		getSqlSession().update("yjkellyjoo.contract.dao.SourcecodeDao.updateData", contract);
	}
	
	/**
	 * SOURCECODE 다수 갱신
	 * @param ContractList
	 */
	public void updateDataList(List<SourcecodeVo> ContractList) {
		
		getSqlSession().update("yjkellyjoo.contract.dao.SourcecodeDao.updateDataList", ContractList);
	}

}
