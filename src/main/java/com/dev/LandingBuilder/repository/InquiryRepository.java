package com.dev.LandingBuilder.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.LandingBuilder.model.client.Inquiry;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
	List<Inquiry> findAllByCompany_Id(Long companyId);
	
	Page<Inquiry> findAllByOrderByCompany_IdDesc(Pageable pageable);
	
	Page<Inquiry> findAllByOrderByIdDesc(Pageable pageable);
}

