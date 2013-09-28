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

import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.search.SearchListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base implementation of {@link SearchListener} notifications.<br/>
 * This class is supposed to be inherited and overriden.
 * 
 * @author stepan
 * 
 */
public abstract class JDWPSearchBase extends JDWPListenerBase implements SearchListener {

  final static Logger logger = LoggerFactory.getLogger(JDWPListenerBase.class);

  @Override
  public void stateAdvanced(Search search) {
    logger.trace("Processing search");

  }

  @Override
  public void stateProcessed(Search search) {
    logger.trace("Processing search");

  }

  @Override
  public void stateBacktracked(Search search) {
    logger.trace("Processing search");

  }

  @Override
  public void statePurged(Search search) {
    logger.trace("Processing search");

  }

  @Override
  public void stateStored(Search search) {
    logger.trace("Processing search");

  }

  @Override
  public void stateRestored(Search search) {
    logger.trace("Processing search");

  }

  @Override
  public void searchStarted(Search search) {
    logger.trace("Processing search");

  }

  @Override
  public void searchConstraintHit(Search search) {
    logger.trace("Processing search");

  }

  @Override
  public void searchFinished(Search search) {
    logger.trace("Processing search");

  }

}
