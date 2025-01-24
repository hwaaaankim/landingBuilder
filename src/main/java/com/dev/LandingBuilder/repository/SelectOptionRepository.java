package com.dev.LandingBuilder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.LandingBuilder.model.client.SelectOption;

@Repository
public interface SelectOptionRepository extends JpaRepository<SelectOption, Long> {
	
	List<SelectOption> findAllByFormField_Id(Long formFieldId);
	
	List<SelectOption> findByFormField_Id(Long formFieldId);
}
