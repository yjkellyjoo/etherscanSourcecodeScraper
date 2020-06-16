package yjkellyjoo.contract.model;

import lombok.Getter;
import lombok.Setter;

/**
 * SOURCECODE 테이블 관리 VO
 * @author  yjkellyjoo
 * @since 	2020. 3. 17.
 */
@Getter
@Setter
public class SourcecodeVo {
	private String abi;
	private String address;
	private String compilerVersion;
	private String constructorArguments;
	private String contractName;
	private String library;
	private String licenseType;
	private int optimizationUsed;
	private int runs;
	private String sourceCode;
	private String swarmSource;
	private String balance;
}
