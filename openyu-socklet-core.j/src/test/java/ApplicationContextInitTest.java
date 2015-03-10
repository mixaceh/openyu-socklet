import static org.junit.Assert.assertNotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.velocity.app.VelocityEngine;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.util.ConfigHelper;

import freemarker.template.Configuration;

public class ApplicationContextInitTest extends BaseTestSupporter {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext-init.xml"//
				});
	}

	@Test
	public void configHelper() {
		ConfigHelper bean = (ConfigHelper) applicationContext
				.getBean("configHelper");
		System.out.println(bean);
		assertNotNull(bean);
	}

	@Test
	public void propertyPlaceholderConfigurer() {
		PropertyPlaceholderConfigurer bean = (PropertyPlaceholderConfigurer) applicationContext
				.getBean("propertyPlaceholderConfigurer");
		System.out.println(bean);
		assertNotNull(bean);
	}

	@Test
	public void propertyOverrideConfigurer() {
		PropertyOverrideConfigurer bean = (PropertyOverrideConfigurer) applicationContext
				.getBean("propertyOverrideConfigurer");
		System.out.println(bean);
		assertNotNull(bean);
	}

	@Test
	public void velocityEngine() {
		VelocityEngine bean = (VelocityEngine) applicationContext
				.getBean("velocityEngine");
		System.out.println(ToStringBuilder.reflectionToString(bean,
				ToStringStyle.MULTI_LINE_STYLE));
		assertNotNull(bean);
	}

	@Test
	public void freeMarkerConfiguration() {
		Configuration bean = (Configuration) applicationContext
				.getBean("freeMarkerConfiguration");
		System.out.println(ToStringBuilder.reflectionToString(bean,
				ToStringStyle.MULTI_LINE_STYLE));
		assertNotNull(bean);
	}
}
