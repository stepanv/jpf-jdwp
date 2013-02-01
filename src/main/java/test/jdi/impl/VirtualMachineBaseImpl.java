package test.jdi.impl;

import gov.nasa.jpf.JPF;

import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

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
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VoidValue;
import com.sun.jdi.event.EventQueue;

public abstract class VirtualMachineBaseImpl implements VirtualMachine {
	
	/**
	 * TODO remove this for production .. only for debugging purposes for easier detection which classes are we entering
	 */
	static {
		PatternLayout layout = new PatternLayout("%l " + PatternLayout.TTCC_CONVERSION_PATTERN);
		Logger root = Logger.getRootLogger();
		root.addAppender(new ConsoleAppender(layout));
	}

	public static final Logger log = org.apache.log4j.Logger.getLogger(VirtualMachineBaseImpl.class);
	
	public VirtualMachineBaseImpl(JPF jpf) {
	}

	@Override
	public void redefineClasses(Map<? extends ReferenceType, byte[]> paramMap) {
		log.debug("Entering method 'redefineClasses'");
		// TODO Auto-generated method stub

	}

	@Override
	public void suspend() {
		log.debug("Entering method 'suspend'");
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
