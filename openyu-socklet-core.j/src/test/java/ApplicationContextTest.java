import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.lang.RuntimeHelper;

public class ApplicationContextTest extends BaseTestSupporter {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		begTime = System.currentTimeMillis();
		// 計算所耗費的記憶體(bytes)
		RuntimeHelper.gc();
		// 原本的記憶體
		long memory = RuntimeHelper.usedMemory();
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" });
		endTime = System.currentTimeMillis();
		usedMemory = Math.max(usedMemory, (RuntimeHelper.usedMemory() - memory));
	}

	@Test
	public void applicationContext() {
		System.out.println(applicationContext);
	}

	@Test
	public void showBean() {
		printBean();
	}

}
