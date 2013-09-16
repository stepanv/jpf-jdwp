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

package gov.nasa.jpf.jdwp.util.test;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.VMListener;

public class JdwpTestListener extends ListenerAdapter implements VMListener {

  final static Logger logger = LoggerFactory.getLogger(JdwpTestListener.class);

  public JdwpTestListener() {
  }

  @Override
  public void methodEntered(VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
    ClassInfo methodClassInfo = enteredMethod.getClassInfo();

    logger.trace("Method entered: {} ... {}", methodClassInfo, enteredMethod);

    if (methodClassInfo != null && methodClassInfo.getName().equals(JdwpVerifier.class.getName())) {
      // we entered verifier class which is a notification to execute the
      // same method outside of SuT

      if (JdwpVerifier.VERIFY_METHOD_NAME.equals(enteredMethod.getName()) && enteredMethod.getArgumentTypeNames().length == 1
          && "java.lang.Object[]".equals(enteredMethod.getArgumentTypeNames()[0])) {

        List<ElementInfo> passedObjects = new LinkedList<ElementInfo>();
        // number [0] is this which we don't care about
        LocalVarInfo passedObjectsAsArray = enteredMethod.getArgumentLocalVars()[1];
        ElementInfo passedObjectsAsArrayElementInfo = (ElementInfo) currentThread.getTopFrame().getLocalValueObject(passedObjectsAsArray);

        for (int i = 0; i < passedObjectsAsArrayElementInfo.arrayLength(); ++i) {
          int objRef = passedObjectsAsArrayElementInfo.getReferenceElement(i);
          passedObjects.add(vm.getHeap().get(objRef));
        }

        int verifierRef = currentThread.getTopFrame().getThis();

        StackFrame testedFrame = currentThread.getTopFrame().getPrevious();
        String testClassName = TestJdwp.verifierTest.getClass().getName();

        while (!testClassName.equals(testedFrame.getMethodInfo().getClassName())) {
          testedFrame = testedFrame.getPrevious();
        }

        int testRef = testedFrame.getThis();
        ElementInfo testEi = vm.getHeap().get(testRef);

        ClassInfo testCi = testEi.getClassInfo();

        String verifierName = null;

        for (FieldInfo fieldInfo : testCi.getDeclaredInstanceFields()) {
          if (fieldInfo.isReference() && testEi.getReferenceField(fieldInfo) == verifierRef) {
            verifierName = fieldInfo.getName();
            break;
          }
        }

        if (verifierName == null) {
          throw new RuntimeException("Verifier id: '" + verifierRef + "' not found in class: " + testCi);
        }

        try {
          Field verifierField = Class.forName(testCi.getName()).getDeclaredField(verifierName);
          verifierField.setAccessible(true);
          JdwpVerifier verifier = (JdwpVerifier) verifierField.get(TestJdwp.verifierTest);
          verifier.verify(passedObjects.toArray());
        } catch (Exception e) {
          throw new TestInError(e);
        }

      }
    }

  }
}
