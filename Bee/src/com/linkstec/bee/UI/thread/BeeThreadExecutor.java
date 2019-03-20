package com.linkstec.bee.UI.thread;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.request.EventRequestManager;
import com.sun.tools.jdi.SocketAttachingConnector;

public class BeeThreadExecutor {

	private BlockingQueue<BeeThread> queue;
	private static BeeThreadExecutor instance;

	private BeeThreadExecutor() {
		List<Connector> connectors = Bootstrap.virtualMachineManager().allConnectors();
		SocketAttachingConnector sac = null;

		for (Connector connector : connectors) {
			if (connector instanceof SocketAttachingConnector) {
				sac = (SocketAttachingConnector) connector;
			}
		}
		// boolean sa = System.getProperty("kis.jdi.sa") != null;
		try {

			// 1. 使用不同SP提供的connector attach到目标VM上面
			VirtualMachine vm = null;

			if (sac != null) {
				Map<String, Connector.Argument> defaultArguments = sac.defaultArguments();
				Connector.Argument hostArg = defaultArguments.get("hostname"); // SocketAttachingConnector#ARG_HOST
				Connector.Argument portArg = defaultArguments.get("port"); // SocketAttachingConnector#ARG_PORT
				hostArg.setValue("localhost");
				portArg.setValue("8787");
				vm = sac.attach(defaultArguments);
			}

			// process = vm.process();
			if (vm == null) {
				return;
			}

			EventRequestManager requestManager = vm.eventRequestManager();
			// vm.allThreads().get(0).threadGroup().

		} catch (Exception e) {

		}
	}

	public static BeeThreadExecutor getInstance() {
		if (instance == null) {
			instance = new BeeThreadExecutor();
		}
		return instance;
	}

	public void suspend(BeeThread thread) {

		try {
			if (!queue.contains(thread)) {
				queue.put(thread);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void resume(BeeThread thread) {
		try {
			queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void clear() {
		queue.clear();
	}

}
