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

/**
 * socket的小程式,此為控制器
 * 
 * 邏輯處理,會放在xxxService中
 */
public abstract class SockletServiceSupporter extends BaseServiceSupporter
		implements SockletService {

	private static final long serialVersionUID = -7346709988055422446L;

	private String id;

	private SockletConfig sockletConfig;

	private Map<String, String> initParameters = new LinkedHashMap<String, String>(
			10);

	private Set<String> acceptors = new LinkedHashSet<String>();

	// /**
	// * 監聽毫秒
	// */
	// private long LISTEN_MILLS = 1L;

	// private Queue<Message> messages = new ConcurrentLinkedQueue<Message>();
	//
	// private Lock messagesLock = new ReentrantLock();
	//
	// private Condition messagesNotEmpty = messagesLock.newCondition();

	private MessageQueue<Message> messageQueue = new MessageQueue<Message>();

	private transient AcceptorStarter acceptorStarter;

	private transient AcceptorService acceptorService;

	private transient String acceptor;

	private transient String instanceId;

	private transient String outputId;

	public SockletServiceSupporter() {
	}

	/**
	 * 初始化
	 *
	 * @throws Exception
	 */
	protected void init() throws Exception {
		super.init();
		//
		// LOGGER.info("Initialization of SockletService [" + this.id + "]");
		threadService.submit(messageQueue);
		SockletConfig sockletConfig = new SockletConfigImpl(this);
		try {
			init(sockletConfig);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 銷毀化
	 *
	 * @throws Exception
	 */
	protected void uninit() throws Exception {
		super.uninit();
		//
		// LOGGER.info("Uninitialization of SockletService [" + this.id + "]");
		messageQueue.setCancel(true);
		setSockletConfig(null);
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
	public void service(Request request, Response response)
			throws SockletException, IOException {
		throw new UnsupportedOperationException();
	}

	// /**
	// * 加入訊息
	// *
	// * @param message
	// * @return
	// */
	// public boolean ___addMessage(Message message)
	// {
	// boolean result = false;
	// result = messages.add(message);
	// return result;
	// }

	// /**
	// * 加入訊息
	// *
	// * @param message
	// * @return
	// */
	// public boolean __2addMessage(Message message)
	// {
	// boolean result = false;
	// //System.out.println("addMessage T: " + Thread.currentThread().getId());
	// // result = messages.add(message);
	// synchronized (messages)
	// {
	// result = messages.add(message);
	// messages.notifyAll();//喚醒所有的進程
	// }
	// return result;
	// }

	/**
	 * 加入訊息
	 * 
	 * @param message
	 * @return
	 */
	public boolean addMessage(Message message) {
		// boolean result = false;
		// try
		// {
		// messagesLock.lockInterruptibly();
		// try
		// {
		// result = messages.offer(message);
		// messagesNotEmpty.signalAll();
		// }
		// catch (Exception ex)
		// {
		// ex.printStackTrace();
		// }
		// finally
		// {
		// messagesLock.unlock();
		// }
		// }
		// catch (InterruptedException ex)
		// {
		// ex.printStackTrace();
		// }
		// return result;

		return messageQueue.offer(message);
	}

	// /**
	// * 服務
	// */
	// protected class ServiceRunner extends BaseRunnableSupporter
	// {
	// public void execute()
	// {
	// while (true)
	// {
	// try
	// {
	// service();
	// //ThreadHelper.sleep(LISTEN_MILLS);
	// }
	// catch (Exception ex)
	// {
	// //ex.printStackTrace();
	// }
	// }
	// }
	// }

	// /**
	// * 服務
	// *
	// */
	// protected void ___service()
	// {
	// //一直監聽,浪費cpu
	// while (!messages.isEmpty())
	// {
	// Message message = messages.poll();
	// if (message == null)
	// {
	// continue;
	// }
	// //
	// service(message);
	// }
	// }

	// /**
	// * 服務
	// */
	// protected void __2service()
	// {
	// //wait/notify 改善,但卻用了 synchronized
	// synchronized (messages)
	// {
	// try
	// {
	// while (messages.isEmpty())
	// {
	// messages.wait();//暫時釋放資源
	// }
	// }
	// catch (InterruptedException ex)
	// {
	// ex.printStackTrace();
	// }
	//
	// //service
	// //System.out.println("service T: " + Thread.currentThread().getId());
	// try
	// {
	// Message message = messages.poll();
	// if (message == null)
	// {
	// return;
	// }
	// //
	// service(message);
	// }
	// catch (Exception ex)
	// {
	// ex.printStackTrace();
	// }
	// }
	// }

	// /**
	// * 服務
	// */
	// protected void service()
	// {
	// //3.改用lock
	// try
	// {
	// messagesLock.lockInterruptibly();
	// try
	// {
	//
	// try
	// {
	// while (messages.isEmpty())
	// {
	// messagesNotEmpty.await();
	// }
	// }
	// catch (InterruptedException ex)
	// {
	// ex.printStackTrace();
	// }
	// //
	// try
	// {
	// Message message = messages.poll();
	// if (message == null)
	// {
	// return;
	// }
	// //
	// service(message);
	// }
	// catch (Exception ex)
	// {
	// ex.printStackTrace();
	// }
	//
	// }
	// catch (Exception ex)
	// {
	// ex.printStackTrace();
	// }
	// finally
	// {
	// messagesLock.unlock();
	// }
	// }
	// catch (InterruptedException ex)
	// {
	// ex.printStackTrace();
	// }
	// }

	/**
	 * 訊息佇列
	 */
	protected class MessageQueue<E> extends TriggerQueueSupporter<E> {
		public MessageQueue() {
		}

		public void process(E e) {
			service((Message) e);
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
				acceptorStarter = (AcceptorStarter) applicationContext
						.getBean("acceptorStarter");
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
