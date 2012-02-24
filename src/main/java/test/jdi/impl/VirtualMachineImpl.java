package test.jdi.impl;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.inspector.client.JPFInspectorClientInterface;
import gov.nasa.jpf.inspector.interfaces.JPFInspectorBackEndInterface;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.StaticElementInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharValue;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LongValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VoidValue;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.EventRequestManager;

public class VirtualMachineImpl implements VirtualMachine {

	public static final Logger log = org.apache.log4j.Logger.getLogger(VirtualMachineImpl.class);
	
	private JVM jvm;
	private JPFInspectorClientInterface inspector;

	public VirtualMachineImpl(JPFInspectorClientInterface inspector, JPF jpf) {
		log.debug("Entering method 'VirtualMachineImpl'");
		
		this.inspector = inspector;
		this.jvm = jpf.getVM();
		jpf.run();
	}

	@Override
	public VirtualMachine virtualMachine() {
		log.debug("Entering method 'virtualMachine'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ReferenceType> classesByName(String paramString) {
		log.debug("Entering method 'classesByName'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ReferenceType> allClasses() {
		log.debug("Entering method 'allClasses'");
		List<ReferenceType> classes = new ArrayList<ReferenceType>();
		
		for (Iterator<StaticElementInfo> it = jvm.getKernelState().getStaticArea().iterator(); it.hasNext(); ) {
			StaticElementInfo elInfo = it.next();
			classes.add(new ReferenceTypeImpl(elInfo));
		}
		return classes;
	}

	@Override
	public void redefineClasses(Map<? extends ReferenceType, byte[]> paramMap) {
		log.debug("Entering method 'redefineClasses'");
		// TODO Auto-generated method stub

	}

	@Override
	public List<ThreadReference> allThreads() {
		log.debug("Entering method 'allThreads'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void suspend() {
		log.debug("Entering method 'suspend'");
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		log.debug("Entering method 'resume'");
		// TODO Auto-generated method stub
	}

	@Override
	public List<ThreadGroupReference> topLevelThreadGroups() {
		log.debug("Entering method 'topLevelThreadGroups'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventQueue eventQueue() {
		log.debug("Entering method 'eventQueue'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventRequestManager eventRequestManager() {
		log.debug("Entering method 'eventRequestManager'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BooleanValue mirrorOf(boolean paramBoolean) {
		log.debug("Entering method 'mirrorOf'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteValue mirrorOf(byte paramByte) {
		log.debug("Entering method 'mirrorOf'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharValue mirrorOf(char paramChar) {
		log.debug("Entering method 'mirrorOf'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShortValue mirrorOf(short paramShort) {
		log.debug("Entering method 'mirrorOf'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegerValue mirrorOf(int paramInt) {
		log.debug("Entering method 'mirrorOf'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongValue mirrorOf(long paramLong) {
		log.debug("Entering method 'mirrorOf'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FloatValue mirrorOf(float paramFloat) {
		log.debug("Entering method 'mirrorOf'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoubleValue mirrorOf(double paramDouble) {
		log.debug("Entering method 'mirrorOf'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringReference mirrorOf(String paramString) {
		log.debug("Entering method 'mirrorOf'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VoidValue mirrorOfVoid() {
		log.debug("Entering method 'mirrorOfVoid'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Process process() {
		log.debug("Entering method 'process'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		log.debug("Entering method 'dispose'");
		// TODO Auto-generated method stub

	}

	@Override
	public void exit(int paramInt) {
		log.debug("Entering method 'exit'");
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canWatchFieldModification() {
		log.debug("Entering method 'canWatchFieldModification'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canWatchFieldAccess() {
		log.debug("Entering method 'canWatchFieldAccess'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetBytecodes() {
		log.debug("Entering method 'canGetBytecodes'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetSyntheticAttribute() {
		log.debug("Entering method 'canGetSyntheticAttribute'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetOwnedMonitorInfo() {
		log.debug("Entering method 'canGetOwnedMonitorInfo'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetCurrentContendedMonitor() {
		log.debug("Entering method 'canGetCurrentContendedMonitor'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetMonitorInfo() {
		log.debug("Entering method 'canGetMonitorInfo'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUseInstanceFilters() {
		log.debug("Entering method 'canUseInstanceFilters'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRedefineClasses() {
		log.debug("Entering method 'canRedefineClasses'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canAddMethod() {
		log.debug("Entering method 'canAddMethod'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUnrestrictedlyRedefineClasses() {
		log.debug("Entering method 'canUnrestrictedlyRedefineClasses'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPopFrames() {
		log.debug("Entering method 'canPopFrames'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetSourceDebugExtension() {
		log.debug("Entering method 'canGetSourceDebugExtension'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRequestVMDeathEvent() {
		log.debug("Entering method 'canRequestVMDeathEvent'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetMethodReturnValues() {
		log.debug("Entering method 'canGetMethodReturnValues'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetInstanceInfo() {
		log.debug("Entering method 'canGetInstanceInfo'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUseSourceNameFilters() {
		log.debug("Entering method 'canUseSourceNameFilters'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canForceEarlyReturn() {
		log.debug("Entering method 'canForceEarlyReturn'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canBeModified() {
		log.debug("Entering method 'canBeModified'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRequestMonitorEvents() {
		log.debug("Entering method 'canRequestMonitorEvents'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetMonitorFrameInfo() {
		log.debug("Entering method 'canGetMonitorFrameInfo'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetClassFileVersion() {
		log.debug("Entering method 'canGetClassFileVersion'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetConstantPool() {
		log.debug("Entering method 'canGetConstantPool'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDefaultStratum(String paramString) {
		log.debug("Entering method 'setDefaultStratum'");
		// TODO Auto-generated method stub

	}

	@Override
	public String getDefaultStratum() {
		log.debug("Entering method 'getDefaultStratum'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] instanceCounts(List<? extends ReferenceType> paramList) {
		log.debug("Entering method 'instanceCounts'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String description() {
		log.debug("Entering method 'description'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String version() {
		log.debug("Entering method 'version'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		log.debug("Entering method 'name'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDebugTraceMode(int paramInt) {
		log.debug("Entering method 'setDebugTraceMode'");
		// TODO Auto-generated method stub

	}

}
