import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.socklet.account.net.socklet.AccountSocklet;
import org.openyu.socklet.account.service.AccountService;

public class ApplicationContextServiceTest extends BaseTestSupporter {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"applicationContext-bean.xml", //
				"applicationContext-acceptor.xml", //
				"applicationContext-service.xml",//
		});
	}

	@Test
	public void accountService() {
		AccountService bean = (AccountService) applicationContext.getBean("accountService");
		System.out.println(bean);
		assertNotNull(bean);
	}

	@Test
	public void accountSocklet() {
		AccountSocklet bean = (AccountSocklet) applicationContext.getBean("accountSocklet");
		System.out.println(bean);
		assertNotNull(bean);
	}
}
