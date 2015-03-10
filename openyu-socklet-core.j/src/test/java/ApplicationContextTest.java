import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.lang.BooleanHelper;

public class ApplicationContextTest extends BaseTestSupporter {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		beg = System.currentTimeMillis();
		applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		end = System.currentTimeMillis();
	}

	@Test
	public void applicationContext() {
		System.out.println(applicationContext);
	}

	@Test
	public void beans() {
		printBeans();
	}

}
