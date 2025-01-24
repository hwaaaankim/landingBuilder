package com.dev.LandingBuilder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.LandingBuilder.model.client.Inquiry;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
	List<Inquiry> findAllByCompany_Id(Long companyId);
}

