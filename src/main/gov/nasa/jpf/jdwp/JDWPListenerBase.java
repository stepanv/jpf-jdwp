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
    logger.trace("VM initialized");

  }

  @Override
  public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
    logger.trace("Execute instruction '{}' in thread '{}'", instructionToExecute, currentThread);

  }

  @Override
  public void instructionExecuted(VM vm, ThreadInfo currentThread, Instruction nextInstruction, Instruction executedInstruction) {
    logger.trace("Instruction '{}' executed in thread '{}'. Next instruction: '{}'", executedInstruction, currentThread, nextInstruction);

  }

  @Override
  public void threadStarted(VM vm, ThreadInfo startedThread) {
    logger.trace("Thread started: {}", startedThread);

  }

  @Override
  public void threadBlocked(VM vm, ThreadInfo blockedThread, ElementInfo lock) {
    logger.trace("Thread blocked: {} on object: {}", blockedThread, lock);

  }

  @Override
  public void threadWaiting(VM vm, ThreadInfo waitingThread) {
    logger.trace("Thread waiting: {}", waitingThread);

  }

  @Override
  public void threadNotified(VM vm, ThreadInfo notifiedThread) {
    logger.trace("Thread notified: {}", notifiedThread);

  }

  @Override
  public void threadInterrupted(VM vm, ThreadInfo interruptedThread) {
    logger.trace("Thread interrupted: {}", interruptedThread);

  }

  @Override
  public void threadTerminated(VM vm, ThreadInfo terminatedThread) {
    logger.trace("Thread terminated: {}", terminatedThread);

  }

  @Override
  public void threadScheduled(VM vm, ThreadInfo scheduledThread) {
    logger.trace("Thread scheduled: {}", scheduledThread);

  }

  @Override
  public void loadClass(VM vm, ClassFile cf) {
    logger.trace("Class load: {}", cf);

  }

  @Override
  public void classLoaded(VM vm, ClassInfo loadedClass) {
    logger.trace("Class loaded: {}", loadedClass);

  }

  @Override
  public void objectCreated(VM vm, ThreadInfo currentThread, ElementInfo newObject) {
    logger.trace("Object created: {}", newObject);

  }

  @Override
  public void objectReleased(VM vm, ThreadInfo currentThread, ElementInfo releasedObject) {
    logger.trace("Object released: {}", releasedObject);

  }

  @Override
  public void objectLocked(VM vm, ThreadInfo currentThread, ElementInfo lockedObject) {
    logger.trace("Object locked: {}", lockedObject);

  }

  @Override
  public void objectUnlocked(VM vm, ThreadInfo currentThread, ElementInfo unlockedObject) {
    logger.trace("Object unlocked: {}", unlockedObject);

  }

  @Override
  public void objectWait(VM vm, ThreadInfo currentThread, ElementInfo waitingObject) {
    logger.trace("Object wait: {}", waitingObject);

  }

  @Override
  public void objectNotify(VM vm, ThreadInfo currentThread, ElementInfo notifyingObject) {
    logger.trace("Object notify: {}", notifyingObject);

  }

  @Override
  public void objectNotifyAll(VM vm, ThreadInfo currentThread, ElementInfo notifyingObject) {
    logger.trace("Object notify all: {}", notifyingObject);

  }

  @Override
  public void gcBegin(VM vm) {
    logger.trace("GC BEGIN");

  }

  @Override
  public void gcEnd(VM vm) {
    logger.trace("GC END");

  }

  @Override
  public void exceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException) {
    logger.trace("Exception '{}' thrown in thread: {}", thrownException, currentThread);

  }

  @Override
  public void exceptionBailout(VM vm, ThreadInfo currentThread) {
    logger.trace("Exception bailout in thread: {}", currentThread);

  }

  @Override
  public void exceptionHandled(VM vm, ThreadInfo currentThread) {
    logger.trace("Exception handled in thread: {}", currentThread);

  }

  @Override
  public void choiceGeneratorRegistered(VM vm, ChoiceGenerator<?> nextCG, ThreadInfo currentThread, Instruction executedInstruction) {
    logger.trace("Choice generator registered: {} for thread: {}, instruction: {}", nextCG, currentThread, executedInstruction);

  }

  @Override
  public void choiceGeneratorSet(VM vm, ChoiceGenerator<?> newCG) {
    logger.trace("Choice generator set: {}", newCG);

  }

  @Override
  public void choiceGeneratorAdvanced(VM vm, ChoiceGenerator<?> currentCG) {
    logger.trace("Choice generator advanced: {}", currentCG);

  }

  @Override
  public void choiceGeneratorProcessed(VM vm, ChoiceGenerator<?> processedCG) {
    logger.trace("Choice generator processed: {}", processedCG);

  }

  @Override
  public void methodEntered(VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
    logger.trace("Method entered: {}", enteredMethod);

  }

  @Override
  public void methodExited(VM vm, ThreadInfo currentThread, MethodInfo exitedMethod) {
    logger.trace("Method exited: {}", exitedMethod);

  }

  /* (non-Javadoc)
   * @see gov.nasa.jpf.vm.VMListener#objectExposed(gov.nasa.jpf.vm.VM, gov.nasa.jpf.vm.ThreadInfo, gov.nasa.jpf.vm.ElementInfo, gov.nasa.jpf.vm.ElementInfo)
   */
  @Override
  public void objectExposed(VM vm, ThreadInfo currentThread, ElementInfo sharedObject, ElementInfo exposedObject) {
    logger.trace("Object exposed: '{}' .. shared object: '{}' in thread '{}'", exposedObject, sharedObject, currentThread);
    
  }

  /* (non-Javadoc)
   * @see gov.nasa.jpf.vm.VMListener#objectShared(gov.nasa.jpf.vm.VM, gov.nasa.jpf.vm.ThreadInfo, gov.nasa.jpf.vm.ElementInfo)
   */
  @Override
  public void objectShared(VM vm, ThreadInfo currentThread, ElementInfo sharedObject) {
    logger.trace("Object shared: '{}' in thread '{}'", sharedObject, currentThread);
    
  }

}
