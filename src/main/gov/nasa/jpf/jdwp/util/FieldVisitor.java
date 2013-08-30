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

package gov.nasa.jpf.jdwp.util;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.FieldAccessEvent;
import gov.nasa.jpf.jdwp.event.FieldModificationEvent;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jvm.bytecode.FieldInstruction;
import gov.nasa.jpf.jvm.bytecode.GETFIELD;
import gov.nasa.jpf.jvm.bytecode.GETSTATIC;
import gov.nasa.jpf.jvm.bytecode.InstructionVisitorAdapter;
import gov.nasa.jpf.jvm.bytecode.PUTFIELD;
import gov.nasa.jpf.jvm.bytecode.PUTSTATIC;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class FieldVisitor extends InstructionVisitorAdapter {

  private ThreadInfo threadInfo;

  /**
   * Field access handler for standard instances and also statics of any class.
   * 
   * @param fieldInstruction
   *          Instruction that has to be investigated
   * @param objectBeingAccessed
   *          Object being accessed or null for statics
   */
  private void fieldAccess(FieldInstruction fieldInstruction, ElementInfo objectBeingAccessed) {

    ClassInfo fieldClassInfo = fieldInstruction.getFieldInfo().getTypeClassInfo();
    Event event = new FieldAccessEvent(threadInfo, Location.factorySafe(fieldInstruction, threadInfo), fieldClassInfo,
        fieldInstruction.getFieldInfo(), objectBeingAccessed);
    Jdwp.notify(event);
  }

  /**
   * Field modification handler for standard instances and also statics of any
   * class.
   * 
   * @param fieldInstruction
   *          Instruction that has to be investigated
   * @param objectBeingModified
   *          Object being modified or null for statics
   */
  private void fieldModification(FieldInstruction fieldInstruction, ElementInfo objectBeingModified) {
    StackFrame topStackFrame = threadInfo.getModifiableTopFrame();

    ClassInfo fieldClassInfo = fieldInstruction.getFieldInfo().getTypeClassInfo();
    Tag tag = Tag.classInfoToTag(fieldClassInfo);

    Event event = new FieldModificationEvent(threadInfo, Location.factorySafe(fieldInstruction, threadInfo), fieldClassInfo,
        fieldInstruction.getFieldInfo(), objectBeingModified, tag, topStackFrame);
    Jdwp.notify(event);
  }

  @Override
  public void visit(GETFIELD ins) {
    fieldAccess(ins, ins.getLastElementInfo());
  }

  @Override
  public void visit(PUTFIELD ins) {
    fieldModification(ins, ins.getLastElementInfo());
  }

  @Override
  public void visit(GETSTATIC ins) {
    fieldAccess(ins, null);
  }

  @Override
  public void visit(PUTSTATIC ins) {

    fieldModification(ins, null);
  }

  public void initalize(ThreadInfo currentThread) {
    this.threadInfo = currentThread;
  }

}
