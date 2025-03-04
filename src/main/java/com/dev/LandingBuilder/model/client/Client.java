package com.dev.LandingBuilder.model.client;

import java.util.Date;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name="tb_client")
@Entity
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="uni")
	@JsonProperty("uni")
	private String uni;
	
	@Column(name="name")
	private String name;
	
	@Column(name="phone")
	private String phone;
	
	@Column(name="email")
	@Nullable
	private String email;
	
	@Column(name="content")
	@Nullable
	private String content;
	
	@Column(name="subject")
	private String subject;
	
	@Column(name="visitdate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date visitDate;
	
	@Column(name="inquirydate")
	private Date inquiryDate;
	
	@Column(name="correctdate")
	private Date correctDate;
	
	@Column(name="sign")
	private Boolean sign;
	
}






















