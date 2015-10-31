package org.openyu.socklet.socklet.service.supporter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.commons.thread.ThreadService;
import org.openyu.commons.thread.supporter.TriggerQueueSupporter;
import org.openyu.socklet.acceptor.service.AcceptorService;
import org.openyu.socklet.acceptor.vo.AcceptorStarter;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.reponse.vo.Response;
import org.openyu.socklet.request.vo.Request;
import org.openyu.socklet.socklet.ex.SockletException;
import org.openyu.socklet.socklet.service.SockletService;
import org.openyu.socklet.socklet.vo.SockletConfig;
import org.openyu.socklet.socklet.vo.impl.SockletConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * socket的小程式,此為控制器
 * 
 * 邏輯處理,會放在xxxService中
 */
public abstract class SockletServiceSupporter extends BaseServiceSupporter implements SockletService {

	private static final long serialVersionUID = -7346709988055422446L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(SockletServiceSupporter.class);

	/**
	 * 線程服務
	 */
	@Autowired
	@Qualifier("threadService")
	protected transient ThreadService threadService;

	private String id;

	private SockletConfig sockletConfig;

	private Map<String, String> initParameters = new LinkedHashMap<String, String>(10);

	private Set<String> acceptors = new LinkedHashSet<String>();

	private MessageQueue<Message> messageQueue;

	private transient AcceptorStarter acceptorStarter;

	private transient AcceptorService acceptorService;

	private transient String acceptor;

	private transient String instanceId;

	private transient String outputId;

	public SockletServiceSupporter() {
	}

	public void setThreadService(ThreadService threadService) {
		this.threadService = threadService;
	}

	@Override
	protected void doStart() throws Exception {
		SockletConfig sockletConfig = new SockletConfigImpl(this);
		init(sockletConfig);
		//
		messageQueue = new MessageQueue<Message>(threadService);
		messageQueue.start();
	}

	@Override
	protected void doShutdown() throws Exception {
		setSockletConfig(null);
		//
		messageQueue.shutdown();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void init(SockletConfig sockletConfig) throws SockletException {
		setSockletConfig(sockletConfig);

	}

	public SockletConfig getSockletConfig() {
		return sockletConfig;
	}

	public void setSockletConfig(SockletConfig sockletConfig) {
		this.sockletConfig = sockletConfig;
		this.initParameters = sockletConfig.getInitParameters();
	}

	public Map<String, String> getInitParameters() {
		return initParameters;
	}

	public void setInitParameters(Map<String, String> initParameters) {
		this.initParameters = initParameters;
	}

	public String getInitParameter(String name) {
		return (String) initParameters.get(name);
	}

	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(initParameters.keySet());
	}

	public Set<String> getAcceptors() {
		return acceptors;
	}

	public void setAcceptors(Set<String> acceptors) {
		this.acceptors = acceptors;
	}

	@Deprecated
	/**
	 * 2012/01/01 已不用改用message,取代request,reponse
	 * 
	 * replaced by service(Message message)
	 * 
	 * @param request
	 * @param response
	 * @throws SockletException
	 * @throws IOException
	 */
	public void service(Request request, Response response) throws SockletException, IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * 加入訊息
	 * 
	 * @param message
	 * @return
	 */
	public boolean addMessage(Message message) {
		return messageQueue.offer(message);
	}

	/**
	 * 訊息佇列
	 */
	protected class MessageQueue<E> extends TriggerQueueSupporter<Message> {

		public MessageQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(Message e) throws Exception {
			service(e);
		}
	}

	// --------------------------------------------------
	/**
	 * 當單元測試時,是沒有真正啟動acceptor,所以沒有acceptorStarter
	 * 
	 * @return
	 */
	protected AcceptorStarter getAcceptorStarter() {
		if (acceptorStarter == null) {
			try {
				acceptorStarter = (AcceptorStarter) applicationContext.getBean("acceptorStarter");
			} catch (Exception ex) {
			}
		}
		return acceptorStarter;
	}

	/**
	 * 當有acceptorStarter,就應有acceptorService
	 * 
	 * @return
	 */
	protected AcceptorService getAcceptorService() {
		if (acceptorService == null) {
			AcceptorStarter starter = getAcceptorStarter();
			if (starter != null) {
				acceptorService = starter.getAcceptorService();
			}
		}
		return acceptorService;
	}

	/**
	 * 取得acceptor id
	 * 
	 * @return
	 */
	protected String getAcceptor() {
		if (acceptor == null) {
			AcceptorService service = getAcceptorService();
			if (service != null) {
				acceptor = service.getId();
			}
		}
		return acceptor;
	}

	/**
	 * 取得實例id
	 * 
	 * @return
	 */
	protected String getInstanceId() {
		if (instanceId == null) {
			AcceptorService service = getAcceptorService();
			if (service != null) {
				instanceId = service.getInstanceId();
			}
		}
		return instanceId;
	}

	/**
	 * 取得輸出id
	 * 
	 * @return
	 */
	protected String getOutputId() {
		if (outputId == null) {
			AcceptorService service = getAcceptorService();
			if (service != null) {
				outputId = service.getOutputId();
			}
		}
		return outputId;
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", id);
		builder.append("acceptors", acceptors);
		builder.append("initParameters", initParameters);
		builder.append("sockletConfig", sockletConfig);
		return builder.toString();
	}

}
