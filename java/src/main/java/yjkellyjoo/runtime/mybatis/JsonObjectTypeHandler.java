/**
 * Copyright (c) 2018 IoTcube, Inc.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of IoTcube, Inc. 
 * You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with IoTcube, Inc.
*/

package yjkellyjoo.runtime.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 	hyeonggookim
 * @since 	2019. 1. 29.
 */
@Slf4j
public class JsonObjectTypeHandler<T> extends BaseTypeHandler<T> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType)
            throws SQLException {

    	String jsonStr = null;
    	
		try {
			jsonStr = new ObjectMapper().writeValueAsString(parameter);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			log.error("error in setNonNullParameter");
		}
		
        ps.setString(i, jsonStr);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
    	return convert(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return convert(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return convert(cs.getString(columnIndex));
    }

    private T convert(String b) {
    	T result = null;
    	
    	if (!StringUtils.isEmpty(b)) {
        	try {
        		result = new ObjectMapper().readValue(b, new TypeReference<T>() {});
    		} catch (Exception e) {
    			e.printStackTrace();
    			log.error("error in convert");
    		}
    	}
    	
    	return result;
    }

}
