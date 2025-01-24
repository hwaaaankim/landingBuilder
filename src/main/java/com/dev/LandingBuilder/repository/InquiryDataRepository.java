package com.dev.LandingBuilder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.LandingBuilder.model.client.InquiryData;

@Repository
public interface InquiryDataRepository extends JpaRepository<InquiryData, Long> {
	List<InquiryData> findAllByInquiry_Id(Long inquiryId);
}
