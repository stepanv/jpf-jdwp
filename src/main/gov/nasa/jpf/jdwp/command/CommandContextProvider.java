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

package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdwp.VirtualMachine;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.vm.VM;

/**
 * This class provides a context for commands so that all possibly required JDWP
 * or JPF objects are accessed in a uniform way.
 * 
 * @author stepan
 * 
 */
public class CommandContextProvider {

  private VirtualMachine virtualMachine;
  private JdwpIdManager objectManager;

  /**
   * Creates the Command Context Provider.
   * 
   * @param virtualMachine
   *          Virtual Machine instance
   * @param objectManager
   *          JDWP ID manager
   */
  public CommandContextProvider(VirtualMachine virtualMachine, JdwpIdManager objectManager) {
    this.virtualMachine = virtualMachine;
    this.objectManager = objectManager;
  }

  /**
   * Get the JDWP ID Manager
   * 
   * @return The JDWP ID Manager.
   */
  public JdwpIdManager getObjectManager() {
    return objectManager;
  }

  /**
   * Get the JPF Virtual Machine class representation.
   * 
   * @return The VM.
   */
  public VM getVM() {
    return VM.getVM();
  }

  /**
   * Get JPF.
   * 
   * @return JPF
   */
  public JPF getJPF() {
    return getVM().getJPF();
  }

  /**
   * Get the JDWP Virtual Machine class representation.
   * 
   * @return the JDWP VM.
   */
  public VirtualMachine getVirtualMachine() {
    return virtualMachine;
  }

}
