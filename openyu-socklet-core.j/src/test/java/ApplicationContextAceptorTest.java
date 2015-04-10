import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.socklet.acceptor.service.AcceptorService;
import org.openyu.socklet.socklet.service.SockletService;

public class ApplicationContextAceptorTest extends BaseTestSupporter {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] {
				"applicationContext-init.xml",//
				"META-INF/applicationContext-commons-core.xml",//
				"META-INF/applicationContext-socklet-core.xml",//
				"applicationContext-aceptor.xml",//
		});
	}

	@Test
	public void masterAcceptor() {
		AcceptorService bean = (AcceptorService) applicationContext
				.getBean("masterAcceptor");
		System.out.println(bean);
		assertNotNull(bean);
	}

	@Test
	public void slave1Acceptor() {
		AcceptorService bean = (AcceptorService) applicationContext
				.getBean("slave1Acceptor");
		System.out.println(bean);
		assertNotNull(bean);
	}

	@Test
	public void slave2Acceptor() {
		AcceptorService bean = (AcceptorService) applicationContext
				.getBean("slave2Acceptor");
		System.out.println(bean);
		assertNotNull(bean);
	}

	@Test
	public void fourSymbolSocklet() {
		SockletService bean = (SockletService) applicationContext
				.getBean("fourSymbolSocklet");
		System.out.println(bean);
		assertNotNull(bean);
	}
}