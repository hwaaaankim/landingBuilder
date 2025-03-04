package com.dev.LandingBuilder.service.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dev.LandingBuilder.model.client.Client;
import com.dev.LandingBuilder.repository.ClientRepository;

@Service
public class ClientService {
	
	@Autowired
	ClientRepository clientRepository;
	
	public Boolean clientInsert(Client client) {
		Date today = new Date();
		client.setSign(false);
		client.setInquiryDate(today);
		client.setCorrectDate(today);
		Client c = clientRepository.save(client);
		if(c!=null) {
			return true;
		}else {
			return false;
		}
	}
	
	public Page<Client> findByInquiryDate(Pageable pageable, String startDate, String endDate) throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat bf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start, end;

        if (startDate == null || startDate.isEmpty()) {
            start = bf.parse(bf.format(new Date()).substring(0, 10) + " 00:00:00");
        } else {
            start = f.parse(startDate);
        }

        if (endDate == null || endDate.isEmpty()) {
            end = bf.parse(bf.format(new Date()).substring(0, 10) + " 23:59:59");
        } else {
            end = f.parse(endDate);
            Calendar c = Calendar.getInstance();
            c.setTime(end);
            c.add(Calendar.DATE, 1);
            end = c.getTime();
        }
        return clientRepository.findByInquiryDateBetweenOrderByInquiryDateDesc(pageable, start, end);
    }
	
	public void clientUpdate(Client client) throws ParseException {
		Date today = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = f.format(today);
		Date day = f.parse(now);
		
		Optional<Client> c = clientRepository.findById(client.getId());
		c.ifPresent(s->{
			s.setName(client.getName());
			s.setPhone(client.getPhone());
			s.setEmail(client.getEmail());
			s.setCorrectDate(day);
			s.setSubject(client.getSubject());
			s.setContent(client.getContent());
			
			clientRepository.save(s);
		});
	}
	
	public void changeSign(Client client) {
		Optional<Client> c = clientRepository.findById(client.getId());
		c.ifPresent(s->{
			s.setSign(true);
			
			clientRepository.save(s);
		});
	}
}
