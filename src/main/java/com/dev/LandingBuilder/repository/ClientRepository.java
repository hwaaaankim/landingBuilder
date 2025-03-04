package com.dev.LandingBuilder.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.LandingBuilder.model.client.Client;


public interface ClientRepository extends JpaRepository<Client, Long>{

	Page<Client> findByOrderByInquiryDateDesc(Pageable pageable);

    Page<Client> findByNameOrderByInquiryDateDesc(Pageable pageable, String name);

    Page<Client> findByPhoneOrderByInquiryDateDesc(Pageable pageable, String phone);

    Page<Client> findByEmailOrderByInquiryDateDesc(Pageable pageable, String email);

    Page<Client> findBySubjectOrderByInquiryDateDesc(Pageable pageable, String subject);

    Page<Client> findByInquiryDateBetweenOrderByInquiryDateDesc(Pageable pageable, Date startDate, Date endDate);
	
}
