package utils;

import exceptions.PortalSegurancaException;

public class StringUtil {

	public static String coverMail(String email) {

		if(email == null) {

			throw new PortalSegurancaException().userMessage("erro.padrao");

		}

		String antesArroba = email.split("@")[0];

		if(antesArroba.length() < 3) {

			return email;

		}

		String emailCoberto = antesArroba.substring(0,1);

		for(int i = 1; i < antesArroba.length() - 1; i++) {

			emailCoberto += "*";

		}

		return emailCoberto.concat(antesArroba.substring(antesArroba.length() - 1, antesArroba.length())).concat("@").concat(email.split("@")[1]);

	}

}