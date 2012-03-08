package gov.nasa.jdi.rmi.client.impl;

import java.io.IOException;
import java.util.List;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.connect.spi.Connection;

public class VirtualMachineManagerImpl implements VirtualMachineManager {

	private LaunchingConnectorImpl launchingConnector = new LaunchingConnectorImpl();
	
	@Override
	public LaunchingConnector defaultConnector() {
		return launchingConnector;
	}

	@Override
	public List<LaunchingConnector> launchingConnectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AttachingConnector> attachingConnectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ListeningConnector> listeningConnectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Connector> allConnectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VirtualMachine> connectedVirtualMachines() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int majorInterfaceVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int minorInterfaceVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public VirtualMachine createVirtualMachine(Connection paramConnection,
			Process paramProcess) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VirtualMachine createVirtualMachine(Connection paramConnection)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final VirtualMachineManager vmm = new VirtualMachineManagerImpl();
	
	public static VirtualMachineManager virtualMachineManager() {
		return vmm;
	}

}
