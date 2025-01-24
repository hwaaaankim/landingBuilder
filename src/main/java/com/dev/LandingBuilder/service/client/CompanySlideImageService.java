package com.dev.LandingBuilder.service.client;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dev.LandingBuilder.model.client.Company;
import com.dev.LandingBuilder.model.client.CompanySlideImage;
import com.dev.LandingBuilder.repository.CompanyRepository;
import com.dev.LandingBuilder.repository.CompanySlideImageRepository;

@Service
public class CompanySlideImageService {
	
    @Autowired
    private CompanySlideImageRepository slideImageRepository;

    @Autowired
    private CompanyRepository companyRepository;
    
    @Value("${upload.path}")
    private String uploadPath;

    // 슬라이드 이미지 저장
    public CompanySlideImage createSlideImage(Long companyId, CompanySlideImage slideImage) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id " + companyId));
        slideImage.setCompany(company);
        slideImage.setStoragePath(createSlideImagePath(company));
        return slideImageRepository.save(slideImage);
    }

    // 슬라이드 이미지 조회
    public List<CompanySlideImage> getSlideImagesByCompanyId(Long companyId) {
        return slideImageRepository.findAllByCompany_Id(companyId);
    }

    // 슬라이드 이미지 업데이트
    public CompanySlideImage updateSlideImage(Long id, CompanySlideImage updatedSlide) {
        return slideImageRepository.findById(id).map(slide -> {
            // 기존 파일 삭제
            deleteFile(slide.getStoragePath());

            // 경로 업데이트
            slide.setStoragePath(updatedSlide.getStoragePath());
            slide.setPublicUrl(updatedSlide.getPublicUrl());
            slide.setDisplayOrder(updatedSlide.getDisplayOrder());
            return slideImageRepository.save(slide);
        }).orElseThrow(() -> new RuntimeException("SlideImage not found with id " + id));
    }

    // 슬라이드 이미지 삭제
    public void deleteSlideImage(Long id) {
        slideImageRepository.findById(id).ifPresent(slide -> {
            deleteFile(slide.getStoragePath());
            slideImageRepository.delete(slide);
        });
    }

    // 기본 경로 생성
    private String getBasePath(Company company) {
        return Paths.get(uploadPath, company.getUrlSlug()).toString();
    }

    // 슬라이드 이미지 경로 생성
    private String createSlideImagePath(Company company) {
        return Paths.get(getBasePath(company), "slide").toString();
    }

    // 파일 삭제
    private void deleteFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                if (!file.delete()) {
                    throw new RuntimeException("Failed to delete file: " + filePath);
                }
            }
        }
    }
}
