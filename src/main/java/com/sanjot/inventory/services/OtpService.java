package com.sanjot.inventory.services;

	import java.security.SecureRandom;
	import java.util.concurrent.ConcurrentHashMap;

	import org.springframework.stereotype.Service;

	@Service
	public class OtpService {
		private final ConcurrentHashMap<String ,String> otpStore=new ConcurrentHashMap<>();
		private final SecureRandom secureRandom=new SecureRandom();
		
		public String generateOtp(String identifier) {
			int otp=100000+secureRandom.nextInt(900000);
			String otpString = String.valueOf(otp);
			otpStore.put(identifier, otpString);
			return otpString;
		}
		public boolean validateOtp(String identifier,String otp) {
			String storeOtp=otpStore.get(identifier);
			
			if(storeOtp!=null && storeOtp.equals(otp)) {
				otpStore.remove(identifier);
				return true;
			}
			return false;
		}

	

}
