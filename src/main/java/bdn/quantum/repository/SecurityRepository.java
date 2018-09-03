package bdn.quantum.repository;

import java.util.List;

import bdn.quantum.model.SecurityEntity;

public interface SecurityRepository {

	List<SecurityEntity> getSecurities();
	List<SecurityEntity> getSecurities(Integer basketId);
	SecurityEntity createSecurity(SecurityEntity security);
	void deleteSecurity(Integer secId);
	
}
