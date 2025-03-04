package com.dev.LandingBuilder.service.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dev.LandingBuilder.model.client.Company;
import com.dev.LandingBuilder.model.client.CompanySlideImage;
import com.dev.LandingBuilder.repository.CompanyRepository;
import com.dev.LandingBuilder.repository.CompanySlideImageRepository;

@Service
public class CompanyService {

	@Autowired
	private CompanySlideImageRepository slideImageRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Value("${upload.path}")
	private String uploadPath;

	public Company createCompany(
			String name, 
			String representativeName,
			String businessRegistrationNumber,
			String address, 
			String conferenceAddress, 
			String urlSlug, 
			MultipartFile logoImage, 
			MultipartFile mainImage,
			MultipartFile leftImage, 
			MultipartFile rightImage, 
			String homepageUrl, 
			String snsUrl, 
			String mapUrl,
			List<MultipartFile> slideImages) {
		try {
			// Define base paths
			String basePath = getBasePath(urlSlug);
			String logoPath = createDirectory(basePath, "logo");
			String mainPath = createDirectory(basePath, "main");
			String slidePath = createDirectory(basePath, "slide");
			String backgroundPath = createDirectory(basePath, "background");

			// Save files
			String logoImagePath = saveFile(logoPath, logoImage);
			String mainImagePath = saveFile(mainPath, mainImage);
			String leftImagePath = saveFile(backgroundPath, leftImage);
			String rightImagePath = saveFile(backgroundPath, rightImage);

			// Create company object
			Company company = new Company();
			company.setName(name);
			company.setRepresentativeName(representativeName);
			company.setBusinessRegistrationNumber(businessRegistrationNumber);
			company.setAddress(address);
			company.setConferenceAddress(conferenceAddress);
			company.setRightImagePath(rightImagePath);
			company.setRightImageRoad(
					"/administration/upload/" + urlSlug + "/background/" + rightImage.getOriginalFilename());
			company.setLeftImagePath(leftImagePath);
			company.setLeftImageRoad(
					"/administration/upload/" + urlSlug + "/background/" + leftImage.getOriginalFilename());
			company.setLogoImagePath(logoImagePath);
			company.setLogoImageRoad("/administration/upload/" + urlSlug + "/logo/" + logoImage.getOriginalFilename());
			company.setMainImagePath(mainImagePath);
			company.setMainImageRoad("/administration/upload/" + urlSlug + "/main/" + mainImage.getOriginalFilename());
			company.setUrlSlug(urlSlug);
			company.setHomepageUrl(homepageUrl);
			company.setSnsUrl(snsUrl);
			company.setMapUrl(mapUrl);

			// Save company in DB
			Company savedCompany = companyRepository.save(company);

			if (slideImages != null && !slideImages.isEmpty()) {
	            List<MultipartFile> validSlideImages = slideImages.stream()
	                    .filter(file -> file != null && !file.isEmpty()) // null 및 빈 파일 제거
	                    .toList();

	            if (!validSlideImages.isEmpty()) {
	                saveSlideImages(validSlideImages, savedCompany, slidePath, urlSlug);
	            }
	        }

			return savedCompany;
		} catch (RuntimeException e) {
			System.err.println("Error occurred during company creation: " + e.getMessage());
			throw e;
		}
	}

	public List<Company> getAllCompanies() {
		return companyRepository.findAll();
	}

	public Optional<Company> getCompanyById(Long id) {
		return companyRepository.findById(id);
	}

	public Optional<Company> getCompanyByUrlSlug(String urlSlug) {
		return companyRepository.findByUrlSlug(urlSlug);
	}

	public Company updateCompany(Long id, Company updatedCompany, MultipartFile logoImage, MultipartFile mainImage,
			MultipartFile leftImage, MultipartFile rightImage, List<MultipartFile> slideImages) {
		return companyRepository.findById(id).map(company -> {
			deleteFile(company.getLogoImagePath());
			deleteFile(company.getMainImagePath());
			deleteFile(company.getLeftImagePath());
			deleteFile(company.getRightImagePath());

			String basePath = getBasePath(updatedCompany.getUrlSlug());
			String logoPath = createDirectory(basePath, "logo");
			String mainPath = createDirectory(basePath, "main");
			String backgroundPath = createDirectory(basePath, "background");
			String slidePath = createDirectory(basePath, "slide");

			company.setLogoImagePath(saveFile(logoPath, logoImage));
			company.setMainImagePath(saveFile(mainPath, mainImage));
			company.setLeftImagePath(saveFile(backgroundPath, leftImage));
			company.setRightImagePath(saveFile(backgroundPath, rightImage));

			company.setLogoImageRoad("/administration/upload/" + updatedCompany.getUrlSlug() + "/logo/"
					+ logoImage.getOriginalFilename());
			company.setMainImageRoad("/administration/upload/" + updatedCompany.getUrlSlug() + "/main/"
					+ mainImage.getOriginalFilename());
			company.setLeftImageRoad("/administration/upload/" + updatedCompany.getUrlSlug() + "/background/"
					+ leftImage.getOriginalFilename());
			company.setRightImageRoad("/administration/upload/" + updatedCompany.getUrlSlug() + "/background/"
					+ rightImage.getOriginalFilename());

			slideImageRepository.deleteAll(company.getSlideImages());
			saveSlideImages(slideImages, company, slidePath, updatedCompany.getUrlSlug());

			return companyRepository.save(company);
		}).orElseThrow(() -> new RuntimeException("Company not found with id " + id));
	}

	private void saveSlideImages(List<MultipartFile> slideImages, Company company, String slidePath, String urlSlug) {
		for (MultipartFile slideImage : slideImages) {
			String slideImagePath = saveFile(slidePath, slideImage);
			CompanySlideImage slideImageEntity = new CompanySlideImage();
			slideImageEntity.setCompany(company);
			slideImageEntity.setStoragePath(slideImagePath);
			slideImageEntity
					.setPublicUrl("/administration/upload/" + urlSlug + "/slide/" + slideImage.getOriginalFilename());
			slideImageEntity.setDisplayOrder(0);
			slideImageRepository.save(slideImageEntity);
		}
	}

	public void deleteCompany(Long id) {
		companyRepository.findById(id).ifPresent(company -> {
			deleteFile(company.getLogoImagePath());
			deleteFile(company.getMainImagePath());
			companyRepository.delete(company);
		});
	}

	private String getBasePath(String urlSlug) {
		try {
			String basePath = Paths.get(uploadPath, urlSlug).toString();
			File baseDir = new File(basePath);
			if (!baseDir.exists() && !baseDir.mkdirs()) {
				throw new RuntimeException("Failed to create base directory: " + basePath);
			}
			System.out.println("Base path validated: " + basePath);
			return basePath;
		} catch (Exception e) {
			throw new RuntimeException("Error while creating base directory for urlSlug: " + urlSlug, e);
		}
	}

	private String createDirectory(String basePath, String subFolder) {
		try {
			String path = Paths.get(basePath, subFolder).toString();
			File dir = new File(path);
			if (!dir.exists() && !dir.mkdirs()) {
				throw new RuntimeException("Failed to create directory: " + path);
			}
			System.out.println("Directory created or exists: " + path);
			return path;
		} catch (Exception e) {
			throw new RuntimeException("Error while creating directory: " + subFolder, e);
		}
	}

	private String saveFile(String directoryPath, MultipartFile file) {
		try {
			// Validate input
			if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
				throw new RuntimeException("Invalid file: File is null or empty");
			}

			// Ensure directory exists
			File dir = new File(directoryPath);
			if (!dir.exists() && !dir.mkdirs()) {
				throw new RuntimeException("Failed to create directory: " + directoryPath);
			}

			// Define file path
			String filePath = Paths.get(directoryPath, file.getOriginalFilename()).toString();
			File destinationFile = new File(filePath);

			// Save file
			file.transferTo(destinationFile);

			// Verify file creation
			if (!destinationFile.exists()) {
				throw new RuntimeException("File not saved: " + filePath);
			}

			return filePath;
		} catch (IOException e) {
			throw new RuntimeException("Error while saving file: " + file.getOriginalFilename(), e);
		}
	}

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
