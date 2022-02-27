package controllers;

import play.Play;

public class Versao extends BaseController {

	public static void versao() {
		Play.configuration.setProperty("versao.play", Play.frameworkPath.getName());
		render();
	}
}
