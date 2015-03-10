package org.openyu.socklet.core;

import org.openyu.commons.core.AppSupporter;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App extends AppSupporter {

	public static void main(String[] args) {
		String name = "openyu-socklet-core.j - OpenYu Socklet Core Java";
		// preTest(args, name);
		System.out.println("--- Pre Test ---");
		System.out.println(name);
		if (args != null) {
			for (String arg : args) {
				System.out.println(arg);
			}
		} else {
			System.out.println("Args is null");
		}
		//
		beg = System.currentTimeMillis();
		applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		end = System.currentTimeMillis();
		//
		printBeans();
	}
}
