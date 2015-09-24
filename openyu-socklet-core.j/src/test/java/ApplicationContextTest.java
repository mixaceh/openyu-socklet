import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openyu.commons.junit.supporter.BaseTestSupporter;

public class ApplicationContextTest extends BaseTestSupporter {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		begTime = System.currentTimeMillis();
		applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		endTime = System.currentTimeMillis();
	}

	@Test
	public void applicationContext() {
		System.out.println(applicationContext);
	}

	@Test
	public void showBeans() {
		printBeans();
	}

}
