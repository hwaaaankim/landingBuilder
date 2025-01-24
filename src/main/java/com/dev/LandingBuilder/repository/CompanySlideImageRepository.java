package com.dev.LandingBuilder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.LandingBuilder.model.client.CompanySlideImage;

@Repository
public interface CompanySlideImageRepository extends JpaRepository<CompanySlideImage, Long> {
	 List<CompanySlideImage> findAllByCompany_Id(Long companyId);
}


