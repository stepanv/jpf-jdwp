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
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.vm.VM;

public class CommandContextProvider {

  private VirtualMachine virtualMachine;
  private JdwpObjectManager objectManager;

  public CommandContextProvider(VirtualMachine virtualMachine, JdwpObjectManager objectManager) {
    this.virtualMachine = virtualMachine;
    this.objectManager = objectManager;
  }

  public JdwpObjectManager getObjectManager() {
    return objectManager;
  }

  public VM getVM() {
    return VM.getVM();
  }

  public JPF getJPF() {
    return getVM().getJPF();
  }

  public VirtualMachine getVirtualMachine() {
    return virtualMachine;
  }

}
