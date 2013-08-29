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
public class JDWPSearchBase extends JDWPListenerBase implements SearchListener {

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
  public void propertyViolated(Search search) {
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
