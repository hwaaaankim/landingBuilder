package com.dev.LandingBuilder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.LandingBuilder.model.client.FormField;

@Repository
public interface FormFieldRepository extends JpaRepository<FormField, Long> {
	List<FormField> findAllByCompany_Id(Long companyId);
	
	List<FormField> findByCompany_Id(Long companyId);
}
