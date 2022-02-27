package utils;

import java.util.Random;

public class Password {

	private static final char[] ALL_CHARS = new char[62];
	private static final Random RANDOM = new Random();

	static {

		// O for abaixo utiliza a table ascii para gerar os caracteres.
		// Os números decimais e as letras do alfabeto maiúsculas e minúsculas
		// estão entre os valores 48 e 122 (intervalo fechado) da tabela ascii, por isso a variável i
		// é inicializada com o valor 48 e a condição de parada é i < 123

		for (int i = 48, j = 0; i < 123; i++) {

			if (Character.isLetterOrDigit(i)) {

				ALL_CHARS[j] = (char) i;
				j++;

			}

		}

	}

	public static String random(int length){

		final char[] result = new char[length];

		for (int i = 0; i < length; i++) {

			result[i] = ALL_CHARS[RANDOM.nextInt(ALL_CHARS.length)];

		}

		return new String(result);

	}

}