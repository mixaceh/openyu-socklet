import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.service.MessageService;

public class ApplicationContextAcceptorTest extends BaseTestSupporter {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"applicationContext-bean.xml", //
				"applicationContext-acceptor.xml",//
		});
	}

	@Test
	public void messageService() {
		MessageService bean = (MessageService) applicationContext.getBean("messageService");
		System.out.println(bean);
		assertNotNull(bean);
	}

	@Test
	public void protocolService() {
		ProtocolService bean = (ProtocolService) applicationContext.getBean("protocolService");
		System.out.println(bean);
		assertNotNull(bean);
	}

}
