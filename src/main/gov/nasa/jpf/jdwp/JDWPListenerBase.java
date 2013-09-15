/* 
   Copyright (C) 2013 Stepan Vavra

This file is part of (Java Debug Wire Protocol) JDWP for 
Java PathFinder (JPF) project.

JDWP for JPF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JDWP for JPF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 
 */

package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jvm.ClassFile;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.VMListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base implementation of {@link VMListener} notifications.<br/>
 * This class is supposed to be inherited and overriden.
 * 
 * @author stepan
 * 
 */
public class JDWPListenerBase implements VMListener {

  final static Logger logger = LoggerFactory.getLogger(JDWPListenerBase.class);

  /**
   * The constructor.
   */
  public JDWPListenerBase() {
  }

  @Override
  public void vmInitialized(VM vm) {
    logger.trace("Processing listener");

  }

  @Override
  public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
    logger.trace("Processing listener");

  }

  @Override
  public void instructionExecuted(VM vm, ThreadInfo currentThread, Instruction nextInstruction, Instruction executedInstruction) {
    logger.trace("Processing listener");

  }

  @Override
  public void threadStarted(VM vm, ThreadInfo startedThread) {
    logger.trace("Processing listener");

  }

  @Override
  public void threadBlocked(VM vm, ThreadInfo blockedThread, ElementInfo lock) {
    logger.trace("Processing listener");

  }

  @Override
  public void threadWaiting(VM vm, ThreadInfo waitingThread) {
    logger.trace("Processing listener");

  }

  @Override
  public void threadNotified(VM vm, ThreadInfo notifiedThread) {
    logger.trace("Processing listener");

  }

  @Override
  public void threadInterrupted(VM vm, ThreadInfo interruptedThread) {
    logger.trace("Processing listener");

  }

  @Override
  public void threadTerminated(VM vm, ThreadInfo terminatedThread) {
    logger.trace("Processing listener");

  }

  @Override
  public void threadScheduled(VM vm, ThreadInfo scheduledThread) {
    logger.trace("Processing listener");

  }

  @Override
  public void loadClass(VM vm, ClassFile cf) {
    logger.trace("Processing listener");

  }

  @Override
  public void classLoaded(VM vm, ClassInfo loadedClass) {
    logger.trace("Processing listener");

  }

  @Override
  public void objectCreated(VM vm, ThreadInfo currentThread, ElementInfo newObject) {
    logger.trace("Processing listener");

  }

  @Override
  public void objectReleased(VM vm, ThreadInfo currentThread, ElementInfo releasedObject) {
    logger.trace("Processing listener: {}", releasedObject);

  }

  @Override
  public void objectLocked(VM vm, ThreadInfo currentThread, ElementInfo lockedObject) {
    logger.trace("Processing listener");

  }

  @Override
  public void objectUnlocked(VM vm, ThreadInfo currentThread, ElementInfo unlockedObject) {
    logger.trace("Processing listener");

  }

  @Override
  public void objectWait(VM vm, ThreadInfo currentThread, ElementInfo waitingObject) {
    logger.trace("Processing listener");

  }

  @Override
  public void objectNotify(VM vm, ThreadInfo currentThread, ElementInfo notifyingObject) {
    logger.trace("Processing listener");

  }

  @Override
  public void objectNotifyAll(VM vm, ThreadInfo currentThread, ElementInfo notifyingObject) {
    logger.trace("Processing listener");

  }

  @Override
  public void gcBegin(VM vm) {
    logger.trace("Processing listener");

  }

  @Override
  public void gcEnd(VM vm) {
    logger.trace("Processing listener");

  }

  @Override
  public void exceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException) {
    logger.trace("Processing listener");

  }

  @Override
  public void exceptionBailout(VM vm, ThreadInfo currentThread) {
    logger.trace("Processing listener");

  }

  @Override
  public void exceptionHandled(VM vm, ThreadInfo currentThread) {
    logger.trace("Processing listener");

  }

  @Override
  public void choiceGeneratorRegistered(VM vm, ChoiceGenerator<?> nextCG, ThreadInfo currentThread, Instruction executedInstruction) {
    logger.trace("Processing listener");

  }

  @Override
  public void choiceGeneratorSet(VM vm, ChoiceGenerator<?> newCG) {
    logger.trace("Processing listener");

  }

  @Override
  public void choiceGeneratorAdvanced(VM vm, ChoiceGenerator<?> currentCG) {
    logger.trace("Processing listener");

  }

  @Override
  public void choiceGeneratorProcessed(VM vm, ChoiceGenerator<?> processedCG) {
    logger.trace("Processing listener");

  }

  @Override
  public void methodEntered(VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
    logger.trace("Processing listener");

  }

  @Override
  public void methodExited(VM vm, ThreadInfo currentThread, MethodInfo exitedMethod) {
    logger.trace("Processing listener");

  }

}
