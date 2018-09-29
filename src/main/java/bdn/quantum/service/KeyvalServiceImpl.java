package bdn.quantum.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bdn.quantum.model.KeyvalEntity;
import bdn.quantum.repository.KeyvalRepository;

@Service("keyvalService")
public class KeyvalServiceImpl implements KeyvalService {

	@Autowired
	private KeyvalRepository keyvalRepository;
	
	
	@Override
	public Iterable<KeyvalEntity> getKeyvals() {
		return keyvalRepository.findAll();
	}

	@Override
	public KeyvalEntity setKeyval(KeyvalEntity ke) {
		if (ke == null || ke.getKey() == null || ke.getKey().trim().length() < 1 || 
				ke.getValue() == null || ke.getValue().trim().length() < 1) {
			return null;
		}

		KeyvalEntity result = keyvalRepository.save(ke);
		return result;
	}

	@Override
	public KeyvalEntity getKeyval(String key) {
		Optional<KeyvalEntity> t = keyvalRepository.findById(key);
		
		KeyvalEntity ke = t.get();
		return ke;
	}

	@Override
	public void deleteKeyval(String key) {
		keyvalRepository.deleteById(key);
	}

}
